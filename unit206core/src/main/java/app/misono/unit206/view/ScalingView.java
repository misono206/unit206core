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

package app.misono.unit206.view;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GestureDetectorCompat;

import app.misono.unit206.debug.Log2;

public class ScalingView extends View {
	private static final String TAG = "ScalingView";

	private static final int MODE_FIT_CENTER = 0;
	private static final int MODE_CENTER_CROP = 1;
	private static final int MODE_CENTER_CROP_FIXED = 2;

	private GestureDetectorCompat detector;
	private ScaleGestureDetector mScaleDetector;
	private SingleTapListener listenerTap;
	private Listener mListener;
	private Matrix mMatrix;
	private Matrix mInvert;
	private float[] mBitmapRect;
	private float[] mTemp;
	private boolean mIsScaling;
	private boolean mIsDowned;
	private float mScale;
	private float mScaleMin;
	private float mDownX;
	private float mDownY;
	private float mMoveX;
	private float mMoveY;
	private float mBitmapX;
	private float mBitmapY;
	private float mViewX;
	private float mViewY;
	private int mBitmapW;
	private int mBitmapH;
	private int mViewW;
	private int mViewH;
	private int mode;

	public interface Listener {
		void onChanged(@NonNull Matrix matrix);
	}

	public interface SingleTapListener {
		void onSingleTapUp(float xView, float yView);
	}

	public ScalingView(@NonNull Context context) {
		super(context);
		init(context);
	}

	public ScalingView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ScalingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@RequiresApi(21)
	public ScalingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	private void init(@NonNull Context context) {
		mMatrix = new Matrix();
		mInvert = new Matrix();
		mBitmapRect = new float[4];
		mTemp = new float[4];
		mode = MODE_FIT_CENTER;
		mScaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				// log("onScale:");
				mScale *= detector.getScaleFactor();
				mScale = Math.max(mScaleMin, mScale);
				callback();
				return true;
			}

			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
				// log("onScaleBegin:");
				mIsScaling = true;
				return super.onScaleBegin(detector);
			}

			@Override
			public void onScaleEnd(ScaleGestureDetector detector) {
				// log("onScaleEnd:");
				mIsScaling = false;
				super.onScaleEnd(detector);
			}
		});
		detector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				if (listenerTap != null) {
					listenerTap.onSingleTapUp(e.getX(), e.getY());
				}
				return super.onSingleTapUp(e);
			}
		});
	}

	public void setCenterCropMode() {
		mode = MODE_CENTER_CROP;
	}

	public void setCenterCropFixedMode() {
		mode = MODE_CENTER_CROP_FIXED;
	}

	@MainThread
	private void callback() {
		mMatrix.setTranslate(mBitmapX, mBitmapY);
		mMatrix.postScale(mScale, mScale);
		mMatrix.postTranslate(mViewX, mViewY);
		correctXY();
		if (mListener != null) {
			mListener.onChanged(new Matrix(mMatrix));
		}
	}

	public void setBitmapAndViewSize(int wBitmap, int hBitmap, int wView, int hView) {
		mBitmapW = wBitmap;
		mBitmapH = hBitmap;
		mBitmapX = -wBitmap / 2f;
		mBitmapY = -hBitmap / 2f;
		mScale = Float.MIN_VALUE;
		resizeView(wView, hView);
	}

	public void multiplyScale(float multiply) {
		mScale *= multiply;
		if (mScale < mScaleMin) {
			mScale = mScaleMin;
		}
		callback();
	}

	public void setScale(float scale) {
		mScale = scale;
		if (mScale < mScaleMin) {
			mScale = mScaleMin;
		}
		callback();
	}

	public void setScaleRelative(float scale) {
		mScale *= scale;
		if (mScale < mScaleMin) {
			mScale = mScaleMin;
		}
		callback();
	}

	public void setScaleAbsolutery(float scale) {
		mScale = mScaleMin * scale;
		if (mScale < mScaleMin) {
			mScale = mScaleMin;
		}
		callback();
	}

	public float getScaleAbsolutery() {
		return mScale / mScaleMin;
	}

	public float resizeView(int wView, int hView) {
		mViewW = wView;
		mViewH = hView;
		mScaleMin = calcMinScale(wView, hView);
		if (mScale < mScaleMin) {
			mScale = mScaleMin;
		}
		mViewX = wView / 2f;
		mViewY = hView / 2f;
		int xOffsetBitmap = 0;
		int yOffsetBitmap = 0;
		if (mode == MODE_CENTER_CROP_FIXED) {
			float rBitmap = (float)mBitmapW / mBitmapH;
			float rView = (float)wView / hView;
			if (rView < rBitmap) {
				xOffsetBitmap = (int)((mBitmapH * rView - mBitmapW) / 2);
			} else {
				yOffsetBitmap = (int)((mBitmapW / rView - mBitmapH) / 2);
			}
		}
		mBitmapRect[0] = xOffsetBitmap;
		mBitmapRect[1] = yOffsetBitmap;
		mBitmapRect[2] = mBitmapW - xOffsetBitmap;
		mBitmapRect[3] = mBitmapH - yOffsetBitmap;

		callback();
		return mScaleMin;
	}

	public float calcMinScale(int wView, int hView) {
		float wScale = (float)wView / mBitmapW;
		float hScale = (float)hView / mBitmapH;
		if (mode == MODE_FIT_CENTER) {
			return Math.min(wScale, hScale);
		} else {
			return Math.max(wScale, hScale);
		}
	}

	public void setListener(@Nullable Listener listener) {
		mListener = listener;
	}

	public void setSingleTapListener(@Nullable SingleTapListener listener) {
		listenerTap = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mScaleDetector.onTouchEvent(ev);
		if (listenerTap != null) {
			detector.onTouchEvent(ev);
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = ev.getX();
			mDownY = ev.getY();
			mMoveX = 0;
			mMoveY = 0;
			mIsDowned = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if (!mIsScaling) {
				if (mIsDowned) {
					// log("move:");
					float sx = ev.getX() - mDownX;
					float sy = ev.getY() - mDownY;
					float dx = sx - mMoveX;
					float dy = sy - mMoveY;
					mMoveX = sx;
					mMoveY = sy;
					if (mListener != null) {
						mBitmapX += dx / mScale;
						mBitmapY += dy / mScale;
						callback();
					}
				}
			} else {
				mIsDowned = false;
			}
			break;
		}
		return true;
	}

	private void correctXY() {
		mMatrix.mapPoints(mTemp, mBitmapRect);
		float x0 = mTemp[0];
		float y0 = mTemp[1];
		float x1 = mTemp[2];
		float y1 = mTemp[3];
		boolean correctX0 = false;
		boolean correctX1 = false;
		boolean correctY0 = false;
		boolean correctY1 = false;
		float moveX = 0;
		float moveY = 0;
		if (x0 < 0 && x1 < mViewW) {
			correctX0 = true;
			moveX = Math.min(-x0, mViewW - x1);
		} else if (0 < x0 && mViewW < x1) {
			correctX1 = true;
			moveX = Math.min(x0, x1 - mViewW);
		}
		if (y0 < 0 && y1 < mViewH) {
			correctY0 = true;
			moveY = Math.min(-y0, mViewH - y1);
		} else if (0 < y0 && mViewH < y1) {
			correctY1 = true;
			moveY = Math.min(y0, y1 - mViewH);
		}
		if (correctX0 || correctX1 || correctY0 || correctY1) {
			mMatrix.invert(mInvert);
			mTemp[0] = 0;
			mTemp[1] = 0;
			mTemp[2] = moveX;
			mTemp[3] = moveY;
			mInvert.mapPoints(mTemp);
			float ax = mTemp[2] - mTemp[0];
			float ay = mTemp[3] - mTemp[1];
			if (correctX0) {
				mBitmapX += ax;
			}
			if (correctX1) {
				mBitmapX -= ax;
			}
			if (correctY0) {
				mBitmapY += ay;
			}
			if (correctY1) {
				mBitmapY -= ay;
			}
			mMatrix.setTranslate(mBitmapX, mBitmapY);
			mMatrix.postScale(mScale, mScale);
			mMatrix.postTranslate(mViewX, mViewY);
		}
	}

	@NonNull
	public float[] getBitmapRectOfViewRect() {
		mMatrix.invert(mInvert);
		float[] rc = new float[4];
		rc[0] = 0;
		rc[1] = 0;
		rc[2] = mViewW;
		rc[3] = mViewH;
		mInvert.mapPoints(rc);
		return rc;
	}

	@NonNull
	public float[] getBitmapPoint(float x, float y) {
		mMatrix.invert(mInvert);
		float[] rc = new float[2];
		rc[0] = x;
		rc[1] = y;
		mInvert.mapPoints(rc);
		return rc;
	}

	@NonNull
	public float[] getDispPoint(float x, float y) {
		float[] rc = new float[2];
		rc[0] = x;
		rc[1] = y;
		mMatrix.mapPoints(rc);
		return rc;
	}

	@NonNull
	public int[] getAppearBitmapRect() {
		if (mBitmapX == 0 || mBitmapY == 0) {
			throw new RuntimeException("Invoke setBitmapAndViewSize() before using...");
		}
		float[] tmp = getBitmapRectOfViewRect();
		int[] rc = new int[5];
		rc[4] = 0;
		if (tmp[0] < 0) {
			rc[0] = 0;
			rc[4]++;
		} else {
			rc[0] = (int)tmp[0];
		}
		if (tmp[1] < 0) {
			rc[1] = 0;
			rc[4]++;
		} else {
			rc[1] = (int)tmp[1];
		}
		if (mBitmapW < tmp[2]) {
			rc[2] = mBitmapW;
			rc[4]++;
		} else {
			rc[2] = (int)tmp[2];
		}
		if (mBitmapH < tmp[3]) {
			rc[3] = mBitmapH;
			rc[4]++;
		} else {
			rc[3] = (int)tmp[3];
		}
		return rc;
	}

	public float getBitmapX() {
		return mBitmapX;
	}

	public float getBitmapY() {
		return mBitmapY;
	}

	public float getScale() {
		if (mBitmapX == 0 || mBitmapY == 0) {
			throw new RuntimeException("Invoke setBitmapAndViewSize() before using...");
		}
		return mScale;
	}

	public float getViewX() {
		return mViewX;
	}

	public float getViewY() {
		return mViewY;
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
