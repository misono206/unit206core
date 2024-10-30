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

package app.misono.unit206.debug;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class LogM {
	private static final boolean DEBUG = BuildConfig.DEBUG;
	private static final SimpleDateFormat form = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

	private static StringBuilder sb;

	public static void e(@NonNull String tag, @NonNull String msg) {
		if (DEBUG) {
			log(tag, msg);
		}
	}

	private static synchronized void log(@NonNull String tag, @NonNull String msg) {
		if (DEBUG) {
			if (sb == null) {
				sb = new StringBuilder();
			}
			sb.append(form.format(new Date()));
			sb.append(" : ");
			sb.append(tag);
			sb.append(": ");
			sb.append(msg);
			sb.append("\n");
		}
	}

	@Nullable
	public static synchronized StringBuilder getString() {
		StringBuilder rc = sb;
		sb = null;
		return rc;
	}

	public static synchronized void printStackTrace(@Nullable Throwable e) {
		if (DEBUG && e != null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(os);
			e.printStackTrace(writer);
			writer.close();
			log("", os.toString());
		}
	}

}
