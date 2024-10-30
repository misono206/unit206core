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

package app.misono.unit206.pipe;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.FifoQueue;
import app.misono.unit206.misc.Utils;

import java.io.Closeable;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

public class PipeTask<C extends PipeCart> implements Closeable {
	private static final String TAG = "PipeTask";

	private final PipeCartCallback<C> onAvailableCart;
	private final FifoQueue<C> queue;
	private final Semaphore semExec, semTake;
	private final Executor executor;

	public PipeTask(@NonNull Executor executor, @NonNull PipeCartCallback<C> onAvailableCart) {
		this.executor = executor;
		this.onAvailableCart = onAvailableCart;
		queue = new FifoQueue<>(1, this::onAvailableQueue);
		semExec = new Semaphore(1);
		semTake = new Semaphore(1);
	}

	@WorkerThread
	private void onAvailableQueue() {
		try {
			semTake.acquire();
			C cart = queue.take();
			semExec.acquire();
			executor.execute(() -> {
				try {
					onAvailableCart.callback(cart);
				} catch (Exception e2) {
					e2.printStackTrace();
				} finally {
					semExec.release();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			semTake.release();
		}
	}

	@WorkerThread
	public void inputSync(@NonNull C cart) throws InterruptedException {
		queue.put(cart);
	}

	@WorkerThread
	public void waitDone() throws InterruptedException {
		queue.waitEmpty();
	}

	@Override
	public void close() {
		Utils.closeSafely(queue);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
