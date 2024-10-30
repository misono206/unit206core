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
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import app.misono.unit206.debug.Log2;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

public abstract class AbstractButton extends MaterialCardView {
	private static final String TAG = "AbstractButton";

	private MaterialTextView text;
	private float scaleText;
	private float perWidth;

	public abstract int getBackgroundColor();

	public AbstractButton(@NonNull Context context) {
		super(context);
		init(context);
	}

	public AbstractButton(@NonNull Context context, @NonNull AttributeSet attr) {
		super(context, attr);
		init(context);
	}

	private void init(@NonNull Context context) {
		scaleText = 0.3f;
		perWidth = 0.8f;

		text = new MaterialTextView(context);
		addView(text, new LayoutParams(0, 0));
		setCardBackgroundColor(getBackgroundColor());
		text.setTextColor(Color.WHITE);
		text.setGravity(Gravity.CENTER);
	}

	public void setText(@NonNull String s) {
		text.setText(s);
	}

	public void setText(@StringRes int idString) {
		text.setText(idString);
	}

	public void setTextColor(int color) {
		text.setTextColor(color);
	}

	public void setTextScale(float scaleText) {
		this.scaleText = scaleText;
	}

	public void setTextWidthPercent(float perWidth) {
		this.perWidth = perWidth;
	}

	public void setPixelSize(int wButton, int hButton) {
		LayoutParams p = (LayoutParams)text.getLayoutParams();
		p.width = wButton;
		p.height = hButton;
		p.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		float sizeText = (float)hButton * scaleText;
		if (perWidth != 0) {
			Paint paint = new Paint();
			paint.setTextSize(sizeText);
			float wText = paint.measureText(text.getText().toString());
			float w = wButton * perWidth;
			if (w < wText) {
				sizeText *= w / wText;
			}
		}
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeText);
		text.setLayoutParams(p);
	}

	@Override
	public void setClickable(boolean clickable) {
		super.setClickable(clickable);
		if (clickable) {
			setCardBackgroundColor(getBackgroundColor());
		} else {
			setCardBackgroundColor(0xffcccccc);
		}
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
