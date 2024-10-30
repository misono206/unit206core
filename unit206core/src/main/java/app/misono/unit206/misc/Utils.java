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

package app.misono.unit206.misc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.icu.text.Normalizer2;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.ConditionVariable;
import android.os.LocaleList;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.exifinterface.media.ExifInterface;

import app.misono.unit206.callback.CallbackException;
import app.misono.unit206.callback.CallbackUri;
import app.misono.unit206.debug.Log2;
import app.misono.unit206.page.PageActivity;
import app.misono.unit206.task.ObjectReference;
import app.misono.unit206.task.Taskz;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class Utils {
	private static final String TAG = "Utils";
	private static final TimeZone JST = TimeZone.getTimeZone("Asia/Tokyo");
	private static final int DEFAULT_ALLOCATE_SIZE = 4 * 1024 * 1024;

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

	@NonNull
	public static String hexString4(int a) {
		return String.format(Locale.US, "%04x", a & 0xffff);
	}

	@NonNull
	public static String hexString2(int a) {
		return String.format(Locale.US, "%02x", a & 0xff);
	}

	@NonNull
	public static String hexDumpString(@NonNull byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(hexString2(b));
			sb.append(" ");
		}
		return sb.toString().trim();
	}

	public static long getAssetFileSize(@NonNull Context context, @NonNull String assetPath) {
		long len;
		try (
			AssetFileDescriptor fd = context.getAssets().openFd(assetPath);
		) {
			len = fd.getLength();
		} catch (IOException e) {
			len = AssetFileDescriptor.UNKNOWN_LENGTH;
			e.printStackTrace();
		}
		return len;
	}

	public static boolean checkAssetFileSizeAndCopyIfNeed(
		@NonNull Context context,
		@NonNull File file,
		@NonNull String pathAsset
	) throws IOException {
		boolean copy = !file.exists();
		if (!copy) {
			long len = getAssetFileSize(context, pathAsset);
			if (len == AssetFileDescriptor.UNKNOWN_LENGTH) {
				throw new RuntimeException("maybe compressed asset file: " + pathAsset);
			}
			copy = file.length() != len;
		}
		if (copy) {
			copyAssetFileToFile(context, pathAsset, file);
		}
		return copy;
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
		try (
			InputStream is = context.getAssets().open(assetPath);
			BufferedInputStream bis = new BufferedInputStream(is);
			FileOutputStream fos = new FileOutputStream(dst);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
		) {
			copyStream(bis, bos);
		}
	}

	@NonNull
	public static byte[] readAssetFileWithException(
		@NonNull Context context,
		@NonNull String assetPath
	) throws IOException {
		try (
			InputStream is = context.getAssets().open(assetPath);
			BufferedInputStream bis = new BufferedInputStream(is);
		) {
			return readStream(bis);
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
		try (
			InputStream is = context.getContentResolver().openInputStream(in);
			BufferedInputStream bis = new BufferedInputStream(is);
		) {
			rc = readStream(bis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rc;
	}

	@WorkerThread
	@NonNull
	public static byte[] readBytesWithException(
		@NonNull Context context,
		@NonNull Uri in
	) throws IOException {
		byte[] rc;
		try (
			InputStream is = context.getContentResolver().openInputStream(in);
			BufferedInputStream bis = new BufferedInputStream(is);
		) {
			rc = readStream(bis);
		}
		return rc;
	}

	@WorkerThread
	public static void readFromUri(
		@NonNull Context context,
		@NonNull Uri in,
		@NonNull File out
	) throws IOException {
		try (
			InputStream is = context.getContentResolver().openInputStream(in);
			BufferedInputStream bis = new BufferedInputStream(is);
			FileOutputStream fos = new FileOutputStream(out);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
		) {
			copyStream(bis, bos);
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
		try (
			BufferedInputStream bis = new BufferedInputStream(is);
		) {
			rc = readStream(bis);
		} catch (IOException e) {
			e.printStackTrace();
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
		try (
			OutputStream os = context.getContentResolver().openOutputStream(out);
			BufferedOutputStream bos = new BufferedOutputStream(os);
		) {
			bos.write(b, 0, b.length);
		}
	}

	@WorkerThread
	public static void writeToUri(
		@NonNull Context context,
		@NonNull File src,
		@NonNull Uri out
	) throws IOException {
		try (
			OutputStream os = context.getContentResolver().openOutputStream(out);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			FileInputStream fis = new FileInputStream(src);
			BufferedInputStream bis = new BufferedInputStream(fis);
		) {
			copyStream(bis, bos);
		}
	}

	@WorkerThread
	private static void copyStream(
		@NonNull InputStream is,
		@NonNull OutputStream os
	) throws IOException {
		byte[] buf = new byte[DEFAULT_ALLOCATE_SIZE];
		for ( ; ; ) {
			int len = is.read(buf);
			if (len < 0) {
				break;
			}
			os.write(buf, 0, len);
		}
	}

	@WorkerThread
	@NonNull
	public static byte[] readRawBinary(@NonNull Resources r, @RawRes int id) throws IOException {
		try (
			InputStream is = r.openRawResource(id);
			BufferedInputStream bis = new BufferedInputStream(is);
		) {
			return readStream(bis);
		}
	}

	@WorkerThread
	@NonNull
	public static byte[] readRawBinary(@NonNull Context context, @RawRes int id) throws IOException {
		Resources r = context.getResources();
		return readRawBinary(r, id);
	}

	@WorkerThread
	@NonNull
	public static byte[] readStream(
		@NonNull BufferedInputStream bis
	) throws IOException {
		ByteArrayBuffer ba = new ByteArrayBuffer();
		for ( ; ; ) {
			int av = bis.available();
			if (av == 0) {
				av = 4096;
			}
			byte[] buf = new byte[av];
			int len = bis.read(buf);
			if (len < 0) {
				break;
			}
			ba.add(buf, 0, len);
		}
		return ba.getByteArray();
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

	@WorkerThread
	@Nullable
	public static File writeToCache(
		@NonNull Context context,
		@NonNull String fname,
		@NonNull byte[] data
	) {
		File rc = null;
		File file = new File(context.getCacheDir(), fname);
		try (
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
		) {
			bos.write(data, 0, data.length);
			rc = file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rc;
	}

	@WorkerThread
	public static void writeToFile(
		@NonNull File file,
		@NonNull byte[] data
	) throws IOException {
		try (
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
		) {
			bos.write(data, 0, data.length);
		}
	}

	@WorkerThread
	public static void writeToFile(
		@NonNull File file,
		@NonNull byte[] data,
		int offset,
		int length
	) throws IOException {
		try (
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
		) {
			bos.write(data, 0, data.length);
		}
	}

	@WorkerThread
	public static void appendToFile(
		@NonNull File file,
		@NonNull byte[] data
	) throws IOException {
		try (
			FileOutputStream fos = new FileOutputStream(file, true);
		) {
			fos.write(data, 0, data.length);
		}
	}

/*
	@Deprecated	// use writeToFile()
	public static void writeToFileWithException(
		@NonNull File file,
		@NonNull byte[] data
	) throws IOException {
		writeToFile(file, data);
	}
*/

	@WorkerThread
	@NonNull
	public static byte[] readBytesWithException(@NonNull File in) throws IOException {
		try (
			FileInputStream fis = new FileInputStream(in);
			BufferedInputStream bis = new BufferedInputStream(fis);
		) {
			return readStream(bis);
		}
	}

	@WorkerThread
	@Nullable
	public static byte[] readBytes(@NonNull File in) {
		try (
			FileInputStream fis = new FileInputStream(in);
		) {
			return readBytes(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@WorkerThread
	@Nullable
	public static byte[] readFromCache(@NonNull Context context, @NonNull String fname) {
		File file = new File(context.getCacheDir(), fname);
		return readBytes(file);
	}

	@WorkerThread
	public static boolean copyDataToUri(@NonNull Context context, @NonNull byte[] data, @NonNull Uri out) {
		boolean success = false;
		try (
			OutputStream os = context.getContentResolver().openOutputStream(out);
			BufferedOutputStream bos = new BufferedOutputStream(os);
		) {
			bos.write(data);
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	@WorkerThread
	public static boolean copyFileToUri(
		@NonNull Context context,
		@NonNull File in,
		@NonNull Uri out
	) {
		boolean success = false;
		try (
			FileInputStream fis = new FileInputStream(in);
			BufferedInputStream is = new BufferedInputStream(fis);
			OutputStream cos = context.getContentResolver().openOutputStream(out);
			BufferedOutputStream os = new BufferedOutputStream(cos);
		) {
			copyStream(is, os);
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	@WorkerThread
	public static boolean copyUriToFileNoException(
		@NonNull Context context,
		@NonNull Uri in,
		@NonNull File out
	) {
		boolean success = false;
		try (
			InputStream cis = context.getContentResolver().openInputStream(in);
			BufferedInputStream is = new BufferedInputStream(cis);
			FileOutputStream fos = new FileOutputStream(out);
			BufferedOutputStream os = new BufferedOutputStream(fos);
		) {
			copyStream(is, os);
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	@WorkerThread
	public static void copyUriToFile(
		@NonNull Context context,
		@NonNull Uri in,
		@NonNull File out
	) throws Exception {
		try (
			InputStream cis = context.getContentResolver().openInputStream(in);
			BufferedInputStream is = new BufferedInputStream(cis);
			FileOutputStream fos = new FileOutputStream(out);
			BufferedOutputStream os = new BufferedOutputStream(fos);
		) {
			copyStream(is, os);
		}
	}

	@WorkerThread
	public static void copyFileToFile(
		@NonNull File in,
		@NonNull File out
	) throws IOException {
		try (
			FileInputStream is = new FileInputStream(in);
			FileOutputStream os = new FileOutputStream(out);
			FileChannel inChannel = is.getChannel();
			FileChannel outChannel = os.getChannel();
		) {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		}
	}

	@RequiresApi(26)
	@WorkerThread
	public static void moveFileToFile(
		@NonNull File from,
		@NonNull File to
	) throws IOException {
		Path pFrom = Paths.get(from.toURI());
		Path pTo = Paths.get(to.toURI());
		Files.move(pFrom, pTo, StandardCopyOption.REPLACE_EXISTING);
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
//			e.printStackTrace();
		}
		return rc;
	}

	public static long longValue(@NonNull String s, long defaultValue) {
		long rc = defaultValue;
		try {
			rc = Long.parseLong(s);
		} catch (NumberFormatException e) {
//			e.printStackTrace();
		}
		return rc;
	}

	public static float floatValue(@NonNull String s, float defaultValue) {
		float rc = defaultValue;
		try {
			rc = Float.parseFloat(s);
		} catch (NumberFormatException e) {
//			e.printStackTrace();
		}
		return rc;
	}

	public static double doubleValue(@NonNull String s, double defaultValue) {
		double rc = defaultValue;
		try {
			rc = Double.parseDouble(s);
		} catch (NumberFormatException e) {
//			e.printStackTrace();
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
	public static String getTimeFormatString(@NonNull Calendar cal) {
		int hh = cal.get(Calendar.HOUR_OF_DAY);
		int mm = cal.get(Calendar.MINUTE);
		int ss = cal.get(Calendar.SECOND);
		return String.format(Locale.US, "%02d:%02d:%02d", hh, mm, ss);
	}

	@NonNull
	public static String getTimeFormatString(long time) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeInMillis(time);
		return getTimeFormatString(cal);
	}

	@NonNull
	public static String getDateTimeFormatString(@NonNull Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hh = cal.get(Calendar.HOUR_OF_DAY);
		int mm = cal.get(Calendar.MINUTE);
		int ss = cal.get(Calendar.SECOND);
		return String.format(Locale.US, "%04d/%02d/%02d %02d:%02d:%02d", year, month, day, hh, mm, ss);
	}

	@NonNull
	public static String getDateTimeFormatString(long time) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeInMillis(time);
		return getDateTimeFormatString(cal);
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

	public static int getH2m2s2(@NonNull Calendar cal) {
		int hh = cal.get(Calendar.HOUR_OF_DAY);
		int mm = cal.get(Calendar.MINUTE);
		int ss = cal.get(Calendar.SECOND);
		return hh * 10000 + mm * 100 + ss;
	}

	@NonNull
	public static String getHhmmString(int hhmm) {
		int hh = hhmm / 100;
		int mm = hhmm % 100;
		return String.format(Locale.US, "%02d:%02d", hh, mm);
	}

	@NonNull
	public static String getHhmmssString(int hhmmss) {
		int hh = hhmmss / 10000;
		int mm = (hhmmss / 100) % 100;
		int ss = hhmmss % 100;
		return String.format(Locale.US, "%02d:%02d:%02d", hh, mm, ss);
	}

	@NonNull
	public static String getTickString(long tick) {
		int msec = (int)(tick % 1000);
		tick /= 1000;
		int ss = (int)(tick % 60);
		tick /= 60;
		int mm = (int)(tick % 60);
		int hh = (int)(tick / 60);
		return String.format(Locale.US, "%02d:%02d:%02d.%03d", hh, mm, ss, msec);
	}

	@NonNull
	public static String getTickSecString(long tick) {
		tick /= 1000;
		int ss = (int)(tick % 60);
		tick /= 60;
		int mm = (int)(tick % 60);
		int hh = (int)(tick / 60);
		return String.format(Locale.US, "%02d:%02d:%02d", hh, mm, ss);
	}

	/**
	 * save PDF with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	@Deprecated
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
	@Deprecated
	public static boolean saveHtmlWithSaf(
		@NonNull PageActivity activity,
		@NonNull String fileName,
		int code,
		@NonNull CallbackUri callback
	) {
		return saveWithSaf(activity, fileName, "text/html", code, callback);
	}

	@AnyThread
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
		String[] sp = text.split("\n");
		float wMax = 0;
		for (String line : sp) {
			float w = paint.measureText(line);
			wMax = Math.max(wMax, w);
		}
		return new PointF(wMax, h);
	}

	/**
	 * Measure width size.
	 *
	 * @param height1 text height pixel size
	 * @return text width pixel size
	 */
	public static float measureTextForHeight(@NonNull String text, float height1, @NonNull Paint paint) {
		Paint.FontMetrics metrics = paint.getFontMetrics();
		float h = metrics.descent - metrics.ascent;
		String[] sp = text.split("\n");
		float wMax = 0;
		for (String line : sp) {
			float w = paint.measureText(line);
			wMax = Math.max(wMax, w / h * height1);
		}
		return wMax;
	}

	public static void fitTextSize(@NonNull TextView view, int width) {
		Paint paint = view.getPaint();
		String lines = view.getText().toString();
		String[] sp = lines.split("\n");
		float wMax = 0;
		for (String line : sp) {
			float wText = paint.measureText(line);
			if (wMax < wText) {
				wMax = wText;
			}
		}
		if (width < wMax) {
			float sizeText = view.getTextSize() * width / wMax;
			view.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeText);
		}
	}

	@NonNull
	public static FloatingActionButton createFab(
		@NonNull Context context,
		@NonNull Drawable d,
		int rightMargin,
		int bottomMargin
	) {
		FloatingActionButton rc = new FloatingActionButton(context);
		rc.setImageDrawable(d);
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT,
			Gravity.END | Gravity.BOTTOM
		);
		p.rightMargin = rightMargin;
		p.bottomMargin = bottomMargin;
		rc.setLayoutParams(p);
		rc.setSize(FloatingActionButton.SIZE_AUTO);
		return rc;
	}

	@NonNull
	public static Paint[] createSameBrightnessColorPaints(
		@NonNull Paint paintBase,
		int colorStart,
		int nPaint
	) {
		Paint[] rc = new Paint[nPaint];
		int[] colors = createColors(colorStart, nPaint);
		for (int i = 0; i < nPaint; i++) {
			Paint p = new Paint(paintBase);
			p.setColor(colors[i]);
			rc[i] = p;
		}
		return rc;
	}

	@NonNull
	public static int[] createColors(int color, int nColor) {
		int[] rc = new int[nColor];
		float[] hsl = new float[3];
		ColorUtils.colorToHSL(color, hsl);
		float step = 360f / nColor;
		for (int i = 0; i < nColor; i++) {
			int c = ColorUtils.HSLToColor(hsl);
			rc[i] = c;
			hsl[0] += step;
			if (360f <= hsl[0]) {
				hsl[0] -= 360f;
			}
		}
		return rc;
	}

	public static int saturationColor(int color, float saturation) {
		float[] hsl = new float[3];
		ColorUtils.colorToHSL(color, hsl);
		hsl[1] = saturation;
		return ColorUtils.HSLToColor(hsl);
	}

	public static int lightnessColor(int color, float lightness) {
		float[] hsl = new float[3];
		ColorUtils.colorToHSL(color, hsl);
		hsl[2] = lightness;
		return ColorUtils.HSLToColor(hsl);
	}

	public static int saturationLightnessColor(int color, float saturation, float lightness) {
		float[] hsl = new float[3];
		ColorUtils.colorToHSL(color, hsl);
		hsl[1] = saturation;
		hsl[2] = lightness;
		return ColorUtils.HSLToColor(hsl);
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

	@Deprecated
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
				FileInputStream is = null;
				try (
					ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
				) {
					byte[] buf = new byte[DEFAULT_ALLOCATE_SIZE];
					for (File f : files) {
						if (f.isFile()) {
							String name = f.getName();
							ZipEntry entry = new ZipEntry(name);
							entry.setTime(f.lastModified());
							is = new FileInputStream(f);
							zos.putNextEntry(entry);
							for ( ; ; ) {
								int len = is.read(buf);
								if (len < 0) break;

								zos.write(buf, 0, len);
							}
							zos.closeEntry();
							Utils.closeSafely(is);
							is = null;
						}
					}
					zos.flush();
				} finally {
					Utils.closeSafely(is);
				}
			}
			return null;
		});
	}

/*
	@Deprecated		// use createZipSync().
	@WorkerThread
	public static void createZip(
		@NonNull File dir,
		@NonNull File zipFile,
		@Nullable String prefix
	) throws IOException {
		createZipSync(dir, zipFile, prefix);
	}
*/

	@WorkerThread
	public static void createZipSync(
		@NonNull File dir,
		@NonNull File zipFile,
		@Nullable String prefix
	) throws IOException {
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			throw new RuntimeException("no files....");
		} else {
			try (
				ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
			) {
				createZip(zos, dir.getAbsolutePath(), dir, prefix);
				zos.flush();
			}
		}
	}

	@WorkerThread
	private static void createZip(
		@NonNull ZipOutputStream zos,
		@NonNull String dirBase,
		@NonNull File dir,
		@Nullable String basePrefix
	) throws IOException {
		File[] files = dir.listFiles();
		if (files != null && files.length != 0) {
			FileInputStream is = null;
			String path = dir.getAbsolutePath();
			String prefix = "";
			if (!TextUtils.isEmpty(basePrefix)) {
				if (basePrefix.charAt(basePrefix.length() - 1) != '/') {
					prefix = basePrefix + '/';
				}
			}
			int lenBase = dirBase.length();
			if (lenBase < path.length()) {
				prefix += path.substring(lenBase + 1) + "/";
			}
			try {
				byte[] buf = new byte[DEFAULT_ALLOCATE_SIZE];
				for (File f : files) {
					if (f.isFile()) {
						String name = prefix + f.getName();
						ZipEntry entry = new ZipEntry(name);
						entry.setTime(f.lastModified());
						is = new FileInputStream(f);
						zos.putNextEntry(entry);
						for ( ; ; ) {
							int len = is.read(buf);
							if (len < 0) break;

							zos.write(buf, 0, len);
						}
						zos.closeEntry();
						Utils.closeSafely(is);
						is = null;
					} else if (f.isDirectory()) {
						createZip(zos, dirBase, f, basePrefix);
					}
				}
			} finally {
				Utils.closeSafely(is);
			}
		}
	}

	@WorkerThread
	public static void extractZipSync(
		@NonNull File dir,
		@NonNull File zipFile
	) throws Exception {
		extractZipSyncInternal(dir, zipFile, null);
	}

	@RequiresApi(24)
	@WorkerThread
	public static void extractZipSync(
		@NonNull File dir,
		@NonNull File zipFile,
		@Nullable Normalizer2 normalizer2
	) throws Exception {
		NormalizeName normalizer = null;
		if (normalizer2 != null) {
			normalizer = new NormalizeName(normalizer2);
		}
		extractZipSyncInternal(dir, zipFile, normalizer);
	}

	@WorkerThread
	private static void extractZipSyncInternal(
		@NonNull File dir,
		@NonNull File zipFile,
		@Nullable INormalizeName normalizer
	) throws Exception {
		if (24 <= Build.VERSION.SDK_INT) {
			SortedMap<String, Charset> map = Charset.availableCharsets();
			if (map != null) {
				try {
					extractZipSyncUtf8(dir, zipFile, normalizer);
				} catch (IllegalArgumentException e) {
					// non UTF-8 file name found...
					Charset sjis = map.get("Shift_JIS");
					if (sjis != null) {
						dir.delete();
						dir.mkdirs();
						extractZipSync(dir, zipFile, sjis, normalizer);
						return;
					}
					throw e;
				}
			} else {
				extractZipSyncUtf8(dir, zipFile, normalizer);
			}
		} else {
			extractZipSyncUtf8(dir, zipFile, normalizer);
		}
	}

	@WorkerThread
	private static void extractZipSyncUtf8(
		@NonNull File dir,
		@NonNull File zipFile,
		@Nullable INormalizeName normalizer
	) throws Exception {
		try (
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
		) {
			extractZipSyncExec(dir, zis, normalizer);
		}
	}

	private interface INormalizeName {
		@NonNull
		String normalize(@NonNull String name);
	}

	@RequiresApi(24)
	private static class NormalizeName implements INormalizeName {
		private final Normalizer2 normalizer2;

		private NormalizeName(@NonNull Normalizer2 normalizer2) {
			this.normalizer2 = normalizer2;
		}

		@Override
		@NonNull
		public String normalize(@NonNull String name) {
			return normalizer2.normalize(name);
		}
	}

	@RequiresApi(24)
	@WorkerThread
	private static void extractZipSync(
		@NonNull File dir,
		@NonNull File zipFile,
		@NonNull Charset charset,
		@Nullable INormalizeName normalizer
	) throws Exception {
		try (
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)), charset);		// API-24
		) {
			extractZipSyncExec(dir, zis, normalizer);
		}
	}

	@WorkerThread
	private static void extractZipSyncExec(
		@NonNull File dir,
		@NonNull ZipInputStream zis,
		@Nullable INormalizeName normalizer
	) throws Exception {
		String dirPath = dir.getCanonicalPath();
		byte[] buf = new byte[DEFAULT_ALLOCATE_SIZE];
		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			String name = ze.getName();
			if (normalizer != null && 24 <= Build.VERSION.SDK_INT) {
				name = normalizer.normalize(name);
			}
			if (!TextUtils.equals(name, "__MACOSX") && !TextUtils.equals(name, ".DS_Store")) {
				File f = new File(dirPath, name);
				String canonicalPath = f.getCanonicalPath();
				if (!canonicalPath.startsWith(dirPath)) {
					throw new SecurityException();
				}
				if (ze.isDirectory()) {
					f.mkdirs();
				} else {
					File parent = f.getParentFile();
					parent.mkdirs();
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
		}
	}

	@AnyThread
	@NonNull
	public static Task<Void> extractZip(
		@NonNull Executor executor,
		@NonNull File dir,
		@NonNull File zipFile
	) {
		return Taskz.call(executor, () -> {
			extractZipSync(dir, zipFile);
			return null;
		});
	}

	@RequiresApi(24)
	@AnyThread
	@NonNull
	public static Task<Void> extractZip(
		@NonNull Executor executor,
		@NonNull File dir,
		@NonNull File zipFile,
		@Nullable Normalizer2 normalizer2
	) {
		return Taskz.call(executor, () -> {
			extractZipSync(dir, zipFile, normalizer2);
			return null;
		});
	}

	@AnyThread
	@NonNull
	public static Task<Void> extractGzip(
		@NonNull Executor executor,
		@NonNull File out,
		@NonNull GZIPInputStream zis
	) {
		return Taskz.call(executor, () -> {
			byte[] buf = new byte[DEFAULT_ALLOCATE_SIZE];
			try (
				FileOutputStream fos = new FileOutputStream(out)
			) {
				for ( ; ; ) {
					int len = zis.read(buf);
					if (len < 0) {
						break;
					}
					fos.write(buf, 0, len);
				}
			}
			return null;
		});
	}

	@AnyThread
	@NonNull
	public static Task<Void> extractGzip(
		@NonNull Context context,
		@NonNull Executor executor,
		@NonNull File out,
		@NonNull Uri uriGz
	) {
		return Taskz.call(executor, () -> {
			try (
				GZIPInputStream zis = new GZIPInputStream(new BufferedInputStream(context.getContentResolver().openInputStream(uriGz)))
			) {
				Tasks.await(extractGzip(executor, out, zis));
			}
			return null;
		});
	}

	public static int getDisplayMinSize(@NonNull Context context) {
		DisplayMetrics disp = context.getResources().getDisplayMetrics();
		return Math.min(disp.widthPixels, disp.heightPixels);
	}

	public static int getDisplayWidth(@NonNull Context context) {
		DisplayMetrics disp = context.getResources().getDisplayMetrics();
		return disp.widthPixels;
	}

	public static int getDisplayHeight(@NonNull Context context) {
		DisplayMetrics disp = context.getResources().getDisplayMetrics();
		return disp.heightPixels;
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
			exif.setAttribute(ExifInterface.TAG_IMAGE_UNIQUE_ID, createUidHexString());
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

	public static void kickGoogleMap(@NonNull Context context, @NonNull String latitude, @NonNull String longitude) {
		String url = "https://www.google.co.jp/maps/place/" + latitude + "," + longitude;
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
		intent.setPackage("com.google.android.apps.maps");
		context.startActivity(intent);
	}

	@Deprecated	// use createLightLongHash() instead.
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

	private static byte[] stateBase;

	@NonNull
	private static synchronized byte[] createStateArray() {
		if (stateBase == null) {
			byte[] state = new byte[256];
			for (int i = 0; i < 256; i++) {
				state[i] = (byte)i;
			}
			int k2 = 0;
			for (int k1 = 0; k1 < 256; k1++) {
				k2 = (k2 + 17 + state[k1]) & 0xff;
				swapByte(state, k1, k2);
			}
			stateBase = state;
		}
		byte[] rc = new byte[256];
		System.arraycopy(stateBase, 0, rc, 0, 256);
		return rc;
	}

	private static void swapByte(@NonNull byte[] state, int k1, int k2) {
		byte t = state[k1];
		state[k1] = state[k2];
		state[k2] = t;
	}

	public static int calcLightIntHash(@NonNull byte[] b, int offset, int len) {
		byte[] state = createStateArray();
		byte[] b4 = new byte[4];
		int s1 = 0;
		int s2 = 0;
		for (int i = 0; i < len; i++) {
			s1 = (s1 + 1) & 0xff;
			s2 += state[s1];
			s2 &= 0xff;
			swapByte(state, s1, s2);
			int k = (state[s1] + state[s2]) & 0xff;
			b4[i % 4] ^= (byte)(b[i] ^ state[k]);
		}
		return Utils.read4le(b4, 0);
	}

	public static int calcLightIntHash(@NonNull String s) {
		byte[] b = s.getBytes();
		return calcLightIntHash(b, 0, b.length);
	}

	public static long calcLightLongHash(@NonNull byte[] b, int offset, int len) {
		byte[] state = createStateArray();
		byte[] b8 = new byte[8];
		int s1 = 0;
		int s2 = 0;
		for (int i = 0; i < len; i++) {
			s1 = (s1 + 1) & 0xff;
			s2 += state[s1];
			s2 &= 0xff;
			swapByte(state, s1, s2);
			int k = (state[s1] + state[s2]) & 0xff;
			b8[i % 8] ^= (byte)(b[i] ^ state[k]);
		}
		return Utils.read8le(b8, 0);
	}

	public static long calcLightLongHash(@NonNull String s) {
		byte[] b = s.getBytes();
		return calcLightLongHash(b, 0, b.length);
	}

	public static void kickAppSetting(@NonNull Activity activity, @NonNull String namePackage) {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", namePackage, null);
		intent.setData(uri);
		activity.startActivity(intent);
	}

	public static void kickUrl(@NonNull Activity activity, @NonNull String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
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

	public static void sendGmail(
		@NonNull Activity activity,
		@Nullable String[] to,
		@Nullable String[] cc,
		@Nullable String subject,
		@Nullable String message,
		@Nullable Uri uriAttachment,
		@Nullable Runnable successSync,
		@Nullable CallbackException errorSync
	) {
		String namePackage = "com.google.android.gm";
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:"));
		intent.setPackage(namePackage);
		if (to != null) {
			intent.putExtra(Intent.EXTRA_EMAIL, to);
		}
		if (cc != null) {
			intent.putExtra(Intent.EXTRA_CC, cc);
		}
		if (subject != null) {
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		}
		if (message != null) {
			intent.putExtra(Intent.EXTRA_TEXT, message);
		}
		if (uriAttachment != null) {
			activity.grantUriPermission(
				namePackage,
				uriAttachment,
				Intent.FLAG_GRANT_READ_URI_PERMISSION
			);
			intent.putExtra(Intent.EXTRA_STREAM, uriAttachment);
		}
		try {
			activity.startActivity(intent);
			if (successSync != null) {
				successSync.run();
			}
		} catch (Exception e) {
			if (errorSync != null) {
				errorSync.callback(e);
			} else {
				Taskz.printStackTrace2(e);
			}
		}
	}

	/**
	 * Send a E-mail.
	 * @return grant package name list.
	 *
	 * startActivityForResult()にしても常に CANCELEDが返るようだ.
	 */
	@SuppressLint("QueryPermissionsNeeded")
	@NonNull
	public static List<String> sendEmail(
		@NonNull Activity activity,
		@Nullable String[] to,
		@Nullable String[] cc,
		@Nullable String subject,
		@Nullable String message,
		@Nullable Uri uriAttachment,
		@NonNull CharSequence msgSelectMailer,
		@Nullable Runnable successSync,
		@Nullable CallbackException errorSync
	) {
		List<String> listGrant = new ArrayList<>();
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:"));
		if (to != null) {
			intent.putExtra(Intent.EXTRA_EMAIL, to);
		}
		if (cc != null) {
			intent.putExtra(Intent.EXTRA_CC, cc);
		}
		if (subject != null) {
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		}
		if (message != null) {
			intent.putExtra(Intent.EXTRA_TEXT, message);
		}
		if (uriAttachment != null) {
			PackageManager manager = activity.getPackageManager();
			List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
			for (ResolveInfo info : infos) {
				String namePackage = info.activityInfo.packageName;
				listGrant.add(namePackage);
				activity.grantUriPermission(
					namePackage,
					uriAttachment,
					Intent.FLAG_GRANT_READ_URI_PERMISSION
				);
			}
			intent.putExtra(Intent.EXTRA_STREAM, uriAttachment);
		}
		try {
			Intent intentChooser = Intent.createChooser(intent, msgSelectMailer);
			activity.startActivity(intentChooser);
			if (!listGrant.isEmpty() && successSync != null) {
				successSync.run();
			}
		} catch (Exception e) {
			if (errorSync != null) {
				errorSync.callback(e);
			} else {
				Taskz.printStackTrace2(e);
			}
		}
		return listGrant;
	}

	@NonNull
	public static String num3(int num) {
		return NumberFormat.getNumberInstance(Locale.US).format(num);
	}

	@NonNull
	public static String num3(long num) {
		return NumberFormat.getNumberInstance(Locale.US).format(num);
	}

	public static int read4(@NonNull byte[] buf, int offset) {
		int rc = 0;
		rc |= (buf[offset++] & 0xff) << 24;
		rc |= (buf[offset++] & 0xff) << 16;
		rc |= (buf[offset++] & 0xff) << 8;
		rc |= (buf[offset  ] & 0xff);
		return rc;
	}

	public static long read8(@NonNull byte[] buf, int offset) {
		long rc = 0;
		rc |= (long)(buf[offset++] & 0xff) << 56;
		rc |= (long)(buf[offset++] & 0xff) << 48;
		rc |= (long)(buf[offset++] & 0xff) << 40;
		rc |= (long)(buf[offset++] & 0xff) << 32;
		rc |= (long)(buf[offset++] & 0xff) << 24;
		rc |= (long)(buf[offset++] & 0xff) << 16;
		rc |= (long)(buf[offset++] & 0xff) << 8;
		rc |= (long)(buf[offset  ] & 0xff);
		return rc;
	}

	public static int read2(@NonNull byte[] buf, int offset) {
		int rc = 0;
		rc |= (buf[offset++] & 0xff) << 8;
		rc |= (buf[offset  ] & 0xff);
		return rc;
	}

	public static int read2le(@NonNull byte[] buf, int offset) {
		int rc = 0;
		rc |= (buf[offset++] & 0xff);
		rc |= (buf[offset  ] & 0xff) << 8;
		return rc;
	}

	public static int read4le(@NonNull byte[] buf, int offset) {
		int rc = 0;
		rc |= (buf[offset++] & 0xff);
		rc |= (buf[offset++] & 0xff) << 8;
		rc |= (buf[offset++] & 0xff) << 16;
		rc |= (buf[offset  ] & 0xff) << 24;
		return rc;
	}

	public static long read8le(@NonNull byte[] buf, int offset) {
		long rc = 0;
		rc |= (long)(buf[offset++] & 0xff);
		rc |= (long)(buf[offset++] & 0xff) << 8;
		rc |= (long)(buf[offset++] & 0xff) << 16;
		rc |= (long)(buf[offset++] & 0xff) << 24;
		rc |= (long)(buf[offset++] & 0xff) << 32;
		rc |= (long)(buf[offset++] & 0xff) << 40;
		rc |= (long)(buf[offset++] & 0xff) << 48;
		rc |= (long)(buf[offset  ] & 0xff) << 56;
		return rc;
	}

	public static int write8(@NonNull byte[] buf, int offset, long a) {
		buf[offset++] = (byte)(a >> 56);
		buf[offset++] = (byte)(a >> 48);
		buf[offset++] = (byte)(a >> 40);
		buf[offset++] = (byte)(a >> 32);
		buf[offset++] = (byte)(a >> 24);
		buf[offset++] = (byte)(a >> 16);
		buf[offset++] = (byte)(a >>  8);
		buf[offset++] = (byte) a;
		return offset;
	}

	public static int write4(@NonNull byte[] buf, int offset, int a) {
		buf[offset++] = (byte)(a >> 24);
		buf[offset++] = (byte)(a >> 16);
		buf[offset++] = (byte)(a >>  8);
		buf[offset++] = (byte) a;
		return offset;
	}

	public static int write2(@NonNull byte[] buf, int offset, int a) {
		buf[offset++] = (byte)(a >>  8);
		buf[offset++] = (byte) a;
		return offset;
	}

	public static int write8le(@NonNull byte[] buf, int offset, long a) {
		buf[offset++] = (byte) a;
		buf[offset++] = (byte)(a >>  8);
		buf[offset++] = (byte)(a >> 16);
		buf[offset++] = (byte)(a >> 24);
		buf[offset++] = (byte)(a >> 32);
		buf[offset++] = (byte)(a >> 40);
		buf[offset++] = (byte)(a >> 48);
		buf[offset++] = (byte)(a >> 56);
		return offset;
	}

	public static int write4le(@NonNull byte[] buf, int offset, int a) {
		buf[offset++] = (byte) a;
		buf[offset++] = (byte)(a >>  8);
		buf[offset++] = (byte)(a >> 16);
		buf[offset++] = (byte)(a >> 24);
		return offset;
	}

	public static int write2le(@NonNull byte[] buf, int offset, int a) {
		buf[offset++] = (byte) a;
		buf[offset++] = (byte)(a >>  8);
		return offset;
	}

	@NonNull
	public static String repeat(@NonNull String s, int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(s);
		}
		return sb.toString();
	}

	public static void repeat(@NonNull StringBuilder sb, @NonNull String s, int count) {
		for (int i = 0; i < count; i++) {
			sb.append(s);
		}
	}

	public static void repeat(@NonNull StringBuilder sb, char c, int count) {
		for (int i = 0; i < count; i++) {
			sb.append(c);
		}
	}

	public static long createUidHash() {
		UUID uuid = UUID.randomUUID();
		long rc = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();
		if (rc == 0 || rc == -1 || rc == 1) {
			rc = createUidHash();
		}
		return rc;
	}

	@NonNull
	public static String createUidHexString() {
		UUID uuid = UUID.randomUUID();
		return String.format(
			Locale.US,
			"%016X%016X",
			uuid.getMostSignificantBits(),
			uuid.getLeastSignificantBits()
		);
	}

	@NonNull
	public static float[] byte2float(@NonNull byte[] b) {
		ByteBuffer bb = ByteBuffer.wrap(b);
		FloatBuffer fb = bb.asFloatBuffer();
		float[] rc = new float[fb.limit()];
		fb.get(rc);
		return rc;
	}

	@NonNull
	public static float[] byte2floatLe(@NonNull byte[] b) {
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb = bb.order(ByteOrder.LITTLE_ENDIAN);
		FloatBuffer fb = bb.asFloatBuffer();
		float[] rc = new float[fb.limit()];
		fb.get(rc);
		return rc;
	}

	@NonNull
	public static float[] byte2floatLe(@NonNull byte[] b, int offset, int len) {
		ByteBuffer bb = ByteBuffer.wrap(b, offset, len);
		bb = bb.order(ByteOrder.LITTLE_ENDIAN);
		FloatBuffer fb = bb.asFloatBuffer();
		float[] rc = new float[fb.limit()];
		fb.get(rc);
		return rc;
	}

	@NonNull
	public static byte[] float2byte(@NonNull float[] f) {
		ByteBuffer bb = ByteBuffer.allocate(4 * f.length);
		bb.asFloatBuffer().put(f);
		return bb.array();
	}

	@NonNull
	public static byte[] float2byte(float f) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.asFloatBuffer().put(f);
		return bb.array();
	}

	@NonNull
	public static double[] byte2double(@NonNull byte[] b) {
		ByteBuffer bb = ByteBuffer.wrap(b);
		DoubleBuffer db = bb.asDoubleBuffer();
		double[] rc = new double[db.limit()];
		db.get(rc);
		return rc;
	}

	@NonNull
	public static byte[] double2byte(@NonNull double[] d) {
		ByteBuffer bb = ByteBuffer.allocate(8 * d.length);
		bb.asDoubleBuffer().put(d);
		return bb.array();
	}

	@NonNull
	public static byte[] double2byte(double d) {
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.asDoubleBuffer().put(d);
		return bb.array();
	}

	@NonNull
	public static int[] byte2int(@NonNull byte[] b) {
		ByteBuffer bb = ByteBuffer.wrap(b);
		IntBuffer db = bb.asIntBuffer();
		int[] rc = new int[db.limit()];
		db.get(rc);
		return rc;
	}

	@NonNull
	public static int[] byte2intLe(@NonNull byte[] b) {
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb = bb.order(ByteOrder.LITTLE_ENDIAN);
		IntBuffer db = bb.asIntBuffer();
		int[] rc = new int[db.limit()];
		db.get(rc);
		return rc;
	}

	@NonNull
	public static int[] byte2intLe(@NonNull byte[] b, int offset, int len) {
		ByteBuffer bb = ByteBuffer.wrap(b, offset, len);
		bb = bb.order(ByteOrder.LITTLE_ENDIAN);
		IntBuffer db = bb.asIntBuffer();
		int[] rc = new int[db.limit()];
		db.get(rc);
		return rc;
	}

	@NonNull
	public static byte[] int2byte(@NonNull int[] ia) {
		ByteBuffer bb = ByteBuffer.allocate(4 * ia.length);
		bb.asIntBuffer().put(ia);
		return bb.array();
	}

	@NonNull
	public static short[] byte2short(@NonNull byte[] b) {
		ByteBuffer bb = ByteBuffer.wrap(b);
		ShortBuffer db = bb.asShortBuffer();
		short[] rc = new short[db.limit()];
		db.get(rc);
		return rc;
	}

	@NonNull
	public static short[] byte2shortLe(@NonNull byte[] b) {
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb = bb.order(ByteOrder.LITTLE_ENDIAN);
		ShortBuffer db = bb.asShortBuffer();
		short[] rc = new short[db.limit()];
		db.get(rc);
		return rc;
	}

	@NonNull
	public static short[] byte2shortLe(@NonNull byte[] b, int offset, int len) {
		ByteBuffer bb = ByteBuffer.wrap(b, offset, len);
		bb = bb.order(ByteOrder.LITTLE_ENDIAN);
		ShortBuffer db = bb.asShortBuffer();
		short[] rc = new short[db.limit()];
		db.get(rc);
		return rc;
	}

	@NonNull
	public static byte[] short2byte(@NonNull short[] sa) {
		ByteBuffer bb = ByteBuffer.allocate(2 * sa.length);
		bb.asShortBuffer().put(sa);
		return bb.array();
	}

	@Deprecated	// use ImageUtils
	@NonNull
	public static Bitmap createBitmap(@NonNull Drawable d, int w, int h, int colorBg) {
		Bitmap rc = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		rc.eraseColor(colorBg);
		Canvas canvas = new Canvas(rc);
		d.setBounds(0, 0, w, h);
		d.draw(canvas);
		return rc;
	}

	@NonNull
	public static Looper createWorkerLooper(@NonNull Executor executor) {
		ObjectReference<Looper> ref = new ObjectReference<>();
		ConditionVariable gate = new ConditionVariable();
		executor.execute(() -> {
			Looper.prepare();
			ref.set(Looper.myLooper());
			gate.open();
			Looper.loop();
		});
		gate.block();
		return ref.get();
	}

	@NonNull
	public static Looper createNewThreadLooper() {
		ObjectReference<Looper> ref = new ObjectReference<>();
		ConditionVariable gate = new ConditionVariable();
		new Thread(() -> {
			Looper.prepare();
			ref.set(Looper.myLooper());
			gate.open();
			Looper.loop();
		}).start();
		gate.block();
		return ref.get();
	}

	@NonNull
	public static long[] toLongArray(@NonNull List<Long> list) {
		int n = list.size();
		long[] rc = new long[n];
		for (int i = 0; i < n; i++) {
			rc[i] = list.get(i);
		}
		return rc;
	}

	@NonNull
	public static int[] toIntArray(@NonNull List<Integer> list) {
		int n = list.size();
		int[] rc = new int[n];
		for (int i = 0; i < n; i++) {
			rc[i] = list.get(i);
		}
		return rc;
	}

	@NonNull
	public static String[] toStringArray(@NonNull List<String> list) {
		int n = list.size();
		String[] rc = new String[n];
		for (int i = 0; i < n; i++) {
			rc[i] = list.get(i);
		}
		return rc;
	}

	@NonNull
	public static String full2half(@NonNull String full) {
		StringBuilder sb = new StringBuilder(full);
		int len = full.length();
		for (int i = 0; i < len; i++) {
			int c = sb.charAt(i);
			if (0xFF00 <= c && c <= 0xFF5E) {
				sb.setCharAt(i, (char)(c - 0xFEE0));
			} else if (c == 0x3000) {
				sb.setCharAt(i, ' ');
			}
		}
		return sb.toString();
	}

	@NonNull
	public static Display getDefaultDisplay(@NonNull Activity activity) {
		DisplayManager manDisplay = (DisplayManager)activity.getSystemService(Context.DISPLAY_SERVICE);
		return manDisplay.getDisplay(Display.DEFAULT_DISPLAY);
	}

	/**
	 * create Locale. language is Locale.toString() value.
	 */
	@NonNull
	public static Locale createLocale(@NonNull String language) {
		String[] sp = language.split("_");
		if (sp.length == 1) {
			return new Locale(sp[0]);
		} else if (sp.length == 2) {
			return new Locale(sp[0], sp[1]);
		} else {
			return new Locale(sp[0], sp[1], sp[2]);
		}
	}

	public static void kickGoogleTranslateWeb(
		@NonNull Context context,
		@NonNull String text,
		@NonNull String langText,
		@NonNull String langTarget
	) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		Uri uri = new Uri.Builder()
			.scheme("https")
			.authority("translate.google.com")
			.appendQueryParameter("q", text)
			.appendQueryParameter("sl", langText)
			.appendQueryParameter("tl", langTarget)
			.build();
		intent.setData(uri);
		context.startActivity(intent);
	}

	@NonNull
	public static PackageInfo getMyPackageInfo(
		@NonNull Context context
	) throws PackageManager.NameNotFoundException {
		return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	}

	@NonNull
	public static String replaceSuffix(@Nullable String fname, @NonNull String suffix) {
		String rc;
		if (fname == null) {
			rc = getPresentTimeString() + suffix;
		} else {
			int idx = fname.lastIndexOf('.');
			if (0 < idx) {
				rc = fname.substring(0, idx) + suffix;
			} else {
				rc = fname + suffix;
			}
		}
		return rc;
	}

	/**
	 * Parse ISO8601 time to msec time.
	 */
	public static long parseIso8601(@NonNull String time) {
		String sYear = time.substring(0, 4);
		String sMonth = time.substring(5, 7);
		String sDay = time.substring(8, 10);
		String sHour = time.substring(11, 13);
		String sMinute = time.substring(14, 16);
		String sSecond = time.substring(17, 19);
		char pmz = 'Z';
		String sZoneHour = "00";
		String sZoneMinute = "00";
		if (20 <= time.length()) {
			pmz = time.charAt(19);
			if (pmz == '+' || pmz == '-') {
				sZoneHour = time.substring(20, 22);
				sZoneMinute = time.substring(23, 25);
			}
		}
		int yy = Integer.parseInt(sYear);
		int mm = Integer.parseInt(sMonth);
		int dd = Integer.parseInt(sDay);
		int hh = Integer.parseInt(sHour);
		int mmm = Integer.parseInt(sMinute);
		int ss = Integer.parseInt(sSecond);
		int zoneHour = Integer.parseInt(sZoneHour);
		int zoneMinute = Integer.parseInt(sZoneMinute);
		int offset = (zoneHour * 3600 + zoneMinute * 60) * 1000;
		if (pmz == '-') {
			offset = -offset;
		}
		TimeZone zone = new SimpleTimeZone(offset, "ID");
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeZone(zone);
		cal.set(yy, mm - 1, dd, hh, mmm, ss);
		return cal.getTimeInMillis();
	}

	/**
	 * Parse a CSV line.
	 */
	@NonNull
	public static List<String> parseCsvLine(@NonNull String line) {
		List<String> rc = new ArrayList<>();
		int state = 0;
		int len = line.length();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			char c = line.charAt(i);
			switch (state) {
			case 0:
				if (c == '"') {
					state = 1;
				} else {
					sb.append(c);
					state = 2;
				}
				break;
			case 1:		// wait "
				if (c == '"') {
					state = 3;
				} else {
					sb.append(c);
				}
				break;
			case 2:		// wait ,
				if (c == ',') {
					rc.add(sb.toString());
					sb.setLength(0);
					state = 0;
				} else {
					sb.append(c);
				}
				break;
			case 3:		// end check
				if (c == ',') {
					rc.add(sb.toString());
					sb.setLength(0);
					state = 0;
				} else {
					sb.append('"');
					sb.append(c);
					state = 1;
				}
				break;
			}
		}
		if (state == 2 || state == 3) {
			rc.add(sb.toString());
		}
		return rc;
	}

	@NonNull
	public static String createCsvLine(@NonNull List<String> items) {
		StringBuilder sb = new StringBuilder();
		boolean atFirst = true;
		for (String item : items) {
			String s = item.replace("\"", "\"\"");
			if (atFirst) {
				atFirst = false;
			} else {
				sb.append(',');
			}
			sb.append('"');
			sb.append(s);
			sb.append('"');
		}
		return sb.toString();
	}

	private static final Runnable nopRunnable = () -> {};

	@NonNull
	public static Runnable getRunnableNop() {
		return nopRunnable;
	}

	public static double calcLatitudeDegreeByMeter(double latitude, double longitude, float meter) {
		float[] result = new float[1];
		double d = 0.0001;
		double latitude1 = latitude + d;
		Location.distanceBetween(latitude, longitude, latitude1, longitude, result);
		return meter / result[0] * d;
	}

	public static double calcLongitudeDegreeByMeter(double latitude, double longitude, float meter) {
		float[] result = new float[1];
		double d = 0.0001;
		double longitude1 = longitude + d;
		Location.distanceBetween(latitude, longitude, latitude, longitude1, result);
		return meter / result[0] * d;
	}

	public static int calcDirectionIndex(float degree, int nStep) {
		float stepDegree = 360f / nStep;
		return (int)((degree + 360 + stepDegree / 2) / stepDegree) % nStep;
	}

	@NonNull
	public static String getPrefix(@NonNull String fname) {
		String rc;
		int idx = fname.lastIndexOf('.');
		if (0 < idx) {
			rc = fname.substring(0, idx);
		} else {
			rc = fname;
		}
		return rc;
	}

	@WorkerThread
	@Nullable
	public static String getPrefectureAndCityNameFromLocation(
		@NonNull Context context,
		double latitude,
		double longitude
	) {
		String rc = null;
		Geocoder corder = new Geocoder(context);
		try {
			List<Address> list = corder.getFromLocation(latitude, longitude, 1);
			if (list != null && !list.isEmpty()) {
				Address address = list.get(0);
				rc = address.getAdminArea();
				String city = address.getLocality();
				if (!TextUtils.isEmpty(city)) {
					char c = city.charAt(0);
					if ('A' <= c && c <= 'z') {		// TODO: add Euro word
						rc += ' ';
					}
					rc += city;
				}
			}
		} catch (Exception e) {
			// nop
		}
		return rc;
	}

	public static int findIndex(@NonNull String[] array, @NonNull String s) {
		for (int i = 0; i < array.length; i++) {
			if (s.contentEquals(array[i])) {
				return i;
			}
		}
		return -1;
	}

	private static void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
