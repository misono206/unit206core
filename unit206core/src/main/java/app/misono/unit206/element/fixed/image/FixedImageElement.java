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

package app.misono.unit206.element.fixed.image;

import android.graphics.Color;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.element.fixed.FixedElement;
import app.misono.unit206.misc.ItemClickListener;
import app.misono.unit206.misc.UnitPref;

import java.util.List;

public class FixedImageElement extends FixedElement<
	FixedImageAdapter,
	FixedImageCardView,
	FixedImageCardLayout,
	FixedImageItem,
	FixedImageViewHolder
> {
	private static final String TAG = "FixedImageElement";

	private int w1, h1;

	public FixedImageElement(
		@NonNull FrameLayout parent,
		int margin,
		boolean fitCenter,
		@NonNull FixedImageCallback cbNoBitmap,
		@Nullable ItemClickListener<FixedImageItem> onClicked
	) {
		super(parent);
		base.setBackgroundColor(Color.LTGRAY); // TODO: AppColor
		setAdapter(
			new FixedImageCardLayout(margin),
			new FixedImageAdapter(cbNoBitmap, fitCenter, this::refreshLayout, onClicked)
		);
	}

	public void setBackgroundColor(int color) {
		base.setBackgroundColor(color);
	}

	public void submitList(@NonNull List<FixedImageItem> list) {
		adapter.submitList(list);
	}

	public void submitList(@NonNull List<FixedImageItem> list, @Nullable Runnable done) {
		adapter.submitList(list, done);
	}

	@Override
	public void changeLayout(int w, int h) {
		int n = adapter.getItemCount();
		int nw = 1;
		int nh = 1;
		float fw = (float)w;
		float fh = (float)h;
		while (nw * nh < n) {
			float w1 = fw / nw;
			float h1 = fh / nh;
			if (w1 < h1) {
				nh++;
			} else {
				nw++;
			}
		}
		setSpanCount(nw);
		w1 = w / nw;
		h1 = h / nh;
		layout.setPixelSize(w1, h1);
		adapter.layoutCard(layout);
		super.changeLayout(w, h);
	}

	public int getWidth1() {
		return w1;
	}

	public int getHeight1() {
		return h1;
	}

	@Override
	@Nullable
	public UnitPref getUnitPref() {
		return null;
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
