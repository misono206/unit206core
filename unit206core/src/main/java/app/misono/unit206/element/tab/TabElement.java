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

package app.misono.unit206.element.tab;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.misono.unit206.element.Element;
import app.misono.unit206.misc.RecyclerPagerListener;
import app.misono.unit206.misc.UnitPref;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TabElement implements Element {
	private static final String TAG = "TabElement";

	private final RecyclerPagerListener listenerPager;
	private final Set<Selected> listeners;
	private final LinearLayout base;
	private final RecyclerView recycler;
	private final List<String> titles;
	private final TabLayout tabs;

	private int posLatest;

	public interface Selected {
		void onSelected(@NonNull String title);
	}

	public TabElement(
		@NonNull ViewGroup parent,	// TODO: FrameLayout
		@NonNull ViewGroup.LayoutParams param, // TODO: @Deprecated
		@NonNull RecyclerView.Adapter<?> adapter
	) {
		titles = new ArrayList<>();
		listeners = new HashSet<>();
		Context context = parent.getContext();
		base = new LinearLayout(context);
		base.setOrientation(LinearLayout.VERTICAL);
		tabs = new TabLayout(context);
		base.addView(tabs, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		recycler = new RecyclerView(context);
		LinearLayoutManager rman = new LinearLayoutManager(context);
		rman.setOrientation(LinearLayoutManager.HORIZONTAL);
		recycler.setLayoutManager(rman);
		recycler.setAdapter(adapter);
		recycler.setBackgroundColor(Color.LTGRAY);
		listenerPager = new RecyclerPagerListener(recycler, rman, pos -> {
			posLatest = pos;
			tabs.selectTab(tabs.getTabAt(pos));
		});
		recycler.addOnScrollListener(listenerPager);
		base.addView(recycler, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		parent.addView(base, param);
		tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				String title = "" + tab.getText();
Log.e(TAG, "onTabSelected:" + title + " " + posLatest);
				int pos = titles.indexOf(title);
				if (0 <= pos && pos != posLatest) {
Log.e(TAG, "COME:");
					posLatest = pos;
					listenerPager.disableOnScroll();
					recycler.smoothScrollToPosition(pos);
				}
				for (Selected listener : listeners) {
					listener.onSelected(title);
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
	}

	public void addListener(@NonNull Selected listener) {
		listeners.add(listener);
	}

	public void removeListener(@Nullable Selected listener) {
		listeners.remove(listener);
	}

	private void setPixelSize(int wPixel, int hPixel) {
		ViewGroup.LayoutParams p1 = base.getLayoutParams();
		p1.width = wPixel;
		p1.height = hPixel;
		base.requestLayout();
	}

	@Override
	public void onResume() {
	}

	@Override
	public void onPause() {
	}

	@Override
	public void setLayoutParams(@NonNull FrameLayout.LayoutParams params) {
		setPixelSize(params.width, params.height);
	}

	@Override
	@Nullable
	public UnitPref getUnitPref() {
		return null;
	}

	@MainThread
	public void setTabTitles(@NonNull List<String> list) {
		if (!areTitlesTheSame(list)) {
			titles.clear();
			titles.addAll(list);
			int n = tabs.getTabCount();
			int n2 = titles.size();
			int min = Math.min(n, n2);
			for (int i = 0; i < min; i++) {
				TabLayout.Tab tab = tabs.getTabAt(i);
				tab.setText(titles.get(i));
			}
			if (n < n2) {
				for (int i = min; i < n2; i++) {
					TabLayout.Tab tab = tabs.newTab();
					tab.setText(titles.get(i));
					tabs.addTab(tab);
				}
			} else if (n2 < n) {
				for (int i = min; i < n; i++) {
					tabs.removeTabAt(i);
				}
			}
		}
	}

	@MainThread
	public boolean areTitlesTheSame(@NonNull List<String> list) {
		int n = list.size();
		int n2 = titles.size();
		if (n != n2) {
			return false;
		}
		for (int i = 0; i < n; i++) {
			if (!titles.get(i).equals(list.get(i))) {
				return false;
			}
		}
		return true;
	}

	public void selectTab(int index) {
Log.e(TAG, "selectTab:" + posLatest);
		tabs.selectTab(tabs.getTabAt(index));
	}

	public void selectTab(@NonNull String title) {
		int index = titles.indexOf(title);
		if (0 <= index) {
			selectTab(index);
		}
	}

}
