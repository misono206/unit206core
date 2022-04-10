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

package app.misono.unit206.drawable.icon;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.NonNull;

import app.misono.unit206.drawable.SkeletonDrawable;

public final class ViewModuleDrawable extends SkeletonDrawable {
	private static final String TAG = "ViewModuleDrawable";

	private static final ElementPath[] MENU = {
		new ElementPath(PATH_RECT, new float[] {
			4,5,9,11,
			10,5,15,11,
			16,5,21,11,
			4,12,9,18,
			10,12,15,18,
			16,12,21,18,
		}),
	};

	private final Paint paint;
	private final Path path;

	public ViewModuleDrawable(int wPixel, int hPixel, int color) {
		super();
		paint = new Paint();
		path = new Path();
		setViewPort(24, 24, wPixel, hPixel);
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		setPath(path, MENU);
	}

	@Override
	public void draw(@NonNull Canvas canvas) {
		canvas.drawPath(path, paint);
	}

}
