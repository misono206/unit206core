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

import android.app.Application;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.task.Taskz;
import app.misono.unit206.viewmodel.HashViewModel;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.List;

public abstract class BangViewModel<
	REC,
	I extends BangItem,
	M extends BangItemMutable<REC>,
	E extends BangEvent<I>
> extends HashViewModel<E, I> {
	private static final String TAG = "BangViewModel";

	private final BangModel.Listener<I> listener;
	private final BangModel<REC, I, M> model;

	public BangViewModel(
		@NonNull Application app,
		@NonNull BangModel<REC, I, M> model,
		boolean enablePostReady
	) {
		super(app, enablePostReady);
		this.model = model;
		listener = new BangModel.Listener<I>() {
			@Override
			@WorkerThread
			public void onInsert(@NonNull I item) throws Exception {
				putItemSync(item);
			}

			@Override
			@WorkerThread
			public void onInsertList(@NonNull List<I> list) throws Exception {
				putListSync(list);
			}

			@Override
			@WorkerThread
			public void onUpdate(@NonNull I item) throws Exception {
				putItemSync(item);
			}

			@Override
			@WorkerThread
			public void onUpdateList(@NonNull List<I> list) throws Exception {
				putListSync(list);
			}

			@Override
			@WorkerThread
			public void onRemove(@NonNull I item) throws Exception {
				removeItemSync(item);
			}

			@Override
			@WorkerThread
			public void onRemoveList(@NonNull List<I> list) throws Exception {
				removeListSync(list);
			}

			@Override
			@WorkerThread
			public void onAllChanged(@NonNull List<I> list) throws Exception {
				newListSync(list);
			}
		};
		model.addListener(listener);
		refreshTask().addOnFailureListener(Taskz::printStackTrace2);
	}

	public BangViewModel(@NonNull Application app, @NonNull BangModel<REC, I, M> model) {
		this(app, model, false);
	}

	@AnyThread
	@NonNull
	public Task<Void> refreshTask() {
		return model.refreshTask();
	}

	@AnyThread
	@NonNull
	public Task<Void> insertTask(@NonNull M item) {
		return model.insertTask(item);
	}

	@WorkerThread
	public void insertItem(@NonNull M item) throws Exception {
		Tasks.await(model.insertTask(item));
	}

	@AnyThread
	@NonNull
	public Task<Void> insertListTask(@NonNull List<M> list) {
		return model.insertListTask(list);
	}

	@WorkerThread
	public void insertList(@NonNull List<M> list) throws Exception {
		Tasks.await(model.insertListTask(list));
	}

	@AnyThread
	@NonNull
	public Task<Integer> updateTask(@NonNull M item) {
		return model.updateTask(item);
	}

	@WorkerThread
	public int updateItem(@NonNull M item) throws Exception {
		return Tasks.await(model.updateTask(item));
	}

	@AnyThread
	@NonNull
	public Task<int[]> updateListTask(@NonNull List<M> list) {
		return model.updateListTask(list);
	}

	@WorkerThread
	public int[] updateList(@NonNull List<M> list) throws Exception {
		return Tasks.await(model.updateListTask(list));
	}

	@AnyThread
	@NonNull
	public Task<Integer> deleteTask(@NonNull I item) {
		return model.deleteTask(item);
	}

	@WorkerThread
	public int deleteItem(@NonNull I item) throws Exception {
		return Tasks.await(model.deleteTask(item));
	}

	@AnyThread
	@NonNull
	public Task<int[]> deleteListTask(@NonNull List<I> list) {
		return model.deleteListTask(list);
	}

	@WorkerThread
	public int[] deleteList(@NonNull List<I> list) throws Exception {
		return Tasks.await(model.deleteListTask(list));
	}

	@AnyThread
	@NonNull
	public Task<Void> deleteAllTask() {
		return model.deleteAllTask();
	}

	@Override
	protected void onCleared() {
		log("onCleared:");
		model.removeListener(listener);
		super.onCleared();
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
