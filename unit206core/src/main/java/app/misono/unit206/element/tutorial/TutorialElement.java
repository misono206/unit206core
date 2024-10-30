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

package app.misono.unit206.element.tutorial;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.misono.unit206.callback.CallbackInteger;
import app.misono.unit206.debug.Log2;
import app.misono.unit206.element.Element;
import app.misono.unit206.misc.RecyclerPagerListener;
import app.misono.unit206.misc.UnitPref;

import java.util.List;

public class TutorialElement<T extends TutorialItem, V extends View> implements Element {
	private static final String TAG = "TutorialElement";

	private final RecyclerPagerListener listenerPager;
	private final TutorialAdapter<T, V> adapter;
	private final TutorialCardLayout<V> layout;
	private final RecyclerView recycler;

	private int pxWidth, pxHeight;

	private CallbackInteger listenerOnSelected;

	public interface ViewCreator<T, V> {
		@NonNull
		V createView(@NonNull Context context, @NonNull T item);
	}

	public TutorialElement(
		@NonNull FrameLayout parent,
		@NonNull DiffUtil.ItemCallback<T> diffCallback,
		@NonNull ViewCreator<T, V> creator,
		@NonNull TutorialCardLayout<V> layout,
		@Nullable CallbackInteger listenerOnSelected
	) {
		this.layout = layout;
		this.listenerOnSelected = listenerOnSelected;
		Context context = parent.getContext();
		adapter = new TutorialAdapter<>(diffCallback, creator, layout);
		recycler = new RecyclerView(context);
		LinearLayoutManager manager = new LinearLayoutManager(context);
		manager.setOrientation(LinearLayoutManager.HORIZONTAL);
		recycler.setLayoutManager(manager);
		recycler.setAdapter(adapter);
		recycler.setBackgroundColor(Color.LTGRAY);
		listenerPager = new RecyclerPagerListener(recycler, manager, listenerOnSelected);
		recycler.addOnScrollListener(listenerPager);
		parent.addView(recycler, new FrameLayout.LayoutParams(0, 0));
	}

	@Override
	public void changeLayout(int pxWidth, int pxHeight) {
		log("changeLayout:" + pxWidth + " " + pxHeight);
		if (this.pxWidth != pxWidth || this.pxHeight != pxHeight) {
			this.pxWidth = pxWidth;
			this.pxHeight = pxHeight;
			ViewGroup.LayoutParams p1 = recycler.getLayoutParams();
			p1.width = pxWidth;
			p1.height = pxHeight;
			recycler.requestLayout();
			adapter.setCardPixelSize(pxWidth, pxHeight);
			int n = recycler.getChildCount();
			log("childCount:" + n);
			for (int i = 0; i < n; i++) {
				TutorialCardView view = (TutorialCardView)recycler.getChildAt(i);
				p1 = view.getLayoutParams();
				p1.width = pxWidth;
				p1.height = pxHeight;
				view.setLayoutParams(p1);
				V v = (V)view.getChildAt(0);
				layout.layout(v);
			}
		}
	}

	@Override
	@NonNull
	public View getView() {
		return recycler;
	}

	public void gotoIndex(int index) {
		listenerPager.setSmoothScrolling(index);
		recycler.smoothScrollToPosition(index);
	}

	public void gotoIndexDirectly(int index) {
		recycler.scrollToPosition(index);
		listenerOnSelected.callback(index);
	}

	@Override
	public void onResume() {
	}

	@Override
	public void onPause() {
	}

	@Override
	@Nullable
	public UnitPref getUnitPref() {
		return null;
	}

	public void submitList(@NonNull List<T> list, @Nullable Runnable done) {
		adapter.submitList(list, done);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
