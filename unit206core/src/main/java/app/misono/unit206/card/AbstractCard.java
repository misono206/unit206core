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

package app.misono.unit206.card;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import app.misono.unit206.drawable.icon.CheckCircleDrawable;

public abstract class AbstractCard extends FrameLayout {
	private static final String TAG = "AbstractCard";

	private ImageView viewCheck;
	private int colorCircle, colorCheck;
	protected int width, height;

	public AbstractCard(@NonNull Context context) {
		super(context);
	}

	public AbstractCard(@NonNull Context context, @NonNull ViewGroup.LayoutParams param) {
		super(context);
		setLayoutParams(param);
		width = param.width;
		height = param.height;
	}

	public void setSelectColor(int colorCircle, int colorCheck) {
		this.colorCircle = colorCircle;
		this.colorCheck = colorCheck;
	}

	@Override
	public void setSelected(boolean selected) {
		Log.e(TAG, "selected:" + selected);
		if (selected) {
			if (viewCheck == null) {
				viewCheck = new ImageView(getContext());
				int w = getLayoutParams().width / 4;
				CheckCircleDrawable d = new CheckCircleDrawable(w, w, colorCircle, colorCheck);
				viewCheck.setImageDrawable(d);
				FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(w, w);
				viewCheck.setLayoutParams(p);
				addView(viewCheck);
			}
		} else {
			if (viewCheck != null) {
				removeView(viewCheck);
				viewCheck = null;
			}
		}
	}

}
