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

package app.misono.unit206.element.fixed.image;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.element.fixed.FixedAdapter;
import app.misono.unit206.misc.ItemClickListener;

class FixedImageAdapter extends FixedAdapter<
	FixedImageCardView,
	FixedImageCardLayout,
	FixedImageItem,
	FixedImageViewHolder
> {
	private static final String TAG = "FixedImageAdapter";

	private final ItemClickListener<FixedImageItem> clicked;

	private int mSizeIcon, mSizeText, mMargin, mPadding;

	FixedImageAdapter(@Nullable Runnable refresh, @Nullable ItemClickListener<FixedImageItem> clicked) {
		super(diffCallback, refresh);
		this.clicked = clicked;
	}

	void setParams(int sizeIcon, int sizeText, int margin, int padding) {
		mSizeIcon = sizeIcon;
		mSizeText = sizeText;
		mMargin = margin;
		mPadding = padding;
	}

	@Override
	@NonNull
	public FixedImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		FixedImageCardView view = new FixedImageCardView(parent.getContext());
		if (clicked != null) {
			view.setOnClickListener(v -> {
				clicked.onClicked(((FixedImageCardView)v).getItem());
			});
		}
		return new FixedImageViewHolder(view);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

	private static final DiffUtil.ItemCallback<FixedImageItem> diffCallback = new DiffUtil.ItemCallback<FixedImageItem>() {
		@Override
		public boolean areItemsTheSame(FixedImageItem item1, FixedImageItem item2) {
			return item1.getLongId() == item2.getLongId();
		}

		@SuppressLint("DiffUtilEquals")
		@Override
		public boolean areContentsTheSame(FixedImageItem item1, FixedImageItem item2) {
			return item1.getLongId() == item2.getLongId()
				&& item1.peekBitmap() == item2.peekBitmap()
				&& TextUtils.equals(item1.getName(), item2.getName());
		}
	};

}
