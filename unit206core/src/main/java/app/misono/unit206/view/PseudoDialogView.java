/*
 * Copyright 2024 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;

public class PseudoDialogView extends FrameLayout {
	private final MaterialCardView vWall;
	private final IconCardView vCancel;

	private View body;
	private float perWallRadius;

	public PseudoDialogView(@NonNull Context context) {
		super(context);
		vWall = new MaterialCardView(context);
		vCancel = new IconCardView(context);
		init();
	}

	public PseudoDialogView(@NonNull Context context, @Nullable AttributeSet attr) {
		super(context, attr);
		vWall = new MaterialCardView(context, attr);
		vCancel = new IconCardView(context, attr);
		init();
	}

	private void init() {
		perWallRadius = 0.05f;

		addView(vWall, new FrameLayout.LayoutParams(0, 0));

		vCancel.setCardBackgroundColor(0);
		vCancel.setCardElevation(0);
		addView(vCancel, new FrameLayout.LayoutParams(0, 0));

		setBackgroundColor(Color.DKGRAY & 0xeeffffff);
		setOnClickListener(null);
	}

	public void setCancelResource(@DrawableRes int idCancel) {
		vCancel.setIcon(idCancel);
	}

	public void setCancelBitmap(@NonNull Bitmap bitmap) {
		vCancel.setIcon(bitmap);
	}

	public void setCancelClickListener(@NonNull Runnable listener) {
		vCancel.setOnClickListener(v -> {
			listener.run();
		});
	}

	public void setWallRadiusPercent(float per) {
		perWallRadius = per;
	}

	public void setWallStrokeColor(int color) {
		vWall.setStrokeColor(color);
	}

	public void setWallStrokeWidth(int width) {
		vWall.setStrokeWidth(width);
	}

	public void setWallColor(int color) {
		vWall.setCardBackgroundColor(color);
	}

	public void setBodyView(@NonNull View view) {
		if (body != null) {
			vWall.removeView(body);
		}
		body = view;
		vWall.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	protected void setPixelSize(int pxCancel, int mgBodyLeft, int mgBodyTop, int wBody, int hBody) {
		FrameLayout.LayoutParams p = (FrameLayout.LayoutParams)vCancel.getLayoutParams();
		p.width = pxCancel;
		p.height = pxCancel;
		p.gravity = Gravity.END | Gravity.TOP;
		vCancel.setLayoutParams(p);

		p = (FrameLayout.LayoutParams)vWall.getLayoutParams();
		p.width = wBody;
		p.height = hBody;
		p.leftMargin = mgBodyLeft;
		p.topMargin = mgBodyTop;
		p.gravity = Gravity.START | Gravity.TOP;
		vWall.setLayoutParams(p);

		float radius = Math.min(wBody, hBody) * perWallRadius;
		vWall.setRadius(radius);
	}

}
