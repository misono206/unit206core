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

package app.misono.unit206.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.AnyThread;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import app.misono.unit206.misc.ImageUtils;

import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;

import java.util.concurrent.Executor;

public class IconCardView extends MaterialCardView {
	private final AppCompatImageView image;

	private boolean insideMargin;
	private int left, top, right, bottom;

	public IconCardView(@NonNull Context context) {
		super(context);
		image = new AppCompatImageView(context);
		init();
	}

	public IconCardView(@NonNull Context context, AttributeSet attr) {
		super(context, attr);
		image = new AppCompatImageView(context, attr);
		init();
	}

	public IconCardView(@NonNull Context context, AttributeSet attr, int defStyleAttr) {
		super(context, attr, defStyleAttr);
		image = new AppCompatImageView(context, attr, defStyleAttr);
		init();
	}

	private void init() {
		image.setAdjustViewBounds(true);
		image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
			LayoutParams.MATCH_PARENT,
			LayoutParams.MATCH_PARENT
		);
		addView(image, p);
	}

	public void setScaleType(@NonNull ImageView.ScaleType type) {
		image.setScaleType(type);
	}

	public void setMatrix(@NonNull Matrix matrix) {
		image.setScaleType(ImageView.ScaleType.MATRIX);
		image.setImageMatrix(matrix);
	}

	public void setIcon(@DrawableRes int idDrawable) {
		image.setImageDrawable(ContextCompat.getDrawable(getContext(), idDrawable));
	}

	public void setIcon(@NonNull Bitmap bitmap) {
		image.setImageBitmap(bitmap);
	}

	@AnyThread
	@NonNull
	public Task<Bitmap> setAssetIcon(
		@NonNull Executor executor,
		@NonNull String pathAsset
	) {
		return ImageUtils.setAssetImageTask(executor, image, pathAsset);
	}

	public void setMargins(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		insideMargin = true;
	}

	public void setOutsideMargins(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		insideMargin = false;
	}

	public void layout(int width, int height) {
		int w, h;

		ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams)getLayoutParams();
		if (insideMargin) {
			w = width - left - right;
			h = height - top - bottom;
		} else {
			w = width;
			h = height;
		}
		if (p2 != null) {
			p2.width = w;
			p2.height = h;
			p2.setMargins(left, top, right, bottom);
			requestLayout();
		} else {
			p2 = new ViewGroup.MarginLayoutParams(w, h);
			p2.setMargins(left, top, right, bottom);
			setLayoutParams(p2);
		}
	}

	public void setIconMargin(int left, int top, int right, int bottom) {
		FrameLayout.LayoutParams p = (FrameLayout.LayoutParams)image.getLayoutParams();
		p.leftMargin = left;
		p.topMargin = top;
		p.rightMargin = right;
		p.bottomMargin = bottom;
		image.setLayoutParams(p);
	}

}

