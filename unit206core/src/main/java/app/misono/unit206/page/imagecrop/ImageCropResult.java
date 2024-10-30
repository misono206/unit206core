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
import android.graphics.Rect;

import androidx.annotation.NonNull;

public class ImageCropResult {
	private final Bitmap bitmap;
	private final Bitmap cropped;
	private final Rect rect;

	public ImageCropResult(@NonNull Bitmap bitmap, @NonNull Bitmap cropped, @NonNull Rect rect) {
		this.bitmap = bitmap;
		this.cropped = cropped;
		this.rect = rect;
	}

	@NonNull
	public Bitmap getBitmap() {
		return bitmap;
	}

	@NonNull
	public Bitmap getCroppedBitmap() {
		return cropped;
	}

	@NonNull
	public Rect getCroppedRect() {
		return rect;
	}

}
