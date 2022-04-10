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

package app.misono.unit206.element.setting;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.element.Element;
import app.misono.unit206.misc.UnitPref;

abstract class AbstractLineElement<T> implements Element {
	private static final String TAG = "AbstractLineElement";

	private LinearLayout.LayoutParams pLeft, pRight;
	private FrameLayout.LayoutParams pBase;
	private LinearLayout base;
	private int perLabelW;

	@NonNull
	abstract View createValueView(@NonNull Context context);
	abstract void setValue(@NonNull T value);
	@NonNull
	abstract T getValue();

	AbstractLineElement(
		@NonNull FrameLayout parent,
		@NonNull FrameLayout.LayoutParams param,
		@NonNull String label, int perLabelW
	) {
		Context context = parent.getContext();
		pBase = param;
		this.perLabelW = perLabelW;
		base = new LinearLayout(context);
		base.setOrientation(LinearLayout.HORIZONTAL);

		TextView title = new TextView(context);
		title.setTextColor(Color.BLACK);
		title.setText(label);
		title.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
		pLeft = new LinearLayout.LayoutParams(0, 0);
		pLeft.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
		title.setLayoutParams(pLeft);
		base.addView(title);

		pRight = new LinearLayout.LayoutParams(0, 0);
		pRight.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
		View value = createValueView(context);
		value.setLayoutParams(pRight);
		base.addView(value, pRight);

		base.setLayoutParams(param);
		parent.addView(base);
		setPixelSize(param.width, param.height);
	}

	private void setPixelSize(int wPixel, int hPixel) {
		pLeft.width = wPixel * perLabelW / 1000;
		pLeft.height = hPixel;
		pRight.width = wPixel - pLeft.width;
		pRight.height = hPixel;
		pBase.width = wPixel;
		pBase.height = hPixel;
		base.requestLayout();
	}

	@Override
	public void onResume() {
	}

	@Override
	public void onPause() {
	}

	@Override
	public void setLayoutParams(@NonNull FrameLayout.LayoutParams params) {
		setPixelSize(params.width, params.height);
	}

	@Override
	@Nullable
	public UnitPref getUnitPref() {
		return null;
	}

}
