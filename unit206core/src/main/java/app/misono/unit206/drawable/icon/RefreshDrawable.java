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

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.NonNull;

import app.misono.unit206.drawable.SkeletonDrawable;

@TargetApi(1)
public final class RefreshDrawable extends SkeletonDrawable {
	private static final String TAG = "RefreshDrawable";

	private static final ElementPath[] PATH = {
			new ElementPath(PATH_ARC, new float[]{4,4,20,20,-45,-315+14.47751219f}),
			new ElementPath(PATH_L, new float[]{12+7.74596672f-2,14}),
			new ElementPath(PATH_ARC, new float[]{6,6,18,18,19.19148612f,315-19.19148612f}),

			new ElementPath(PATH_L, new float[]{
					13,11,
					20,11,
					20,4,
			}),
	};

	private Paint paint;
	private Path path;

	public RefreshDrawable(int wPixel, int hPixel, int color) {
		super();
		setViewPort(24, 24, wPixel, hPixel);
		paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		path = new Path();
		setPath(path, PATH);
	}

	@Override
	public void draw(@NonNull Canvas canvas) {
		canvas.drawPath(path, paint);
	}

}
