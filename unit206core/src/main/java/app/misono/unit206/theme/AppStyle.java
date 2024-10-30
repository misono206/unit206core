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

package app.misono.unit206.theme;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import app.misono.unit206.page.PageActivity;
import app.misono.unit206.page.PagePref;

public abstract class AppStyle implements PagePref {
	private static final String TAG = "AppStyle";
	private static final int MODE_OS = 0;
	private static final int MODE_FORCE_LIGHT = 1;
	private static final int MODE_FORCE_DARK = 2;

	private final float mm1;
	private final int hTextMin;

	private boolean isOsDarkMode;
	private int mode;

	public AppStyle(@NonNull PageActivity activity) {
		activity.setAppStyle(this);
		DisplayMetrics disp = activity.getResources().getDisplayMetrics();
		mm1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, disp);
		hTextMin = (int)(mm1 * 1.8f);
		onResumeActivity(activity);
	}

	public void onResumeActivity(@NonNull Context context) {
		isOsDarkMode = isOsDarkMode(context);
	}

	public boolean isDarkMode() {
		switch (mode) {
		case MODE_FORCE_LIGHT:
			return false;
		case MODE_FORCE_DARK:
			return true;
		default:
			return isOsDarkMode;
		}
	}

	public void setDarkMode() {
		mode = MODE_FORCE_DARK;
	}

	public void setLightMode() {
		mode = MODE_FORCE_LIGHT;
	}

	public void setOsMode() {
		mode = MODE_OS;
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

	public int getTextColor() {
		if (isDarkMode()) {
			return Color.WHITE;
		} else {
			return Color.BLACK;
		}
	}

	public int getOsTextColor() {
		if (isOsDarkMode) {
			return Color.WHITE;
		} else {
			return Color.BLACK;
		}
	}

	public int getWhiteColor() {
		if (isDarkMode()) {
			return Color.BLACK;
		} else {
			return Color.WHITE;
		}
	}

	public int getOsWhiteColor() {
		if (isOsDarkMode) {
			return Color.BLACK;
		} else {
			return Color.WHITE;
		}
	}

	public int getBlackColor() {
		if (isDarkMode()) {
			return Color.WHITE;
		} else {
			return Color.BLACK;
		}
	}

	public int getOsBlackColor() {
		if (isOsDarkMode) {
			return Color.WHITE;
		} else {
			return Color.BLACK;
		}
	}

	public int getColor() {
		return Color.MAGENTA;
	}

	public float getMm1() {
		return mm1;
	}

	public int getMinTextHeight() {
		return hTextMin;
	}

	public static boolean isOsDarkMode(@NonNull Context context) {
		int uiMode = context.getResources().getConfiguration().uiMode;
		return (uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
	}

}
