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

package app.misono.unit206.theme;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import app.misono.unit206.page.PagePref;

public abstract class AppStyle implements PagePref {
	private static final String TAG = "AppStyle";

	private final int hTextMin;

	public AppStyle(@NonNull Context context) {
		DisplayMetrics disp = context.getResources().getDisplayMetrics();
		hTextMin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 8, disp);
	}

	public void setMainColor(int rgb) {

	}

	public int getBackgroundColor() {
		return Color.LTGRAY;
	}

	public int getButtonBackgroundColor() {
		return MaterialDesign.COLOR_TEAL_200;
	}

	public int getTitlebarBackgroundColor() {
		return MaterialDesign.COLOR_TEAL_100;
	}

	public int getAccentColor() {
		return 0;
	}

	public int getTextColor(@NonNull Context context) {
		int rc = getBlackColor();
		if (isDarkMode(context)) {
			rc = getWhiteColor();
		}
		return rc;
	}

	public int getWhiteColor() {
		return Color.WHITE;
	}

	public int getBlackColor() {
		return Color.BLACK;
	}

	public int getColor() {
		return Color.MAGENTA;
	}

	public int getMinTextHeight() {
		return hTextMin;
	}

	public static boolean isDarkMode(@NonNull Context context) {
		int uiMode = context.getResources().getConfiguration().uiMode;
		return (uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
	}

}
