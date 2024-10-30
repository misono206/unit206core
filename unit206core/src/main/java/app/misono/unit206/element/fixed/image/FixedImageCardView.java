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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import app.misono.unit206.element.fixed.FixedCardView;

import com.google.android.material.textview.MaterialTextView;

public class FixedImageCardView extends FixedCardView<
	FixedImageCardView,
	FixedImageCardLayout,
	FixedImageItem
> {
	private static final String TAG = "FixedImageCardView";

	final AppCompatImageView image;
	final AppCompatImageView checked;
	final MaterialTextView text;

	private FixedImageCallback cbNoBitmap;		// TODO: ちょっと違う感じ
	private FixedImageItem item;

	FixedImageCardView(@NonNull Context context) {
		super(context);
		text = new MaterialTextView(context);
		image = new AppCompatImageView(context);
		checked = new AppCompatImageView(context);
		init();
	}

	FixedImageCardView(@NonNull Context context, @NonNull AttributeSet attr) {
		super(context, attr);
		text = new MaterialTextView(context, attr);
		image = new AppCompatImageView(context, attr);
		checked = new AppCompatImageView(context, attr);
		init();
	}

	private void init() {
		text.setTextColor(Color.YELLOW);
		text.setSingleLine();
		text.setEllipsize(TextUtils.TruncateAt.END);
		text.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		addView(text, new LayoutParams(0, 0));

		image.setAdjustViewBounds(true);
		image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		addView(image, new LayoutParams(0, 0));

		checked.setAdjustViewBounds(true);
		checked.setScaleType(ImageView.ScaleType.FIT_CENTER);
		addView(checked, new LayoutParams(0, 0));
	}

	void setNoBitmapCallback(@NonNull FixedImageCallback callback) {
		cbNoBitmap = callback;
	}

	@MainThread
	public void setBitmap(@Nullable Bitmap bitmap) {
		image.setImageBitmap(bitmap);
	}

	@MainThread
	public void setCheckedDrawable(@Nullable Drawable d) {
		checked.setImageDrawable(d);
	}

	@MainThread
	public void setCheckedBitmap(@Nullable Bitmap bitmap) {
		checked.setImageBitmap(bitmap);
	}

	@NonNull
	public FixedImageItem getItem() {
		return item;
	}

	public void setScaleTypeFitCenter() {
		image.setScaleType(ImageView.ScaleType.FIT_CENTER);
	}

	@Override
	public void setItem(@NonNull FixedImageItem item) {
		this.item = item;

		image.setImageBitmap(null);
		checked.setImageBitmap(null);

		String name = item.getName();
		if (name != null) {
			text.setText(name);
		} else {
			text.setVisibility(View.GONE);
		}
		Bitmap bitmap = item.getBitmap();
		if (bitmap != null) {
			setBitmap(bitmap);
		} else {
			if (cbNoBitmap != null) {
				cbNoBitmap.callback(this, item);
			}
		}
	}

}

