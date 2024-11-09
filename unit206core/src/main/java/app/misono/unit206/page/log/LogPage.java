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

package app.misono.unit206.page.log;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.widget.FrameLayout;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import app.misono.unit206.R;
import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.Utils;
import app.misono.unit206.page.AbstractPage;
import app.misono.unit206.page.PageActivity;
import app.misono.unit206.task.SingletonTask;
import app.misono.unit206.task.Taskz;
import app.misono.unit206.theme.AppStyle;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogPage extends AbstractPage {
	private static final String TAG = "LogPage";
	private static final String FNAME = TAG + ".txt";
	private static final int STATE_IDLE = 0;
	private static final int STATE_WRITE_LOG = 1;

	private static final SimpleDateFormat form = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);
	private static final SingletonTask task1 = new SingletonTask();

	// task1 instance
	private static final StringBuilder log = new StringBuilder();

	private static File fileLog;

	private final LogLayout layout;
	private final LogView view;
	private final LogPref pref;
	private final int codeSaf;

	public LogPage(
		@NonNull PageActivity activity,
		int codeSaf,
		@Nullable Runnable clickBack
	) {
		super(activity, clickBack);
		this.codeSaf = codeSaf;
		layout = new LogLayout();
		view = new LogView(activity);
		fileLog = new File(activity.getCacheDir(), FNAME);

		pref = new LogPref(activity, this);
		setPref(pref);

		setOptionsMenu(R.menu.menu_log);
		addMenuCallback(R.id.log_action_clear_log, this::actionClearData);
		addMenuCallback(R.id.log_action_save_log, this::actionSaveLog);

		mBase.addView(
			view,
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.MATCH_PARENT
		);
		AppStyle style = activity.getAppStyle();
		if (style != null) {
			mBase.setBackgroundColor(style.getWhiteColor());
		} else {
			mBase.setBackgroundColor(Color.WHITE);
		}

		task1.call(Taskz.getExecutor(), () -> {
			String s = Utils.readStringWithException(fileLog);
			log.append(s);
			return null;
		}).addOnFailureListener(Taskz::printStackTrace2);

		setActivityResultCallback();
	}

	private void setActivityResultCallback() {
		int state = pref.getState();
		log("setActivityResultCallback:" + state);
		switch (state) {
		case STATE_WRITE_LOG:
			activity.setSafCallback(this::callbackWriteLog);
			break;
		}
	}

	@RequiresApi(19)
	private void saveLogWithSaf() {
		int state = STATE_WRITE_LOG;
		pref.setState(state);
		setActivityResultCallback();
		String fname = "log-" + Utils.getPresentTimeString() + ".txt";
		activity.saveWithSaf(fname, "text/plain", codeSaf + state);
	}

	@MainThread
	private void callbackWriteLog(@NonNull Uri uri) {
		log("callbackWriteResult:");
		Snackbar snack = showSnackProgress(R.string.log_saving);
		Taskz.call(Taskz.getExecutor(), () -> {
			Utils.writeToUri(activity, fileLog, uri);
			return null;
		}).addOnCompleteListener(task -> {
			snack.dismiss();
			Taskz.printStackTrace2(task.getException());
			pref.setState(STATE_IDLE);
		});
	}

	private void actionClearData() {
		clearLog();
		onBackPressed();
	}

	private void actionSaveLog() {
		if (19 <= Build.VERSION.SDK_INT) {
			saveLogWithSaf();
		}
	}

	private void clearLog() {
		task1.call(Taskz.getExecutor(), () -> {
			log.setLength(0);
			fileLog.delete();
			return null;
		}).addOnFailureListener(Taskz::printStackTrace2);
	}

	@Override
	public void onResume() {
		super.onResume();
		task1.call(Taskz.getExecutor(), () -> {
			String s = log.toString();
			Taskz.call(() -> {
				view.setLogText(s);
				return null;
			}).addOnFailureListener(Taskz::printStackTrace2);
			return null;
		}).addOnFailureListener(Taskz::printStackTrace2);
	}

	@NonNull
	public Task<String> getLogString() {
		return task1.call(Taskz.getExecutor(), log::toString);
	}

	@Nullable
	@Override
	public String refreshPage() {
		activity.invalidateOptionsMenu();
		return activity.getString(R.string.log_title);
	}

	@NonNull
	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public void changeLayout(int wAdBase, int hAdBase) {
		super.changeLayout(wAdBase, hAdBase);
		log("changeLayout:" + wAdBase + " " + hAdBase + " " + mPortrait);
		layout.setPixelSize(wAdBase, hAdBase);
		layout.layout(view);
	}

	private static void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

	public static void e(@NonNull String tag, @NonNull String msg) {
		e(tag + ":" + msg);
	}

	public static void e(@NonNull String msg) {
		if (fileLog != null) {
			task1.call(Taskz.getExecutor(), () -> {
				log(msg);
				String s = form.format(new Date()) + " : " + msg + "\n";
				log.append(s);
				try (
					FileOutputStream os = new FileOutputStream(fileLog, true);
				) {
					os.write(s.getBytes());
				}
				return null;
			}).addOnFailureListener(Taskz::printStackTrace2);
		} else {
			log(msg);
		}
	}

	public static void lf() {
		if (fileLog != null) {
			task1.call(Taskz.getExecutor(), () -> {
				String s = "\n";
				log.append(s);
				try (
					FileOutputStream os = new FileOutputStream(fileLog, true);
				) {
					os.write(s.getBytes());
				}
				return null;
			}).addOnFailureListener(Taskz::printStackTrace2);
		}
	}

}
