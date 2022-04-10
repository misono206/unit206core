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
public final class DeleteDrawable extends SkeletonDrawable {
	private static final String TAG = "DeleteDrawable";

	private static final ElementPath[] PATH = {
			new ElementPath(PATH_M, new float[]{6,19}),
			new ElementPath(PATH_c, new float[]{0,1.1f,.9f,2,2,2}),
			new ElementPath(PATH_l, new float[]{8,0}),
			new ElementPath(PATH_c, new float[]{1.1f,0,2,-.9f,2,-2}),
			new ElementPath(PATH_L, new float[]{0,7}), //
			new ElementPath(PATH_L, new float[]{6,7}),
			new ElementPath(PATH_l, new float[]{0,12}),

			new ElementPath(PATH_M, new float[]{19,4}),
			new ElementPath(PATH_l, new float[]{-3.5f,0}),
			new ElementPath(PATH_l, new float[]{-1,-1}),
			new ElementPath(PATH_l, new float[]{0,-5}),
			new ElementPath(PATH_l, new float[]{-1,1}),
			new ElementPath(PATH_L, new float[]{5,0}), //
			new ElementPath(PATH_l, new float[]{0,2}),
			new ElementPath(PATH_l, new float[]{14,0}),
			new ElementPath(PATH_L, new float[]{0,4}), //
	};

	private Paint paint;
	private Path path;

	public DeleteDrawable(int wPixel, int hPixel, int color) {
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
