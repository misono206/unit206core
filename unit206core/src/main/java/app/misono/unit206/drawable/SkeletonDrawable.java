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

package app.misono.unit206.drawable;

import android.animation.TimeAnimator;
import android.annotation.TargetApi;
import android.graphics.ColorFilter;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@TargetApi(1)
public abstract class SkeletonDrawable extends Drawable implements Animatable {
	private static final String TAG = "SkeletonDrawable";

	protected static final int PATH_M = 0;
	protected static final int PATH_m = 1;
	protected static final int PATH_L = 2;
	protected static final int PATH_l = 3;
	protected static final int PATH_C = 8;
	protected static final int PATH_c = 9;
	protected static final int PATH_S = 10;
	protected static final int PATH_s = 11;

	protected static final int PATH_ARC = 100;
	protected static final int PATH_CIRCLE = 101;
	protected static final int PATH_RECT = 102;

	protected int msecDuration, msecDelay, msecAnimateDuration;
	protected float xScale, yScale, xOffset, yOffset;

	private TimeAnimator animator;
	private int tick;

	public SkeletonDrawable() {
	}

	public SkeletonDrawable(int duration, int delay, int early) {
		super();
		msecDuration = duration;
		msecDelay = delay;
		msecAnimateDuration = duration - delay - early;
		animator = new TimeAnimator();
	}

	protected void setViewPort(int xViewPort, int yViewPort, int wPixel, int hPixel) {
		xScale = (float)wPixel / xViewPort;
		yScale = (float)hPixel / yViewPort;
		xOffset = 0;
		yOffset = 0;
	}

	protected void setViewPortFix(int xViewPort, int yViewPort, int wPixel, int hPixel) {
		float scale;
		if (wPixel * yViewPort < hPixel * xViewPort) {
			// 上下が空く
			scale = (float)wPixel / xViewPort;
			xOffset = 0;
			yOffset = (hPixel - scale * yViewPort) / 2;
		} else {
			// 左右が空く
			scale = (float)hPixel / yViewPort;
			xOffset = (wPixel - scale * xViewPort) / 2;
			yOffset = 0;
		}
		xScale = scale;
		yScale = scale;
	}

	protected float pixel(float x) {
		return x * xScale;
	}

	protected float pixelX(float x) {
		return x * xScale + xOffset;
	}

	protected float pixelY(float y) {
		return y * yScale + yOffset;
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(@Nullable ColorFilter colorFilter) {

	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	/**
	 * invoked from draw()
	 */
	protected int getTick() {
		int rc;
		if (tick < msecDelay) {
			rc = 0;
		} else {
			rc = tick - msecDelay;
			if (msecAnimateDuration <= rc) {
				animator.end();
				rc = msecAnimateDuration;
			}
		}
		return rc;
	}

	protected int getSimpleTick() {
		return tick;
	}

	private int path_L(@NonNull Path path, @NonNull float[] data, int index) {
		path.lineTo(pixelX(data[index]), pixelY(data[index + 1]));
		return index + 2;
	}

	private int path_l(@NonNull Path path, @NonNull float[] data, int index) {
		path.rLineTo(data[index] * xScale, data[index + 1] * yScale);
		return index + 2;
	}

	protected void setPath(@NonNull Path path, @NonNull ElementPath[] elements) {
		path.reset();
		addPath(path, elements);
	}

	protected void addPath(@NonNull Path path, @NonNull ElementPath[] elements) {
		RectF rect;
		float x, y, x1, y1, x2, y2, r, angleStart, angleSweep;
		for (ElementPath e : elements) {
			float[] data = e.data;
			switch (e.e) {
			case PATH_M:
				path.moveTo(pixelX(data[0]), pixelY(data[1]));
				break;
			case PATH_m:
				path.rMoveTo(data[0] * xScale, data[1] * yScale);
				break;
			case PATH_L:
				for (int index = 0; index < data.length; ) {
					index = path_L(path, data, index);
				}
				break;
			case PATH_l:
				for (int index = 0; index < data.length; ) {
					index = path_l(path, data, index);
				}
				break;
			case PATH_C:
				for (int index = 0; index < data.length; ) {
					x1 = pixelX(data[index++]);
					y1 = pixelY(data[index++]);
					x2 = pixelX(data[index++]);
					y2 = pixelY(data[index++]);
					x = pixelX(data[index++]);
					y = pixelY(data[index++]);
					path.cubicTo(x1, y1, x2, y2, x, y);
				}
				break;
			case PATH_c:
				for (int index = 0; index < data.length; ) {
					x1 = data[index++] * xScale;
					y1 = data[index++] * yScale;
					x2 = data[index++] * xScale;
					y2 = data[index++] * yScale;
					x = data[index++] * xScale;
					y = data[index++] * yScale;
					path.rCubicTo(x1, y1, x2, y2, x, y);
				}
				break;
			case PATH_S:
				// deprecated
				for (int index = 0; index < data.length; ) {
					x2 = pixelX(data[index++]);
					y2 = pixelY(data[index++]);
					x = pixelX(data[index++]);
					y = pixelY(data[index++]);
					path.quadTo(x2, y2, x, y);
				}
				break;
			case PATH_s:
				// deprecated
				for (int index = 0; index < data.length; ) {
					x2 = data[index++] * xScale;
					y2 = data[index++] * yScale;
					x = data[index++] * xScale;
					y = data[index++] * yScale;
					path.rQuadTo(x2, y2, x, y);
				}
				break;
			case PATH_ARC:
				for (int index = 0; index < data.length; ) {
					x = pixelX(data[index++]);
					y = pixelY(data[index++]);
					x1 = pixelX(data[index++]);
					y1 = pixelY(data[index++]);
					rect = new RectF(x, y, x1, y1);
					angleStart = data[index++];
					angleSweep = data[index++];
					path.arcTo(rect, angleStart, angleSweep);
				}
				break;
			case PATH_CIRCLE:
				for (int index = 0; index < data.length; ) {
					x = pixelX(data[index++]);
					y = pixelY(data[index++]);
					r = data[index++] * (xScale + yScale) / 2;
					path.addCircle(x, y, r, Path.Direction.CW);
				}
				break;
			case PATH_RECT:
				for (int index = 0; index < data.length; ) {
					x = pixelX(data[index++]);
					y = pixelY(data[index++]);
					x1 = pixelX(data[index++]);
					y1 = pixelY(data[index++]);
					path.addRect(x, y, x1, y1, Path.Direction.CW);
				}
				break;
			default:
				Log.e(TAG, "unknown element:" + e.e);
				break;
			}
		}
		path.close();
	}

	@Override
	public void start() {
		tick = 0;
		animator.setTimeListener((animation, totalTime, deltaTime) -> {
			tick = (int)totalTime;
			Callback cb = getCallback();
			if (cb != null) {
				cb.invalidateDrawable(this);
			}
		});
		animator.start();
	}

	@Override
	public void stop() {
		animator.end();
	}

	@Override
	public boolean isRunning() {
		return animator.isRunning();
	}

	public boolean isAnimatable() {
		return false;
	}

	protected static class ElementPath {
		public int e;
		public float[] data;

		public ElementPath(int e, @NonNull float[] data) {
			this.e = e;
			this.data = data;
		}
	}

}
