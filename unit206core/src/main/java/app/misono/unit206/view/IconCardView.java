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

package app.misono.unit206.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;

public class IconCardView extends MaterialCardView {
	private final AppCompatImageView image;

	private int left, top, right, bottom;

	public IconCardView(@NonNull Context context) {
		this(context, null);
	}

	public IconCardView(@NonNull Context context, @Nullable AttributeSet attr) {
		super(context, attr);

		image = new AppCompatImageView(context);
		image.setAdjustViewBounds(true);
		image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		addView(image, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	public void setIcon(@DrawableRes int idDrawable) {
		image.setImageDrawable(ContextCompat.getDrawable(getContext(), idDrawable));
	}

	public void setMargins(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public void layout(int width, int height) {
		ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams)getLayoutParams();
		int w = width - left - right;
		int h = height - top - bottom;
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

}

