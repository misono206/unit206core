/*
 * Copyright 2023 Atelier Misono, Inc. @ https://misono.app/
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
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.textview.MaterialTextView;

public class SettingText extends FrameLayout {
	private final MaterialTextView vTitle;
	private final MaterialTextView vValue;

	public SettingText(@NonNull Context context) {
		super(context);
		vTitle = new MaterialTextView(context);
		vValue = new MaterialTextView(context);
		init();
	}

	public SettingText(@NonNull Context context, @Nullable AttributeSet attr) {
		super(context, attr);
		vTitle = new MaterialTextView(context, attr);
		vValue = new MaterialTextView(context, attr);
		init();
	}

	private void init() {
		vTitle.setEllipsize(TextUtils.TruncateAt.END);
		vTitle.setSingleLine();
		vTitle.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		addView(vTitle, new LayoutParams(0, 0));
		vValue.setEllipsize(TextUtils.TruncateAt.END);
		vValue.setSingleLine();
		vValue.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
		addView(vValue, new LayoutParams(0, 0));
	}

	public void setPixelSize(int width, int height, float perTitle, int xMidMargin, float sizeText) {
		LayoutParams p;

		int wValid = width - xMidMargin;
		int wTitle = (int)(wValid * perTitle);
		p = (LayoutParams)vTitle.getLayoutParams();
		p.width = wTitle;
		p.height = height;
		p.leftMargin = 0;
		p.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
		vTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeText);
		vTitle.setLayoutParams(p);

		int wValue = wValid - wTitle;
		p = (LayoutParams)vValue.getLayoutParams();
		p.width = wValue;
		p.height = height;
		p.rightMargin = 0;
		p.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
		vValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeText);
		vValue.setLayoutParams(p);
	}

	public void setTextColor(int color) {
		vTitle.setTextColor(color);
		vValue.setTextColor(color);
	}

	public void setTitle(@NonNull CharSequence title) {
		vTitle.setText(title);
	}

	public void setTitle(@StringRes int idTitle) {
		vTitle.setText(idTitle);
	}

	public void setValue(@NonNull CharSequence value) {
		vValue.setText(value);
	}

	public void setValue(@StringRes int idValue) {
		vValue.setText(idValue);
	}

	public void setValueHint(@NonNull CharSequence hint) {
		vValue.setHint(hint);
	}

	public void setValueHint(@StringRes int hint) {
		vValue.setHint(hint);
	}

	public void setValueTypeface(@NonNull Typeface typeface) {
		vValue.setTypeface(typeface);
	}

	@NonNull
	public CharSequence getValue() {
		return vValue.getText();
	}

}
