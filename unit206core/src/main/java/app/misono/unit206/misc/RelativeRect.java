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

package app.misono.unit206.misc;

/**
 * Calculate a card view rect size.
 */
public class RelativeRect {
	private static final int MODE_HEIGHT_RATIO_1 = 0;	// hRatio

	private float hRatio;
	private float radius;
	private int mode;
	private int width, height;
	private int w1, h1;
	private int mLeft, mTop, mRight, mBottom;

	public RelativeRect() {
		mLeft = 1;
		mTop = 1;
		mRight = 1;
		mBottom = 1;
		radius = -1;
	}

	public void setHeightRatio1(float hRatio) {
		mode = MODE_HEIGHT_RATIO_1;
		this.hRatio = hRatio;
		calc();
	}

	public void setMargins(int left, int top, int right, int bottom) {
		mLeft = left;
		mTop = top;
		mRight = right;
		mBottom = bottom;
	}

	public int getLeftMargine() {
		return mLeft;
	}

	public int getTopMargine() {
		return mTop;
	}

	public int getRightMargine() {
		return mRight;
	}

	public int getBottomMargine() {
		return mBottom;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getRadius() {
		return radius;
	}

	public void changeLayout(int width, int height) {
		this.width = width;
		this.height = height;
		calc();
	}

	/**
	 * calculate w1 and h1.
	 */
	private void calc() {
		w1 = calcWidth1(width, height);
		h1 = calcHeight1(width, height);
	}

	public int calcWidth1(int w, int h) {
		int rc = 0;
		switch (mode) {
		case MODE_HEIGHT_RATIO_1:
			rc = w;
			break;
		default:
			throw new RuntimeException("unknown mode:" + mode);
		}
		return rc;
	}

	public int calcHeight1(int w, int h) {
		int rc = 0;
		switch (mode) {
		case MODE_HEIGHT_RATIO_1:
			if (w != 0) {
				rc = (int)(w * hRatio);
			}
			break;
		default:
			throw new RuntimeException("unknown mode:" + mode);
		}
		return rc;
	}

	public int getItemWidth() {
		return w1;
	}

	public int getItemHeight() {
		return h1;
	}

}

