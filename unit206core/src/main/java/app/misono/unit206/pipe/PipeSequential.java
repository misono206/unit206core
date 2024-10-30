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

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.util.concurrent.atomic.AtomicInteger;

public class PipeSequential<C extends PipeCart> {
	private final PipeCartCallback<C> sender;
	private final AtomicInteger no;
	private final int maxCapacity;

	// synchronized instance
	private final SparseArray<C> array;
	private int noSendNext;

	public PipeSequential(int maxCapacity, @NonNull PipeCartCallback<C> sender) {
		this.maxCapacity = maxCapacity;
		this.sender = sender;
		array = new SparseArray<>();
		no = new AtomicInteger();
	}

	public int getNewNo() {
		return no.getAndIncrement();
	}

	public int getLatestNo() {
		return no.get() - 1;
	}

	@WorkerThread
	public synchronized void cartReady(@NonNull C cart) throws Exception {
		int no = cart.getNo();
		if (no == noSendNext) {
			sender.callback(cart);
			noSendNext++;
			for ( ; ; ) {
				C c = array.get(noSendNext);
				if (c == null) {
					break;
				}
				array.remove(noSendNext);
				sender.callback(c);
				noSendNext++;
			}
		} else if (noSendNext < no) {
			array.append(no, cart);
			int size = array.size();
			if (maxCapacity <= size) {
				throw new RuntimeException("capacity over... maxCapacity:" + maxCapacity + " size:" + size + " no:" + no + " noSendNext:" + noSendNext);
			}
		} else {
			throw new RuntimeException("no is too small... no:" + no + " noSendNext:" + noSendNext);
		}
	}

}
