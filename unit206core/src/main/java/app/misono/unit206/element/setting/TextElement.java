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
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.misc.UnitPref;

public final class TextElement extends AbstractLineElement<String> {
	private static final String TAG = "TextElement";

	private EditText text;

	public TextElement(@NonNull FrameLayout parent, @NonNull FrameLayout.LayoutParams param,
			@NonNull String label, int perLabelW) {

		super(parent, param, label, perLabelW);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void setValue(@NonNull String value) {
		text.setText(value);
	}

	@Override
	@NonNull
	public String getValue() {
		return text.getText().toString();
	}

	@Override
	@NonNull
	public View createValueView(@NonNull Context context) {
		text = new EditText(context);
		text.setSingleLine();
		text.setGravity(Gravity.CENTER_VERTICAL);
		return text;
	}

	@Override
	@Nullable
	public UnitPref getUnitPref() {
		return null;
	}

}
