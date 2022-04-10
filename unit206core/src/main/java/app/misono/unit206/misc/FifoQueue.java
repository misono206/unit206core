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

package app.misono.unit206.misc;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *	Provides a blocking FIFO queue.
 */
public class FifoQueue<E> implements Closeable {
	private final ReentrantLock lock;
	private final Runnable onAvailable;
	private final int capacity;

	// lock instance
	private Condition notFull, notEmpty;
	private LinkedList<E> fifo;

	public FifoQueue(int capacity) {
		this(capacity, null);
	}

	public FifoQueue(int capacity, @Nullable Runnable onAvailable) {
		this.capacity = capacity;
		this.onAvailable = onAvailable;
		lock = new ReentrantLock();
		notFull = lock.newCondition();
		notEmpty = lock.newCondition();
		fifo = new LinkedList<E>();
	}

	@Override
	public void close() {
		if (fifo != null) {
			lock.lock();
			if (fifo != null) {
				notEmpty.signalAll();
				notFull.signalAll();
			}
			notFull = null;
			notEmpty = null;
			fifo = null;
			lock.unlock();
		}
	}

	@WorkerThread
	public void put(E e) throws InterruptedException {
		lock.lock();
		try {
			if (fifo != null) {
				if (fifo.size() == capacity) {
					notFull.await();
				}
				if (fifo != null) {
					fifo.add(e);
					notEmpty.signal();
					if (onAvailable != null) {
						onAvailable.run();
					}
				}
			}
		} finally {
			lock.unlock();
		}
	}

	@WorkerThread
	public E take() throws InterruptedException {
		E rc = null;
		lock.lock();
		try {
			if (fifo != null) {
				if (fifo.isEmpty()) {
					notEmpty.await();
				}
				if (fifo != null) {
					rc = fifo.removeFirst();
					notFull.signal();
				}
			}
		} finally {
			lock.unlock();
		}
		return rc;
	}

	public int count() {
		return fifo.size();
	}

}
