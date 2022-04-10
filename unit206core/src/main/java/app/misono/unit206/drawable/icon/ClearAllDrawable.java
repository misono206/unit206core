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
public final class ClearAllDrawable extends SkeletonDrawable {
	private static final String TAG = "ClearAllDrawable";

	private static final ElementPath[] PATH = {
			new ElementPath(PATH_m, new float[]{3,17}),
			new ElementPath(PATH_l, new float[]{
					14,0,
					0,-2,
					-14,0,
					0,2,
			}),

			new ElementPath(PATH_M, new float[]{5,13}),
			new ElementPath(PATH_l, new float[]{
					14,0,
					0,-2,
					-14,0,
					0,2,
			}),

			new ElementPath(PATH_M, new float[]{7,9}),
			new ElementPath(PATH_l, new float[]{
					14,0,
					0,-2,
					-14,0,
					0,2,
			}),
	};

	private Paint paint;
	private Path path;

	public ClearAllDrawable(int wPixel, int hPixel, int color) {
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
