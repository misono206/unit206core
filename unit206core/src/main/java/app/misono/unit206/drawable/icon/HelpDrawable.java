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
public final class HelpDrawable extends SkeletonDrawable {
	private static final String TAG = "HelpDrawable";

	private static final ElementPath[] HELP = {
			new ElementPath(PATH_M, new float[]{13,19}),
			new ElementPath(PATH_l, new float[] {
					-2,0,
					0,-2,
					2,0,
					0,2,
			}),
			new ElementPath(PATH_m, new float[]{2.07f,-7.75f}),
			new ElementPath(PATH_l, new float[]{-.9f,-.92f}),
			new ElementPath(PATH_C, new float[]{13.45f,12.9f,13f,13.5f,13f,15f}),
			new ElementPath(PATH_l, new float[] {
					-2,0,
					0,-0.5f,
			}),
			new ElementPath(PATH_c, new float[]{0,-1.1f,.45f,-2.1f,1.17f,-2.83f}),
			new ElementPath(PATH_l, new float[]{1.24f,-1.26f}),
			new ElementPath(PATH_c, new float[]{.37f,-.36f,.59f,-.86f,.59f,-1.41f,0,-1.1f,-.9f,-2,-2,-2}),
			new ElementPath(PATH_s, new float[]{-2,.9f,-2,2}),
			new ElementPath(PATH_l, new float[]{-2,0}), //???
			new ElementPath(PATH_c, new float[]{0,-2.21f,1.79f,-4,4,-4}),
			new ElementPath(PATH_s, new float[]{4,1.79f,4,4}),
			new ElementPath(PATH_c, new float[]{0,.88f,-.36f,1.68f,-.93f,2.25f}),
	};

	private Paint paint;
	private Path path;

	public HelpDrawable(int wPixel, int hPixel, int color) {
		super();
		setViewPort(24, 24, wPixel, hPixel);
		paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		path = new Path();
		setPath(path, HELP);
	}

	@Override
	public void draw(@NonNull Canvas canvas) {
		canvas.drawPath(path, paint);
	}

}
