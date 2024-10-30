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

package app.misono.unit206.selection;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import app.misono.unit206.debug.Log2;

public abstract class SelectionAdapter<T extends LongId, VH extends LongIdViewHolder>
	extends ListAdapter<T, VH> {

	private static final String TAG = "SelectionAdapter";

	protected SelectionListener<T> mListener;
	protected ISelection<T> mSelection;
	protected boolean mPortrait;
	protected boolean mSquare;
	protected float mCountItem;
	protected float ratioPortY, ratioLandY;
	protected int mWidth;
	protected int mHeight;
	protected int mLandCountX, mPortCountX;
	protected int pxMinHeight, pxMaxHeight;

	public SelectionAdapter(
		float nItem,
		int nLandX,
		@NonNull DiffUtil.ItemCallback<T> diffCallback,
		@Nullable SelectionListener<T> listener
	) {
		super(diffCallback);
		mSquare = false;
		mCountItem = nItem;
		mPortCountX = 1;
		mLandCountX = nLandX;
		mListener = listener;
		setHasStableIds(true);
	}

	/**
	 * for square item.
	 * if true, ignore nItem.
	 */
	public SelectionAdapter(
		boolean square,
		float nItem,
		int nPortX,
		int nLandX,
		@NonNull DiffUtil.ItemCallback<T> diffCallback,
		@Nullable SelectionListener<T> listener
	) {
		super(diffCallback);
		mSquare = square;
		mCountItem = nItem;
		mPortCountX = nPortX;
		mLandCountX = nLandX;
		mListener = listener;
		setHasStableIds(true);
	}

	public SelectionAdapter(
		float ratioPortY,
		float ratioLandY,
		@NonNull DiffUtil.ItemCallback<T> diffCallback,
		@Nullable SelectionListener<T> listener
	) {
		super(diffCallback);
		this.ratioPortY = ratioPortY;
		this.ratioLandY = ratioLandY;
		mSquare = false;
		mCountItem = 0;
		mPortCountX = 1;
		mLandCountX = 1;
		mListener = listener;
		setHasStableIds(true);
	}

	public void setMinHeightMm(@NonNull Context context, float mmHeight) {
		float ydpi = context.getResources().getDisplayMetrics().ydpi;
		pxMinHeight = (int)(ydpi * mmHeight / 25.4f);
	}

	public void setMaxHeightMm(@NonNull Context context, float mmHeight) {
		float ydpi = context.getResources().getDisplayMetrics().ydpi;
		pxMaxHeight = (int)(ydpi * mmHeight / 25.4f);
	}

	public void setSelection(@NonNull ISelection<T> selection) {
		mSelection = selection;
	}

	@Override
	public long getItemId(int position) {
		T item = getItem(position);
		return item == null ? RecyclerView.NO_ID : item.getLongId();
	}

	@Override
	public void onBindViewHolder(@NonNull VH holder, int position) {
		T item = getItem(position);
		if (item != null) {
			SelectableCardView<T> view = (SelectableCardView<T>)holder.itemView;
			int w1, h1;
			if (mSquare) {
				if (mPortrait) {
					w1 = mWidth / mPortCountX;
				} else {
					w1 = mWidth / mLandCountX;
				}
				h1 = w1;
			} else {
				if (mPortrait) {
					w1 = mWidth / mPortCountX;
					if (mCountItem != 0) {
						h1 = (int)(mHeight / (mCountItem / mPortCountX));
					} else {
						h1 = (int)(mWidth * ratioPortY);
					}
				} else {
					w1 = mWidth / mLandCountX;
					if (mCountItem != 0) {
						h1 = (int)(mHeight / (mCountItem / mLandCountX));
					} else {
						h1 = (int)(mWidth * ratioLandY);
					}
				}
			}
			if (pxMinHeight != 0) {
				h1 = Math.max(h1, pxMinHeight);
			}
			if (pxMaxHeight != 0) {
				h1 = Math.min(h1, pxMaxHeight);
			}
			view.setItem(item, w1, h1, mSelection.isSelected(item.getLongId()));
			view.setOnClickListener(v -> {
				if (mListener != null) {
					mListener.onClick(item);
				}
			});
		}
	}

	@Override
	public void onViewAttachedToWindow(@NonNull VH holder) {
//		log("onViewAttachedToWindow:" + holder.getLongId());
		super.onViewAttachedToWindow(holder);
		holder.onViewAttachedToWindow();
	}

	@Override
	public void onViewDetachedFromWindow(@NonNull VH holder) {
//		log("onViewDetachedFromWindow:" + holder.getLongId());
		holder.onViewDetachedFromWindow();
		super.onViewDetachedFromWindow(holder);
	}

	public void setPixelSize(int wPixel, int hPixel) {
		mWidth = wPixel;
		mHeight = hPixel;
		mPortrait = wPixel < hPixel;
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
