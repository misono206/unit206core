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

package app.misono.unit206.page;

import android.animation.TimeAnimator;
import android.graphics.Matrix;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * FrameAnimator will be deprecated.
 * use PageAnimator instead.
 */
@Deprecated
public class FrameAnimator implements Animatable {
	private static final String TAG = "FrameAnimator";

	private final List<AbstractItem> mList;
	private final TimeAnimator mAnimator;
	private final long mDuration;

	private Runnable mDone;
	private boolean mDisappear;

	public FrameAnimator(long msecDuration) {
		mList = new ArrayList<>();
		mDuration = msecDuration;
		mAnimator = new TimeAnimator();
		mAnimator.setTimeListener((animation, totalTime, deltaTime) -> {
			if (mDuration <= totalTime) {
				animation.end();
				totalTime = mDuration;
			}
			for (AbstractItem item : mList) {
				item.update((int)totalTime, (int)mDuration);
			}
			if (mDuration <= totalTime) {
				if (mDisappear) {
					for (AbstractItem item : mList) {
						ViewParent parent = item.getParent();
						if (parent != null) {
							((ViewGroup)parent).removeView(item.getView());
						}
					}
				}
				if (mDone != null) {
					mDone.run();
				}
			}
		});
	}

	public void addItem(@NonNull View view, @NonNull FrameLayout.LayoutParams to) {
		mList.add(new Item(view, to, null, 0, 0));
	}

	public void addItem(@NonNull View view, @NonNull FrameLayout.LayoutParams to, @Nullable Padding padding) {
		mList.add(new Item(view, to, padding, 0, 0));
	}

	public void addItem(@NonNull View view, @Nullable Padding padding) {
		mList.add(new Item(view, null, padding, 0, 0));
	}

	public void addItem(@NonNull View view, @NonNull FrameLayout.LayoutParams to, int delay, int early) {
		mList.add(new Item(view, to, null, delay, early));
	}

	public void addItem(
		@NonNull View view,
		@NonNull FrameLayout.LayoutParams to,
		@Nullable Padding padding,
		int delay,
		int early
	) {
		mList.add(new Item(view, to, padding, delay, early));
	}

	public void addItemAlpha(@NonNull IAlpha onAlphaChange) {
		mList.add(new Item(onAlphaChange));
	}

	public void addItemMatrix(
		@NonNull ImageView view,
		float bitmapX,
		float bitmapY,
		float scaleStart,
		float scaleEnd,
		float viewStartX,
		float viewStartY,
		float viewEndX,
		float viewEndY,
		int delay,
		int early
	) {
		mList.add(new ItemMatrix(view, bitmapX, bitmapY, scaleStart, scaleEnd, viewStartX, viewStartY,
				viewEndX, viewEndY, delay, early));
	}

	public void clearItems() {
		mList.clear();
	}

	public void clear() {
		clearItems();
		mDisappear = false;
		mDone = null;
	}

	public void setDisappear(boolean disappear) {
		mDisappear = disappear;
	}

	public boolean getDisappear() {
		return mDisappear;
	}

	public void setDone(@Nullable Runnable done) {
		mDone = done;
	}

	public void start(@NonNull Runnable done) {
		mDone = done;
		start();
	}

	@Override
	public void start() {
		mAnimator.start();
	}

	@Override
	public void stop() {
		mAnimator.cancel();
	}

	@Override
	public boolean isRunning() {
		return mAnimator.isRunning();
	}

	private static abstract class AbstractItem {
		private final int msecDelay, msecEarly;

		protected AbstractItem(int delay, int early) {
			msecDelay = delay;
			msecEarly = early;
		}

		protected boolean update(int tick, int duration) {
			if (msecDelay <= tick) {
				tick -= msecDelay;
				duration -= msecDelay + msecEarly;
				if (duration < tick) {
					tick = duration;
				}
				return true;
			} else {
				return false;
			}
		}

		protected abstract ViewParent getParent();
		protected abstract View getView();
	}

	private final class Item extends AbstractItem {
		private View me;
		private FrameLayout.LayoutParams base, ref, end;
		private Padding refPadding, endPadding;
		private IAlpha alpha;

		private Item(
			@NonNull View view,
			@Nullable FrameLayout.LayoutParams to,
			@Nullable Padding toPadding,
			int delay,
			int early
		) {
			super(delay, early);
			FrameLayout.LayoutParams from = (FrameLayout.LayoutParams)view.getLayoutParams();
			if (to != null) {
				if (from.gravity != to.gravity) {
					throw new IllegalArgumentException("must be same gravity...");
				}
				ref = copy(from);
				base = from;
				end = to;
			}
			me = view;
			if (toPadding != null) {
				endPadding = toPadding;
				int left = view.getPaddingLeft();
				int top = view.getPaddingTop();
				int right = view.getPaddingRight();
				int bottom = view.getPaddingBottom();
				refPadding = new Padding(left, top, right, bottom);
			}
		}

		private Item(@NonNull IAlpha onAlphaChange) {
			super(0, 0);
			alpha = onAlphaChange;
		}

		@Override
		protected boolean update(int tick, int duration) {
			boolean rc = super.update(tick, duration);
			if (rc) {
				if (alpha != null) {
					if (mDisappear) {
						alpha.setAlpha((float)(duration - tick) / duration);
					} else {
						alpha.setAlpha((float)tick / duration);
					}
				} else {
					if (ref != null) {
						base.width = (end.width - ref.width) * tick / duration + ref.width;
						base.height = (end.height - ref.height) * tick / duration + ref.height;
						base.topMargin = (end.topMargin - ref.topMargin) * tick / duration + ref.topMargin;
						base.bottomMargin = (end.bottomMargin - ref.bottomMargin) * tick / duration + ref.bottomMargin;
						base.leftMargin = (end.leftMargin - ref.leftMargin) * tick / duration + ref.leftMargin;
						base.rightMargin = (end.rightMargin - ref.rightMargin) * tick / duration + ref.rightMargin;
					}
					if (refPadding != null) {
						int left = (endPadding.left - refPadding.left) * tick / duration + refPadding.left;
						int top = (endPadding.top - refPadding.top) * tick / duration + refPadding.top;
						int right = (endPadding.right - refPadding.right) * tick / duration + refPadding.right;
						int bottom = (endPadding.bottom - refPadding.bottom) * tick / duration + refPadding.bottom;
						me.setPadding(left, top, right, bottom);
					}
					me.requestLayout();
				}
			}
			return rc;
		}

		@Override
		protected ViewParent getParent() {
			return me.getParent();
		}

		@Override
		protected View getView() {
			return me;
		}
	}

	private static final class ItemMatrix extends AbstractItem {
		private final ImageView me;
		private final Matrix mMatrix;
		private final float mBitmapX;
		private final float mBitmapY;
		private final float mScaleStart;
		private final float mScaleEnd;
		private final float mViewStartX;
		private final float mViewStartY;
		private final float mViewEndX;
		private final float mViewEndY;

		private ItemMatrix(
			@NonNull ImageView view,
			float bitmapX,
			float bitmapY,
			float scaleStart,
			float scaleEnd,
			float viewStartX,
			float viewStartY,
			float viewEndX,
			float viewEndY,
			int delay,
			int early
		) {
			super(delay, early);
			me = view;
			mMatrix = new Matrix();
			mBitmapX = bitmapX;
			mBitmapY = bitmapY;
			mScaleStart = scaleStart;
			mScaleEnd = scaleEnd;
			mViewStartX = viewStartX;
			mViewStartY = viewStartY;
			mViewEndX = viewEndX;
			mViewEndY = viewEndY;
		}

		@Override
		protected boolean update(int tick, int duration) {
			boolean rc = super.update(tick, duration);
			if (rc) {
				float per = (float)tick / duration;
				float scale = (mScaleEnd - mScaleStart) * per + mScaleStart;
				float viewX = (mViewEndX - mViewStartX) * per + mViewStartX;
				float viewY = (mViewEndY - mViewStartY) * per + mViewStartY;
				mMatrix.setTranslate(mBitmapX, mBitmapY);
				mMatrix.postScale(scale, scale);
				mMatrix.postTranslate(viewX, viewY);
				me.setImageMatrix(mMatrix);
			}
			return rc;
		}

		@Override
		protected ViewParent getParent() {
			return me.getParent();
		}

		@Override
		protected View getView() {
			return me;
		}
	}

	@NonNull
	protected FrameLayout.LayoutParams copy(@NonNull FrameLayout.LayoutParams src) {
		FrameLayout.LayoutParams rc;
		if (19 <= Build.VERSION.SDK_INT) {
			rc = new FrameLayout.LayoutParams(src);
		} else {
			rc = new FrameLayout.LayoutParams(src.width, src.height, src.gravity);
			rc.leftMargin = src.leftMargin;
			rc.topMargin = src.topMargin;
			rc.rightMargin = src.rightMargin;
			rc.bottomMargin = src.bottomMargin;
			rc.layoutAnimationParameters = src.layoutAnimationParameters;
		}
		return rc;
	}

	public static class Padding {
		int left, top, right, bottom;

		public Padding(int left, int top, int right, int bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}
	}

	public interface IAlpha {
		void setAlpha(float alpha);
	}

}
