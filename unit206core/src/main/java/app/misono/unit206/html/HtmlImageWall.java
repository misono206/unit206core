/*
 * Copyright 2024 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.html;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

/**
 * サムネイル画像をまとめて1つの画像にして imgタグで表示できるようにするもの.
 * thread un-safe.
 */
public class HtmlImageWall implements Closeable {
	private final Map<String, HtmlImageTile> map;		// key: uid
	private final ImageWriter cbWriter;
	private final int wPixel1, hPixel1;
	private final int n;

	private Bitmap base;
	private Canvas canvas;
	private int noImage;
	private int cc;

	public interface ImageWriter {
		@WorkerThread
		void writeImage(@NonNull Bitmap bitmap, int noImage);
	}

	public HtmlImageWall(int wPixel1, int hPixel1, int n, boolean alwaysAppend, @NonNull ImageWriter cbWriter) {
		this.wPixel1 = wPixel1;
		this.hPixel1 = hPixel1;
		this.n = n;
		this.cbWriter = cbWriter;
		if (alwaysAppend) {
			map = null;
		} else {
			map = new HashMap<>();
		}
		createNewCanvas();
		noImage = 0;
		cc = 0;
	}

	private void createNewCanvas() {
		base = Bitmap.createBitmap(wPixel1 * n, hPixel1 * n, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(base);
	}

	@WorkerThread
	@NonNull
	public HtmlImageTile addTile(@Nullable String uid, @NonNull Bitmap bitmap) {
		int wPixel = bitmap.getWidth();
		int hPixel = bitmap.getHeight();
		int x = cc % n;
		int y = cc / n;
		int xPixel = wPixel1 * x;
		int yPixel = hPixel1 * y;
		canvas.drawBitmap(bitmap, xPixel, yPixel, null);
		HtmlImageTile rc = new HtmlImageTile(noImage, xPixel, yPixel, wPixel, hPixel);
		if (map != null || uid != null) {
			map.put(uid, rc);
		}
		if (n * n <= ++cc) {
			cbWriter.writeImage(base, noImage);
			createNewCanvas();
			cc = 0;
			noImage++;
		}
		return rc;
	}

	@Nullable
	public HtmlImageTile getTile(@NonNull String uid) {
		if (map == null) {
			throw new RuntimeException("'alwaysAppend' mode...");
		}
		return map.get(uid);
	}

	@Nullable
	public String createImageTag(
		@NonNull String uid,
		@Nullable String attrs,
		@NonNull HtmlImageTile.ImagePathCreator creator
	) {
		if (map == null) {
			throw new RuntimeException("'alwaysAppend' mode...");
		}
		HtmlImageTile tile = map.get(uid);
		if (tile == null) {
			return null;
		}
		return tile.createImageTag(attrs, creator);
	}

	@Override
	@WorkerThread
	public void close() {
		if (cc != 0) {
			cbWriter.writeImage(base, noImage);
			cc = 0;
		}
	}

}
