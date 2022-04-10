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
public final class CheckCircleDrawable extends SkeletonDrawable {
	private static final String TAG = "CheckCircleDrawable";

	private static final ElementPath[] CIRCLE = {
			new ElementPath(PATH_CIRCLE, new float[]{12,12,10}),
	};
	private static final ElementPath[] WHITE = {
			new ElementPath(PATH_M, new float[]{10,17}),
			new ElementPath(PATH_l, new float[]{-5,-5,1.41f,-1.41f}),
			new ElementPath(PATH_L, new float[]{10,14.17f}),
			new ElementPath(PATH_l, new float[]{7.59f,-7.59f}),
			new ElementPath(PATH_L, new float[]{19,8}),
			new ElementPath(PATH_l, new float[]{-9,9}),
	};

	private Paint paint, paintCheck;
	private Path path, pathCheck;

	public CheckCircleDrawable(int wPixel, int hPixel, int colorCircle, int colorCheck) {
		super();

		paint = new Paint();
		paint.setColor(colorCircle);
		paint.setStyle(Paint.Style.FILL);
		path = new Path();

		paintCheck = new Paint();
		paintCheck.setColor(colorCheck);
		paintCheck.setStyle(Paint.Style.FILL);
		pathCheck = new Path();

		setPixelSize(wPixel, hPixel);
	}

	public void setPixelSize(int wPixel, int hPixel) {
		setViewPort(24, 24, wPixel, hPixel);
		path.reset();
		setPath(path, CIRCLE);
		pathCheck.reset();
		setPath(pathCheck, WHITE);
	}

	@Override
	public void draw(@NonNull Canvas canvas) {
		canvas.drawPath(path, paint);
		canvas.drawPath(pathCheck, paintCheck);
	}

}
