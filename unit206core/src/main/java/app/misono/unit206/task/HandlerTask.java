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

package app.misono.unit206.task;

import android.os.Handler;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;

import app.misono.unit206.misc.ThreadGate;

import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

/**
 * Task for Handler
 */
public class HandlerTask {
	private final Executor mExecutor;
	private final Handler mHandler;

	public HandlerTask(@NonNull Executor executor, @NonNull Handler handler) {
		mExecutor = executor;
		mHandler = handler;
	}

	@AnyThread
	@NonNull
	public Task<Void> call(@NonNull Runnable runnable) {
		return Taskz.call(mExecutor, () -> {
			ObjectReference<Exception> refException = new ObjectReference<>();
			ThreadGate gate = new ThreadGate();
			mHandler.post(() -> {
				try {
					runnable.run();
				} catch (Exception e) {
					refException.set(e);
				} finally {
					gate.open();
				}
			});
			gate.block();
			Exception e = refException.get();
			if (e != null) {
				throw e;
			}
			return null;
		});
	}

}
