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

package app.misono.unit206.element.tutorial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

public class TutorialIndicator extends FrameLayout {
	private static final String TAG = "TutorialIndicator";

	private AppCompatImageView[] vSelected, vUnselected;
	private Paint selected;
	private Paint unselected;
	private int nIndicator;
	private int position;
	private int height, width;
	private int distance;

	public TutorialIndicator(Context ctx) {
		super(ctx);
		init(ctx);
	}

	public TutorialIndicator(Context ctx, AttributeSet attr) {
		super(ctx, attr);
		init(ctx);
	}

	private void init(@NonNull Context ctx) {
		position = -1;
		selected = new Paint();
		selected.setStyle(Paint.Style.FILL_AND_STROKE);
		selected.setAntiAlias(true);
		unselected = new Paint();
		unselected.setStyle(Paint.Style.STROKE);
		unselected.setAntiAlias(true);
	}

	@MainThread
	public void setCount(int n, int colorSelected, int colorUnselected) {
		nIndicator = n;
		selected.setColor(colorSelected);
		unselected.setColor(colorUnselected);
		vSelected = new AppCompatImageView[n];
		vUnselected = new AppCompatImageView[n];
		Context context = getContext();
		removeAllViews();
		for (int i = 0; i < n; i++) {
			AppCompatImageView v;
			v = new AppCompatImageView(context);
			v.setAdjustViewBounds(true);
			v.setScaleType(ImageView.ScaleType.FIT_CENTER);
			v.setVisibility(View.GONE);
			addView(v, new FrameLayout.LayoutParams(0, 0));
			vSelected[i] = v;
			v = new AppCompatImageView(context);
			v.setAdjustViewBounds(true);
			v.setScaleType(ImageView.ScaleType.FIT_CENTER);
			v.setVisibility(View.VISIBLE);
			addView(v, new FrameLayout.LayoutParams(0, 0));
			vUnselected[i] = v;
		}
	}

	public int getCount() {
		return nIndicator;
	}

	@MainThread
	public void setPixelHeight(int height, int distance) {
		Canvas c;
		if (nIndicator == 0) {
			throw new RuntimeException("invoke setCount() first...");
		}
		float wStroke = height * 0.05f;
		this.height = height;
		this.distance = distance;
		unselected.setStrokeWidth(wStroke);
		float radius = (float)height * 0.95f / 2 - wStroke;
		width = distance * (nIndicator - 1) + height;
		int xy2 = height / 2;
		Bitmap bSelected = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);
		c = new Canvas(bSelected);
		c.drawCircle(xy2, xy2, radius, selected);
		Bitmap bUnselected = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);
		c = new Canvas(bUnselected);
		c.drawCircle(xy2, xy2, radius, unselected);
		for (int i = 0; i < nIndicator; i++) {
			FrameLayout.LayoutParams p;
			vSelected[i].setImageBitmap(bSelected);
			vUnselected[i].setImageBitmap(bUnselected);
			int leftMargin = i * distance;

			p = (FrameLayout.LayoutParams)vSelected[i].getLayoutParams();
			p.width = height;
			p.height = height;
			p.leftMargin = leftMargin;
			p.topMargin = 0;
			p.gravity = Gravity.START | Gravity.TOP;
			vSelected[i].setLayoutParams(p);

			p = (FrameLayout.LayoutParams)vUnselected[i].getLayoutParams();
			p.width = height;
			p.height = height;
			p.leftMargin = leftMargin;
			p.topMargin = 0;
			p.gravity = Gravity.START | Gravity.TOP;
			vUnselected[i].setLayoutParams(p);
		}
	}

	public int getPixelWidth() {
		return width;
	}

	@MainThread
	public void setPosition(int position) {
		if (0 <= this.position) {
			vSelected[this.position].setVisibility(View.GONE);
			vUnselected[this.position].setVisibility(View.VISIBLE);
		}
		this.position = position;
		if (0 <= position) {
			vSelected[position].setVisibility(View.VISIBLE);
			vUnselected[position].setVisibility(View.GONE);
		}
	}

}
