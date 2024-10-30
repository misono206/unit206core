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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;
import android.util.Base64;
import android.widget.ImageView;

import androidx.annotation.AnyThread;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;
import androidx.exifinterface.media.ExifInterface;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.task.Taskz;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

public class ImageUtils {
	private static final String TAG = "ImageUtils";

	@NonNull
	public static Task<Bitmap> setImageTask(
		@NonNull Executor executor,
		@NonNull ImageView image,
		@NonNull byte[] data
	) {
		return Taskz.call(executor, () -> {
			Bitmap bitmap = createBitmapResizeRotate(data, 0, false);
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
			Bitmap bitmap = readBitmapResizeRotate(image.getContext(), uri, 0, false);
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

	@WorkerThread
	public static void setImageAsync(
		@NonNull ImageView image,
		@NonNull byte[] data,
		@Nullable Runnable done
	) {
		Bitmap bitmap = createBitmapResizeRotate(data, 0, false);
		Taskz.call(() -> {
			image.setImageBitmap(bitmap);
			if (done != null) {
				done.run();
			}
			return null;
		}).addOnFailureListener(Taskz::printStackTrace2);
	}

	@NonNull
	public static AppCompatImageView createFixedInsideImageView(@NonNull Context context) {
		AppCompatImageView rc = new AppCompatImageView(context);
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
	public static Bitmap createBitmap(
		@NonNull RenderScript rs,
		@NonNull Allocation in,
		int x,
		int y,
		int w,
		int h
	) {
		Type type = new Type.Builder(rs, Element.U8_4(rs)).setX(w).setY(h).create();
		Allocation argb = Allocation.createTyped(rs, type);
		argb.copy2DRangeFrom(0, 0, w, h, in, x, y);
		Bitmap rc = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		argb.copyTo(rc);
		return rc;
	}

	@WorkerThread
	public static void copyToBitmap(
		@NonNull RenderScript rs,
		@NonNull Allocation in,
		int x,
		int y,
		@NonNull Bitmap bitmap
	) {
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
		int pixel,
		boolean mutable
	) {
		return Taskz.call(executor, () -> {
			return readBitmapResizeRotate(context, uri, pixel, mutable);
		});
	}

	@WorkerThread
	@Nullable
	public static Bitmap readBitmapResizeRotate(
		@NonNull Context context,
		@NonNull Uri uri,
		int pixel,
		boolean mutable
	) {
		byte[] binary = Utils.readBytes(context, uri);
		Bitmap rc = null;
		if (binary != null) {
			int rotateDegree = getImageRotateDegree(context, uri);
			rc = decodeBitmapResizeRotate(binary, rotateDegree, mutable, pixel);
		}
		return rc;
	}

	@AnyThread
	@NonNull
	public static Task<Bitmap> createBitmapResizeRotateTask(
		@NonNull Executor executor,
		@NonNull byte[] image,
		int pixel,
		boolean mutable
	) {
		return Taskz.call(executor, () -> {
			return createBitmapResizeRotate(image, pixel, mutable);
		});
	}

	@WorkerThread
	@Nullable
	public static Bitmap createBitmapResizeRotate(@NonNull byte[] image, int pixel, boolean mutable) {
		int rotateDegree = getImageRotateDegree(image);
		return decodeBitmapResizeRotate(image, rotateDegree, mutable, pixel);
	}

	@WorkerThread
	@Nullable
	private static Bitmap decodeBitmapResizeRotate(
		@NonNull byte[] image,
		int rotateDegree,
		boolean mutable,
		int pixel
	) {
		return decodeBitmapResizeRotate(image, rotateDegree, pixel, true, mutable);
	}

	@WorkerThread
	@Nullable
	private static Bitmap decodeBitmapResizeRotate(
		@NonNull byte[] image,
		int rotateDegree,
		int pixel,
		boolean filter,
		boolean mutable
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
			if (mutable) {
				rc = toMutable(rc);
			}
		}
		return rc;
	}

	@NonNull
	public static Bitmap toMutable(@NonNull Bitmap bitmap) {
		if (!bitmap.isMutable()) {
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		}
		return bitmap;
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

	@Deprecated		// TODO Callback型にする
	public interface ExecuteBitmap {
		@WorkerThread
		boolean run(@NonNull Bitmap bitmap) throws Exception;
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

	public static String base64JpegForImageTag(@NonNull Bitmap bitmap, int quality) {
		byte[] image = createJpeg(bitmap, quality);
		String b64 = Base64.encodeToString(image, Base64.NO_WRAP);
		return "data:image/jpeg;base64," + b64;
	}

	@Deprecated		// use above method (with int quality)
	public static String base64JpegForImageTag(@NonNull Bitmap bitmap) {
		return base64JpegForImageTag(bitmap, 90);
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

	@NonNull
	public static byte[] createWebp(@NonNull Bitmap bitmap, int quality) {
		Bitmap.CompressFormat fmt;
		if (30 <= Build.VERSION.SDK_INT) {
			fmt = Bitmap.CompressFormat.WEBP_LOSSY;
		} else {
			fmt = Bitmap.CompressFormat.WEBP;
		}
		return toByteArray(bitmap, fmt, quality);
	}

	public static String base64WebpForImageTag(@NonNull Bitmap bitmap, int quality) {
		byte[] image = createWebp(bitmap, quality);
		return base64WebpForImageTag(image);
	}

	public static String base64WebpForImageTag(@NonNull byte[] image) {
		String b64 = Base64.encodeToString(image, Base64.NO_WRAP);
		return "data:image/webp;base64," + b64;
	}

	@NonNull
	public static byte[] createLosslessWebp(@NonNull Bitmap bitmap) {
		if (30 <= Build.VERSION.SDK_INT) {
			return toByteArray(bitmap, Bitmap.CompressFormat.WEBP_LOSSLESS, 100);
		} else {
			return createWebp(bitmap, 100);
		}
	}

	public static String base64LosslessWebpForImageTag(@NonNull Bitmap bitmap) {
		if (30 <= Build.VERSION.SDK_INT) {
			byte[] image = toByteArray(bitmap, Bitmap.CompressFormat.WEBP_LOSSLESS, 100);
			return base64WebpForImageTag(image);
		} else {
			return base64WebpForImageTag(bitmap, 100);
		}
	}

	public static String base64ForImageTag(
		@NonNull byte[] image,
		@NonNull Bitmap.CompressFormat fmt
	) {
		String b64 = Base64.encodeToString(image, Base64.NO_WRAP);
		String type;
		switch (fmt) {
		case JPEG:
			type = "jpeg";
			break;
		case PNG:
			type = "png";
			break;
		case WEBP_LOSSLESS:
		case WEBP:
		case WEBP_LOSSY:
			type = "webp";
			break;
		default:
			throw new RuntimeException("unknown format:" + fmt);
		}
		return "data:image/" + type + ";base64," + b64;
	}

	@Nullable
	public static String getImageSuffix(@NonNull Bitmap.CompressFormat format) {
		String rc = null;
		switch (format) {
		case JPEG:
			rc = "jpg";
			break;
		case PNG:
			rc = "png";
			break;
		case WEBP:
		case WEBP_LOSSLESS:
		case WEBP_LOSSY:
			rc = "webp";
			break;
		}
		return rc;
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

	@WorkerThread
	@Nullable
	public static Bitmap ppm2bitmap(@NonNull byte[] b) {
		String s;
		StringBuilder sb = new StringBuilder();
		int idx = 0;

		idx = readLine(b, idx, sb);
		s = sb.toString();
		if (!s.contentEquals("P6")) {
			log("ppm2bitmap:not P6:" + s);
			return null;
		}

		idx = readLine(b, idx, sb);
		s = sb.toString();
		String[] sp = s.split(" ");
		if (sp.length != 2) {
			log("ppm2bitmap:not 2 item:" + sp.length + ":" + s);
			return null;
		}
		int width = Integer.parseInt(sp[0]);
		int height = Integer.parseInt(sp[1]);

		idx = readLine(b, idx, sb);
		s = sb.toString();
		if (!s.contentEquals("255")) {
			log("ppm2bitmap:not 255:" + s);
			return null;
		}

		int[] colors = new int[width * height];
		int[] rgb = new int[3];
		int cc = 0;
		int total = 0;
		while (idx < b.length) {
			rgb[cc++] = b[idx++] & 0xff;
			if (cc == 3) {
				cc = 0;
				colors[total++] = Color.rgb(rgb[0], rgb[1], rgb[2]);
			}
		}
		return Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
	}

	private static int readLine(@NonNull byte[] b, int idx, @NonNull StringBuilder out) {
		out.setLength(0);
		while (idx < b.length) {
			char c = (char)b[idx++];
			if (c == '\n') {
				return idx;
			}
			out.append(c);
		}
		return idx;
	}

	@NonNull
	public static Bitmap leftright(@NonNull Bitmap src) {
		int w = src.getWidth();
		int h = src.getHeight();
		Matrix leftright = new Matrix();
		leftright.setScale(-1, 1);
		leftright.postTranslate(w, 0);
		return Bitmap.createBitmap(
			src,
			0,
			0,
			w,
			h,
			leftright,
			true
		);
	}

	public static void drawShadowText(
		@NonNull Canvas canvas,
		@NonNull String text,
		float x,
		float y,
		int offsetShadow,
		@NonNull Paint paint,
		int colorShadow
	) {
		int colorText = paint.getColor();
		paint.setColor(colorShadow);
		for (int dy = -offsetShadow; dy <= offsetShadow; dy++) {
			for (int dx = -offsetShadow; dx <= offsetShadow; dx++) {
				if (dx != 0 && dy != 0) {
					canvas.drawText(text, x + dx, y + dy, paint);
				}
			}
		}
		paint.setColor(colorText);
		canvas.drawText(text, x, y, paint);
	}

	@NonNull
	public static Bitmap createBitmap(@NonNull Drawable d, int w, int h, int colorBg) {
		Bitmap rc = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		rc.eraseColor(colorBg);
		Canvas canvas = new Canvas(rc);
		d.setBounds(0, 0, w, h);
		d.draw(canvas);
		return rc;
	}

	@Nullable
	public static Bitmap createBitmapIcon(
		@NonNull Context context,
		@DrawableRes int idDrawable,
		int pxSize,
		int colorBg
	) {
		Bitmap rc = null;
		Resources r = context.getResources();
		Drawable d = ResourcesCompat.getDrawable(r, idDrawable, null);
		if (d != null) {
			rc = createBitmap(d, pxSize, pxSize, colorBg);
		}
		return rc;
	}

	private static void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
