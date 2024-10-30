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

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.Utils;
import app.misono.unit206.page.PagePref;

import java.util.List;

/**
 * Override Adapter.getItemId() and invoke Adapter.setHasStableIds(true);
 */
public final class NoSelection<T extends LongId> implements ISelection<T>, PagePref {
	private static final String TAG = "NoSelection";

	private final ListAdapter<T, RecyclerView.ViewHolder> mAdapter;
	private final RecyclerView.LayoutManager mManager;

	private boolean mUpdating;

	public interface SelectionObserver {
		void onItemStateChanged(long id, boolean selected);
	}

	public NoSelection(
		@NonNull String tag,
		@NonNull RecyclerView recycler
	) {
		mManager = recycler.getLayoutManager();
		RecyclerView.Adapter adapter = recycler.getAdapter();
		if (adapter instanceof ListAdapter) {
			mAdapter = (ListAdapter)adapter;
		} else {
			throw new RuntimeException("adapter is NOT ListAdapter...");
		}
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
			// wait ListAdapter animation...
			Utils.sleep(500, () -> {
				mUpdating = false;
				if (done != null) {
					done.run();
				}
			});
			log("submitList:done...");
		});
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean isSelected(long id) {
		return false;
	}

	@Override
	public int getSelectedCount() {
		return 0;
	}

	@Override
	public boolean clearSelection() {
		return false;
	}

	@Override
	public int getPosition(Long key) {
		return 0;
	}

	@Override
	@Nullable
	public Bundle createBundle() {
		return null;
	}

	@Override
	public void restoreBundle(@NonNull Bundle bundle) {
	}

	@Override
	public void apply() {
	}

	@Override
	@NonNull
	public String getKey() {
		return TAG;
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

}

