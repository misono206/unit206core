/*
 * Copyright 2023 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.bang;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.task.SingletonTask;
import app.misono.unit206.task.Taskz;
import app.misono.unit206.viewmodel.HashItemUpdater;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public abstract class BangModel<REC, I extends BangItem, M extends BangItemMutable<REC>> {
	private static final String TAG = "BangModel";

	private final Set<Listener<I>> listeners;
	private final SingletonTask task1;
	private final Executor executor;

	private HashItemUpdater<M> updater;
	private BangBao<REC> bao;

	public abstract M createMutable(@NonNull REC rec) throws Exception;

	protected BangModel() {
		this(Taskz.getExecutor());
	}

	protected BangModel(@NonNull Executor executor) {
		this.executor = executor;
		task1 = new SingletonTask();
		listeners = new HashSet<>();
	}

	public void setItemUpdater(@Nullable HashItemUpdater<M> updater) {
		this.updater = updater;
	}

	protected void setBao(@NonNull BangBao<REC> bao) {
		this.bao = bao;
	}

	@WorkerThread
	private void refreshInternalUnsafe() throws Exception {
		List<I> list = readAllItemsUnSafe();
		list = Collections.unmodifiableList(list);
		for (Listener<I> listener : listeners) {
			listener.onAllChanged(list);
		}
	}

	@WorkerThread
	@NonNull
	protected List<I> readAllItemsUnSafe() throws Exception {
		List<REC> listRecord = bao.loadAllRecords();
		List<I> list = new ArrayList<>();
		for (REC rec : listRecord) {
			M item = createMutable(rec);
			if (updater != null) {
				updater.update(item);
			}
			list.add((I)item);
		}
		return list;
	}

	@AnyThread
	@NonNull
	protected Task<Void> refreshTask() {
		return task1.call(executor, () -> {
			refreshInternalUnsafe();
			return null;
		});
	}

	@AnyThread
	@NonNull
	protected Task<Void> insertTask(@NonNull M item) {
		return task1.call(executor, () -> {
			insertItemUnsafe(item);
			return null;
		});
	}

	@WorkerThread
	protected void insertItemUnsafe(@NonNull M item) throws Exception {
		if (updater != null) {
			updater.update(item);
		}
		item.setCreatedTime();
		item.setLongId(0);
		item.setLongId(bao.insertItem(item.toRecord()));
		if (0 < item.getLongId()) {
			for (Listener<I> listener : listeners) {
				listener.onInsert((I)item);
			}
		}
	}

	@AnyThread
	@NonNull
	protected Task<Void> insertListTask(@NonNull List<M> list) {
		return task1.call(executor, () -> {
			insertListUnsafe(list);
			return null;
		});
	}

	@WorkerThread
	protected void insertListUnsafe(@NonNull List<M> list) throws Exception {
		List<I> changed = new ArrayList<>();
		for (M item : list) {
			if (updater != null) {
				updater.update(item);
			}
			item.setCreatedTime();
			item.setLongId(0);
			long id = bao.insertItem(item.toRecord());
			item.setLongId(id);
			if (0 < id) {
				changed.add((I)item);
			}
		}
		if (!changed.isEmpty()) {
			changed = Collections.unmodifiableList(changed);
			for (Listener<I> listener : listeners) {
				listener.onInsertList(changed);
			}
		}
	}

	@AnyThread
	@NonNull
	protected Task<Integer> updateTask(@NonNull M item) {
		return task1.call(executor, () -> {
			return updateItemUnsafe(item);
		});
	}

	@WorkerThread
	protected int updateItemUnsafe(@NonNull M item) throws Exception {
		if (updater != null) {
			updater.update(item);
		}
		item.setModifiedTime();
		int rows = bao.updateItem(item.toRecord());
		if (0 < rows) {
			for (Listener<I> listener : listeners) {
				listener.onUpdate((I)item);
			}
		}
		return rows;
	}

	@AnyThread
	@NonNull
	protected Task<int[]> updateListTask(@NonNull List<M> list) {
		return task1.call(executor, () -> {
			return updateListUnsafe(list);
		});
	}

	@WorkerThread
	@NonNull
	protected int[] updateListUnsafe(@NonNull List<M> list) throws Exception {
		List<I> changed = new ArrayList<>();
		int[] rc = new int[list.size()];
		int i = 0;
		for (M item : list) {
			if (updater != null) {
				updater.update(item);
			}
			item.setModifiedTime();
			int rows = bao.updateItem(item.toRecord());
			rc[i++] = rows;
			if (0 < rows) {
				changed.add((I)item);
			}
		}
		if (!changed.isEmpty()) {
			changed = Collections.unmodifiableList(changed);
			for (Listener<I> listener : listeners) {
				listener.onUpdateList(changed);
			}
		}
		return rc;
	}

	@AnyThread
	@NonNull
	protected Task<Integer> deleteTask(@NonNull I item) {
		return task1.call(executor, () -> {
			return deleteItemUnsafe(item);
		});
	}

	@WorkerThread
	protected int deleteItemUnsafe(@NonNull I item) throws Exception {
		int rows = bao.deleteItem(item.getLongId());
		if (0 < rows) {
			for (Listener<I> listener : listeners) {
				listener.onRemove(item);
			}
		}
		return rows;
	}

	@AnyThread
	@NonNull
	protected Task<int[]> deleteListTask(@NonNull List<I> list) {
		return task1.call(executor, () -> {
			return deleteListUnsafe(list);
		});
	}

	@WorkerThread
	@NonNull
	protected int[] deleteListUnsafe(@NonNull List<I> list) throws Exception {
		List<I> changed = new ArrayList<>();
		int[] rc = new int[list.size()];
		int i = 0;
		for (I item : list) {
			int rows = bao.deleteItem(item.getLongId());
			rc[i++] = rows;
			if (0 < rows) {
				changed.add(item);
			}
		}
		if (!changed.isEmpty()) {
			changed = Collections.unmodifiableList(changed);
			for (Listener<I> listener : listeners) {
				listener.onRemoveList(changed);
			}
		}
		return rc;
	}

	@AnyThread
	@NonNull
	protected Task<Void> deleteAllTask() {
		return task1.call(executor, () -> {
			bao.deleteAllRecords();
			refreshInternalUnsafe();
			return null;
		});
	}

	@AnyThread
	@NonNull
	protected <T> Task<T> executeSafe(@NonNull Callable<T> runnable) {
		return task1.call(executor, runnable);
	}

	@AnyThread
	protected void addListener(@NonNull Listener<I> listener) {
		task1.call(executor, () -> {
			listeners.add(listener);
			return null;
		}).addOnFailureListener(Taskz::printStackTrace2);
	}

	@AnyThread
	protected void removeListener(@NonNull Listener<I> listener) {
		task1.call(executor, () -> {
			listeners.remove(listener);
			return null;
		}).addOnFailureListener(Taskz::printStackTrace2);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

	protected interface Listener<I> {
		@WorkerThread
		void onInsert(@NonNull I item) throws Exception;
		@WorkerThread
		void onInsertList(@NonNull List<I> item) throws Exception;
		@WorkerThread
		void onUpdate(@NonNull I item) throws Exception;
		@WorkerThread
		void onUpdateList(@NonNull List<I> item) throws Exception;
		@WorkerThread
		void onRemove(@NonNull I item) throws Exception;
		@WorkerThread
		void onRemoveList(@NonNull List<I> item) throws Exception;
		@WorkerThread
		void onAllChanged(@NonNull List<I> list) throws Exception;
	}

}
