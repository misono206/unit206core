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

package app.misono.unit206.element.table;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.misono.unit206.element.Element;
import app.misono.unit206.misc.UnitPref;

import java.util.List;

public class TableElement<T extends TableCardItem> implements Element {
	private static final String TAG = "TableElement";

	private final TableAdapter<T> adapter;
	private final RecyclerView recycler;
	private final TableParam param;

	private int pxWidth, pxHeight;

	public TableElement(
		@NonNull ViewGroup parent,	// TODO: FrameLayout
		@NonNull ViewGroup.LayoutParams param,	// TODO: @Deprecated
		@NonNull TableParam paramT,
		@NonNull DiffUtil.ItemCallback<T> diffCallback,
		@Nullable TableTrListener<T> listener
	) {
		this.param = paramT;
		Context context = parent.getContext();
		adapter = new TableAdapter<>(paramT, diffCallback, listener);
		recycler = new RecyclerView(context);
		LinearLayoutManager rman;
		int[] colorBg = paramT.getBackgroundColor();
		if (colorBg != null && 1 < colorBg.length) {
			rman = new LinearLayoutManager(context) {
				@Override
				public void onLayoutCompleted(RecyclerView.State state) {
					super.onLayoutCompleted(state);
					int n = adapter.getItemCount();
					for (int i = 0; i < n; i++) {
						RecyclerView.ViewHolder vh = recycler.findViewHolderForAdapterPosition(i);
						if (vh != null) {
							paramT.setBackgroundColor(vh.itemView, i);
						}
					}
				}
			};
		} else {
			rman = new LinearLayoutManager(context);
		}
		rman.setOrientation(LinearLayoutManager.VERTICAL);
		recycler.setLayoutManager(rman);
		recycler.setAdapter(adapter);
		recycler.setBackgroundColor(Color.LTGRAY);
		parent.addView(recycler, param);
	}

	private void setPixelSize(int pxWidth, int pxHeight) {
		if (this.pxWidth != pxWidth || this.pxHeight != pxHeight) {
			this.pxWidth = pxWidth;
			this.pxHeight = pxHeight;
			param.setPixel(pxWidth, pxHeight);
			ViewGroup.LayoutParams p1 = recycler.getLayoutParams();
			p1.width = pxWidth;
			p1.height = pxHeight;
			recycler.requestLayout();
		}
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

	public void submitList(@NonNull List<T> list, @Nullable Runnable done) {
		adapter.submitList(list, done);
	}

}
