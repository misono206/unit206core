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

package app.misono.unit206.misc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import app.misono.unit206.debug.Log2;

import com.google.android.material.navigation.NavigationView;

public class HamburgerMenu {
	private static final String TAG = "HamburgerMenu";

	private ActionBarDrawerToggle mToggle;
	private DrawerLayout mDrawer;
	private ActionBar mActionBar;
	private Toolbar mToolbar;

	public interface ClickMenu {
		void onClicked(int idMenu);
	}

	public HamburgerMenu() {
	}

	public void onCreate(
		@NonNull AppCompatActivity activity,
		@NonNull Toolbar toolbar,
		@NonNull DrawerLayout drawer,
		@NonNull NavigationView navi,
		@StringRes int idDrawerOpen,
		@StringRes int idDrawerClose,
		@NonNull Runnable clickBack,
		@NonNull ClickMenu listener
	) {
		mToolbar = toolbar;
		mDrawer = drawer;
		activity.setSupportActionBar(mToolbar);
		mToggle = new ActionBarDrawerToggle(activity, drawer, mToolbar, idDrawerOpen, idDrawerClose);
		drawer.addDrawerListener(mToggle);
		mToggle.setToolbarNavigationClickListener(v -> {
			clickBack.run();
		});
		mActionBar = activity.getSupportActionBar();
		navi.setNavigationItemSelectedListener(item -> {
			drawer.closeDrawer(navi);
			listener.onClicked(item.getItemId());
			return false;
		});
	}

	public boolean onBackPressed() {
		if (mDrawer.isDrawerOpen(GravityCompat.START)) {
			mDrawer.closeDrawers();
			return true;
		}
		return false;
	}

	public void onPostCreate() {
		mToggle.syncState();
	}

	public void setTitle(@Nullable CharSequence title) {
		if (title != null) {
			mToolbar.setTitle(title);
		}
	}

	public void setTitle(@StringRes int idRes) {
		mToolbar.setTitle(idRes);
	}

	public void setSubtitle(@Nullable CharSequence title) {
		if (title != null) {
			mToolbar.setSubtitle(title);
		}
	}

	public void setSubtitle(@StringRes int idRes) {
		mToolbar.setSubtitle(idRes);
	}

	public void showBackIcon() {
		mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		// Remove hamburger
		mToggle.setDrawerIndicatorEnabled(false);
		// Show back button
		mActionBar.setDisplayHomeAsUpEnabled(true);
	}

	public void showHamburgerIcon() {
		mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		// Remove back button
		mActionBar.setDisplayHomeAsUpEnabled(false);
		// Show hamburger
		mToggle.setDrawerIndicatorEnabled(true);
	}

	public void enableDrawer() {
		mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
	}

	public void disableDrawer() {
		mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}

	public void addDrawerListener(@NonNull DrawerLayout.DrawerListener listener) {
		mDrawer.addDrawerListener(listener);
	}

	public void removeDrawerListener(@NonNull DrawerLayout.DrawerListener listener) {
		mDrawer.removeDrawerListener(listener);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
