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

package app.misono.unit206.page;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.BlockClick;
import app.misono.unit206.misc.Views;
import app.misono.unit206.task.Taskz;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public abstract class AbstractPage implements Page {
	private static final String TAG = "AbstractPage";

	protected static final int COLOR_BG_TRANSPARENT = 0xAA222222;

	protected PageActivity activity;
	protected FrameLayout mAdBase;
	protected FrameLayout mParent;
	protected FrameLayout mBase;
	protected PageManager manager;
	protected Runnable mClickBack;
	protected AdView mAdView;
	protected boolean mPortrait;
	protected int mWidth;
	protected int mHeightAdBase;
	protected int mHeight;

	private final FrameAnimator animator;

	private PagePref pref;
	private View smoke;
	private boolean enableToolbar;

	protected AbstractPage(
		@NonNull PageManager manager,
		@NonNull PageActivity activity,
		@NonNull FrameLayout adbase,
		@NonNull FrameLayout parent,
		@Nullable AdView adview,
		@Nullable Runnable clickBack
	) {
		this(manager, activity, adbase, parent, adview, 150, clickBack);
	}

	protected AbstractPage(
		@NonNull PageManager manager,
		@NonNull PageActivity activity,
		@NonNull FrameLayout adbase,
		@NonNull FrameLayout parent,
		@Nullable AdView adview,
		int msecAnimator,
		@Nullable Runnable clickBack
	) {
		this.manager = manager;
		this.activity = activity;
		mClickBack = clickBack;
		mAdBase = adbase;
		mParent = parent;
		mAdView = adview;
		enableToolbar = true;
		mBase = new FrameLayout(parent.getContext());
		mBase.setOnClickListener(null);
		if (msecAnimator != 0) {
			animator = new FrameAnimator(msecAnimator);
		} else {
			animator = null;
		}
	}

	protected AbstractPage(
		@NonNull PageManager manager,
		@NonNull PageActivity activity,
		@NonNull FrameLayout parent,
		@Nullable Runnable clickBack
	) {
		this(manager, activity, parent, parent, null, clickBack);
	}

	protected AbstractPage(
		@NonNull PageManager manager,
		@NonNull PageActivity activity,
		@NonNull FrameLayout parent,
		int msecAnimator,
		@Nullable Runnable clickBack
	) {
		this(manager, activity, parent, parent, null, msecAnimator, clickBack);
	}

	@Override
	@NonNull
	public FrameLayout getBaseView() {
		return mBase;
	}

	protected void enableToolbar(boolean enable) {
		enableToolbar = enable;
	}

	@Override
	public boolean isActiveToolbar() {
		return enableToolbar;
	}

	public boolean isActive() {
		return mBase.getParent() != null;
	}

	protected void refreshActionBar() {
		manager.refreshActionBar(this);
	}

	public boolean isTopPage() {
		return manager.getTopPage() == this;
	}

	/**
	 * Check top page.
	 * iSTopPage() && activity is onResume.
	 */
	public boolean isActiveTopPage() {
		return isTopPage() && activity.isOnResume();
	}

	@Override
	@NonNull
	public Context getContext() {
		return mParent.getContext();
	}

	@Override
	@NonNull
	public PageActivity getPageActivity() {
		return activity;
	}

	@Override
	public boolean isHamburgerIcon() {
		return true;
	}

	@Override
	public void changeLayout(int wAdBase, int hAdBase) {
		mWidth = wAdBase;
		mHeightAdBase = hAdBase;
		mHeight = hAdBase;
		if (mAdView != null) {
			mHeight -= mAdView.getHeight();
		}
		mPortrait = mWidth < mHeight;
		FrameLayout.LayoutParams p = (FrameLayout.LayoutParams)mBase.getLayoutParams();
		if (p != null) {
			p.width = mWidth;
			p.height = mHeight;
			mBase.setLayoutParams(p);
		}
	}

	@Override
	public void onResume() {
		Toolbar toolbar = activity.getToolbar();
		if (toolbar != null) {
			toolbar.setVisibility(enableToolbar ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public void onPause() {
		Toolbar toolbar = activity.getToolbar();
		if (toolbar != null) {
			toolbar.setVisibility(View.VISIBLE);
		}
		unblockClick();
	}

	protected void blockClick() {
		BlockClick block = activity.mBlock;
		if (block != null) {
			block.block();
		} else {
			Log2.e(TAG, "blockClick: block == null...");
		}
	}

	protected void unblockClick() {
		BlockClick block = activity.mBlock;
		if (block != null) {
			block.unblock();
		} else {
			Log2.e(TAG, "unblockClick: block == null...");
		}
	}

	@Override
	public void addToParent(@Nullable Runnable done) {
		if (animator != null) {
			if (smoke != null) {
				mParent.removeView(smoke);
			}
			smoke = new View(mBase.getContext());
			smoke.setBackgroundColor(COLOR_BG_TRANSPARENT & 0xffffff);
			mParent.addView(smoke, mWidth, mHeight);
			mBase.setPadding(mWidth, 0, 0, 0);
			mParent.addView(mBase, mWidth, mHeight);
			animator.clear();
			animator.addItemAlpha(alpha -> {
				int a = (int)(((COLOR_BG_TRANSPARENT >> 24) & 0xff) * alpha);
				int color = (a << 24) | (COLOR_BG_TRANSPARENT & 0xffffff);
				smoke.setBackgroundColor(color);
			});
			animator.start(() -> {
				FrameAnimator.Padding to = new FrameAnimator.Padding(0, 0, 0, 0);
				animator.clear();
				animator.addItem(mBase, to);
				animator.start(() -> {
					mParent.removeView(smoke);
					smoke = null;
					if (done != null) {
						done.run();
					}
				});
			});
		} else {
			mBase.setPadding(0, 0, 0, 0);
			mParent.addView(mBase, mWidth, mHeight);
			if (done != null) {
				Taskz.call(() -> {
					done.run();
					return null;
				}).addOnFailureListener(Taskz::printStackTrace2);
			}
		}
	}

	@Override
	public void removeFromParent(@Nullable Runnable done) {
		Views.hideKeyboard(mBase);
		if (animator != null) {
			FrameAnimator.Padding to = new FrameAnimator.Padding(mWidth, 0, 0, 0);
			if (smoke != null) {
				mParent.removeView(smoke);
			}
			smoke = new View(mBase.getContext());
			smoke.setBackgroundColor(COLOR_BG_TRANSPARENT);
			FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(mWidth, mHeight);
			mParent.addView(smoke, mParent.getChildCount() - 1, p);
			animator.clear();
			animator.addItem(mBase, to);
			animator.start(() -> {
				mParent.removeView(mBase);
				animator.clear();
				animator.addItemAlpha(alpha -> {
					int a = (int)(((COLOR_BG_TRANSPARENT >> 24) & 0xff) * (1f - alpha));
					int color = (a << 24) | (COLOR_BG_TRANSPARENT & 0xffffff);
					smoke.setBackgroundColor(color);
				});
				animator.start(() -> {
					mParent.removeView(smoke);
					smoke = null;
					if (done != null) {
						Taskz.call(() -> {
							done.run();
							return null;
						}).addOnFailureListener(Taskz::printStackTrace2);
					}
				});
			});
		} else {
			mParent.removeView(mBase);
			if (done != null) {
				done.run();
			}
		}
	}

	@Deprecated
	protected int pixelX(int per) {
		return mWidth * per / 1000;
	}

	protected int pixelX(float ratio) {
		return (int)(mWidth * ratio);
	}

	@Deprecated
	protected int pixelButtonX(int per) {
		return pixelX(per);
	}

	protected int pixelButtonX(float ratio) {
		return pixelX(ratio);
	}

	@Deprecated
	protected int pixelY(int per) {
		return mHeight * per / 1000;
	}

	protected int pixelY(float ratio) {
		return (int)(mHeight * ratio);
	}

	@Deprecated
	protected int pixel(int per) {
		return Math.min(mWidth, mHeight) * per / 1000;
	}

	protected int pixel(float ratio) {
		return (int)(Math.min(mWidth, mHeight) * ratio);
	}

	protected void setButtonParamAtTopLeft(FrameLayout.LayoutParams param, int x, int y, int w) {
		int pixel = pixelButtonX(w);
		param.width = pixel;
		param.height = pixel;
		param.leftMargin = pixelX(x);
		param.topMargin = pixelY(y);
		param.gravity = Gravity.TOP | Gravity.START;
	}

	protected void setButtonParam(FrameLayout.LayoutParams param, int x, int y, int w) {
		int pixel = pixelButtonX(w);
		int d = pixel / 2;
		param.width = pixel;
		param.height = pixel;
		param.leftMargin = pixelX(x) - d;
		param.topMargin = pixelY(y) - d;
		param.gravity = Gravity.TOP | Gravity.START;
	}

	@NonNull
	protected FrameLayout.LayoutParams createParamButtonAtTopLeft(int x, int y, int w) {
		FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(0, 0);
		setButtonParamAtTopLeft(param, x, y, w);
		return param;
	}

	@NonNull
	protected FrameLayout.LayoutParams createParamWithWidth(int x, int y, int w) {
		FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(0, 0);
		setButtonParam(param, x, y, w);
		return param;
	}

	@NonNull
	protected FrameLayout.LayoutParams createParamCenter(int wPixel, int hPixel) {
		return createParamCenter(mWidth, mHeight, wPixel, hPixel);
	}

	@NonNull
	protected FrameLayout.LayoutParams createParamCenter(
		int wPixelParent,
		int hPixelParent,
		int wPixel,
		int hPixel
	) {
		FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(wPixel, hPixel);
		param.leftMargin = (wPixelParent - wPixel) / 2;
		param.topMargin = (hPixelParent - hPixel) / 2;
		return param;
	}

	@NonNull
	protected ImageView createImageView(@NonNull Context context) {
		ImageView view = new ImageView(context);
		view.setAdjustViewBounds(true);
		view.setScaleType(ImageView.ScaleType.CENTER_CROP);
		view.setBackgroundColor(COLOR_BG_TRANSPARENT);
		return view;
	}
/*
	@NonNull
	protected ImageView createImageView(@NonNull Context context, @DrawableRes int idDrawable) {
		ImageView view = createImageView(context);
		view.setImageDrawable(context.getDrawable(idDrawable));
		return view;
	}

	@NonNull
	protected ImageView createImageViewPaddingX(@NonNull Context context, @DrawableRes int idDrawable, int padding) {
		ImageView view = createImageView(context);
		view.setImageDrawable(context.getDrawable(idDrawable));
		int p = pixelX(padding);
		view.setPadding(p, p, p, p);
		return view;
	}
*/
	@NonNull
	protected ImageView createImageViewPaddingX(@NonNull Context context, @NonNull Drawable d, int padding) {
		ImageView view = createImageView(context);
		view.setImageDrawable(d);
		int p = pixelX(padding);
		view.setPadding(p, p, p, p);
		return view;
	}

	@Override
	public void onBackPressed() {
		onPause();
		if (mClickBack != null) {
			mClickBack.run();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		return false;
	}

	@Override
	public void showSnackbar(@NonNull String msg) {
		Snackbar.make(mBase, msg, Snackbar.LENGTH_LONG).show();
	}

	@Override
	public void showSnackbar(@StringRes int idMessage) {
		Snackbar.make(mBase, idMessage, Snackbar.LENGTH_LONG).show();
	}

	@Override
	@NonNull
	public Task<Void> showSnackbarTask(@NonNull String msg) {
		return Taskz.call(() -> {
			Snackbar.make(mBase, msg, Snackbar.LENGTH_LONG).show();
			return null;
		});
	}

	@Override
	@NonNull
	public Task<Void> showSnackbarTask(@StringRes int idMessage) {
		return Taskz.call(() -> {
			Snackbar.make(mBase, idMessage, Snackbar.LENGTH_LONG).show();
			return null;
		});
	}

	@Override
	@NonNull
	public Snackbar showSnackProgress(@StringRes int idMessage) {
		return Views.showSnackProgress(mBase, idMessage);
	}

	@Override
	@NonNull
	public Snackbar showSnackProgress(
		@StringRes int idMessage,
		@StringRes int idAction,
		@NonNull View.OnClickListener listener
	) {
		return Views.showSnackProgress(mBase, idMessage, idAction, listener);
	}

	protected FloatingActionButton addFab(
		@NonNull Context context,
		@DrawableRes int idDrawable
	) {
		FloatingActionButton rc = new FloatingActionButton(context);
		rc.setImageResource(idDrawable);
		int m = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.WRAP_CONTENT,
			FrameLayout.LayoutParams.WRAP_CONTENT
		);
		p.setMargins(0, 0, m, m);
		p.gravity = Gravity.END | Gravity.BOTTOM;
		mBase.addView(rc, p);
		return rc;
	}

	protected void setPref(@NonNull PagePref pref) {
		this.pref = pref;
		manager.loadPref(pref);
	}

	protected void enableDrawer() {
		manager.enableDrawer();
	}

	protected void disableDrawer() {
		manager.disableDrawer();
	}

	@Override
	@Nullable
	public PagePref getPref() {
		return pref;
	}

}
