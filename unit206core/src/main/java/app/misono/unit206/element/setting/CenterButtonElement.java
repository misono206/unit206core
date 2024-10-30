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

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.element.Element;
import app.misono.unit206.misc.UnitPref;

public final class CenterButtonElement implements Element {

	private final Button button;

	public CenterButtonElement(
		@NonNull FrameLayout parent,
		@NonNull String text, int pixelY,
		@Nullable View.OnClickListener clicked
	) {
		button = new Button(parent.getContext());
		button.setText(text);
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		p.gravity = Gravity.CENTER_HORIZONTAL;
		p.topMargin = pixelY;
		button.setLayoutParams(p);
		button.setOnClickListener(clicked);
		parent.addView(button);
	}

	public void setText(@NonNull String text) {
		button.setText(text);
	}

	@Override
	public void onResume() {
	}

	@Override
	public void onPause() {
	}

	@Override
	public void changeLayout(int width, int height) {
		// TODO
	}

	@Override
	@NonNull
	public View getView() {
		return button;
	}

	@Override
	@Nullable
	public UnitPref getUnitPref() {
		return null;
	}

}
