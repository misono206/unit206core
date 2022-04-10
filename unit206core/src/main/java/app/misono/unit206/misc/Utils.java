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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.LocaleList;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import app.misono.unit206.callback.CallbackUri;
import app.misono.unit206.debug.Log2;
import app.misono.unit206.page.PageActivity;
import app.misono.unit206.task.Taskz;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class Utils {
	private static final String TAG = "Utils";
	private static final TimeZone JST = TimeZone.getTimeZone("Asia/Tokyo");

	@NonNull
	public static String hexString16(long a) {
		return String.format(Locale.US, "%016x", a);
	}

	@NonNull
	public static String hexString8(int a) {
		return String.format(Locale.US, "%08x", a);
	}

	@NonNull
	public static String hexString6(int a) {
		return String.format(Locale.US, "%06x", a & 0xffffff);
	}

	public static long getAssetFileSize(@NonNull Context context, @NonNull String assetPath) {
		AssetFileDescriptor fd = null;
		long len;
		try {
			fd = context.getAssets().openFd(assetPath);
			len = fd.getLength();
			fd.close();
			fd = null;
		} catch (IOException e) {
			len = AssetFileDescriptor.UNKNOWN_LENGTH;
			e.printStackTrace();
		} finally {
			if (fd != null) {
				try {
					fd.close();
				} catch (IOException e) {
					// nop
				}
			}
		}
		return len;
	}

	@Nullable
	public static byte[] readAssetFile(@NonNull Context context, @NonNull String assetPath) {
		byte[] rc = null;
		try {
			rc = readAssetFileWithException(context, assetPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rc;
	}

	@WorkerThread
	public static void copyAssetFileToFile(
		@NonNull Context context,
		@NonNull String assetPath,
		@NonNull File dst
	) throws IOException {
		BufferedInputStream is = null;
		BufferedOutputStream os = null;
		try {
			is = new BufferedInputStream(context.getAssets().open(assetPath));
			os = new BufferedOutputStream(new FileOutputStream(dst));
			for ( ; ; ) {
				byte[] buf = new byte[4096];
				int av = is.available();
				if (av == 0) {
					av = 4096;
				}
				if (buf.length < av) {
					buf = new byte[av];
				}
				int len = is.read(buf);
				if (len < 0) {
					break;
				}
				os.write(buf, 0, len);
			}
			is.close();
			is = null;
			os.close();
			os = null;
		} finally {
			closeSafely(is);
			closeSafely(os);
		}
	}

	@NonNull
	public static byte[] readAssetFileWithException(
		@NonNull Context context,
		@NonNull String assetPath
	) throws IOException {
		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(context.getAssets().open(assetPath));
			ByteArrayBuffer ba = new ByteArrayBuffer();
			for ( ; ; ) {
				byte[] buf;
				int av = is.available();
				if (av == 0) {
					av = 4096;
				}
				buf = new byte[av];
				int len = is.read(buf);
				if (len < 0) {
					break;
				}
				ba.add(buf, 0, len);
			}
			is.close();
			is = null;
			return ba.getByteArray();
		} finally {
			closeSafely(is);
		}
	}

	@NonNull
	public static String readAssetFileStringWithException(
		@NonNull Context context,
		@NonNull String assetPath
	) throws IOException {
		return new String(readAssetFileWithException(context, assetPath));
	}

	@Nullable
	public static String readAssetFileString(@NonNull Context context, @NonNull String assetPath) {
		String rc = null;
		byte[] b = readAssetFile(context, assetPath);
		if (b != null) {
			rc = new String(b);
		}
		return rc;
	}

	public static void closeSafely(@Nullable Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// nop
			}
		}
	}

	@NonNull
	public static List<Locale> getDefaultLocaleList() {
		List<Locale> rc = new ArrayList<>();
		if (24 <= Build.VERSION.SDK_INT) {
			LocaleList list = LocaleList.getDefault();
			int n = list.size();
			for (int i = 0; i < n; i++) {
				rc.add(list.get(i));
			}
		} else {
			rc.add(Locale.getDefault());
		}
		return rc;
	}

	@NonNull
	public static List<String> getDefaultLanguage3List() {
		List<String> rc = new ArrayList<>();
		if (24 <= Build.VERSION.SDK_INT) {
			LocaleList list = LocaleList.getDefault();
			int n = list.size();
			for (int i = 0; i < n; i++) {
				rc.add(list.get(i).getISO3Language());
			}
		} else {
			rc.add(Locale.getDefault().getISO3Language());
		}
		return rc;
	}

	@WorkerThread
	@Nullable
	public static String readString(@NonNull Context context, @NonNull Uri in) {
		String rc = null;
		byte[] b = readBytes(context, in);
		if (b != null) {
			rc = new String(b);
		}
		return rc;
	}

	@WorkerThread
	@NonNull
	public static String readStringWithException(
		@NonNull Context context,
		@NonNull Uri in
	) throws IOException {
		byte[] b = readBytesWithException(context, in);
		return new String(b);
	}

	@WorkerThread
	@NonNull
	public static String readStringWithException(@NonNull File file) throws IOException {
		byte[] b = readBytesWithException(file);
		return new String(b);
	}

	@WorkerThread
	@Nullable
	public static byte[] readBytes(@NonNull Context context, @NonNull Uri in) {
		byte[] rc = null;
		InputStream is = null;
		try {
			is = context.getContentResolver().openInputStream(in);
			if (is != null) {
				ByteArrayBuffer ba = new ByteArrayBuffer();
				is = new BufferedInputStream(is);
				for ( ; ; ) {
					byte[] buf;
					int av = is.available();
					if (av == 0) {
						av = 4096;
					}
					buf = new byte[av];
					int len = is.read(buf);
					if (len < 0) {
						break;
					}
					ba.add(buf, 0, len);
				}
				rc = ba.getByteArray();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeSafely(is);
		}
		return rc;
	}

	@WorkerThread
	@Nullable
	public static byte[] readBytesWithException(
		@NonNull Context context,
		@NonNull Uri in
	) throws IOException {
		InputStream is = null;
		try {
			byte[] rc = null;
			is = context.getContentResolver().openInputStream(in);
			if (is != null) {
				ByteArrayBuffer ba = new ByteArrayBuffer();
				is = new BufferedInputStream(is);
				for ( ; ; ) {
					byte[] buf;
					int av = is.available();
					if (av == 0) {
						av = 4096;
					}
					buf = new byte[av];
					int len = is.read(buf);
					if (len < 0) {
						break;
					}
					ba.add(buf, 0, len);
				}
				rc = ba.getByteArray();
			}
			return rc;
		} finally {
			closeSafely(is);
		}
	}

	@WorkerThread
	public static void readFromUri(
		@NonNull Context context,
		@NonNull Uri in,
		@NonNull File out
	) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			os = new FileOutputStream(out);
			os = new BufferedOutputStream(os);
			is = context.getContentResolver().openInputStream(in);
			if (is != null) {
				is = new BufferedInputStream(is);
				for ( ; ; ) {
					int av = is.available();
					if (av == 0) {
						av = 65536;
					}
					byte[] buf = new byte[av];
					int len = is.read(buf);
					if (len < 0) {
						break;
					}
					os.write(buf, 0, len);
				}
			}
		} finally {
			closeSafely(os);
			closeSafely(is);
		}
	}

	@AnyThread
	@NonNull
	public static Task<String> readStringTask(
		@NonNull Executor executor,
		@NonNull Context context,
		@NonNull Uri in
	) {
		return Taskz.call(executor, () -> {
			return readString(context, in);
		});
	}

	@AnyThread
	@NonNull
	public static Task<byte[]> readBytesTask(
		@NonNull Executor executor,
		@NonNull Context context,
		@NonNull Uri in
	) {
		return Taskz.call(executor, () -> {
			return readBytes(context, in);
		});
	}

	@Nullable
	public static byte[] readBytes(@NonNull InputStream is) {
		byte[] rc = null;
		try {
			ByteArrayBuffer ba = new ByteArrayBuffer();
			is = new BufferedInputStream(is);
			for ( ; ; ) {
				byte[] buf;
				int av = is.available();
				if (av == 0) {
					av = 4096;
				}
				buf = new byte[av];
				int len = is.read(buf);
				if (len < 0) {
					break;
				}
				ba.add(buf, 0, len);
			}
			rc = ba.getByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeSafely(is);
		}
		return rc;
	}

	@WorkerThread
	public static boolean writeStringToUri(
		@NonNull Context context,
		@NonNull String s,
		@NonNull Uri out
	) {
		return writeToUri(context, s.getBytes(), out);
	}

	@WorkerThread
	public static boolean writeToUri(@NonNull Context context, @NonNull byte[] b, @NonNull Uri out) {
		boolean success = false;
		try {
			writeToUriWithException(context, b, out);
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	@WorkerThread
	public static void writeStringToUriWithException(
		@NonNull Context context,
		@NonNull String s,
		@NonNull Uri out
	) throws IOException {
		writeToUriWithException(context, s.getBytes(), out);
	}

	@WorkerThread
	public static void writeToUriWithException(
		@NonNull Context context,
		@NonNull byte[] b,
		@NonNull Uri out
	) throws IOException {
		BufferedOutputStream os = null;
		try {
			OutputStream outstream = context.getContentResolver().openOutputStream(out);
			if (outstream != null) {
				os = new BufferedOutputStream(outstream);
				os.write(b, 0, b.length);
			} else {
				throw new RuntimeException("openOutputStream fail...");
			}
		} finally {
			closeSafely(os);
		}
	}

	@WorkerThread
	public static void writeToUri(
		@NonNull Context context,
		@NonNull File src,
		@NonNull Uri out
	) throws IOException {
		BufferedOutputStream os = null;
		InputStream is = null;
		try {
			OutputStream outstream = context.getContentResolver().openOutputStream(out);
			if (outstream != null) {
				os = new BufferedOutputStream(outstream);
				is = new FileInputStream(src);
				is = new BufferedInputStream(is);
				for ( ; ; ) {
					byte[] buf;
					int av = is.available();
					if (av == 0) {
						av = 65536;
					}
					buf = new byte[av];
					int len = is.read(buf);
					if (len < 0) {
						break;
					}
					os.write(buf, 0, buf.length);
				}
			} else {
				throw new RuntimeException("openOutputStream fail...");
			}
		} finally {
			closeSafely(is);
			closeSafely(os);
		}
	}

	@AnyThread
	@NonNull
	public static Task<Void> writeBytesTask(
		@NonNull Executor executor,
		@NonNull Context context,
		@NonNull byte[] b,
		@NonNull Uri out
	) {
		return Taskz.call(executor, () -> {
			writeToUriWithException(context, b, out);
			return null;
		});
	}

	@Nullable
	public static File writeToCache(
		@NonNull Context context,
		@NonNull String fname,
		@NonNull byte[] data
	) {
		File rc = null;
		BufferedOutputStream os = null;
		try {
			File file = new File(context.getCacheDir(), fname);
			os = new BufferedOutputStream(new FileOutputStream(file));
			os.write(data, 0, data.length);
			os.close();
			os = null;
			rc = file;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeSafely(os);
		}
		return rc;
	}

	public static void writeToFileWithException(
		@NonNull File file,
		@NonNull byte[] data
	) throws IOException {
		BufferedOutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(file));
			os.write(data, 0, data.length);
			os.close();
			os = null;
		} finally {
			closeSafely(os);
		}
	}

	@NonNull
	public static byte[] readBytesWithException(@NonNull File in) throws IOException {
		InputStream is = null;
		try {
			is = new FileInputStream(in);
			ByteArrayBuffer ba = new ByteArrayBuffer();
			is = new BufferedInputStream(is);
			for ( ; ; ) {
				byte[] buf;
				int av = is.available();
				if (av == 0) {
					av = 4096;
				}
				buf = new byte[av];
				int len = is.read(buf);
				if (len < 0) {
					break;
				}
				ba.add(buf, 0, len);
			}
			return ba.getByteArray();
		} finally {
			closeSafely(is);
		}
	}

	@Nullable
	public static byte[] readBytes(@NonNull File in) {
		InputStream is = null;
		try {
			is = new FileInputStream(in);
			byte[] rc = readBytes(is);
			is = null;
			return rc;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeSafely(is);
		}
		return null;
	}

	@Nullable
	public static byte[] readFromCache(@NonNull Context context, @NonNull String fname) {
		File file = new File(context.getCacheDir(), fname);
		byte[] rc = null;
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			rc = readBytes(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeSafely(is);
		}
		return rc;
	}

	public static boolean copyDataToUri(@NonNull Context context, @NonNull byte[] data, @NonNull Uri out) {
		boolean success = false;
		BufferedOutputStream os = null;
		try {
			OutputStream outstream = context.getContentResolver().openOutputStream(out);
			if (outstream != null) {
				os = new BufferedOutputStream(outstream);
				os.write(data);
				os.close();
				os = null;
				success = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeSafely(os);
		}
		return success;
	}

	public static boolean copyFileToUri(
		@NonNull Context context,
		@NonNull File in,
		@NonNull Uri out
	) {
		boolean success = false;
		OutputStream os = null;
		InputStream is = null;
		try {
			is = new FileInputStream(in);
			is = new BufferedInputStream(is);
			os = context.getContentResolver().openOutputStream(out);
			os = new BufferedOutputStream(os);
			byte[] buf = new byte[4096];
			for ( ; ; ) {
				int len = is.read(buf);
				if (len < 0) {
					break;
				}
				os.write(buf, 0, len);
			}
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeSafely(is);
			closeSafely(os);
		}
		return success;
	}

	public static boolean copyUriToFileNoException(
		@NonNull Context context,
		@NonNull Uri in,
		@NonNull File out
	) {
		boolean success = false;
		OutputStream os = null;
		InputStream is = null;
		try {
			is = context.getContentResolver().openInputStream(in);
			is = new BufferedInputStream(is);
			os = new FileOutputStream(out);
			os = new BufferedOutputStream(os);
			byte[] buf = new byte[4096];
			for ( ; ; ) {
				int len = is.read(buf);
				if (len < 0) {
					break;
				}
				os.write(buf, 0, len);
			}
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeSafely(is);
			closeSafely(os);
		}
		return success;
	}

	public static void copyUriToFile(
		@NonNull Context context,
		@NonNull Uri in,
		@NonNull File out
	) throws Exception {
		OutputStream os = null;
		InputStream is = null;
		try {
			is = context.getContentResolver().openInputStream(in);
			is = new BufferedInputStream(is);
			os = new FileOutputStream(out);
			os = new BufferedOutputStream(os);
			byte[] buf = new byte[4096];
			for ( ; ; ) {
				int len = is.read(buf);
				if (len < 0) {
					break;
				}
				os.write(buf, 0, len);
			}
		} finally {
			closeSafely(is);
			closeSafely(os);
		}
	}

	@WorkerThread
	public static void copyFileToFile(
		@NonNull File in,
		@NonNull File out
	) throws Exception {
		FileOutputStream os = null;
		FileInputStream is = null;
		try {
			is = new FileInputStream(in);
			os = new FileOutputStream(out);
			FileChannel inChannel = is.getChannel();
			FileChannel outChannel = os.getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			closeSafely(is);
			closeSafely(os);
		}
	}

	/**
	 *	delete a cache file.
	 *	@return true if success.
	 */
	public static boolean deleteCache(@NonNull Context context, @NonNull String fname) {
		File file = new File(context.getCacheDir(), fname);
		return file.delete();
	}

	public static void deleteCaches(@NonNull Context context, @Nullable FilenameFilter filter) {
		File dir = context.getCacheDir();
		File[] files = dir.listFiles(filter);
		if (files != null) {
			for (File f : files) {
				f.delete();
			}
		}
	}

	/**
	 * delete dirctory or file.
	 */
	public static void deleteFile(@NonNull File file) {
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					deleteFile(f);
				}
			}
			file.delete();
		}
	}

	public static int intValue(@NonNull String s, int defaultValue) {
		int rc = defaultValue;
		try {
			rc = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return rc;
	}

	@NonNull
	public static String getTimeString(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return getTimeString(cal);
	}

	@NonNull
	public static String getTimeString(@NonNull TimeZone zone, long time) {
		Calendar cal = Calendar.getInstance(zone);
		cal.setTimeInMillis(time);
		return getTimeString(cal);
	}

	@NonNull
	public static TimeZone getTimeZoneJST() {
		return JST;
	}

	@NonNull
	public static String getTimeString(@NonNull Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hh = cal.get(Calendar.HOUR_OF_DAY);
		int mm = cal.get(Calendar.MINUTE);
		int ss = cal.get(Calendar.SECOND);
		return String.format(Locale.US, "%04d%02d%02d-%02d%02d%02d", year, month, day, hh, mm, ss);
	}

	@NonNull
	public static String getPresentTimeString() {
		return getTimeString(Calendar.getInstance());
	}

	@NonNull
	public static String getPresentTimeString(@NonNull TimeZone zone) {
		return getTimeString(Calendar.getInstance(zone));
	}

	@NonNull
	public static String getDateString(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return getDateString(cal);
	}

	@NonNull
	public static String getDateString(@NonNull TimeZone zone, long time) {
		Calendar cal = Calendar.getInstance(zone);
		cal.setTimeInMillis(time);
		return getDateString(cal);
	}

	@NonNull
	public static String getDateString(@NonNull Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return String.format(Locale.US, "%04d%02d%02d", year, month, day);
	}

	@NonNull
	public static String getPresentDateString() {
		return getDateString(Calendar.getInstance());
	}

	@NonNull
	public static String getPresentDateString(@NonNull TimeZone zone) {
		return getDateString(Calendar.getInstance(zone));
	}

	public static int getDayOfWeek(@NonNull String date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
		cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6, 8)));
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public static int getDayOfWeek(@NonNull TimeZone zone, @NonNull String date) {
		Calendar cal = Calendar.getInstance(zone);
		cal.setTimeInMillis(0);
		cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
		cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6, 8)));
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * save PDF with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	@RequiresApi(19)
	public static boolean savePdfWithSaf(
		@NonNull PageActivity activity,
		@NonNull String fileName,
		int code,
		@NonNull CallbackUri callback
	) {
		return saveWithSaf(activity, fileName, "application/pdf", code, callback);
	}

	/**
	 * save HTML with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	@RequiresApi(19)
	public static boolean saveHtmlWithSaf(
		@NonNull PageActivity activity,
		@NonNull String fileName,
		int code,
		@NonNull CallbackUri callback
	) {
		return saveWithSaf(activity, fileName, "text/html", code, callback);
	}

	@AnyThread
	@RequiresApi(19)
	public static boolean saveStringWithSaf(
		@NonNull PageActivity activity,
		@NonNull String fileName,
		int code,
		@NonNull CallbackUri callback
	) {
		return saveWithSaf(activity, fileName, "text/plain", code, callback);
	}

	/**
	 * save JSON with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	@RequiresApi(19)
	public static boolean saveJsonWithSaf(
		@NonNull PageActivity activity,
		@NonNull String fileName,
		int code,
		@NonNull CallbackUri callback
	) {
		return saveWithSaf(activity, fileName, "application/json", code, callback);
	}

	/**
	 * save with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	@RequiresApi(19)
	public static boolean saveWithSaf(
		@NonNull PageActivity activity,
		@NonNull String fileName,
		@NonNull String typeMime,
		int code,
		@NonNull PageActivity.CallbackActivityResult callback
	) {
		boolean rc;
		Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setType(typeMime);
		intent.putExtra(Intent.EXTRA_TITLE, fileName);
		try {
			activity.addOnActivityResult(code, callback);
			activity.startActivityForResult(intent, code);
			rc = true;
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			rc = false;
		}
		return rc;
	}

	/**
	 * save with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	@RequiresApi(19)
	public static boolean saveWithSaf(
		@NonNull PageActivity activity,
		@NonNull String fileName,
		@NonNull String typeMime,
		int code,
		@NonNull CallbackUri callback
	) {
		return saveWithSaf(activity, fileName, typeMime, code, (result, intent) -> {
			if (result == Activity.RESULT_OK && intent != null) {
				Uri uri = intent.getData();
				if (uri != null) {
					callback.callback(uri);
				}
			}
		});
	}

	/**
	 * load json with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	@RequiresApi(19)
	public static boolean loadJsonWithSaf(
		@NonNull PageActivity activity,
		int code,
		@NonNull CallbackUri callback
	) {
		return loadWithSaf(activity, "application/json", code, callback);
	}

	/**
	 * load image with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	@RequiresApi(19)
	public static boolean loadImageWithSaf(
		@NonNull PageActivity activity,
		int code,
		@NonNull CallbackUri callback
	) {
		return loadWithSaf(activity, "image/*", code, callback);
	}

	/**
	 * load html with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	@RequiresApi(19)
	public static boolean loadHtmlWithSaf(
		@NonNull PageActivity activity,
		int code,
		@NonNull CallbackUri callback
	) {
		return loadWithSaf(activity, "text/html", code, callback);
	}

	/**
	 * load plain text with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	@RequiresApi(19)
	public static boolean loadPlainTextWithSaf(
		@NonNull PageActivity activity,
		int code,
		@NonNull CallbackUri callback
	) {
		return loadWithSaf(activity, "text/plain", code, callback);
	}

	/**
	 * load with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	@RequiresApi(19)
	public static boolean loadWithSaf(
		@NonNull PageActivity activity,
		@NonNull String typeMime,
		int code,
		@NonNull PageActivity.CallbackActivityResult callback
	) {
		boolean rc;
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType(typeMime);
		try {
			activity.addOnActivityResult(code, callback);
			activity.startActivityForResult(intent, code);
			rc = true;
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			rc = false;
		}
		return rc;
	}

	/**
	 * load with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	@RequiresApi(19)
	public static boolean loadWithSaf(
		@NonNull PageActivity activity,
		@NonNull String typeMime,
		int code,
		@NonNull CallbackUri callback
	) {
		return loadWithSaf(activity, typeMime, code, (result, intent) -> {
			if (result == Activity.RESULT_OK && intent != null) {
				Uri uri = intent.getData();
				if (uri != null) {
					callback.callback(uri);
				}
			}
		});
	}

	@Nullable
	public static String getDisplayName(@NonNull Context context, @NonNull Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		String name = null;
		try {
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
				if (0 <= index) {
					name = cursor.getString(index);
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return name;
	}

	@NonNull
	public static String incrementName(@NonNull String name) {
		int idx = name.length() - 1;
		if (4 <= idx) {
			if (name.charAt(idx) == ')') {
				int ee = idx;
				idx--;
				for ( ; 2 <= idx; ) {
					char c = name.charAt(idx);
					if (!('0' <= c && c <= '9')) {
						if (c == '(' && name.charAt(idx - 1) == ' ') {
							String s = name.substring(idx + 1, ee);
							try {
								int num = Integer.parseInt(s);
								return name.substring(0, idx + 1) + (num + 1) + ")";
							} catch (NumberFormatException e) {
								Log2.e(TAG, "incrementName:(" + s + ")");
								e.printStackTrace();
							}
						}
						break;
					}
					idx--;
				}
			}
		}
		return name + " (1)";
	}

	@AnyThread
	public static void sleep(@NonNull Executor executor, long msec, @NonNull Runnable runnable) {
		Taskz.call(executor, () -> {
			Thread.sleep(msec);
			Taskz.call(() -> {
				runnable.run();
				return null;
			}).addOnFailureListener(Taskz::printStackTrace2);
			return null;
		}).addOnFailureListener(Taskz::printStackTrace2);
	}

	@AnyThread
	public static void sleep(long msec, @NonNull Runnable runnable) {
		sleep(Taskz.getExecutor(), msec, runnable);
	}

	@NonNull
	public static String getNewName(@NonNull List<String> list, @NonNull String name) {
		for ( ; list.contains(name); ) {
			name = incrementName(name);
		}
		return name;
	}

	public static boolean isOutOfMemoryError(@Nullable Throwable e) {
		boolean rc = false;
		if (e != null) {
			for ( ; ; ) {
				if (e instanceof OutOfMemoryError) {
					rc = true;
					break;
				}
				e = e.getCause();
				if (e == null) {
					break;
				}
			}
		}
		return rc;
	}

	@NonNull
	public static TextMeasure measureTextBaseline(@NonNull String text, @NonNull Paint paint) {
		Paint.FontMetrics metrics = paint.getFontMetrics();
		float h = metrics.descent - metrics.ascent;
		float w = paint.measureText(text);
		return new TextMeasure(w, h, -metrics.ascent);
	}

	@NonNull
	public static PointF measureText(@NonNull String text, @NonNull Paint paint) {
		Paint.FontMetrics metrics = paint.getFontMetrics();
		float h = metrics.descent - metrics.ascent;
		float w = paint.measureText(text);
		return new PointF(w, h);
	}

	public static float measureTextForHeight(@NonNull String text, float height, @NonNull Paint paint) {
		Paint.FontMetrics metrics = paint.getFontMetrics();
		float h = metrics.descent - metrics.ascent;
		float w = paint.measureText(text);
		return w / h * height;
	}

	@NonNull
	public static FloatingActionButton createFab(@NonNull Context context, @NonNull Drawable d,
			int rightMargin, int bottomMargin) {

		FloatingActionButton rc = new FloatingActionButton(context);
		rc.setImageDrawable(d);
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.END | Gravity.BOTTOM);
		p.rightMargin = rightMargin;
		p.bottomMargin = bottomMargin;
		rc.setLayoutParams(p);
		rc.setSize(FloatingActionButton.SIZE_AUTO);
		return rc;
	}

	public static int findNum(@NonNull CharSequence s, int offset) {
		if (0 <= offset) {
			int n = s.length();
			for ( ; offset < n; offset++) {
				char c = s.charAt(offset);
				if ('0' <= c && c <= '9') {
					return offset;
				}
			}
		}
		return -1;
	}

	public static int getNum(@NonNull CharSequence s, int offset) {
		int rc = 0;
		if (0 <= offset) {
			int n = s.length();
			for ( ; offset < n; offset++) {
				char c = s.charAt(offset);
				if ('0' <= c && c <= '9') {
					rc = rc * 10 + c - '0';
				} else {
					break;
				}
			}
		}
		return rc;
	}

	public static void invokeRunnable(@Nullable Runnable runnable) {
		if (runnable != null) {
			runnable.run();
		}
	}

	@AnyThread
	@NonNull
	public static Task<Void> createZip(
		@NonNull Executor executor,
		@NonNull File dir,
		@NonNull File zipFile
	) {
		return Taskz.call(executor, () -> {
			File[] files = dir.listFiles();
			if (files == null || files.length == 0) {
				throw new RuntimeException("no files....");
			} else {
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
				byte[] buf = new byte[1024 * 1024];
				for (File f : files) {
					if (f.isFile()) {
						String name = f.getName();
						ZipEntry entry = new ZipEntry(name);
						entry.setTime(f.lastModified());
						FileInputStream is = new FileInputStream(f);
						zos.putNextEntry(entry);
						for ( ; ; ) {
							int len = is.read(buf);
							if (len < 0) break;

							zos.write(buf, 0, len);
						}
						zos.closeEntry();
						Utils.closeSafely(is);
					}
				}
				zos.flush();
				zos.close();
			}
			return null;
		});
	}

	@AnyThread
	@NonNull
	public static Task<Void> extractZip(
		@NonNull Executor executor,
		@NonNull File dir,
		@NonNull File zipFile
	) {
		return Taskz.call(executor, () -> {
			try (
				FileInputStream fis = new FileInputStream(zipFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				ZipInputStream zis = new ZipInputStream(bis)
			) {
				byte[] buf = new byte[4 * 1024];
				ZipEntry ze;
				while ((ze = zis.getNextEntry()) != null) {
					String name = ze.getName();
					File f = new File(dir, name);
					try (FileOutputStream fos = new FileOutputStream(f)) {
						for ( ; ; ) {
							int len = zis.read(buf);
							if (len < 0) {
								break;
							}
							fos.write(buf, 0, len);
						}
					}
				}
			}
			return null;
		});
	}

	public static int getDisplayWidth(@NonNull Context context) {
		DisplayMetrics disp = context.getResources().getDisplayMetrics();
		return disp.widthPixels;
	}

	public static float get1mmPixel(@NonNull Context context) {
		DisplayMetrics disp = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, disp);
	}

	public static float get1dpPixel(@NonNull Context context) {
		DisplayMetrics disp = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, disp);
	}

	public static float get1spPixel(@NonNull Context context) {
		DisplayMetrics disp = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, disp);
	}

	public static void addExif(
		@NonNull File f,
		@NonNull Calendar cal,
		@Nullable Location loc,
		int w,
		int h,
		@Nullable String nameApp
	) {
		try {
			String sw = "" + w;
			String sh = "" + h;
			ExifInterface exif = new ExifInterface(f.getAbsolutePath());
			setExifDateTime(exif, cal);
			if (loc != null) {
				setExifLocation(exif, loc);
			}
			exif.setAttribute(ExifInterface.TAG_MAKE, Build.MANUFACTURER);
			exif.setAttribute(ExifInterface.TAG_MODEL, Build.MODEL);
			exif.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, sw);
			exif.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, sh);
			exif.setAttribute(ExifInterface.TAG_PIXEL_X_DIMENSION, sw);
			exif.setAttribute(ExifInterface.TAG_PIXEL_Y_DIMENSION, sh);
			exif.setAttribute(ExifInterface.TAG_ORIENTATION, "" + ExifInterface.ORIENTATION_NORMAL);
			exif.setAttribute(ExifInterface.TAG_EXIF_VERSION, "0220");
			if (nameApp != null) {
				exif.setAttribute(ExifInterface.TAG_SOFTWARE, nameApp);
			}
			exif.saveAttributes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String convertLocation(double latlng) {
		latlng = Math.abs(latlng);
		int deg = (int)latlng;
		latlng -= deg;
		latlng *= 60;
		int min = (int)latlng;
		latlng -= min;
		latlng *= 60 * 10000;
		int sec10000 = (int)latlng;
		return "" + deg + "/1," + min + "/1," + sec10000 + "/10000";
	}

	private static void setExifLocation(@NonNull ExifInterface exif, @NonNull Location loc) {
		double lng = loc.getLongitude();
		double lat = loc.getLatitude();
		double alt = loc.getAltitude();
		exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convertLocation(lng));
		exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, lng < 0 ? "W" : "E");
		exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convertLocation(lat));
		exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, lat < 0 ? "S" : "N");
		exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, convertLocation(alt));
		exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, "0");
	}

	private static void setExifDateTime(@NonNull ExifInterface exif, @NonNull Calendar cal) {
		String dt = String.format(
			Locale.US,
			"%04d:%02d:%02d %02d:%02d:%02d",
			cal.get(Calendar.YEAR),
			cal.get(Calendar.MONTH)+ 1,
			cal.get(Calendar.DATE),
			cal.get(Calendar.HOUR_OF_DAY),
			cal.get(Calendar.MINUTE),
			cal.get(Calendar.SECOND)
		);
		exif.setAttribute(ExifInterface.TAG_DATETIME, dt);
		exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL,	dt);
		exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED,	dt);

		Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		utc.setTimeInMillis(cal.getTimeInMillis());
		dt = String.format(
			Locale.US,
			"%04d:%02d:%02d",
			utc.get(Calendar.YEAR),
			utc.get(Calendar.MONTH)+ 1,
			utc.get(Calendar.DATE)
		);
		exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, dt);
		dt = String.format(
			Locale.US,
			"%02d:%02d:%02d",
			utc.get(Calendar.HOUR_OF_DAY),
			utc.get(Calendar.MINUTE),
			utc.get(Calendar.SECOND)
		);
		exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, dt);
	}

	public static void kickYouTube(@NonNull Context context, @NonNull String id) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + id));
			context.startActivity(intent);
		}
	}

	public static long calcLongHash(@NonNull String s) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] digest = md5.digest(s.getBytes());
		long rc = 0;
		rc |= (long)(digest[0] & 0xff) << 56;
		rc |= (long)(digest[1] & 0xff) << 48;
		rc |= (long)(digest[2] & 0xff) << 40;
		rc |= (long)(digest[3] & 0xff) << 32;
		rc |= (long)(digest[4] & 0xff) << 24;
		rc |= (long)(digest[5] & 0xff) << 16;
		rc |= (long)(digest[6] & 0xff) << 8;
		rc |= (long)(digest[7] & 0xff);
		return rc;
	}

	public static void kickAppSetting(@NonNull Activity activity, @NonNull String namePackage) {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", namePackage, null);
		intent.setData(uri);
		activity.startActivity(intent);
	}

	@MainThread
	public static void requestPermission(
		@NonNull PageActivity activity,
		int codeRequest,
		@NonNull String manifestPermission,
		@NonNull Runnable granted,
		@NonNull Runnable denied,
		@NonNull Runnable neverAsked
	) {
		if (Build.VERSION.SDK_INT < 23) {
			granted.run();
		} else {
			if (isGrantedPermission(activity, manifestPermission)) {
				granted.run();
			} else {
				activity.addOnRequestPermissionsResult(codeRequest, () -> {
					Taskz.call(() -> {
						checkSelfPermission(activity, manifestPermission, granted, denied, neverAsked);
						return null;
					}).addOnFailureListener(Taskz::printStackTrace2);
				});
				ActivityCompat.requestPermissions(activity, new String[] { manifestPermission }, codeRequest);
			}
		}
	}

	public static void checkSelfPermission(
		@NonNull Activity activity,
		@NonNull String manifestPermission,
		@NonNull Runnable granted,
		@NonNull Runnable denied,
		@NonNull Runnable neverAsked
	) {
		if (!isGrantedPermission(activity, manifestPermission)) {
			if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, manifestPermission)) {
				neverAsked.run();
			} else {
				denied.run();
			}
		} else {
			granted.run();
		}
	}

	@MainThread
	public static void requestPermission(
		@NonNull PageActivity activity,
		int codeRequest,
		@NonNull String manifestPermission,
		@NonNull Runnable callback
	) {
		requestPermissions(activity, codeRequest, new String[] { manifestPermission }, callback);
	}

	@MainThread
	public static void requestPermissions(
		@NonNull PageActivity activity,
		int codeRequest,
		@NonNull String[] manifestPermissions,
		@NonNull Runnable callback
	) {
		if (Build.VERSION.SDK_INT < 23) {
			callback.run();
		} else {
			if (isGrantedPermissions(activity, manifestPermissions)) {
				callback.run();
			} else {
				activity.addOnRequestPermissionsResult(codeRequest, () -> {
					Taskz.call(() -> {
						callback.run();
						return null;
					}).addOnFailureListener(Taskz::printStackTrace2);
				});
				ActivityCompat.requestPermissions(activity, manifestPermissions, codeRequest);
			}
		}
	}

	public static boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
		return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
	}

	public static boolean isGrantedPermissions(@NonNull Context context, @NonNull String[] permissions) {
		for (String permission : permissions) {
			if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}


}
