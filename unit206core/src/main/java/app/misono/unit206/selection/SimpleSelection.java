/*
 * Copyright 2020 Atelier Misono, Inc. @ https://misono.app/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.misono.unit206.selection;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.Utils;
import app.misono.unit206.page.PagePref;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Override Adapter.getItemId() and invoke Adapter.setHasStableIds(true);
 */
public final class SimpleSelection<T extends LongId> implements ISelection<T>, PagePref {
	private static final String TAG = "SimpleSelection";

	private final ListAdapter<T, RecyclerView.ViewHolder> mAdapter;
	private final RecyclerView.LayoutManager mManager;
	private final LongSparseArray<Boolean> mSelected;
	private final SelectionTracker<Long> mTracker;
	private final Set<SelectionObserver> mObservers;
	private final LongIdKeyProvider mProvider;

	private boolean mUpdating;

	public interface SelectionObserver {
		void onItemStateChanged(long id, boolean selected);
	}

	public SimpleSelection(
		@NonNull String tag,
		@NonNull RecyclerView recycler,
		boolean enableTracker
	) {
		mManager = recycler.getLayoutManager();
		mProvider = new LongIdKeyProvider();
		mTracker = new SelectionTracker.Builder<>(
			tag,
			recycler,
			mProvider,
			new ItemIdLookup(recycler),
			StorageStrategy.createLongStorage()
		).withSelectionPredicate(
			SelectionPredicates.createSelectAnything()
		).build();
		mSelected = new LongSparseArray<>();
		RecyclerView.Adapter adapter = recycler.getAdapter();
		if (adapter instanceof ListAdapter) {
			mAdapter = (ListAdapter)adapter;
		} else {
			throw new RuntimeException("adapter is NOT ListAdapter...");
		}
		mObservers = new HashSet<>();
		if (enableTracker) {
			mTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
				@Override
				public void onItemStateChanged(@NonNull Long key, boolean selected) {
					log("onItemStateChanged:" + key + " " + selected + " " + mUpdating);
					if (mUpdating) {
						mSelected.put(key, selected);
					} else {
						setSelected(key, selected);
					}
				}
			});
		}
	}

	public SimpleSelection(@NonNull String tag, @NonNull RecyclerView recycler) {
		this(tag, recycler, true);
	}

	@NonNull
	public SelectionTracker<Long> getSelectionTracker() {
		return mTracker;
	}

	public void addObserver(@NonNull SelectionObserver observer) {
		mObservers.add(observer);
	}

	public void removeObserver(@Nullable SelectionObserver observer) {
		mObservers.remove(observer);
	}

	@NonNull
	public List<Integer> getSelectedPositions() {
		Selection<Long> sel = mTracker.getSelection();
		Iterator<Long> iterator = sel.iterator();
		List<Integer> rc = new ArrayList<>();
		for ( ; iterator.hasNext(); ) {
			rc.add(mProvider.getPosition(iterator.next()));
		}
		return rc;
	}

	@NonNull
	public List<Long> getSelectedIds() {
		Selection<Long> sel = mTracker.getSelection();
		Iterator<Long> iterator = sel.iterator();
		List<Long> rc = new ArrayList<>();
		for ( ; iterator.hasNext(); ) {
			rc.add(iterator.next());
		}
		return rc;
	}

	@MainThread
	public void submitList(@NonNull List<T> list) {
		submitList(list, null);
	}

	@MainThread
	public void submitList(@NonNull List<T> list, @Nullable Runnable done) {
		log("submitList:" + list.size());
		mUpdating = true;
		mAdapter.submitList(list, () -> {
			mProvider.setLongIdList(list);
			// wait ListAdapter animation...
			Utils.sleep(500, () -> {
				int n = mSelected.size();
				log("wakeup:" + n);
				for (int i = 0; i < n; i++) {
					long id = mSelected.keyAt(i);
					boolean selected = mSelected.valueAt(i);
					setSelected(id, selected);
				}
				mSelected.clear();
				mUpdating = false;

				List<Long> listId = mProvider.getIdList();
				Selection<Long> selection = mTracker.getSelection();
				for (Long id : selection) {
					if (!listId.contains(id)) {
						mTracker.deselect(id);
					}
				}
				if (done != null) {
					done.run();
				}
			});
			log("submitList:done...");
		});
	}

	private void setSelected(long key, boolean selected) {
		List<Long> listId = mProvider.getIdList();
		int position = listId.indexOf(key);
		log("setSelected:" + key + " pos:" + position + " " + selected);
		View card = mManager.findViewByPosition(position);
		if (card != null) {
			card.setSelected(selected);
		} else {
			log("card == null...");
		}
		for (SelectionObserver observer : mObservers) {
			observer.onItemStateChanged(key, selected);
		}
	}

	public boolean isEmpty() {
		return !mTracker.hasSelection();
	}

	public boolean isSelected(long id) {
		return mTracker.isSelected(id);
	}

	public int getSelectedCount() {
		return mTracker.getSelection().size();
	}

	public boolean clearSelection() {
		return mTracker.clearSelection();
	}

	/**
	 * the position corresponding to the selection key, or RecyclerView.NO_POSITION.
	 */
	public int getPosition(Long key) {
		return mProvider.getPosition(key);
	}

	@Override
	public Bundle createBundle() {
		Bundle b = new Bundle();
		mTracker.onSaveInstanceState(b);
		return b;
	}

	@Override
	public void restoreBundle(@NonNull Bundle bundle) {
		Bundle b = bundle.getBundle(TAG);
		if (b != null) {
			mTracker.onRestoreInstanceState(b);
		}
	}

	@Override
	public void apply() {
	}

	@Override
	@NonNull
	public String getKey() {
		return TAG;
	}

	private static class ItemIdLookup extends ItemDetailsLookup<Long> {
		private final RecyclerView mRecycler;

		private ItemIdLookup(@NonNull RecyclerView recycler) {
			super();
			mRecycler = recycler;
		}

		@Nullable
		@Override
		public ItemDetails<Long> getItemDetails(@NonNull MotionEvent event) {
			View view = mRecycler.findChildViewUnder(event.getX(), event.getY());
			if (view != null) {
				RecyclerView.ViewHolder viewHolder = mRecycler.getChildViewHolder(view);
				return new ItemDetailsLookup.ItemDetails<Long>() {
					@Override
					public int getPosition() {
						return viewHolder.getAdapterPosition();
					}

					@Override
					public Long getSelectionKey() {
						return viewHolder.getItemId();
					}
				};
			}
			return null;
		}
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

	@NonNull
	public static <VH extends RecyclerView.ViewHolder> RecyclerView createRecyclerView(
		@NonNull Context context,
		@NonNull RecyclerView.Adapter<VH> adapter,
		int bgColor
	) {
		RecyclerView recycler = new RecyclerView(context);
		recycler.setBackgroundColor(bgColor);
		recycler.setMotionEventSplittingEnabled(false);
		GridLayoutManager manager = new GridLayoutManager(context, 1);
//		manager.setOrientation(RecyclerView.VERTICAL);
		recycler.setLayoutManager(manager);
		recycler.setAdapter(adapter);
		recycler.setItemAnimator(new SelectionItemAnimator());
		return recycler;
	}

	@NonNull
	public static <T extends LongId> List<Long> getIdList(@NonNull List<T> list) {
		List<Long> rc = new ArrayList<>();
		for (LongId item : list) {
			rc.add(item.getLongId());
		}
		return rc;
	}

}

