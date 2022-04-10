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
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;
import android.util.Base64;
import android.widget.ImageView;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.ColorUtils;
import androidx.exifinterface.media.ExifInterface;

import app.misono.unit206.R;
import app.misono.unit206.admob.AdMobUtils;
import app.misono.unit206.debug.Log2;
import app.misono.unit206.page.Page;
import app.misono.unit206.task.ObjectReference;
import app.misono.unit206.task.Taskz;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageUtils {
	private static final String TAG = "ImageUtils";

	@NonNull
	public static Task<Bitmap> setImageTask(
		@NonNull Executor executor,
		@NonNull ImageView image,
		@NonNull byte[] data
	) {
		return Taskz.call(executor, () -> {
			Bitmap bitmap = createBitmapResizeRotate(data, 0);
			Tasks.await(Taskz.call(() -> {
				image.setImageBitmap(bitmap);
				return null;
			}));
			return bitmap;
		});
	}

	@AnyThread
	@NonNull
	public static Task<Bitmap> setImageTask(
		@NonNull Executor executor,
		@NonNull ImageView image,
		@NonNull Uri uri
	) {
		return Taskz.call(executor, () -> {
			Bitmap bitmap = readBitmapResizeRotate(image.getContext(), uri, 0);
			Task<Void> task = Taskz.call(() -> {
				image.setImageBitmap(bitmap);
				return null;
			});
			Tasks.await(task);
			return bitmap;
		});
	}

	@AnyThread
	@NonNull
	public static Task<Bitmap> setAssetImageTask(
		@NonNull Executor executor,
		@NonNull ImageView image,
		@NonNull String pathAsset
	) {
		return Taskz.call(executor, () -> {
			byte[] b = Utils.readAssetFileWithException(image.getContext(), pathAsset);
			return Tasks.await(setImageTask(executor, image, b));
		});
	}

	@NonNull
	public static AppCompatImageView createFixedInsideImageView(@NonNull Activity activity) {
		AppCompatImageView rc = new AppCompatImageView(activity);
		rc.setAdjustViewBounds(true);
		rc.setScaleType(ImageView.ScaleType.FIT_CENTER);
		return rc;
	}

	/**
	 * Deprecated.
	 * use Grayscale class instead.
	 */
	@WorkerThread
	@NonNull
	@Deprecated
	public static Bitmap toGrayscale(@NonNull Bitmap bitmap) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap gray = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(gray);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bitmap, 0, 0, paint);
		return gray;
	}

	@WorkerThread
	@NonNull
	public static byte[] toByteArray(
		@NonNull Bitmap bitmap,
		@NonNull Bitmap.CompressFormat format,
		int quality
	) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(format, quality, os);
		return os.toByteArray();
	}

	@WorkerThread
	@NonNull
	public static Bitmap createBitmap(@NonNull RenderScript rs, @NonNull Allocation in,
			int x, int y, int w, int h) {

		Type type = new Type.Builder(rs, Element.U8_4(rs)).setX(w).setY(h).create();
		Allocation argb = Allocation.createTyped(rs, type);
		argb.copy2DRangeFrom(0, 0, w, h, in, x, y);
		Bitmap rc = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		argb.copyTo(rc);
		return rc;
	}

	@WorkerThread
	public static void copyToBitmap(@NonNull RenderScript rs, @NonNull Allocation in,
			int x, int y, @NonNull Bitmap bitmap) {

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Type type = new Type.Builder(rs, Element.U8_4(rs)).setX(w).setY(h).create();
		Allocation argb = Allocation.createTyped(rs, type);
		argb.copy2DRangeFrom(0, 0, w, h, in, x, y);
		argb.copyTo(bitmap);
	}

	public static int getImageRotateDegree(@NonNull Context context, @NonNull Uri uri) {
		int rotateDegree = 0;
		InputStream is = null;
		try {
			is = context.getContentResolver().openInputStream(uri);
			if (is != null) {
				is = new BufferedInputStream(is);
				rotateDegree = getImageRotateDegree(is);
			}
		} catch (IOException | RuntimeException e) {
			// RuntimeException if HEIF.
		} finally {
			Utils.closeSafely(is);
		}
		return rotateDegree;
	}

	public static int getImageRotateDegree(@NonNull byte[] image) {
		int rotateDegree = 0;
		ByteArrayInputStream is = new ByteArrayInputStream(image);
		try {
			rotateDegree = getImageRotateDegree(is);
		} catch (IOException | RuntimeException e) {
			// RuntimeException if HEIF.
		} finally {
			Utils.closeSafely(is);
		}
		return rotateDegree;
	}

	private static int getImageRotateDegree(@NonNull InputStream is) throws IOException {
		int rotateDegree = 0;
		ExifInterface exif = new ExifInterface(is);
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
		switch (orientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			rotateDegree = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			rotateDegree = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			rotateDegree = 270;
			break;
		}
		return rotateDegree;
	}

	@AnyThread
	@NonNull
	public static Task<Bitmap> readBitmapResizeRotateTask(
		@NonNull Executor executor,
		@NonNull Context context,
		@NonNull Uri uri,
		int pixel
	) {
		return Taskz.call(executor, () -> {
			return readBitmapResizeRotate(context, uri, pixel);
		});
	}

	@WorkerThread
	@Nullable
	public static Bitmap readBitmapResizeRotate(@NonNull Context context, @NonNull Uri uri, int pixel) {
		byte[] binary = Utils.readBytes(context, uri);
		Bitmap rc = null;
		if (binary != null) {
			int rotateDegree = getImageRotateDegree(context, uri);
			rc = decodeBitmapResizeRotate(binary, rotateDegree, pixel);
		}
		return rc;
	}

	@AnyThread
	@NonNull
	public static Task<Bitmap> createBitmapResizeRotateTask(
		@NonNull Executor executor,
		@NonNull byte[] image,
		int pixel
	) {
		return Taskz.call(executor, () -> {
			return createBitmapResizeRotate(image, pixel);
		});
	}

	@WorkerThread
	@Nullable
	public static Bitmap createBitmapResizeRotate(@NonNull byte[] image, int pixel) {
		int rotateDegree = getImageRotateDegree(image);
		return decodeBitmapResizeRotate(image, rotateDegree, pixel);
	}

	@WorkerThread
	@Nullable
	private static Bitmap decodeBitmapResizeRotate(@NonNull byte[] image, int rotateDegree, int pixel) {
		return decodeBitmapResizeRotate(image, rotateDegree, pixel, true);
	}

	@WorkerThread
	@Nullable
	private static Bitmap decodeBitmapResizeRotate(
		@NonNull byte[] image,
		int rotateDegree,
		int pixel,
		boolean filter
	) {
		Bitmap rc = null;
		Bitmap bitmap = decodeImageBestEffort(image);
		if (bitmap != null) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			float width, height;
			if (rotateDegree == 0 || rotateDegree == 180) {
				width = w;
				height = h;
			} else {
				width = h;
				height = w;
			}

			float scale;
			if (height < width) {
				scale = (float)pixel / width;
			} else {
				scale = (float)pixel / height;
			}
			if (1 <= scale || pixel == 0) {
				scale = 1;
			}
			Matrix matrix = new Matrix();
			matrix.setRotate(rotateDegree, (float)w / 2, (float)h / 2);
			matrix.postScale(scale, scale);

			rc = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, filter);
		}
		return rc;
	}

	@WorkerThread
	@Nullable
	public static Bitmap decodeImageBestEffort(@NonNull byte[] binary) {
		Bitmap bitmap = null;
		for (int i = 0; i < 4; i++) { // try max 4 times...
			try {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = 1 << i;
				bitmap = BitmapFactory.decodeByteArray(binary, 0, binary.length, opts);
				break;
			} catch (OutOfMemoryError e) {
				log("decodeImageBestEffort:retry:" + i);
			}
		}
		return bitmap;
	}

	@Nullable
	public static Bitmap decodeBitmapWithRotate(@NonNull byte[] image) {
		Bitmap rc = null;
		Bitmap bitmap = decodeImageBestEffort(image);
		if (bitmap != null) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			int rotateDegree = getImageRotateDegree(image);
			Matrix matrix = new Matrix();
			matrix.setRotate(rotateDegree, (float)w / 2, (float)h / 2);

			rc = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		}
		return rc;
	}

	public interface ExecuteBitmap {
		@WorkerThread
		boolean run(@NonNull Bitmap bitmap) throws Exception;
	}

	@MainThread
	@NonNull
	public static Task<Bitmap> loadBitmapUriWithAdMobTask(
		@NonNull Page page,
		@NonNull Uri uri,
		@NonNull String idInterstitial,
		boolean showSnackResult,
		@Nullable ExecuteBitmap exec
	) {
		ExecutorService executor = Executors.newCachedThreadPool();
		ObjectReference<Snackbar> refSnack = new ObjectReference<>();
		ObjectReference<Boolean> refSuccess = new ObjectReference<>();
		Snackbar snack = page.showSnackProgress(R.string.progress_doing, R.string.action_cancel, cancel -> {
			executor.shutdownNow();
			refSnack.get().dismiss();
			page.showSnackbar(R.string.progress_canceled);
		});
		refSnack.set(snack);
		ThreadGate gate = new ThreadGate();
		Context context = page.getContext();
		AdMobUtils.showInterstitial(context, idInterstitial, gate::open);
		return Taskz.call(executor, () -> {
			Bitmap bitmap;
			try {
				bitmap = readBitmapResizeRotate(context, uri, 0);
				boolean success = bitmap != null;
				refSuccess.set(success);
				if (success) {
					if (exec != null) {
						refSuccess.set(exec.run(bitmap));
					}
				}
			} finally {
				gate.block();
			}
			return bitmap;
		}).addOnCompleteListener(task -> {
			snack.dismiss();
			if (showSnackResult) {
				Boolean success = refSuccess.get();
				if (success != null) {
					if (success) {
						page.showSnackbar(R.string.progress_loaded);
					} else {
						page.showSnackbar(R.string.progress_load_error);
					}
				}
			}
		});
	}

	public static int getLight(int argb, int colorLayerDown) {
		int a = (argb >> 24) & 0xff;
		int r = (argb >> 16) & 0xff;
		int g = (argb >> 8) & 0xff;
		int b = argb & 0xff;
		int v0 = (r + g + b) / 3 * a / 255;
		int rd = (colorLayerDown >> 16) & 0xff;
		int gd = (colorLayerDown >> 8) & 0xff;
		int bd = colorLayerDown & 0xff;
		int v1 = (rd + gd + bd) / 3 * (255 - a) / 255;
		return v0 + v1;
	}

	@NonNull
	public static byte[] createJpeg(@NonNull Bitmap bitmap, int quality) {
		return toByteArray(bitmap, Bitmap.CompressFormat.JPEG, quality);
	}

	public static String base64JpegForImageTag(@NonNull Bitmap bitmap) {
		byte[] image = createJpeg(bitmap, 90);
		String b64 = Base64.encodeToString(image, Base64.NO_WRAP);
		return "data:image/jpeg;base64," + b64;
	}

	@NonNull
	public static byte[] createPng(@NonNull Bitmap bitmap) {
		return toByteArray(bitmap, Bitmap.CompressFormat.PNG, 100);
	}

	public static String base64PngForImageTag(@NonNull Bitmap bitmap) {
		byte[] image = toByteArray(bitmap, Bitmap.CompressFormat.PNG, 100);
		String b64 = Base64.encodeToString(image, Base64.NO_WRAP);
		return "data:image/png;base64," + b64;
	}

	@WorkerThread
	@NonNull
	public static float[] calcHslCirclePosition(int sizePixel, int color) {
		float r = sizePixel / 2f;
		float[] hsl = new float[3];
		ColorUtils.colorToHSL(color, hsl);
		float hue = hsl[0];			// Hue [0..360)
		double rad = 2 * Math.PI * hue / 360;
		float saturation = hsl[1];	// Saturation [0...1]
		float rs = r * saturation;
		float[] rc = new float[5];
		rc[0] = (float)(rs * Math.cos(rad) + r);
		rc[1] = (float)(rs * Math.sin(rad) + r);
		rc[2] = hue;
		rc[3] = saturation;
		rc[4] = hsl[2];
		return rc;
	}

	@WorkerThread
	@NonNull
	public static Bitmap createHslCircle(int sizePixel, boolean centerWhite) {
		Bitmap rc = Bitmap.createBitmap(sizePixel, sizePixel, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(rc);
		int r = sizePixel / 2;
		int r2 = r * r;
		float[] hsl = new float[3];
		hsl[1] = 1.0f;
		hsl[2] = 0.5f;
		Paint paint = new Paint();
		paint.setStrokeWidth(1f);
		for (int y = 0; y < sizePixel; y++) {
			int y1 = y - r;
			for (int x = 0; x < sizePixel; x++) {
				int x1 = x - r;
				int d2 = x1 * x1 + y1 * y1;
				if (d2 < r2) {
					float deg = (float)(Math.atan((double)y1 / (double)x1) * 180 / Math.PI);
					if (x1 < 0) {
						deg += 180;
					}
					if (deg < 0) {
						deg += 360;
					}
					if (360 <= deg) {
						deg -= 360;
					}
					hsl[0] = deg;
					float d = (float)Math.sqrt(d2);
					if (centerWhite) {
						hsl[2] = 0.5f + (1 - d / r) / 2;
					} else {
						hsl[1] = d / r;
					}
					int color = ColorUtils.HSLToColor(hsl);
					paint.setColor(color);
					canvas.drawPoint(x, y, paint);
				}
			}
		}
		return rc;
	}

	public static void destroySafely(@Nullable RenderScript rs) {
		if (rs != null) {
			try {
				rs.destroy();
			} catch (Exception e) {
				// nop
			}
		}
	}

	/**
	 * bitmapの指定ブロック中の平均色を求める.
	 * @return average color code
	 */
	public static int averageColor(
		@NonNull Bitmap bitmap,
		int x1,
		int y1,
		int w1,
		int h1
	) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int r = 0;
		int g = 0;
		int b = 0;
		int cc = 0;
		for (int dy = 0; dy < h1; dy++) {
			int y = y1 + dy;
			if (y < h) {
				for (int dx = 0; dx < w1; dx++) {
					int x = x1 + dx;
					if (x < w) {
						int c = bitmap.getPixel(x, y);	// c is ARGB.
						r += (c >> 16) & 0xff;
						g += (c >> 8) & 0xff;
						b +=  c & 0xff;
						cc++;
					}
				}
			}
		}
		r /= cc;
		g /= cc;
		b /= cc;
		return 0xff000000 | (r << 16) | (g << 8) | b;
	}

	public static void destroySafely(@Nullable Allocation alloc) {
		if (alloc != null) {
			try {
				alloc.destroy();
			} catch (Exception e) {
				// nop
			}
		}
	}

	private static void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
