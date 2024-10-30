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

package app.misono.unit206.misc;

import android.util.SparseArray;

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

	private final SparseArray<Runnable> cbMenu;

	private ActionBarDrawerToggle toggle;
	private DrawerLayout drawer;
	private ActionBar actionBar;
	private Toolbar toolbar;

	@Deprecated
	public interface ClickMenu {
		void onClicked(int idMenu);
	}

	public HamburgerMenu() {
		cbMenu = new SparseArray<>();
	}

	@Deprecated
	public void onCreate(
		@NonNull AppCompatActivity activity,
		@Nullable Toolbar toolbar,
		@NonNull DrawerLayout drawer,
		@NonNull NavigationView navi,
		@StringRes int idDrawerOpen,
		@StringRes int idDrawerClose,
		@NonNull Runnable clickBack,
		@NonNull ClickMenu listener
	) {
		this.toolbar = toolbar;
		this.drawer = drawer;
		if (toolbar != null) {
			activity.setSupportActionBar(toolbar);
			toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, idDrawerOpen, idDrawerClose);
			drawer.addDrawerListener(toggle);
			toggle.setToolbarNavigationClickListener(v -> {
				clickBack.run();
			});
			actionBar = activity.getSupportActionBar();
		}
		navi.setNavigationItemSelectedListener(item -> {
			drawer.closeDrawer(navi);
			listener.onClicked(item.getItemId());
			return false;
		});
	}

	public void onCreate(
		@NonNull AppCompatActivity activity,
		@Nullable Toolbar toolbar,
		@NonNull DrawerLayout drawer,
		@NonNull NavigationView navi,
		@StringRes int idDrawerOpen,
		@StringRes int idDrawerClose,
		@NonNull Runnable clickBack
	) {
		this.toolbar = toolbar;
		this.drawer = drawer;
		if (toolbar != null) {
			activity.setSupportActionBar(toolbar);
			toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, idDrawerOpen, idDrawerClose);
			drawer.addDrawerListener(toggle);
			toggle.setToolbarNavigationClickListener(v -> {
				clickBack.run();
			});
			actionBar = activity.getSupportActionBar();
		}
		navi.setNavigationItemSelectedListener(item -> {
			drawer.closeDrawer(navi);
			int idMenu = item.getItemId();
			Runnable cb = cbMenu.get(idMenu);
			if (cb != null) {
				cb.run();
			}
			return false;
		});
	}

	public void addMenuCallback(int idMenu, @NonNull Runnable callback) {
		cbMenu.append(idMenu, callback);
	}

	public boolean onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawers();
			return true;
		}
		return false;
	}

	public void onPostCreate() {
		if (toggle != null) {
			toggle.syncState();
		}
	}

	public void setTitle(@Nullable CharSequence title) {
		if (toolbar != null && title != null) {
			toolbar.setTitle(title);
		}
	}

	public void setTitle(@StringRes int idRes) {
		if (toolbar != null) {
			toolbar.setTitle(idRes);
		}
	}

	public void setSubtitle(@Nullable CharSequence title) {
		if (toolbar != null && title != null) {
			toolbar.setSubtitle(title);
		}
	}

	public void setSubtitle(@StringRes int idRes) {
		if (toolbar != null) {
			toolbar.setSubtitle(idRes);
		}
	}

	public void showBackIcon() {
		drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		if (toggle != null) {
			// Remove hamburger
			toggle.setDrawerIndicatorEnabled(false);
		}
		if (actionBar != null) {
			// Show back button
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	public void showHamburgerIcon() {
		drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		if (actionBar != null) {
			// Remove back button
			actionBar.setDisplayHomeAsUpEnabled(false);
		}
		if (toggle != null) {
			// Show hamburger
			toggle.setDrawerIndicatorEnabled(true);
		}
	}

	public void enableDrawer() {
		drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
	}

	public void disableDrawer() {
		drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}

	public void addDrawerListener(@NonNull DrawerLayout.DrawerListener listener) {
		drawer.addDrawerListener(listener);
	}

	public void removeDrawerListener(@NonNull DrawerLayout.DrawerListener listener) {
		drawer.removeDrawerListener(listener);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
