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

import android.view.View;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.WorkerThread;

import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Taskz {
	private static Executor executor;

	@AnyThread
	@NonNull
	public static synchronized Executor getExecutor() {
		if (executor == null) {
			executor = Executors.newCachedThreadPool();
		}
		return executor;
	}

	@AnyThread
	@NonNull
	public static <T> Task<T> call(@NonNull Executor executor, @NonNull Callable<T> callable) {
		try {
			return callUnCatch(executor, null, callable);
		} catch (Exception e) {
			return Tasks.forException(e);
		}
	}

	@MainThread
	@NonNull
	public static <T> Task<T> call(
		@NonNull Executor executor,
		@NonNull View base,
		@StringRes int idMsg,
		@NonNull Callable<T> callable
	) {
		Snackbar snack = Snackbar.make(base, idMsg, Snackbar.LENGTH_INDEFINITE);
		snack.show();
		try {
			return callUnCatch(executor, null, callable).addOnCompleteListener(task -> {
				snack.dismiss();
			});
		} catch (Exception e) {
			snack.dismiss();
			return Tasks.forException(e);
		}
	}

	@AnyThread
	@NonNull
	public static <T> Task<T> call(
		@NonNull Executor executor,
		@Nullable CancellationToken cancel,
		@NonNull Callable<T> callable
	) {
		try {
			return callUnCatch(executor, cancel, callable);
		} catch (Exception e) {
			return Tasks.forException(e);
		}
	}

	@AnyThread
	@NonNull
	public static <T> Task<T> call(@NonNull Callable<T> callable) {
		try {
			return callUnCatch(TaskExecutors.MAIN_THREAD, null, callable);
		} catch (Exception e) {
			return Tasks.forException(e);
		}
	}

	@AnyThread
	@NonNull
	public static <T> Task<T> callUnCatch(@NonNull Executor executor, @NonNull Callable<T> callable) {
		return callUnCatch(executor, null, callable);
	}

	@AnyThread
	@NonNull
	public static <T> Task<T> callUnCatch(
		@NonNull Executor executor,
		@Nullable CancellationToken cancel,
		@NonNull Callable<T> callable
	) {
		ObjectReference<Thread> refThread = new ObjectReference<>();
		TaskCompletionSource<T> src;
		if (cancel != null) {
			cancel.onCanceledRequested(() -> {
				Thread thread = refThread.get();
				if (thread != null) {
					thread.interrupt();
				}
			});
			src = new TaskCompletionSource<>(cancel);
		} else {
			src = new TaskCompletionSource<>();
		}
		executor.execute(() -> {
			refThread.set(Thread.currentThread());
			try {
				src.setResult(callable.call());
			} catch (Exception e) {
				if (!(e instanceof InterruptedException)) {
					src.setException(e);
				}
			}
			refThread.set(null);
		});
		return src.getTask();
	}

	@WorkerThread
	public static <T> T awaitThrowIfException(@NonNull Task<T> task) throws Exception {
		Tasks.await(task);
		if (!task.isSuccessful()) {
			Exception e = task.getException();
			if (e != null) {
				throw e;
			}
		}
		return task.getResult();
	}

	@AnyThread
	public static boolean printStackTrace(@Nullable Throwable e) {
		boolean rc = e != null;
		if (rc) {
			e.printStackTrace();
		}
		return rc;
	}

	@AnyThread
	public static boolean printStackTrace2(@Nullable Throwable e) {
		boolean rc = e != null && !(e instanceof InterruptedException);
		if (rc) {
			e.printStackTrace();
		}
		return rc;
	}

	@AnyThread
	public static boolean snackStackTrace2(@Nullable Throwable e, @NonNull View base) {
		boolean rc = printStackTrace2(e);
		if (rc && e != null) {
			Taskz.call(TaskExecutors.MAIN_THREAD, () -> {
				Snackbar.make(base, e.toString(), Snackbar.LENGTH_SHORT).show();
				return null;
			});
		}
		return rc;
	}

}
