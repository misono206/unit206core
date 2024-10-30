/*
 * Copyright 2022 Atelier Misono, Inc. @ https://misono.app/
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

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import app.misono.unit206.callback.CallbackIntent;
import app.misono.unit206.callback.CallbackUri;
import app.misono.unit206.callback.CallbackWorker;
import app.misono.unit206.callback.CallbackWorkerIntent;
import app.misono.unit206.callback.CallbackWorkerUri;
import app.misono.unit206.debug.Log2;
import app.misono.unit206.page.PageActivity;
import app.misono.unit206.task.Taskz;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;

public class Saf {
	private static final String TAG = "Saf";

	private final SparseArray<CallbackIntent> cbIntents;
	private final SparseArray<CallbackUri> cbUris;
	private final PageActivity activity;
	private final SafPref pref;
	private final int codeBaseSaf;
	private final int stateIdle;

	public interface LoadCallback {
		@WorkerThread
		void loaded(@Nullable String fname, @Nullable byte[] b) throws Exception;
	}

	public interface TaskCallback {
		@MainThread
		void completed(@NonNull Task<?> task);
	}

	public Saf(
		@NonNull PageActivity activity,
		@NonNull SafPref pref,
		int codeBaseSaf,
		int stateIdle
	) {
		this.activity = activity;
		this.pref = pref;
		this.codeBaseSaf = codeBaseSaf;
		this.stateIdle = stateIdle;
		cbIntents = new SparseArray<>();
		cbUris = new SparseArray<>();
	}

	public void addUriCallback(
		int state,
		@NonNull CallbackWorkerUri callback,
		@Nullable Runnable prepare,
		@Nullable TaskCallback done
	) {
		cbUris.append(state, uri -> {
			if (prepare != null) {
				prepare.run();
			}
			Taskz.call(Taskz.getExecutor(), () -> {
				callback.callback(uri);
				return null;
			}).addOnCompleteListener(task -> {
				pref.setSafState(stateIdle);
				if (done != null) {
					done.completed(task);
				} else {
					Taskz.printStackTrace2(task.getException());
				}
			});
		});
	}

	@Deprecated
	public interface SafCallback {
		@NonNull
		File toFile(@Nullable String fname);
	}

	@Deprecated		// use addFileCallback()
	public void addUriToFileCallback(
		@NonNull Context context,
		int state,
		@NonNull SafCallback toFile,
		@Nullable Runnable prepare,
		@Nullable TaskCallback done
	) {
		cbUris.append(state, uri -> {
			if (prepare != null) {
				prepare.run();
			}
			Taskz.call(Taskz.getExecutor(), () -> {
				File file = toFile.toFile(Utils.getDisplayName(activity, uri));
				Utils.copyUriToFile(context, uri, file);
				return null;
			}).addOnCompleteListener(task -> {
				pref.setSafState(stateIdle);
				if (done != null) {
					done.completed(task);
				} else {
					Taskz.printStackTrace2(task.getException());
				}
			});
		});
	}

	public void addFileCallback(
		@NonNull Context context,
		int state,
		@NonNull File file,
		@Nullable Runnable prepare,
		@NonNull OnCompleteListener<String> done
	) {
		cbUris.append(state, uri -> {
			if (prepare != null) {
				prepare.run();
			}
			Taskz.call(Taskz.getExecutor(), () -> {
				String fname = Utils.getDisplayName(activity, uri);
				Utils.copyUriToFile(context, uri, file);
				return fname;
			}).addOnCompleteListener(task -> {
				pref.setSafState(stateIdle);
				done.onComplete(task);
			});
		});
	}

	public void addIntentCallback(
		int state,
		@NonNull CallbackWorkerIntent callback,
		@Nullable Runnable prepare,
		@Nullable TaskCallback done
	) {
		cbIntents.append(state, intent -> {
			if (prepare != null) {
				prepare.run();
			}
			Taskz.call(Taskz.getExecutor(), () -> {
				callback.callback(intent);
				return null;
			}).addOnCompleteListener(task -> {
				pref.setSafState(stateIdle);
				if (done != null) {
					done.completed(task);
				} else {
					Taskz.printStackTrace2(task.getException());
				}
			});
		});
	}

	public void addUriSimpleCallback(
		int state,
		@NonNull CallbackUri callback
	) {
		cbUris.append(state, callback);
	}

	public void addLoadCallback(
		int state,
		@NonNull LoadCallback callback,
		@Nullable Runnable prepare,
		@Nullable TaskCallback done
	) {
		cbUris.append(state, uri -> {
			if (prepare != null) {
				prepare.run();
			}
			Taskz.call(Taskz.getExecutor(), () -> {
				String fname = Utils.getDisplayName(activity, uri);
				byte[] b = Utils.readBytesWithException(activity, uri);
				callback.loaded(fname, b);
				return null;
			}).addOnCompleteListener(task -> {
				pref.setSafState(stateIdle);
				if (done != null) {
					done.completed(task);
				} else {
					Taskz.printStackTrace2(task.getException());
				}
			});
		});
	}

	public void addSaveCallback(
		int state,
		@NonNull File file,
		@Nullable Runnable prepare,
		@Nullable TaskCallback done
	) {
		cbUris.append(state, uri -> {
			if (prepare != null) {
				prepare.run();
			}
			Taskz.call(Taskz.getExecutor(), () -> {
				Utils.writeToUri(activity, file, uri);
				return null;
			}).addOnCompleteListener(task -> {
				pref.setSafState(stateIdle);
				if (done != null) {
					done.completed(task);
				} else {
					Taskz.printStackTrace2(task.getException());
				}
			});
		});
	}

	@Deprecated		// use load()
	public void loadWithSaf(
		@NonNull String typeMime,
		int state
	) {
		pref.setSafState(state);
		setCallback();
		activity.loadWithSaf(typeMime, codeBaseSaf + state);
	}

	public void load(
		@NonNull String typeMime,
		int state
	) {
		pref.setSafState(state);
		setCallback();
		activity.loadWithSaf(typeMime, codeBaseSaf + state);
	}

	@Deprecated		// use save()
	@NonNull
	public Task<Void> saveWithSaf(
		@NonNull String fname,
		@NonNull String typeMime,
		int state,
		@NonNull CallbackWorker worker
	) {
		Task<Void> task = Taskz.call(Taskz.getExecutor(), () -> {
			worker.callback();
			return null;
		});
		return task.addOnSuccessListener(result -> {
			save(fname, typeMime, state);
		});
	}

	@NonNull
	public Task<Void> save(
		@NonNull String fname,
		@NonNull String typeMime,
		int state,
		@NonNull CallbackWorker worker
	) {
		Task<Void> task = Taskz.call(Taskz.getExecutor(), () -> {
			worker.callback();
			return null;
		});
		return task.addOnSuccessListener(result -> {
			save(fname, typeMime, state);
		});
	}

	@Deprecated		// use save()
	@MainThread
	public void saveWithSaf(
		@NonNull String fname,
		@NonNull String typeMime,
		int state
	) {
		pref.setSafState(state);
		setCallback();
		activity.saveWithSaf(fname, typeMime, codeBaseSaf + state);
	}

	@MainThread
	public void save(
		@NonNull String fname,
		@NonNull String typeMime,
		int state
	) {
		pref.setSafState(state);
		setCallback();
		activity.saveWithSaf(fname, typeMime, codeBaseSaf + state);
	}

	@MainThread
	public void startActivityForResult(@NonNull Intent intent, int state) {
		pref.setSafState(state);
		setCallback();
		activity.startActivityForResult(intent, codeBaseSaf + state);
	}

	@Deprecated		// use setCallback()
	public void setSafCallback() {
		int state = pref.getSafState();
		log("setSafCallback:" + state);
		CallbackUri cb = cbUris.get(state);
		if (cb != null) {
			activity.setSafCallback(cb);
		} else {
			CallbackIntent cb2 = cbIntents.get(state);
			if (cb2 != null) {
				activity.setSafCallback(cb2);
			}
		}
	}

	public void setCallback() {
		int state = pref.getSafState();
		log("setCallback:" + state);
		CallbackUri cb = cbUris.get(state);
		if (cb != null) {
			activity.setSafCallback(cb);
		} else {
			CallbackIntent cb2 = cbIntents.get(state);
			if (cb2 != null) {
				activity.setSafCallback(cb2);
			}
		}
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}

