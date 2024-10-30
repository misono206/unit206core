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

import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;

import app.misono.unit206.element.fixed.FixedCardLayout;

class FixedImageCardLayout implements FixedCardLayout<
	FixedImageCardView,
	FixedImageCardLayout,
	FixedImageItem
> {
	private static final String TAG = "FixedImageCardLayout";

	private int m;
	private int wPixel, hPixel;
	private int width, height, sizeText, hText, hImage, hChecked;

	FixedImageCardLayout(int margin) {
		m = margin;
	}

	void setPixelSize(int w, int h) {
		wPixel = w;
		hPixel = h;
		width = w - m * 2;
		height = h - m * 2;
//		hText = h / 10;
		hText = 0;	// TODO: とりあえず textなし
		sizeText = hText / 2;
		hImage = height - hText;
		hChecked = (int)(height * 0.2f);
	}

	@Override
	public void refreshCard(@NonNull FixedImageCardView card) {
		ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams)card.getLayoutParams();
		if (p2 != null) {
			p2.width = width;
			p2.height = height;
			p2.setMargins(m, m, m, m);
			card.requestLayout();
		} else {
			p2 = new ViewGroup.MarginLayoutParams(width, height);
			p2.setMargins(m, m, m, m);
			card.setLayoutParams(p2);
		}

		CardView.LayoutParams p;

		AppCompatTextView text = card.text;
		if (text != null) {
			p = (FrameLayout.LayoutParams)text.getLayoutParams();
			p.width = width - m * 2;
			p.height = hText;
			p.leftMargin = m;
			p.gravity = Gravity.START | Gravity.TOP;
			text.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeText);
			text.setLayoutParams(p);
		}

		p = (FrameLayout.LayoutParams)card.image.getLayoutParams();
		p.width = width;
		p.height = hImage;
		p.topMargin = hText;
		p.gravity = Gravity.START | Gravity.TOP;
		card.image.setLayoutParams(p);

		p = (FrameLayout.LayoutParams)card.checked.getLayoutParams();
		p.width = hChecked;
		p.height = hChecked;
		p.gravity = Gravity.END | Gravity.TOP;
		card.checked.setLayoutParams(p);
	}

	@Override
	public void layoutCard(@NonNull FixedImageCardView card, int position) {
		refreshCard(card);
	}

}
