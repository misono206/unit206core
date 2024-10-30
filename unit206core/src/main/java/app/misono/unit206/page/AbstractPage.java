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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.AnyThread;
import androidx.annotation.DrawableRes;
import androidx.annotation.MainThread;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import app.misono.unit206.callback.CallbackMenu;
import app.misono.unit206.debug.Log2;
import app.misono.unit206.element.Element;
import app.misono.unit206.misc.ImageUtils;
import app.misono.unit206.misc.Saf;
import app.misono.unit206.misc.Views;
import app.misono.unit206.task.Taskz;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractPage implements Page {
	private static final String TAG = "AbstractPage";

	protected static final int COLOR_BG_TRANSPARENT = 0xAA222222;

	protected PageActivity activity;
	protected FrameLayout mAdBase;
	protected FrameLayout mParent;
	protected FrameLayout mBase;
	protected PageManager manager;
	protected Runnable mClickBack;
	protected boolean mPortrait;
	protected float dp1;
	protected int mWidth;
	protected int mHeightAdBase;
	protected int mHeight;

	private final SparseArray<Runnable> menues;
	private final FrameAnimator animator;
	private final Set<Element> elements;

	private CallbackMenu cbMenu;
	private PagePref pref;
	private View smoke;
	private Saf saf;
	private boolean enableToolbar;
	private boolean isHamburgerIcon;
	private int idMenu;
	private int hAdView;

	@Deprecated
	protected AbstractPage(
		@NonNull PageManager manager,
		@NonNull PageActivity activity,
		@NonNull FrameLayout parent,
		@Nullable Runnable clickBack
	) {
		this(manager, activity, parent, 150, clickBack);
	}

	@Deprecated
	protected AbstractPage(
		@NonNull PageManager manager,
		@NonNull PageActivity activity,
		@NonNull FrameLayout parent,
		int msecAnimator,
		@Nullable Runnable clickBack
	) {
		this(activity, 150, clickBack);
	}

	protected AbstractPage(
		@NonNull PageActivity activity,
		@Nullable Runnable clickBack
	) {
		this(activity, 150, clickBack);
	}

	protected AbstractPage(
		@NonNull PageActivity activity,
		int msecAnimator,
		@Nullable Runnable clickBack
	) {
		this.activity = activity;
		manager = activity.getPageManager();
		mParent = activity.getParentView();
		mAdBase = mParent;
		mClickBack = clickBack;
		enableToolbar = true;
		isHamburgerIcon = true;
		menues = new SparseArray<>();
		elements = new HashSet<>();
		dp1 = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			1,
			activity.getResources().getDisplayMetrics()
		);
		mBase = new FrameLayout(mParent.getContext());
		mBase.setOnClickListener(null);
		if (msecAnimator != 0) {
			animator = new FrameAnimator(msecAnimator);
		} else {
			animator = null;
		}
	}

	@Override
	@NonNull
	public FrameLayout getBaseView() {
		return mBase;
	}

	protected void addElement(@NonNull Element element) {
		elements.add(element);
	}

	protected void removeElement(@Nullable Element element) {
		elements.remove(element);
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

	protected void setBackIcon() {
		isHamburgerIcon = false;
	}

	protected void setHamburgerIcon() {
		isHamburgerIcon = true;
	}

	@Override
	public boolean isHamburgerIcon() {
		return isHamburgerIcon;
	}

	protected void setAdViewHeight(int h) {
		hAdView = h;
	}

	@Override
	public void changeLayout(int wAdBase, int hAdBase) {
		mWidth = wAdBase;
		mHeightAdBase = hAdBase;
		mHeight = hAdBase - hAdView;
		mPortrait = mWidth < mHeight;
		log("changeLayout:" + wAdBase + " " + hAdBase + " " + mHeight);
		FrameLayout.LayoutParams p = (FrameLayout.LayoutParams)mBase.getLayoutParams();
		if (p != null) {
			p.width = mWidth;
			p.height = mHeight;
			mBase.setLayoutParams(p);
		}
		log("changeLayout:done");
	}

	@Override
	public void onResume() {
		Toolbar toolbar = activity.getToolbar();
		if (toolbar != null) {
			toolbar.setVisibility(enableToolbar ? View.VISIBLE : View.GONE);
		}
		for (Element element : elements) {
			element.onResume();
		}
		if (saf != null) {
			saf.setCallback();
		}
	}

	@Override
	public void onPause() {
		Toolbar toolbar = activity.getToolbar();
		if (toolbar != null) {
			toolbar.setVisibility(View.VISIBLE);
		}
		for (Element element : elements) {
			element.onPause();
		}
		unblockClick();
	}

	public void setToolbarIcon(@DrawableRes int idDrawable, float hRatio) {
		Toolbar toolbar = activity.getToolbar();
		if (toolbar != null) {
			Resources r = activity.getResources();
			Drawable d = ResourcesCompat.getDrawable(r, idDrawable, null);
			if (d != null) {
				int hToolbar = toolbar.getHeight();
				int wh = (int)(hToolbar * hRatio);
				Bitmap bitmap = ImageUtils.createBitmap(d, wh, wh, Color.TRANSPARENT);
				toolbar.setLogo(new BitmapDrawable(r, bitmap));
			}
		}
	}

	@NonNull
	protected File getCacheFile(@NonNull String fname) {
		File dir = activity.getCacheDir();
		return new File(dir, fname);
	}

	@NonNull
	protected File getStorageFile(@NonNull String fname) {
		File dir = activity.getFilesDir();
		return new File(dir, fname);
	}

	protected void blockClick() {
		activity.blockClick();
	}

	protected void unblockClick() {
		activity.unblockClick();
	}

	protected void keepScreenOn() {
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	protected void notkeepScreenOn() {
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	protected void hideIme() {
		View v = activity.getCurrentFocus();
		if (v != null) {
			InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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

	@Override
	public void removeFromParentImmediately() {
		mParent.removeView(mBase);
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

	protected void setOptionsMenu(@MenuRes int idMenu) {
		this.idMenu = idMenu;
		cbMenu = null;
	}

	protected void setOptionsMenu(@MenuRes int idMenu, @Nullable CallbackMenu cbMenu) {
		this.idMenu = idMenu;
		this.cbMenu = cbMenu;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (idMenu != 0) {
			activity.getMenuInflater().inflate(idMenu, menu);
			if (cbMenu != null) {
				cbMenu.callback(menu);
			}
			return true;
		}
		return false;
	}

	@Override
	public void addMenuCallback(int idItem, @Nullable Runnable menu) {
		menues.append(idItem, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		Runnable menu = menues.get(id);
		if (menu != null) {
			menu.run();
			return true;
		}
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
	@AnyThread
	@NonNull
	public Task<Void> showSnackbarTask(@NonNull String msg) {
		return Taskz.call(() -> {
			Snackbar.make(mBase, msg, Snackbar.LENGTH_LONG).show();
			return null;
		});
	}

	@Override
	@AnyThread
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

	@Override
	@NonNull
	public Snackbar showSnackProgress(@NonNull String msg) {
		return Views.showSnackProgress(mBase, msg);
	}

	@Override
	@NonNull
	public Snackbar showSnackProgress(
		@NonNull String msg,
		@StringRes int idAction,
		@NonNull View.OnClickListener listener
	) {
		return Views.showSnackProgress(mBase, msg, idAction, listener);
	}

	@Override
	@MainThread
	public boolean showSnackStackTrace2(@Nullable Throwable e) {
		boolean rc = Taskz.printStackTrace2(e);
		if (rc && e != null) {
			Snackbar.make(mBase, e.toString(), Snackbar.LENGTH_LONG).show();
		}
		return rc;
	}

	@Override
	@AnyThread
	public boolean showSnackStackTrace2Task(@Nullable Throwable e) {
		boolean rc = Taskz.printStackTrace2(e);
		if (rc && e != null) {
			Taskz.call(() -> {
				Snackbar.make(mBase, e.toString(), Snackbar.LENGTH_LONG).show();
				return null;
			}).addOnFailureListener(Taskz::printStackTrace2);
		}
		return rc;
	}

	protected FloatingActionButton addFab(
		@NonNull Context context,
		@DrawableRes int idDrawable
	) {
		FloatingActionButton rc = new FloatingActionButton(context);
		rc.setImageResource(idDrawable);
		int m = (int)(16 * dp1);
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.WRAP_CONTENT,
			FrameLayout.LayoutParams.WRAP_CONTENT
		);
		p.setMargins(0, 0, m, m);
		p.gravity = Gravity.END | Gravity.BOTTOM;
		mBase.addView(rc, p);
		return rc;
	}

	public void setFullScreen(boolean on) {
		View decorView = activity.getWindow().getDecorView();
		int uiSystem;
		if (on) {
			uiSystem = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		} else {
			uiSystem = 0;
		}
		decorView.setSystemUiVisibility(uiSystem);
	}

	protected void setPref(@NonNull PagePref pref) {
		this.pref = pref;
		manager.loadPref(pref);
	}

	protected void setSaf(@Nullable Saf saf) {
		this.saf = saf;
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

	@Override
	@NonNull
	public String getString(@StringRes int id) {
		return activity.getString(id);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
