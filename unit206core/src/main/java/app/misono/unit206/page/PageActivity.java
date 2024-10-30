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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import app.misono.unit206.callback.CallbackIntent;
import app.misono.unit206.callback.CallbackUri;
import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.BlockClick;
import app.misono.unit206.misc.HamburgerMenu;
import app.misono.unit206.misc.Views;
import app.misono.unit206.task.Taskz;
import app.misono.unit206.theme.AppStyle;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashSet;
import java.util.Set;

public class PageActivity extends AppCompatActivity {
	private static final String TAG = "PageActivity";

	public static final int CODE_SAF_START = 29000;
	public static final int CODE_SAF_END = 30000;

	private static final String BUNDLE_PAGES = "UNIT206";

	private SparseArray<CallbackActivityResult> cbSaf;	// TODO: old type
	private SparseArray<Runnable> cbPermission;
	private CallbackIntent cbActivityResultIntent;
	private HamburgerMenu hamburger;
	private Set<Runnable> cbOnResume, cbOnPause, cbOnStart, cbOnStop;
	private CallbackUri cbActivityResultUri;
	private AppStyle style;
	private boolean onResume, onStart;

	protected PageManager mManager;
	protected FrameLayout mAdBase;
	protected FrameLayout mParent;
	protected FrameLayout sizeBase;
	protected BlockClick mBlock;
	protected Toolbar vToolbar;
	protected View blockBase;

	public interface CallbackActivityResult {
		void onActivityResult(int codeResult, @Nullable Intent intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle savedInstanceStateUnit206 = null;
		if (savedInstanceState != null) {
			savedInstanceStateUnit206 = savedInstanceState.getBundle(BUNDLE_PAGES);
		}
		mManager = new PageManager(savedInstanceStateUnit206);
		cbOnResume = new HashSet<>();
		cbOnPause = new HashSet<>();
		cbOnStart = new HashSet<>();
		cbOnStop = new HashSet<>();
		cbPermission = new SparseArray<>();
		cbSaf = new SparseArray<>();
	}

	@Override
	public void setSupportActionBar(@Nullable Toolbar toolbar) {
		super.setSupportActionBar(toolbar);
		vToolbar = toolbar;
	}

	@Nullable
	public Toolbar getToolbar() {
		return vToolbar;
	}

	@Nullable
	public TextView getToolbarTextView() {
		if (vToolbar != null) {
			int n = vToolbar.getChildCount();
			for (int i = 0; i < n; i++) {
				View view = vToolbar.getChildAt(i);
				if (view instanceof TextView) {
					return (TextView)view;
				}
			}
		}
		return null;
	}

	public void setToolbarEllipsize(@NonNull TextUtils.TruncateAt truncate) {
		TextView vTitle = getToolbarTextView();
		if (vTitle != null) {
			vTitle.setEllipsize(truncate);
		}
	}

	protected void setBlockClickView(int idSizeView, int idBlockView) {
		blockBase = findViewById(idBlockView);
		sizeBase = findViewById(idSizeView);
	}

	@Nullable
	public <T extends AppStyle> T getAppStyle() {
		return (T)style;
	}

	public void setAppStyle(@NonNull AppStyle style) {
		this.style = style;
	}

	@Override
	protected void onResume() {
		super.onResume();
		onResume = true;
		if (style != null) {
			style.onResumeActivity(this);
		}
		for (Runnable cb : cbOnResume) {
			cb.run();
		}
	}

	public void addOnResumeCallback(@NonNull Runnable callback) {
		cbOnResume.add(callback);
	}

	public void removeOnResumeCallback(@Nullable Runnable callback) {
		cbOnResume.remove(callback);
	}

	public boolean isOnResume() {
		return onResume;
	}

	@Override
	protected void onStart() {
		super.onStart();
		onStart = true;
		for (Runnable cb : cbOnStart) {
			cb.run();
		}
	}

	public void addOnStartCallback(@NonNull Runnable callback) {
		cbOnStart.add(callback);
	}

	public void removeOnStartCallback(@Nullable Runnable callback) {
		cbOnStart.remove(callback);
	}

	public boolean isOnStart() {
		return onStart;
	}

	@Override
	protected void onPause() {
		super.onPause();
		onResume = false;
		for (Runnable cb : cbOnPause) {
			cb.run();
		}
	}

	public void addOnPauseCallback(@NonNull Runnable callback) {
		cbOnPause.add(callback);
	}

	public void removeOnPauseCallback(@Nullable Runnable callback) {
		cbOnPause.remove(callback);
	}

	@Override
	protected void onStop() {
		super.onStop();
		onStart = false;
		for (Runnable cb : cbOnStop) {
			cb.run();
		}
	}

	public void addOnStopCallback(@NonNull Runnable callback) {
		cbOnStop.add(callback);
	}

	public void removeOnStopCallback(@Nullable Runnable callback) {
		cbOnStop.remove(callback);
	}

	@Deprecated	// use Page.setFullScreen()
	public void setFullScreenMode(boolean on) {
		View decorView = getWindow().getDecorView();
		int uiSystem;
		if (on) {
			uiSystem = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		} else {
			uiSystem = 0;
		}
		decorView.setSystemUiVisibility(uiSystem);
	}

	protected void setFullscreen() {
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN
		);
	}

	protected void setParentView(int idAdBase, int idParent) {
		mAdBase = findViewById(idAdBase);
		mParent = findViewById(idParent);
		if (blockBase != null) {
			mBlock = new BlockClick(sizeBase, blockBase, this::changeLayout);
		}
	}

	private void changeLayout() {
		mManager.changeLayout(sizeBase.getWidth(), sizeBase.getHeight());
	}

	protected void setParentView(int idParent) {
		setParentView(idParent, idParent);
	}

	@NonNull
	public FrameLayout getParentView() {
		return mParent;
	}

	@NonNull
	public PageManager getPageManager() {
		return mManager;
	}

	protected void setHamburger(
		@Nullable HamburgerMenu hamburger,
		int idAdBase,
		int idParent
	) {
		this.hamburger = hamburger;
		mManager.setHamburger(hamburger);
		mAdBase = findViewById(idAdBase);
		mParent = findViewById(idParent);
		if (blockBase != null) {
			mBlock = new BlockClick(sizeBase, blockBase, this::changeLayout);
		}
	}

	protected void setHamburger(
		@Nullable HamburgerMenu hamburger,
		int idParent
	) {
		setHamburger(hamburger, idParent, idParent);
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		log("onPostCreate:");
		super.onPostCreate(savedInstanceState);
		if (hamburger != null) {
			hamburger.onPostCreate();
		}
	}

	@Override
	public void onBackPressed() {
		log("onBackPressed:");
		if (hamburger != null && hamburger.onBackPressed()) {
			// nop
		} else {
			Page page = mManager.getTopPage();
			if (page != null) {
				page.onBackPressed();
			} else {
				super.onBackPressed();
			}
		}
		log("onBackPressed:done");
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		log("onSaveInstanceState:");
		super.onSaveInstanceState(outState);
		outState.putBundle(BUNDLE_PAGES, mManager.createBundle());
	}

	public void setSafCallback(@Nullable CallbackUri callback) {
		cbActivityResultUri = callback;
		cbActivityResultIntent = null;
	}

	public void setSafCallback(@Nullable CallbackIntent callback) {
		cbActivityResultIntent = callback;
		cbActivityResultUri = null;
	}

	@Override
	protected void onActivityResult(int codeRequest, int codeResult, @Nullable Intent data) {
		log("onActivityResult:" + codeRequest + " " + codeResult + " " + data);
		boolean callSuper = true;
		if (CODE_SAF_START <= codeRequest
			&& codeRequest < CODE_SAF_END
			&& (cbActivityResultUri != null || cbActivityResultIntent != null)
		) {
			if (codeResult == Activity.RESULT_OK && data != null) {
				if (cbActivityResultUri != null) {
					Uri uri = data.getData();
					if (uri != null) {
						callSuper = false;
						cbActivityResultUri.callback(uri);
					}
				} else {
					callSuper = false;
					cbActivityResultIntent.callback(data);
				}
			}
		} else {
			// TODO: old type???
			CallbackActivityResult called = cbSaf.get(codeRequest);
			if (called != null) {
				callSuper = false;
				cbSaf.remove(codeRequest);
				called.onActivityResult(codeResult, data);
			}
		}
		if (callSuper) {
			super.onActivityResult(codeRequest, codeResult, data);
		}
	}

	/**
	 * load with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	public boolean loadWithSaf(@NonNull String typeMime, int code) {
		boolean rc;
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType(typeMime);
		try {
			startActivityForResult(intent, code);
			rc = true;
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			rc = false;
		}
		return rc;
	}

	/**
	 * save with startActivityForResult.
	 *
	 * @return true if success.
	 */
	@AnyThread
	public boolean saveWithSaf(@NonNull String fileName, @NonNull String typeMime, int code) {
		boolean rc;
		Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setType(typeMime);
		intent.putExtra(Intent.EXTRA_TITLE, fileName);
		try {
			startActivityForResult(intent, code);
			rc = true;
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			rc = false;
		}
		return rc;
	}

	@AnyThread
	public boolean saveStringWithSaf(@NonNull String fileName, int code) {
		return saveWithSaf(fileName, "text/plain", code);
	}

	@Deprecated
	public void addOnActivityResult(int codeRequest, @NonNull CallbackActivityResult callback) {
		cbSaf.append(codeRequest, callback);
	}

	@Deprecated
	public void removeOnActivityResult(int codeRequest) {
		cbSaf.remove(codeRequest);
	}

	@Override
	public void onRequestPermissionsResult(
		int requestCode,
		@NonNull String[] permissions,
		@NonNull int[] grantResults
	) {
		log("onRequestPermissionsResult:" + requestCode);
		Runnable called = cbPermission.get(requestCode);
		if (called != null) {
			cbPermission.remove(requestCode);
			called.run();
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	public void addOnRequestPermissionsResult(int codeRequest, @NonNull Runnable callback) {
		cbPermission.append(codeRequest, callback);
	}

	public void removeOnRequestPermissionsResult(int codeRequest) {
		cbPermission.remove(codeRequest);
	}

	@Override
	public boolean onCreateOptionsMenu(@NonNull Menu menu) {
		log("onCreateOptionsMenu:");
		Page page = mManager.getTopPage();
		if (page != null) {
			page.onCreateOptionsMenu(menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		Page page = mManager.getTopPage();
		boolean known = false;
		if (page != null) {
			known = page.onOptionsItemSelected(item);
		}
		if (!known) {
			known = super.onOptionsItemSelected(item);
		}
		return known;
	}

	@Override
	protected void onDestroy() {
		log("onDestroy:");
		super.onDestroy();
		mManager.close();
	}

	public void blockClick() {
		BlockClick block = mBlock;
		if (block != null) {
			block.block();
		} else {
			Log2.e(TAG, "blockClick: block == null...");
		}
	}

	public void unblockClick() {
		BlockClick block = mBlock;
		if (block != null) {
			block.unblock();
		} else {
			Log2.e(TAG, "unblockClick: block == null...");
		}
	}

	@MainThread
	public void showSnackbar(@NonNull String msg) {
		Snackbar.make(mAdBase, msg, Snackbar.LENGTH_LONG).show();
	}

	@MainThread
	public void showSnackbar(@StringRes int idMessage) {
		Snackbar.make(mAdBase, idMessage, Snackbar.LENGTH_LONG).show();
	}

	@NonNull
	public Snackbar showSnackProgress(@StringRes int idMessage) {
		return Views.showSnackProgress(mAdBase, idMessage);
	}

	@NonNull
	public Snackbar showSnackProgress(
		@StringRes int idMessage,
		@StringRes int idAction,
		@NonNull View.OnClickListener listener
	) {
		return Views.showSnackProgress(mAdBase, idMessage, idAction, listener);
	}

	@NonNull
	public Snackbar showSnackProgress(@NonNull String msg) {
		return Views.showSnackProgress(mAdBase, msg);
	}

	@MainThread
	public boolean showSnackStackTrace2(@Nullable Throwable e) {
		boolean rc = Taskz.printStackTrace2(e);
		if (rc && e != null) {
			Snackbar.make(mAdBase, e.toString(), Snackbar.LENGTH_LONG).show();
		}
		return rc;
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
