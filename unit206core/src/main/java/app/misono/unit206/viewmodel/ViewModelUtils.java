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

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import app.misono.unit206.task.ConditionCall;
import app.misono.unit206.task.ObjectReference;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class ViewModelUtils {

	@MainThread
	public static <T> void observeOnce(
		@NonNull LifecycleOwner owner,
		@NonNull LiveData<T> liveData,
		@NonNull Observer<T> observer
	) {
		ObjectReference<Observer<T>> refObserver = new ObjectReference<>();
		refObserver.set(event -> {
			liveData.removeObserver(refObserver.get());
			observer.onChanged(event);
		});
		liveData.observe(owner, refObserver.get());
	}

	private interface EventReady {
		void ready(int index);
	}

	@MainThread
	private static <T> void observeOnceUndef(
		@NonNull LifecycleOwner owner,
		@NonNull LiveData<T> liveData,
		int index,
		@NonNull EventReady ready
	) {
		ObjectReference<Observer<T>> refObserver = new ObjectReference<>();
		refObserver.set(event -> {
			liveData.removeObserver(refObserver.get());
			ready.ready(index);
		});
		liveData.observe(owner, refObserver.get());
	}

	@MainThread
	public static <T> void awaitAllEvents(
		@Nullable Executor executor,
		@NonNull LifecycleOwner owner,
		@NonNull List<LiveData<T>> listLiveData,
		@NonNull Callable<Void> callable
	) {
		int n = listLiveData.size();
		ConditionCall cond = new ConditionCall(executor, n, callable, null);
		for (int i = 0; i < n; i++) {
			observeOnceUndef(owner, listLiveData.get(i), i, index -> {
				cond.setCondition(index, true);
			});
		}
	}

}
