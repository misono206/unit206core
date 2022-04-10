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
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import app.misono.unit206.misc.UnitPref;

public final class SwitchElement extends AbstractLineElement<Boolean> {
	private static final String TAG = "SwitchElement";

	private SwitchCompat onoff;

	public SwitchElement(
		@NonNull FrameLayout parent,
		@NonNull FrameLayout.LayoutParams param,
		@NonNull String label, int perLabelW
	) {
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
	public void setValue(@NonNull Boolean value) {
		onoff.setChecked(value);
	}

	@Override
	@NonNull
	public Boolean getValue() {
		return onoff.isChecked();
	}

	@Override
	@NonNull
	public View createValueView(@NonNull Context context) {
		onoff = new SwitchCompat(context);
		onoff.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		return onoff;
	}

	@Override
	@Nullable
	public UnitPref getUnitPref() {
		return null;
	}

}
