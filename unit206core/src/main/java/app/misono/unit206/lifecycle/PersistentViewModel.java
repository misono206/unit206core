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

package app.misono.unit206.lifecycle;

import android.app.Application;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import app.misono.unit206.misc.ThreadGate;
import app.misono.unit206.task.Taskz;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.Closeable;

@Deprecated	// use viewmodel path
public class PersistentViewModel<E> extends AndroidViewModel implements Closeable {
	private final MutableLiveData<E> liveData;
	private final ThreadGate gate;

	private Observer<E> observer;

	public PersistentViewModel(@NonNull Application app) {
		super(app);
		liveData = new MutableLiveData<>();
		gate = new ThreadGate();
		observer = event -> {
			if (event != null) {
				gate.open();
				liveData.removeObserver(observer);
				observer = null;
			}
		};
		liveData.observeForever(observer);
	}

	protected void postEvent(@NonNull E event) {
		liveData.postValue(event);
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

	public void observeForever(@NonNull Observer<E> observer) {
		liveData.observeForever(observer);
	}

	public void removeObserver(@NonNull Observer<E> observer) {
		liveData.removeObserver(observer);
	}

	@Override
	@MainThread
	public void close() {
		onCleared();
	}

}

