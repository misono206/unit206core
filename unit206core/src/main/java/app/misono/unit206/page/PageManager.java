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

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.HamburgerMenu;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PageManager implements Closeable {
	private static final String TAG = "PageManager";
	private static final String BUNDLE_ARRAY_PAGE = "bundle_array_page";

	private final List<Page> list;
	private final List<Page> stack;
	private final Bundle savedInstanceState;

	private HamburgerMenu hamburger;
	private int mWidth;
	private int mHeight;

	public PageManager(@Nullable Bundle savedInstanceState) {
		this.savedInstanceState = savedInstanceState;
		list = new ArrayList<>();
		stack = new ArrayList<>();
	}

	public void setHamburger(@Nullable HamburgerMenu hamburger) {
		this.hamburger = hamburger;
	}

	private void refreshTopPage() {
		Page page = getTopPage();
		if (page != null) {
			refreshTopPage(page);
		}
	}

	private void refreshTopPage(@NonNull Page page) {
		setActionbarTitle(page);
		if (mWidth != 0 && mHeight != 0) {
			page.changeLayout(mWidth, mHeight);
		}
	}

	public void refreshActionBar(@NonNull Page me) {
		Page page = getTopPage();
		if (page == me) {
			setActionbarTitle(page);
		}
	}

	private void setActionbarTitle(@NonNull Page page) {
		if (hamburger != null) {
			String title = page.refreshPage();
			hamburger.setTitle(title);
			if (page.isHamburgerIcon()) {
				hamburger.showHamburgerIcon();
			} else {
				hamburger.showBackIcon();
			}
		}
	}

	public void setHamburgerTitle(@NonNull String title) {
		if (hamburger != null) {
			hamburger.setTitle(title);
		}
	}

	public void addInstance(@NonNull Page page) {
		list.add(page);
	}

	public void removeInstance(@NonNull Page page) {
		PagePref pref = page.getPref();
		if (pref != null) {
			pref.apply();
		}
		list.remove(page);
	}

	public void addStack(@NonNull Page page, @Nullable Runnable done) {
		Page top = getTopPage();
		if (top != null) {
			top.onPause();
		}
		stack.add(page);
		refreshTopPage(page);
		page.addToParent(done);
		page.onResume();
	}

	public void addStackIfNeed(@NonNull Page page, @Nullable Runnable done) {
		if (!stack.contains(page)) {
			Page top = getTopPage();
			if (top != null && page != top) {
				top.onPause();
			}
			stack.add(page);
			refreshTopPage(page);
			page.addToParent(done);
			page.onResume();
		} else {
			if (done != null) {
				done.run();
			}
		}
	}

	public boolean removeStack(@NonNull Page page, @Nullable Runnable done) {
		page.removeFromParent(done);
		page.onPause();
		stack.remove(page);
		refreshTopPage();
		boolean empty = stack.isEmpty();
		if (!empty) {
			Page top = getTopPage();
			if (top != null) {
				top.onResume();
			}
		}
		return empty;
	}

	public void removeStackOrFinish(@NonNull Activity activity, @NonNull Page page, @Nullable Runnable done) {
		if (stack.size() <= 1) {
			activity.finish();
		} else {
			page.onPause();
			page.removeFromParent(done);
			stack.remove(page);
			refreshTopPage();
			Page top = getTopPage();
			if (top != null) {
				top.onResume();
			}
		}
	}

	public boolean backToStack(@NonNull Page page, @Nullable Runnable done) {
		int size = stack.size();
		for ( ; 0 < size; ) {
			int index = size - 1;
			Page ref = stack.get(index);
			if (ref == page) {
				break;
			}
			removeStack(ref, done);
			done = null;
			size = index;
		}
		refreshTopPage();
		return stack.isEmpty();
	}

	public void gotoStack(@NonNull Page page, @Nullable Runnable done) {
		if (stack.contains(page)) {
			backToStack(page, done);
		} else {
			addStack(page, done);
		}
	}

	public int getStackSize() {
		return stack.size();
	}

	public boolean isEmptyStack() {
		return stack.isEmpty();
	}

	@Nullable
	public Page getTopPage() {
		if (stack.isEmpty()) {
			return null;
		} else {
			return stack.get(stack.size() - 1);
		}
	}

	@Override
	public void close() {
		for (Page page : list) {
			PagePref pref = page.getPref();
			if (pref != null) {
				pref.apply();
			}
			if (page instanceof Closeable) {
				try {
					((Closeable)page).close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		list.clear();
		stack.clear();
	}

	@NonNull
	public Bundle createBundle() {
		Bundle bundle = new Bundle();
		for (Page page : list) {
			PagePref pref = page.getPref();
			if (pref != null) {
				Bundle b = pref.createBundle();
				if (b != null) {
					bundle.putBundle(pref.getKey(), b);
				}
			}
		}
		ArrayList<String> names = new ArrayList<>();
		for (Page page : stack) {
			names.add(page.getTag());
		}
		bundle.putStringArrayList(BUNDLE_ARRAY_PAGE, names);
		return bundle;
	}

	public void restoreSnapshot(@NonNull Bundle bundle) {
/*
		Map<String, Page> map = new HashMap<>();
		for (Page page : list) {
			String tag = page.getTag();
			map.put(tag, page);
			PagePref pref = page.getPref();
			if (pref != null) {
				Bundle b = bundle.getBundle(tag);
				if (b != null) {
					pref.restoreBundle(b);
				}
			}
		}
*/
		ArrayList<String> names = bundle.getStringArrayList(BUNDLE_ARRAY_PAGE);
		if (names != null) {
			for (String name : names) {
				for (Page page : list) {
					if (name.contentEquals(page.getTag())) {
						stack.add(page);
						break;
					}
				}
			}
		}
	}

	void loadPref(@NonNull PagePref pref) {
		if (savedInstanceState != null) {
			Bundle b = savedInstanceState.getBundle(pref.getKey());
			if (b != null) {
				pref.restoreBundle(b);
			}
		}
	}

	public void changeLayout(int w, int h) {
		Page page = getTopPage();
		if (page != null && (mWidth != w || mHeight != h)) {
			mWidth = w;
			mHeight = h;
			page.changeLayout(w, h);
		}
	}

	public void enableDrawer() {
		if (hamburger != null) {
			hamburger.enableDrawer();
		}
	}

	public void disableDrawer() {
		if (hamburger != null) {
			hamburger.disableDrawer();
		}
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
