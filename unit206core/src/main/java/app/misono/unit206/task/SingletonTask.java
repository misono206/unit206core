/*
 * Copyright 2020-2022 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.task;

import android.os.OperationCanceledException;

import androidx.annotation.NonNull;

import app.misono.unit206.misc.ThreadGate;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class SingletonTask {
	private final Object LOCK = new Object();
	private final List<TaskSemaphore> list;

	public SingletonTask() {
		list = new ArrayList<>();
	}

	@NonNull
	public <T> Task<T> call(@NonNull Executor executor, @NonNull Callable<T> callable) {
		TaskSemaphore sem = getSemaphore();
		try {
			return Taskz.call(executor, () -> {
				sem.acquire();
				T rc;
				try {
					rc = callable.call();
				} finally {
					sem.release();
				}
				return rc;
			});
		} catch (Exception e) {
			sem.release();
			return Tasks.forException(e);
		}
	}

	@NonNull
	public <T> Task<T> callLatest(@NonNull Executor executor, @NonNull Callable<T> callable) {
		TaskSemaphore sem = getSemaphore();
		try {
			return Taskz.call(executor, () -> {
				sem.acquire();
				if (sem.e == null) {
					T rc;
					try {
						rc = callable.call();
					} finally {
						sem.releaseLatest();
					}
					return rc;
				} else {
					throw sem.e;
				}
			});
		} catch (Exception e) {
			sem.releaseLatest();
			return Tasks.forException(e);
		}
	}

	@NonNull
	public Task<Void> call(@NonNull Executor executor, @NonNull BlockingRunnable runnable) {
		TaskSemaphore sem = getSemaphore();
		try {
			return Taskz.call(executor, () -> {
				sem.acquire();
				ThreadGate block = new ThreadGate();
				try {
					runnable.run(block);
				} catch (Exception e) {
					sem.release();
					throw e;
				}
				block.block();
				sem.release();
				return null;
			});
		} catch (Exception e) {
			sem.release();
			return Tasks.forException(e);
		}
	}

	private TaskSemaphore getSemaphore() {
		TaskSemaphore sem = new TaskSemaphore();
		synchronized(LOCK) {
			list.add(sem);
			if (list.size() == 1) {
				sem.open();
			}
		}
		return sem;
	}

	private class TaskSemaphore  {
		private final ThreadGate gate;

		private Exception e;

		private TaskSemaphore() {
			gate = new ThreadGate();
		}

		private void open() {
			gate.open();
		}

		private void acquire() throws InterruptedException {
			gate.block();
		}

		private void release() {
			synchronized(LOCK) {
				if (!list.isEmpty()) {
					TaskSemaphore sem = list.get(0);
					if (sem == this) {
						list.remove(0);
						if (!list.isEmpty()) {
							list.get(0).open();
						}
					} else {
						list.remove(this);
					}
				}
			}
		}

		private void releaseLatest() {
			synchronized(LOCK) {
				int size = list.size();
				if (size != 0) {
					TaskSemaphore sem;
					for ( ; size != 1; ) {
						sem = list.remove(0);
						if (sem != this) {
							sem.e = new OperationCanceledException("not latest...");
							sem.open();
						}
						size--;
					}
					sem = list.get(0);
					if (sem != this) {
						sem.open();
					} else {
						list.remove(0);
					}
				}
			}
		}
	}

}
