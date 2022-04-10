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

package app.misono.unit206.theme.color;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import app.misono.unit206.page.FrameAnimator;
import app.misono.unit206.page.IAnimatorLayout;

class ColorBoxView extends FrameLayout implements IAnimatorLayout {
	private final View mBox;

	private FrameAnimator mAnime;

	ColorBoxView(@NonNull Context context) {
		super(context);
		FrameLayout.LayoutParams paramBox = new FrameLayout.LayoutParams(0, 0, Gravity.CENTER);
		mBox = new View(context);
		addView(mBox, paramBox);
	}

	void setRgb(int rgb) {
		mBox.setBackgroundColor(rgb);
	}

	@Override
	public void setFrameAnimator(@NonNull FrameAnimator animator) {
		mAnime = animator;
	}

	@Override
	public int addItems(boolean portrait, int width, int height) {
		int rc = 0;
		if (portrait) {
			int wBox = width / 2;
			int hBox = (int)(wBox / 1.6180339f);
			FrameLayout.LayoutParams toBox = new FrameLayout.LayoutParams(wBox, hBox, Gravity.CENTER);
			toBox.leftMargin = 0;
			toBox.rightMargin = 0;
			toBox.topMargin = width / 30;
			toBox.bottomMargin = width / 30;
			mAnime.addItem(mBox, toBox);
			rc = hBox + toBox.topMargin + toBox.bottomMargin;
		} else {
			int wBox = (int)(width * 0.8f);
			int hBox = (int)(wBox / 1.6180339f);
			FrameLayout.LayoutParams toBox = new FrameLayout.LayoutParams(wBox, hBox, Gravity.CENTER);
			toBox.leftMargin = (width - wBox) / 2;
			toBox.rightMargin = toBox.leftMargin;
			toBox.topMargin = (height - hBox) / 2;
			toBox.bottomMargin = toBox.topMargin;
			mAnime.addItem(mBox, toBox);
		}
		return rc;
	}

}
