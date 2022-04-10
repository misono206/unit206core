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

package app.misono.unit206.selection;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.drawable.icon.CheckCircleDrawable;
import app.misono.unit206.page.FrameAnimator;

import com.google.android.material.card.MaterialCardView;

public abstract class SelectableCardView<T extends LongId> extends MaterialCardView implements LongId {
	private static final String TAG = "SelectableCardView";

	public static final int TYPE_LINE = 0;
	public static final int TYPE_GRID_SQUARE = 1;

	private static final int STATE_IDLE = 0;
	private static final int STATE_SELECTING = 1;
	private static final int STATE_DESELECTING = 2;
	private static final int STATE_SELECTED = 3;

	private FrameLayout.LayoutParams mParamCheck;
	private CheckCircleDrawable mDrawableCheck;
	private FrameAnimator mAnime;
	private FrameLayout mFrame;
	private ImageView mViewCheck;
	private int mColorCircle;
	private int mColorCheck;
	private int mType;
	private int mState;

	protected T mItem;
	protected boolean mSelected;
	protected int mWidth;
	protected int mHeight;

	public SelectableCardView(@NonNull Context context) {
		super(context);
		init(context);
	}

	public SelectableCardView(Context context, AttributeSet attr) {
		super(context, attr);
		init(context);
	}

	public SelectableCardView(@NonNull Context context, @NonNull ViewGroup.LayoutParams param) {
		super(context);
		init(context);
		setLayoutParams(param);
		mWidth = param.width;
		mHeight = param.height;
	}

	private void init(@NonNull Context context) {
		setSelectAction(Color.WHITE, Color.BLACK);
		mFrame = new FrameLayout(context);
		mViewCheck = new ImageView(context);
		mAnime = new FrameAnimator(200);
		addView(mViewCheck);
		addView(mFrame, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mViewCheck.setVisibility(View.GONE);
		mState = STATE_IDLE;
	}

	public void setItem(@NonNull T item, int width, int height, boolean isSelected) {
		mItem = item;
		mWidth = width;
		mHeight = height;
		mSelected = isSelected;
		onViewAttachedToWindow();
	}

	public void onViewAttachedToWindow() {
		int w = mHeight / 2;
		if (mDrawableCheck != null) {
			mDrawableCheck.setPixelSize(w, w);
		}
		if (mParamCheck != null) {
			mParamCheck.width = w;
			mParamCheck.height = w;
			mParamCheck.topMargin = w / 2;
			mParamCheck.leftMargin = w / 4;
		}
	}

	public void onViewDetachedFromWindow() {
		clear();
	}

	public void clear() {
		mViewCheck.setVisibility(View.GONE);
		mState = STATE_IDLE;
		mAnime.stop();
		mFrame.setPadding(0, 0, 0, 0);
	}

	public void setType(int type) {
		mType = type;
	}

	@Override
	public long getLongId() {
		return mItem.getLongId();
	}

	public void setSelectAction(int colorCircle, int colorCheck) {
		mColorCircle = colorCircle;
		mColorCheck = colorCheck;
	}

	@Override
	public void setSelected(boolean selected) {
//		log("selected:" + mItem.getLongId() + " " + selected + " state:" + mState);
		switch (mType) {
		case TYPE_LINE:
			setSelectedLine(selected);
			break;
		case TYPE_GRID_SQUARE:
			setSelectedGridSquare(selected);
			break;
		}
	}

	private void setSelectedLine(boolean selected) {
		int w = getHeight();
		int w2 = w / 2;
		int w4 = w / 4;
		int w8 = w / 8;
		if (selected) {
			if (mState == STATE_IDLE) {
				mState = STATE_SELECTING;
				mDrawableCheck = new CheckCircleDrawable(w2, w2, mColorCircle, mColorCheck);
				mViewCheck.setImageDrawable(mDrawableCheck);
				FrameAnimator.Padding pad = new FrameAnimator.Padding(w2, 0, 0, 0);
				mAnime.clear();
				mAnime.addItem(mFrame, pad);
				mAnime.start(() -> {
					mState = STATE_SELECTED;
					mParamCheck = new FrameLayout.LayoutParams(w2, w2, Gravity.START | Gravity.TOP);
					mParamCheck.topMargin = w4;
					mParamCheck.leftMargin = w8;
					mViewCheck.setVisibility(View.VISIBLE);
					updateViewLayout(mViewCheck, mParamCheck);
				});
			} else {
				if (mFrame.getPaddingLeft() == 0 && mState == STATE_SELECTED) {
					mState = STATE_SELECTING;
					FrameAnimator.Padding pad = new FrameAnimator.Padding(w2, 0, 0, 0);
					mAnime.clear();
					mAnime.addItem(mFrame, pad);
					mAnime.start(() -> {
						mState = STATE_SELECTED;
						mFrame.setPadding(w2, 0, 0, 0);
						mDrawableCheck = new CheckCircleDrawable(w2, w2, mColorCircle, mColorCheck);
						mViewCheck.setImageDrawable(mDrawableCheck);
						mViewCheck.setVisibility(View.VISIBLE);
						mParamCheck = new FrameLayout.LayoutParams(w2, w2, Gravity.START | Gravity.TOP);
						mParamCheck.topMargin = w4;
						mParamCheck.leftMargin = w8;
						mViewCheck.setVisibility(View.VISIBLE);
						updateViewLayout(mViewCheck, mParamCheck);
					});
				}
			}
		} else {
			if (mState == STATE_SELECTED || mState == STATE_SELECTING) {
				mState = STATE_DESELECTING;
				mViewCheck.setVisibility(View.GONE);
				FrameAnimator.Padding pad = new FrameAnimator.Padding(0, 0, 0, 0);
				mAnime.clear();
				mAnime.addItem(mFrame, pad);
				mAnime.start(() -> {
					mState = STATE_IDLE;
				});
			}
		}
	}

	private void setSelectedGridSquare(boolean selected) {
		//
	}

	public void addViewBody(View view, ViewGroup.LayoutParams params) {
		mFrame.addView(view, params);
	}

	public void addViewBody(View view, int wParam, int hParam) {
		mFrame.addView(view, wParam, hParam);
	}

	public void removeViewBody(View view) {
		mFrame.removeView(view);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
