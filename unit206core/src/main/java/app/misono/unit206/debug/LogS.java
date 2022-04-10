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

package app.misono.unit206.debug;

import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.misc.Utils;
import app.misono.unit206.task.SingletonTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class LogS {
	private static final boolean DEBUG = true; // TODO: BuildConfig
	private static SimpleDateFormat form = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);
	private static ExecutorService executor;
	private static SingletonTask thread;
	private static File file;

	static void e(@NonNull String tag, @NonNull String msg) {
		if (DEBUG) {
			log(tag, msg);
		}
	}

	private static synchronized void log(@NonNull String tag, @NonNull String msg) {
		if (DEBUG) {
			if (executor == null) {
				executor = Executors.newCachedThreadPool();
				thread = new SingletonTask();
				Calendar cal = Calendar.getInstance();
				String fname = String.format(Locale.US, "%04d%02d%02d-%02d%02d%02d.txt", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
				String dir;
				if (19 <= Build.VERSION.SDK_INT) {
					dir = Environment.DIRECTORY_DOCUMENTS;
				} else {
					dir = Environment.DIRECTORY_DOWNLOADS;
				}
				file = new File(Environment.getExternalStoragePublicDirectory(dir), fname);
			}

			String s = form.format(new Date()) + " : " + tag + ":" + msg + "\n";
			thread.call(executor, () -> {
				FileOutputStream os = null;
				try {
					os = new FileOutputStream(file, true);
					os.write(s.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					Utils.closeSafely(os);
				}
				return null;
			});
		}
	}

	public static void printStackTrace(@Nullable Throwable e) {
		if (DEBUG && e != null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(os);
			e.printStackTrace(writer);
			writer.close();
			log("", new String(os.toByteArray()));
		}
	}

}
