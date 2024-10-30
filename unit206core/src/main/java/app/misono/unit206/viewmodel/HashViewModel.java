/*
 * Copyright 2024 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.viewmodel;

import android.app.Application;
import android.util.LongSparseArray;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.selection.LongId;
import app.misono.unit206.task.SingletonTask;
import app.misono.unit206.task.Taskz;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

public abstract class HashViewModel<E, I extends LongId> extends PersistentViewModel<E> {
	private static final String TAG = "HashViewModel";

	private final SingletonTask task1;

	// task1 instance
	private final LongSparseArray<I> hash;

	@WorkerThread
	@NonNull
	public abstract E createEvent(@NonNull List<I> list);

	public HashViewModel(@NonNull Application app, boolean enablePostReady) {
		super(app, enablePostReady);
		hash = new LongSparseArray<>();
		task1 = new SingletonTask();
	}

	public HashViewModel(@NonNull Application app) {
		this(app, false);
	}

	@AnyThread
	@NonNull
	protected Task<Void> putItem(@NonNull I item) {
		return task1.call(Taskz.getExecutor(), () -> {
			hash.put(item.getLongId(), item);
			postValue();
			return null;
		});
	}

	@WorkerThread
	protected void putItemSync(@NonNull I item) throws Exception {
		Tasks.await(putItem(item));
	}

	@AnyThread
	@NonNull
	public Task<Void> putList(@NonNull List<I> list) {
		return task1.call(Taskz.getExecutor(), () -> {
			for (I item : list) {
				hash.put(item.getLongId(), item);
			}
			postValue();
			return null;
		});
	}

	@WorkerThread
	protected void putListSync(@NonNull List<I> list) throws Exception {
		Tasks.await(putList(list));
	}

	@AnyThread
	@NonNull
	protected Task<Void> removeItem(@NonNull I item) {
		return task1.call(Taskz.getExecutor(), () -> {
			hash.delete(item.getLongId());
			postValue();
			return null;
		});
	}

	@WorkerThread
	protected void removeItemSync(@NonNull I item) throws Exception {
		Tasks.await(removeItem(item));
	}

	@AnyThread
	@NonNull
	public Task<Void> removeList(@NonNull List<I> list) {
		return task1.call(Taskz.getExecutor(), () -> {
			for (I item : list) {
				hash.delete(item.getLongId());
			}
			postValue();
			return null;
		});
	}

	@WorkerThread
	protected void removeListSync(@NonNull List<I> list) throws Exception {
		Tasks.await(removeList(list));
	}

	@AnyThread
	@NonNull
	public Task<Void> newList(@NonNull List<I> list) {
		return task1.call(Taskz.getExecutor(), () -> {
			hash.clear();
			for (I item : list) {
				hash.put(item.getLongId(), item);
			}
			postValue();
			return null;
		});
	}

	@WorkerThread
	protected void newListSync(@NonNull List<I> list) throws Exception {
		Tasks.await(newList(list));
	}

	// task1 method
	private void postValue() {
		List<I> list = new ArrayList<>();
		int n = hash.size();
		for (int i = 0; i < n; i++) {
			I item = hash.valueAt(i);
			list.add(item);
		}
		postEvent(createEvent(list));
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
