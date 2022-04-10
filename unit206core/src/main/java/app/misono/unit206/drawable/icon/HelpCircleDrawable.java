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

@TargetApi(19)
public final class HelpCircleDrawable extends SkeletonDrawable {
	private static final String TAG = "HelpCircleDrawable";

	private static final ElementPath[] CIRCLE = {
			new ElementPath(PATH_CIRCLE, new float[]{12,12,10}),
	};
	private static final ElementPath[] DOT = {
			new ElementPath(PATH_M, new float[]{13,19}),
			new ElementPath(PATH_l, new float[] {
					-2,0,
					0,-2,
					2,0,
					0,2,
			}),
	};
	private static final ElementPath[] PLUS = {
			new ElementPath(PATH_M, new float[]{17,13}),
			new ElementPath(PATH_l, new float[] {
					-4,0,
					0,4,
					-2,0,
					0,-4,
					-4,0,
					0,-2,
					4,0,
					0,-4,
					2,0,
					0,4,
					4,0,
					0,2,
			}),
	};

	private Paint paint;
	private Path path;

	public HelpCircleDrawable(int wPixel, int hPixel, int color) {
		super();
		setViewPort(24, 24, wPixel, hPixel);
		paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		path = new Path();
		setPath(path, CIRCLE);
		Path pathDot = new Path();
		setPath(pathDot, DOT);
		path.op(pathDot, Path.Op.DIFFERENCE);
	}

	@Override
	public void draw(@NonNull Canvas canvas) {
		canvas.drawPath(path, paint);
	}

}
