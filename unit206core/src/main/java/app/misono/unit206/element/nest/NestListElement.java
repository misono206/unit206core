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

package app.misono.unit206.element.nest;

import android.graphics.Color;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.element.Element;
import app.misono.unit206.misc.UnitPref;

public final class NestListElement implements Element {
	private static final String TAG = "NestListElement";

	private FrameLayout base;

	public NestListElement(@NonNull FrameLayout parent, @NonNull FrameLayout.LayoutParams param) {
		base.setBackgroundColor(Color.CYAN);
		parent.addView(base, param);
		setPixelSize(param.width, param.height);
	}

	private void setPixelSize(int wPixel, int hPixel) {
//		pixelIcon = hPixel * 2 / 3;
		//		pixelIcon = hPixel / 2;
		FrameLayout.LayoutParams p1 = (FrameLayout.LayoutParams)base.getLayoutParams();
		p1.width = wPixel;
		p1.height = hPixel;
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
