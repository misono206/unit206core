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

package app.misono.unit206.element.chart;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

public class ChartElement { // TODO: implements Element {
	private final Canvas mCanvas;

	public ChartElement(
		@NonNull ViewGroup parent,
		@NonNull ViewGroup.LayoutParams param,
		int width,
		int height
	) {
		Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(b);
		AppCompatImageView base = new AppCompatImageView(parent.getContext());
		parent.addView(base, param);
	}

	public void drawLineChart(
		@NonNull Rect rect,
		int color,
		float lineWidth,
		@NonNull float[] points,
		float minX,
		float maxX,
		float minY,
		float maxY
	) {
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStrokeWidth(lineWidth);
		float[] xy = new float[points.length];
		int wPixel = rect.width();
		int hPixel = rect.height();
		float w = maxX - minX;
		float h = maxY - minY;
		float scaleX = wPixel / w;
		float scaleY = hPixel / h;
		int xBase = rect.left;
		int yBase = rect.bottom;
		for (int i = 0; i < points.length; i += 2) {
			xy[i] = xBase + (points[i] - minX) * scaleX;
			xy[i + 1] = yBase - (points[i + 1] - minY) * scaleY;
		}
		mCanvas.drawLines(xy, paint);
	}

	// TODO: ing...
	public void drawBarChart(
		@NonNull Rect rect,
		int color,
		float lineWidth,
		@NonNull float[] points,
		float minX,
		float maxX,
		float minY,
		float maxY
	) {
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStrokeWidth(lineWidth);
		float[] xy = new float[points.length];
		int wPixel = rect.width();
		int hPixel = rect.height();
		float w = maxX - minX;
		float h = maxY - minY;
		float scaleX = wPixel / w;
		float scaleY = hPixel / h;
		int xBase = rect.left;
		int yBase = rect.bottom;
		for (int i = 0; i < points.length; i += 2) {
			xy[i] = xBase + (points[i] - minX) * scaleX;
			xy[i + 1] = yBase - (points[i + 1] - minY) * scaleY;
		}
		mCanvas.drawLines(xy, paint);
	}

}
