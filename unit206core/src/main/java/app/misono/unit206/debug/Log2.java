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

package app.misono.unit206.debug;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public final class Log2 {
	private static final boolean LOG_SAVE = false;
	private static final boolean DEBUG = BuildConfig.DEBUG;

	public static void e(@NonNull String tag, @NonNull String log) {
		if (DEBUG) {
			Log.e(tag, log);
			if (LOG_SAVE) {
				LogS.e(tag, log);
			}
		}
	}

	public static void printStackTrace(@Nullable Throwable e) {
		if (DEBUG && e != null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(os);
			e.printStackTrace(writer);
			writer.close();
			Log.e("", os.toString());
			if (LOG_SAVE) {
				LogS.printStackTrace(e);
			}
		}
	}

	public static void printStackTrace2(@Nullable Throwable e) {
		if (e != null && !(e instanceof InterruptedException)) {
			printStackTrace(e);
		}
	}

	public static void e(@NonNull String tag, @NonNull String log, @NonNull Throwable e) {
		if (DEBUG) {
			Log.e(tag, log);
			printStackTrace2(e);
		}
	}

}
