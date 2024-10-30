/*
 * Copyright 2022 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.element.textlist;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.element.Element;
import app.misono.unit206.misc.UnitPref;

import java.util.Collections;
import java.util.List;

public class TextListElement<I extends TextListItem> implements Element {
	private static final String TAG = "TextListElement";

	private final TextListAdapter<I> adapter;
	private final FrameLayout base;

	private List<I> list;

	public TextListElement(
		@NonNull Context context,
		@NonNull TextListCardStyle<I> style,
		@Nullable TextListSeeker<I> seeker,
		@Nullable TextListCallback<I> clickItem,
		@Nullable TextListCallback<I> clickInfo
	) {
		base = new FrameLayout(context);
		adapter = new TextListAdapter<>(style, seeker, clickItem, clickInfo);
		RecyclerView recycler = new RecyclerView(context);
		recycler.setBackgroundColor(Color.GRAY);
		base.addView(recycler, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		LinearLayoutManager man = new LinearLayoutManager(context);
		man.setOrientation(RecyclerView.VERTICAL);
		recycler.setLayoutManager(man);
		recycler.setAdapter(adapter);
	}

	public void setViewType(int typeView) {
		adapter.setViewType(typeView);
	}

	public void submitList(@NonNull List<I> list, @Nullable Runnable done) {
		log("submitList:" + list.size());
		this.list = list;
		adapter.submitList(list, done);
	}

	public void notifyDataSetChanged() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void changeLayout(int w, int h) {
		adapter.changeLayout(w, h);
		adapter.submitList(Collections.emptyList(), () -> {
			adapter.submitList(list);
		});
	}

	@NonNull
	@Override
	public View getView() {
		return base;
	}

	@Override
	public void onResume() {
	}

	@Override
	public void onPause() {
	}

	@Nullable
	@Override
	public UnitPref getUnitPref() {
		return null;
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}
}