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

package app.misono.unit206.viewmodel;

import android.app.Application;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.ThreadGate;
import app.misono.unit206.task.SingletonTask;
import app.misono.unit206.task.Taskz;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.Closeable;
import java.util.HashSet;
import java.util.Set;

public class PersistentViewModel<E> extends AndroidViewModel implements Closeable {
	private static final String TAG = "PersistentViewModel";

	private final MutableLiveData<E> liveData;
	private final SingletonTask task1;
	private final ThreadGate gate;
	private final boolean enablePostReady;

	private Observer<E> observer;

	// task1 instance
	private final Set<Observer<E>> observers;
	private final Set<Observer<E>> notReady;
	private E latest;

	@MainThread
	public PersistentViewModel(@NonNull Application app, boolean enablePostReady) {
		super(app);
		this.enablePostReady = enablePostReady;
		if (enablePostReady) {
			task1 = new SingletonTask();
			observers = new HashSet<>();
			notReady = new HashSet<>();
		} else {
			task1 = null;
			observers = null;
			notReady = null;
		}
		liveData = new MutableLiveData<>();
		gate = new ThreadGate();
		observer = event -> {
			if (event != null) {
				gate.open();
				removeObserver(observer);
				observer = null;
			}
		};
		observeForever(observer);
		setPostReady(observer);
	}

	@Deprecated
	public PersistentViewModel(@NonNull Application app) {
		this(app, false);
	}

	/**
	 * It's valid if enablePostReady == true.
	 * observer全員が　setPostReady()しないと次の eventはこない.
	 */
	public void setPostReady(@NonNull Observer<E> observer) {
		if (enablePostReady) {
			task1.call(Taskz.getExecutor(), () -> {
				notReady.remove(observer);
				if (latest != null && notReady.isEmpty()) {
					liveData.postValue(latest);
					latest = null;
					notReady.addAll(observers);
				}
				return null;
			}).addOnFailureListener(Taskz::printStackTrace2);
		}
	}

	protected void postEvent(@NonNull E event) {
		if (enablePostReady) {
			task1.call(Taskz.getExecutor(), () -> {
				if (notReady.isEmpty()) {
					liveData.postValue(event);
					notReady.addAll(observers);
				} else {
					latest = event;
				}
				return null;
			}).addOnFailureListener(Taskz::printStackTrace2);
		} else {
			liveData.postValue(event);
		}
	}

	@NonNull
	public E getEvent() {
		E rc = liveData.getValue();
		if (rc == null) {
			throw new RuntimeException("liveData.getValue() is null... Please invoke waitReady().");
		}
		return liveData.getValue();
	}

	@NonNull
	public Task<Void> waitReady() {
		if (!gate.isReady()) {
			return Taskz.call(Taskz.getExecutor(), () -> {
				gate.block();
				return null;
			});
		} else {
			return Tasks.forResult(null);
		}
	}

	@WorkerThread
	public void waitReadySync() throws Exception {
		if (!gate.isReady()) {
			gate.block();
		}
	}

	public boolean isReady() {
		return gate.isReady();
	}

	@MainThread
	public void observeForever(@NonNull Observer<E> observer) {
		liveData.observeForever(observer);
		if (enablePostReady) {
			task1.call(Taskz.getExecutor(), () -> {
				observers.add(observer);
				return null;
			}).addOnFailureListener(Taskz::printStackTrace2);
		}
	}

	@MainThread
	public void removeObserver(@NonNull Observer<E> observer) {
		liveData.removeObserver(observer);
		if (enablePostReady) {
			task1.call(Taskz.getExecutor(), () -> {
				observers.remove(observer);
				notReady.remove(observer);
				if (latest != null && notReady.isEmpty()) {
					liveData.postValue(latest);
					latest = null;
					notReady.addAll(observers);
				}
				return null;
			}).addOnFailureListener(Taskz::printStackTrace2);
		}
	}

	@Override
	@MainThread
	public void close() {
		onCleared();
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}

