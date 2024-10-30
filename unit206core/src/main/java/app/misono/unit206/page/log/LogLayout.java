/*
 * Copyright 2022 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.page.log;

import androidx.annotation.NonNull;

import app.misono.unit206.debug.Log2;

class LogLayout {
	private static final String TAG = "LogLayout";

	private int width, height;

	LogLayout() {
	}

	void setPixelSize(int w, int h) {
		log("setPixelSize:" + w + " " + h);
		width = w;
		height = h;
	}

	void layout(@NonNull LogView view) {
		log("layout:");
/*
		FrameLayout.LayoutParams p;

		p = (FrameLayout.LayoutParams)view.image.getLayoutParams();
		p.width = width;
		p.height = height;
		p.gravity = Gravity.START | Gravity.TOP;
		view.image.setLayoutParams(p);
*/
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
