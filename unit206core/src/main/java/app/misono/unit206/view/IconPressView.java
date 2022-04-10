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
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import app.misono.unit206.debug.Log2;

import com.google.android.material.card.MaterialCardView;

public class IconPressView extends MaterialCardView {
	private static final String TAG = "IconPressView";

	private AppCompatImageView image;
	private Runnable cbPressed, cbReleased;
	private boolean enable;

	public IconPressView(@NonNull Context context) {
		super(context);
		init(context);
	}

	public IconPressView(@NonNull Context context, @NonNull AttributeSet attr) {
		super(context, attr);
		init(context);
	}

	private void init(@NonNull Context context) {
		enable = true;
		image = new AppCompatImageView(context);
		image.setAdjustViewBounds(true);
		image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		addView(image, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (enable) {
			super.onTouchEvent(event);

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (cbPressed != null) {
					cbPressed.run();
				}
				return true;
			case MotionEvent.ACTION_UP:
				if (cbReleased != null) {
					cbReleased.run();
				}
				performClick();
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean performClick() {
		super.performClick();
		return true;
	}

	public void enableTouch(boolean enable) {
		this.enable = enable;
	}

	public void setIcon(@DrawableRes int idDrawable) {
		image.setImageDrawable(ContextCompat.getDrawable(getContext(), idDrawable));
	}

	public void setPressedListener(@Nullable Runnable listener) {
		cbPressed = listener;
	}

	public void setReleasedListener(@Nullable Runnable listener) {
		cbReleased = listener;
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
