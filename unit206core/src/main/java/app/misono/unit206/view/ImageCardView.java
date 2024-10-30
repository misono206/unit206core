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
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.card.MaterialCardView;

public class ImageCardView extends MaterialCardView {
	private final AppCompatImageView image;

	private float ratio;
	private int margin;

	public ImageCardView(@NonNull Context context) {
		super(context);
		image = new AppCompatImageView(context);
		init(context);
	}

	public ImageCardView(@NonNull Context context, @Nullable AttributeSet attr) {
		super(context, attr);
		image = new AppCompatImageView(context, attr);
		init(context);
	}

	public ImageCardView(@NonNull Context context, AttributeSet attr, int defStyleAttr) {
		super(context, attr, defStyleAttr);
		image = new AppCompatImageView(context, attr, defStyleAttr);
		init(context);
	}

	private void init(@NonNull Context context) {
		margin = (int)TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_MM,
			0.5f,
			context.getResources().getDisplayMetrics()
		);
		image.setAdjustViewBounds(true);
		image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		addView(image, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	public void setScaleType(@NonNull ImageView.ScaleType type) {
		image.setScaleType(type);
	}

	public void setBitmap(@NonNull Bitmap bitmap) {
		image.setImageBitmap(bitmap);
		ratio = (float)bitmap.getWidth() / bitmap.getHeight();
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}

	public void layoutHeight(int height) {
		ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams)getLayoutParams();
		int w = (int)(height * ratio) - margin * 2;
		int h = height - margin * 2;
		if (p2 != null) {
			p2.width = w;
			p2.height = h;
			p2.setMargins(margin, margin, margin, margin);
			requestLayout();
		} else {
			p2 = new ViewGroup.MarginLayoutParams(w, h);
			p2.setMargins(margin, margin, margin, margin);
			setLayoutParams(p2);
		}
	}

}

