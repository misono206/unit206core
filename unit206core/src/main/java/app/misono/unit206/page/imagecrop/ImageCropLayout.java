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

package app.misono.unit206.page.imagecrop;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import app.misono.unit206.debug.Log2;

class ImageCropLayout {
	private static final String TAG = "ImageCropLayout";

	private int width, height;

	ImageCropLayout() {
	}

	void setPixelSize(int w, int h) {
		width = w;
		height = h;
	}

	void layout(@NonNull ImageCropView view) {
		FrameLayout.LayoutParams p;

		Bitmap bitmap = view.bitmapLatest;
		if (bitmap != null) {
			int wImage = bitmap.getWidth();
			int hImage = bitmap.getHeight();

			view.vScaling.setBitmapAndViewSize(wImage, hImage, width, height);
		}

		p = (FrameLayout.LayoutParams)view.vImage.getLayoutParams();
		p.width = width;
		p.height = height;
		p.gravity = Gravity.START | Gravity.TOP;
		view.vImage.setLayoutParams(p);

		p = (FrameLayout.LayoutParams)view.vGuide.getLayoutParams();
		p.width = width;
		p.height = height;
		p.gravity = Gravity.CENTER;
		view.vGuide.setLayoutParams(p);

		float hText = height * 0.07f;
		view.vTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, hText * 0.6f);
		p = (FrameLayout.LayoutParams)view.vTitle.getLayoutParams();
		if (TextUtils.isEmpty(view.vTitle.getText())) {
			p.width = 0;
			p.height = 0;
		} else {
			p.width = width;
			p.height = (int)hText;
		}
		p.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		view.vTitle.setLayoutParams(p);

		p = (FrameLayout.LayoutParams)view.vScaling.getLayoutParams();
		p.width = width;
		p.height = height;
		p.gravity = Gravity.START | Gravity.TOP;
		view.vScaling.setLayoutParams(p);

		int size = (int)(height * 0.07f);
		int m = (int)(width * 0.05f);
//		view.done.setMargins(0, 0, 0, m);	// 長方形になる
		view.done.setMargins(0, 0, 0, 0);
		view.done.layout(size, size);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
