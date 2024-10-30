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

import androidx.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class ConditionCall {
	private final SingletonTask mTask;
	private final Callable<Void> mCallTrue;
	private final Callable<Void> mCallFalse;
	private final Executor mExecutor;
	private final boolean[] mCondition;

	private boolean mMatch;

	public ConditionCall(
		int nCondition,
		@Nullable Callable<Void> callTrue,
		@Nullable Callable<Void> callFalse
	) {
		this(null, nCondition, callTrue, callFalse);
	}

	public ConditionCall(
		@Nullable Executor executor,
		int nCondition,
		@Nullable Callable<Void> callTrue,
		@Nullable Callable<Void> callFalse
	) {
		mExecutor = executor;
		mCallTrue = callTrue;
		mCallFalse = callFalse;
		mTask = new SingletonTask();
		mCondition = new boolean[nCondition];
	}

	public void setCondition(int index, boolean b) {
		mTask.call(Taskz.getExecutor(), () -> {
			mCondition[index] = b;
			if (b) {
				check();
			}
			return null;
		}).addOnFailureListener(Taskz::printStackTrace2);
	}

	private void check() {
		boolean b = true;
		for (boolean bx : mCondition) {
			if (!bx) {
				b = false;
				break;
			}
		}
		if (!mMatch && b) {
			if (mCallTrue != null) {
				if (mExecutor == null) {
					Taskz.call(mCallTrue).addOnFailureListener(Taskz::printStackTrace2);
				} else {
					Taskz.call(mExecutor, mCallTrue).addOnFailureListener(Taskz::printStackTrace2);
				}
			}
		} else if (mMatch && !b) {
			if (mCallFalse != null) {
				if (mExecutor == null) {
					Taskz.call(mCallFalse).addOnFailureListener(Taskz::printStackTrace2);
				} else {
					Taskz.call(mExecutor, mCallFalse).addOnFailureListener(Taskz::printStackTrace2);
				}
			}
		}
		mMatch = b;
	}

}
