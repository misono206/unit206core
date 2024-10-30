/*
 * Copyright 2022 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.page.imagecrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.view.IconCardView;
import app.misono.unit206.view.ScalingView;

import com.google.android.material.textview.MaterialTextView;

public class ImageCropView extends FrameLayout {
	private static final String TAG = "ImageCropView";

	final AppCompatImageView vImage;
	final AppCompatImageView vGuide;
	final MaterialTextView vTitle;
	final IconCardView done;
	final ScalingView vScaling;

	Bitmap bitmapLatest;

	private Runnable cbDone;
	private Matrix matrixGesture;
	private RectF rectCrop;
	private View vDone;

	public ImageCropView(@NonNull Context context) {
		this(context, null);
	}

	public ImageCropView(@NonNull Context context, @Nullable AttributeSet attr) {
		super(context, attr);

		vImage = new AppCompatImageView(context);
		vImage.setScaleType(ImageView.ScaleType.MATRIX);
		vImage.setBackgroundColor(Color.LTGRAY);
		addView(vImage, 0, 0);

		vGuide = new AppCompatImageView(context);
		vGuide.setAdjustViewBounds(true);
		vGuide.setScaleType(ImageView.ScaleType.CENTER_CROP);
		addView(vGuide, 0, 0);

		vTitle = new MaterialTextView(context);
		vTitle.setTextColor(Color.BLACK);
		vTitle.setBackgroundColor(Color.LTGRAY);
		vTitle.setGravity(Gravity.CENTER);
		vTitle.setSingleLine();
		addView(vTitle, 0, 0);

		vScaling = new ScalingView(context);
		vScaling.setCenterCropFixedMode();
		vScaling.setListener(this::tryScaling);
		addView(vScaling, 0, 0);

		done = new IconCardView(context);
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(0, 0);
		p.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		addView(done, p);
	}

	void setGuideImage(@DrawableRes int idGuide) {
		vGuide.setImageResource(idGuide);
	}

	void setTitle(@NonNull String title) {
		vTitle.setText(title);
	}

	@MainThread
	private void tryScaling(@NonNull Matrix matrix) {
		matrixGesture = matrix;
		if (bitmapLatest != null) {
			vImage.setImageMatrix(matrixGesture);
			vImage.setImageBitmap(bitmapLatest);
		}
	}

	void setDoneCallback(@NonNull Runnable callback) {
		cbDone = callback;
		done.setOnClickListener(v -> {
			log("done clicked:");
			callback.run();
		});
	}

	@MainThread
	void setCropArea(@NonNull RectF rect) {
		rectCrop = rect;
	}

	@MainThread
	void setDoneIcon(@DrawableRes int idIcon) {
		done.setIcon(idIcon);
	}

	@MainThread
	void setDoneView(@Nullable View vDone) {
		if (this.vDone != null) {
			removeView(this.vDone);
		}
		this.vDone = vDone;
		if (vDone != null) {
			addView(vDone);
			vDone.setOnClickListener(v -> {
				log("vDone clicked:");
				cbDone.run();
			});
			done.setVisibility(View.GONE);
		} else {
			done.setVisibility(View.VISIBLE);
		}
	}

	@MainThread
	void setImageBitmap(@NonNull Bitmap bitmap) {
		bitmapLatest = bitmap;
		vImage.setImageMatrix(matrixGesture);
		vImage.setImageBitmap(bitmap);
	}

	@MainThread
	@NonNull
	ImageCropResult getCroppedBitmap() {
		float[] xy0, xy1;
		if (rectCrop != null) {
			int w = vImage.getWidth();
			int h = vImage.getHeight();
			xy0 = vScaling.getBitmapPoint(w * rectCrop.left, h * rectCrop.top);
			xy1 = vScaling.getBitmapPoint(w * rectCrop.right, h * rectCrop.bottom);
		} else {
			throw new RuntimeException("please invoke ImageCropPage#setCropRect();");
		}
		log("x0:y0:" + xy0[0] + " " + xy0[1]);
		log("x1:y1:" + xy1[0] + " " + xy1[1]);
		int left = (int)xy0[0];
		int top = (int)xy0[1];
		int right = (int)xy1[0];
		int bottom = (int)xy1[1];
		int width = right - left;
		int height = bottom - top;
		Rect rect = new Rect(left, top, right, bottom);
		Bitmap cropped = Bitmap.createBitmap(
			bitmapLatest,
			left,
			top,
			width,
			height,
			matrixGesture,
			true
		);
		return new ImageCropResult(bitmapLatest, cropped, rect);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
