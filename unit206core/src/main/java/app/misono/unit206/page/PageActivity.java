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

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.BlockClick;
import app.misono.unit206.misc.HamburgerMenu;

import com.google.android.gms.ads.AdView;

import java.util.HashSet;
import java.util.Set;

public class PageActivity extends AppCompatActivity {
	private static final String TAG = "PageActivity";

	private static final String BUNDLE_PAGES = "UNIT206";

	private SparseArray<CallbackActivityResult> cbSaf;
	private SparseArray<Runnable> cbPermission;
	private HamburgerMenu hamburger;
	private Set<Runnable> cbOnResume, cbOnPause, cbOnStart, cbOnStop;

	protected PageManager mManager;
	protected FrameLayout mAdBase;
	protected FrameLayout mParent;
	protected FrameLayout sizeBase;
	protected BlockClick mBlock;
	protected Toolbar vToolbar;
	protected AdView mAdView;
	protected View blockBase;

	private boolean onResume, onStart;

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

	protected void setBlockClickView(int idSizeView, int idBlockView) {
		blockBase = findViewById(idBlockView);
		sizeBase = findViewById(idSizeView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		onResume = true;
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

	protected void setParentView(
		int idAdBase,
		int idParent,
		int idAdView
	) {
		mAdBase = findViewById(idAdBase);
		mParent = findViewById(idParent);
		if (idAdView != 0) {
			mAdView = findViewById(idAdView);
		}
		if (blockBase != null) {
			mBlock = new BlockClick(sizeBase, blockBase, this::changeLayout);
		}
	}

	private void changeLayout() {
		mManager.changeLayout(sizeBase.getWidth(), sizeBase.getHeight());
	}

	protected void setParentView(int idParent) {
		setParentView(idParent, idParent, 0);
	}

	protected void setHamburger(
		@Nullable HamburgerMenu hamburger,
		int idAdBase,
		int idParent,
		int idAdView
	) {
		this.hamburger = hamburger;
		mManager.setHamburger(hamburger);
		mAdBase = findViewById(idAdBase);
		mParent = findViewById(idParent);
		if (idAdView != 0) {
			mAdView = findViewById(idAdView);
		}
		if (blockBase != null) {
			mBlock = new BlockClick(sizeBase, blockBase, this::changeLayout);
		}
	}

	protected void setHamburger(
		@Nullable HamburgerMenu hamburger,
		int idParent
	) {
		setHamburger(hamburger, idParent, idParent, 0);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		log("onActivityResult:" + requestCode + " " + resultCode);
		CallbackActivityResult called = cbSaf.get(requestCode);
		if (called != null) {
			cbPermission.remove(requestCode);
			called.onActivityResult(resultCode, data);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void addOnActivityResult(int codeRequest, @NonNull CallbackActivityResult callback) {
		cbSaf.append(codeRequest, callback);
	}

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
	public boolean onCreateOptionsMenu(Menu menu) {
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

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
