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

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.element.fixed.FixedAdapter;
import app.misono.unit206.misc.ItemClickListener;

@Deprecated		// create the your adapter class.
class FixedImageAdapter extends FixedAdapter<
	FixedImageCardView,
	FixedImageCardLayout,
	FixedImageItem,
	FixedImageViewHolder
> {
	private static final String TAG = "FixedImageAdapter";

	private final ItemClickListener<FixedImageItem> clicked;
	private final FixedImageCallback cbNoBitmap;

	private boolean fitCenter;

	FixedImageAdapter(
		@NonNull FixedImageCallback cbNoBitmap,
		boolean fitCenter,
		@Nullable Runnable refresh,
		@Nullable ItemClickListener<FixedImageItem> clicked
	) {
		super(diffCallback, refresh);
		this.fitCenter = fitCenter;
		this.cbNoBitmap = cbNoBitmap;
		this.clicked = clicked;
	}

	@Override
	@NonNull
	public FixedImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		FixedImageCardView view = new FixedImageCardView(parent.getContext());
		if (fitCenter) {
			view.setScaleTypeFitCenter();
		}
		if (clicked != null) {
			view.setOnClickListener(v -> {
				clicked.onClicked(((FixedImageCardView)v).getItem());
			});
		}
		return new FixedImageViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull FixedImageViewHolder holder, int position) {
		FixedImageCardView view = holder.getView();
		view.setNoBitmapCallback(cbNoBitmap);
		super.onBindViewHolder(holder, position);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

	private static final DiffUtil.ItemCallback<FixedImageItem> diffCallback = new DiffUtil.ItemCallback<FixedImageItem>() {
		@Override
		public boolean areItemsTheSame(FixedImageItem item1, FixedImageItem item2) {
			return item1.getLongId() == item2.getLongId();
		}

		@Override
		public boolean areContentsTheSame(FixedImageItem item1, FixedImageItem item2) {
			return item1.getModifiedTime() == item2.getModifiedTime();
		}
	};

}
