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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import app.misono.unit206.element.fixed.FixedCardView;
import app.misono.unit206.task.Taskz;

class FixedImageCardView extends FixedCardView<
	FixedImageCardView,
	FixedImageCardLayout,
	FixedImageItem
> {
	private static final String TAG = "FixedImageCardView";

	AppCompatImageView image;
	AppCompatTextView text;

	private FixedImageItem item;

	FixedImageCardView(@NonNull Context context) {
		super(context);
	}

	FixedImageCardView(@NonNull Context context, @NonNull AttributeSet attr) {
		super(context, attr);
	}

	@Override
	public void init(@NonNull Context context) {
		setCardBackgroundColor(Color.LTGRAY); // TODO: AppColor
		text = new AppCompatTextView(context);
		text.setTextColor(Color.YELLOW);
		text.setSingleLine();
		text.setEllipsize(TextUtils.TruncateAt.END);
		text.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		addView(text);
		image = new AppCompatImageView(context);
		image.setAdjustViewBounds(true);
		image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		addView(image);
	}

	@MainThread
	private void addBitmap(@NonNull Bitmap bitmap) {
		image.setImageBitmap(bitmap);
	}

	@NonNull
	public FixedImageItem getItem() {
		return item;
	}

	@Override
	public void setItem(@NonNull FixedImageItem item) {
Log.e(TAG, "setItem:" + item.getName());
		this.item = item;
		String name = item.getName();
		if (name != null) {
			text.setText(name);
		} else {
			text.setVisibility(View.GONE);
		}
		item.getBitmap().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				addBitmap(task.getResult());
			} else {
				Taskz.printStackTrace2(task.getException());
			}
		});
	}

}

