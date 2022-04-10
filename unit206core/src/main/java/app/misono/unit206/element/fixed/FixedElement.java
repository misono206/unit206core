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

package app.misono.unit206.element.fixed;

import android.widget.FrameLayout;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.misono.unit206.element.Element;
import app.misono.unit206.misc.UnitPref;

import java.util.Set;

public class FixedElement<
	A extends FixedAdapter<C, L, I, V>,
	C extends FixedCardView<C, L, I>,
	L extends FixedCardLayout<C, L, I>,
	I extends FixedItem,
	V extends FixedViewHolder<C, L, I>
> implements Element {
	private static final String TAG = "FixedElement";

	private final DefaultItemAnimator da;

	protected GridLayoutManager manager;
	protected RecyclerView base;
	protected A adapter;
	protected L layout;

	private FrameLayout.LayoutParams params;
	private int nSpan;
	private int durationMove, durationAdd;

	public FixedElement(@NonNull FrameLayout parent) {
		base = new RecyclerView(parent.getContext());
		nSpan = 1;
		durationMove = 300;
		durationAdd = 300;
		manager = new GridLayoutManager(parent.getContext(), nSpan);
		manager.setOrientation(LinearLayoutManager.VERTICAL);
		manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				return position == 0 ? nSpan : 1;
			}
		});
		base.setLayoutManager(manager);

		da = new DefaultItemAnimator();
		da.setSupportsChangeAnimations(false);
		da.setMoveDuration(durationMove);
		da.setAddDuration(durationAdd);
		base.setItemAnimator(da);

		parent.addView(base);
	}

	@Override
	public void onResume() {
	}

	@Override
	public void onPause() {
	}

	public void setAdapter(@NonNull L layout, @NonNull A adapter) {
		this.layout = layout;
		this.adapter = adapter;
		adapter.layoutCard(layout);
		base.setAdapter(adapter);
	}

	public void setMoveDuration(int msec) {
		durationMove = msec;
//		da.setMoveDuration(durationMove);
	}

	public void setAddDuration(int msec) {
		durationAdd = msec;
//		da.setAddDuration(durationAdd);
	}

	@Override
	public void setLayoutParams(@NonNull FrameLayout.LayoutParams params) {
		this.params = params;
		base.setLayoutParams(params);
		Set<V> set = adapter.getAttachedViewHolders();
		for (V holder : set) {
			C card = holder.getView();
			layout.refreshCard(card);
		}
	}

	public void refreshLayout() {
		if (params != null) {
			setLayoutParams(params);
		}
	}

	public void setPadding(int left, int top, int right, int bottom) {
		base.setPadding(left, top, right, bottom);
	}

	@MainThread
	public void setSpanCount(int countSpan) {
		manager.requestSimpleAnimationsInNextLayout();
		manager.setSpanCount(countSpan);
	}

	@Override
	@Nullable
	public UnitPref getUnitPref() {
		return null;
	}

}
