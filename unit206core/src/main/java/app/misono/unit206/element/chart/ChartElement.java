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

package app.misono.unit206.element.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.element.Element;
import app.misono.unit206.misc.TextMeasure;
import app.misono.unit206.misc.UnitPref;
import app.misono.unit206.misc.Utils;

import java.util.ArrayList;
import java.util.List;

public class ChartElement implements Element {
	private static final String TAG = "ChartElement";

	private final AppCompatImageView base;
	private final List<LineChart> listLine;
	private final List<Axis> listAxisX, listAxisY;
	private final List<Text> listText;

	private RectF axis, perRect;
	private int wView, hView;
	private int colorBg;

	public ChartElement(@NonNull Context context) {
		base = new AppCompatImageView(context);
		base.setAdjustViewBounds(true);
		base.setScaleType(ImageView.ScaleType.FIT_CENTER);
		listLine = new ArrayList<>();
		listAxisX = new ArrayList<>();
		listAxisY = new ArrayList<>();
		listText = new ArrayList<>();
		colorBg = Color.WHITE;
	}

	public void setBackgroundColor(int color) {
		colorBg = color;
	}

	public void clearLineChart() {
		listLine.clear();
	}

	public void clearAxis() {
		listAxisX.clear();
		listAxisY.clear();
	}

	public void clearText() {
		listText.clear();
	}

	public void clearAll() {
		clearAxis();
		clearLineChart();
		clearText();
	}

	@Deprecated
	public void addLineChart(
		int color,
		int pxStroke,
		@NonNull float[] points,
		@NonNull RectF axis,
		@NonNull RectF perRect
	) {
		listLine.add(new LineChart(color, pxStroke, points, axis, perRect));
	}

	public void addLineChart(
		int color,
		int pxStroke,
		@NonNull float[] points
	) {
		listLine.add(new LineChart(color, pxStroke, points, axis, perRect));
	}

	public void addText(@NonNull String text, float perX, float perY, float perSizeY, int color) {
		listText.add(new Text(text, perX, perY, perSizeY, color));
	}

	public void setAxis(@NonNull RectF axis, @NonNull RectF perRect) {
		this.axis = axis;
		this.perRect = perRect;
	}

	public void addAxisX(
		int color,
		int pxStroke,
		@Nullable List<ChartAxisText> listText,
		float yAxis
	) {
		listAxisX.add(new Axis(color, pxStroke, listText, yAxis));
	}

	public void addAxisX(
		int color,
		int pxStroke,
		float yAxis
	) {
		listAxisX.add(new Axis(color, pxStroke, null, yAxis));
	}

	public void addAxisY(
		int color,
		int pxStroke,
		@Nullable List<ChartAxisText> listText,
		float xAxis
	) {
		listAxisY.add(new Axis(color, pxStroke, listText, xAxis));
	}

	public void addAxisY(
		int color,
		int pxStroke,
		float xAxis
	) {
		listAxisY.add(new Axis(color, pxStroke, null, xAxis));
	}

	private void drawText(@NonNull Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		for (Text t : listText) {
			float x = wView * t.perX;
			float y = hView * t.perY;
			float size = hView * t.perSizeY;
			paint.setTextSize(size);
			paint.setColor(t.color);
			canvas.drawText(t.text, x, y, paint);
		}
	}

	private void drawAxis(@NonNull Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		float wPixel = wView * perRect.width();
		float hPixel = hView * perRect.height();
		float wAxis = axis.width();
		float hAxis = axis.height();
		float scaleX = wPixel / wAxis;
		float scaleY = hPixel / hAxis;
		float xBase0 = wView * perRect.left;
		float xBase1 = wView * perRect.right;
		float yBase0 = hView * perRect.top;
		float yBase1 = hView * perRect.bottom;
		for (Axis xAxis : listAxisX) {
			paint.setColor(xAxis.color);
			paint.setStrokeWidth(xAxis.pxStroke);
			float y = yBase0 + hPixel - (xAxis.axisOther - axis.top) * scaleY;
			canvas.drawLine(xBase0, y, xBase1, y, paint);
			List<ChartAxisText> listText = xAxis.listText;
			if (listText != null) {
				// TODO:
			}
		}
		for (Axis yAxis : listAxisY) {
			paint.setColor(yAxis.color);
			paint.setStrokeWidth(yAxis.pxStroke);
			float x = xBase0 + (yAxis.axisOther - axis.left) * scaleX;
			canvas.drawLine(x, yBase0, x, yBase1, paint);
			List<ChartAxisText> listText = yAxis.listText;
			if (listText != null) {
				for (ChartAxisText t : listText) {
					float y = yBase0 + hPixel - (t.getAxisPoint() - axis.top) * scaleY;
					float size = hView * t.getPerSizeY();
					paint.setTextSize(size);
					paint.setColor(t.getColor());
					String text = t.getText();
					TextMeasure m = Utils.measureTextBaseline(text, paint);
					canvas.drawText(text, x - m.getWidth() - yAxis.pxStroke, y + m.getCenterOffset(), paint);
				}
			}
		}
	}

	public void drawChart() {
		if (wView != 0 && hView != 0 && axis != null && perRect != null) {
			log("drawChart: wView:" + wView + " hView:" + hView);
			Bitmap bitmap = Bitmap.createBitmap(wView, hView, Bitmap.Config.ARGB_8888);
			bitmap.eraseColor(colorBg);
			Canvas canvas = new Canvas(bitmap);
			drawAxis(canvas);
			log("nListLine:" + listLine.size());
			for (LineChart line : listLine) {
				RectF perRect = line.perRect;
				float wPixel = wView * perRect.width();
				float hPixel = hView * perRect.height();
				float[] points = line.points;
				int n = points.length / 2;
				int xyLen = (n - 1) * 4;
				float[] xy = new float[xyLen];
				RectF axis = line.axis;
				float wAxis = axis.width();
				float hAxis = axis.height();
				float scaleX = wPixel / wAxis;
				float scaleY = hPixel / hAxis;
				float xBase = wView * perRect.left;
				float yBase = hView * perRect.top;
				int idx = 0;
				for (int i = 0; i < n; i++) {
					float x = xBase + (points[i * 2] - axis.left) * scaleX;
					float y = yBase + hPixel - (points[i * 2 + 1] - axis.top) * scaleY;
					xy[idx++] = x;
					xy[idx++] = y;
					if (i != 0 && idx < xyLen) {
						xy[idx++] = x;
						xy[idx++] = y;
					}
				}
				canvas.drawLines(xy, line.createPaint());
			}
			drawText(canvas);
			base.setImageBitmap(bitmap);
		}
	}

	// TODO: ing...
/*
	public void drawBarChart(
		@NonNull Rect rect,
		int color,
		float lineWidth,
		@NonNull float[] points,
		float minAxisX,
		float maxAxisX,
		float minAxisY,
		float maxAxisY
	) {
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStrokeWidth(lineWidth);
		float[] xy = new float[points.length];
		int wPixel = rect.width();
		int hPixel = rect.height();
		float w = maxAxisX - minAxisX;
		float h = maxAxisY - minAxisY;
		float scaleX = wPixel / w;
		float scaleY = hPixel / h;
		int xBase = rect.left;
		int yBase = rect.bottom;
		for (int i = 0; i < points.length; i += 2) {
			xy[i] = xBase + (points[i] - minAxisX) * scaleX;
			xy[i + 1] = yBase - (points[i + 1] - minAxisY) * scaleY;
		}
		mCanvas.drawLines(xy, paint);
	}
*/

	@Override
	public void changeLayout(int width, int height) {
		if (wView != width || hView != height) {
			wView = width;
			hView = height;
			drawChart();
		}
	}

	@Override
	@NonNull
	public View getView() {
		return base;
	}

	@Override
	public void onResume() {
		// nop
	}

	@Override
	public void onPause() {
		// nop
	}

	@Override
	@Nullable
	public UnitPref getUnitPref() {
		return null;
	}

	private static class LineChart {
		private final RectF axis;
		private final RectF perRect;
		private final int color;
		private final int pxStroke;
		private final float[] points;

		private LineChart(
			int color,
			int pxStroke,
			@NonNull float[] points,
			@NonNull RectF axis,
			@NonNull RectF perRect
		) {
			this.color = color;
			this.pxStroke = pxStroke;
			this.points = points;
			this.axis = axis;
			this.perRect = perRect;
		}

		@NonNull
		private Paint createPaint() {
			Paint rc = new Paint();
			rc.setColor(color);
			rc.setStrokeWidth(pxStroke);
			return rc;
		}
	}

	private static class Axis {
		private final List<ChartAxisText> listText;
		private final float axisOther;
		private final int color;
		private final int pxStroke;

		private Axis(
			int color,
			int pxStroke,
			@Nullable List<ChartAxisText> listText,
			float axisOther
		) {
			this.color = color;
			this.pxStroke = pxStroke;
			this.listText = listText;
			this.axisOther = axisOther;
		}

		@NonNull
		private Paint createPaint() {
			Paint rc = new Paint();
			rc.setColor(color);
			rc.setStrokeWidth(pxStroke);
			return rc;
		}
	}

	private static class Text {
		private final String text;
		private final float perX;
		private final float perY;
		private final float perSizeY;
		private final int color;

		private Text(@NonNull String text, float perX, float perY, float perSizeY, int color) {
			this.text = text;
			this.perX = perX;
			this.perY = perY;
			this.perSizeY = perSizeY;
			this.color = color;
		}
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
