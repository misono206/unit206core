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

package app.misono.unit206.element;

import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.misc.UnitPref;

public interface Element {
	void setLayoutParams(@NonNull FrameLayout.LayoutParams params);
	void onResume();
	void onPause();
	@Nullable
	UnitPref getUnitPref();

}
