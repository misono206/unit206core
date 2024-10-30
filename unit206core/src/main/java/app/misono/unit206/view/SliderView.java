/*
 * Copyright 2023 Atelier Misono, Inc. @ https://misono.app/
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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.debug.Log2;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;

import java.util.Locale;

/**
 * SliderView.
 *
 * TODO: calculate min max text size.
 * TODO: add title.
 */
public class SliderView extends FrameLayout {
	private static final String TAG = "SliderView";

	private final MaterialTextView vMin, vMax;
	private final Slider slider;

	private String fmtMinMax;
	private float wRatio;

	public SliderView(@NonNull Context context) {
		super(context);
		slider = new Slider(context);
		vMin = new MaterialTextView(context);
		vMax = new MaterialTextView(context);
		init();
	}

	public SliderView(@NonNull Context context, @Nullable AttributeSet attr) {
		super(context, attr);
		slider = new Slider(context, attr);
		vMin = new MaterialTextView(context, attr);
		vMax = new MaterialTextView(context, attr);
		init();
	}

	private void init() {
		wRatio = 1;
		addView(slider, new LayoutParams(0, 0));
		vMin.setTextColor(Color.BLACK);
		vMin.setGravity(Gravity.CENTER);
		vMin.setTypeface(Typeface.MONOSPACE);
		addView(vMin, new LayoutParams(0, 0));
		vMax.setTextColor(Color.BLACK);
		vMax.setGravity(Gravity.CENTER);
		vMax.setTypeface(Typeface.MONOSPACE);
		addView(vMax, new LayoutParams(0, 0));
	}

	public void setSliderWidthRatio(float ratio) {
		wRatio = ratio;
	}

	public void setMinMaxTextFormat(@Nullable String fmt) {
		fmtMinMax = fmt;
		drawMinMaxText();
	}

	public void setMinMaxColor(int color) {
		vMin.setTextColor(color);
		vMax.setTextColor(color);
	}

	private void drawMinMaxText() {
		if (fmtMinMax != null) {
			String min = String.format(Locale.US, fmtMinMax, slider.getValueFrom());
			vMin.setText(min);
			vMin.setVisibility(VISIBLE);
			String max = String.format(Locale.US, fmtMinMax, slider.getValueTo());
			vMax.setText(max);
			vMax.setVisibility(VISIBLE);
		} else {
			vMin.setVisibility(GONE);
			vMax.setVisibility(GONE);
		}
	}

	public void setPixelSize(int width, int height) {
		LayoutParams p;

		int wSlider = (int)(width * wRatio);
		p = (LayoutParams)slider.getLayoutParams();
		p.width = wSlider;
		p.height = height;
		p.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		slider.setLayoutParams(p);

		int wValue = (width - wSlider) / 2;
		p = (LayoutParams)vMin.getLayoutParams();
		p.width = wValue;
		p.height = height;
		p.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
		vMin.setLayoutParams(p);
		p = (LayoutParams)vMax.getLayoutParams();
		p.width = wValue;
		p.height = height;
		p.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
		vMax.setLayoutParams(p);
	}

	public void addOnChangeListener(@NonNull Slider.OnChangeListener listener) {
		slider.addOnChangeListener(listener);
	}

	public void addOnSliderTouchListener(@NonNull Slider.OnSliderTouchListener listener) {
		slider.addOnSliderTouchListener(listener);
	}

	public void clearOnChangeListeners() {
		slider.clearOnChangeListeners();
	}


	public void clearOnSliderTouchListeners() {
		slider.clearOnSliderTouchListeners();
	}

	/**
	 * Returns the value of the slider.
	 */
	public float getValue() {
		return slider.getValue();
	}

	/**
	 * Returns the slider's valueFrom value.
	 */
	public float getValueFrom() {
		return slider.getValueFrom();
	}

	/**
	 * Returns the slider's valueTo value.
	 */
	public float getValueTo() {
		return slider.getValueTo();
	}

	/**
	 * Removes a callback to be invoked when the slider touch event is being started or stopped.
	 */
	public void removeOnSliderTouchListener(@NonNull Slider.OnSliderTouchListener listener) {
		slider.removeOnSliderTouchListener(listener);
	}

	/**
	 * Sets the custom thumb drawable which will be used for all value positions.
	 */
	public void setCustomThumbDrawable(@NonNull Drawable drawable) {
		slider.setCustomThumbDrawable(drawable);
	}

	/**
	 * Sets the custom thumb drawable which will be used for all value positions.
	 */
	public void setCustomThumbDrawable(int drawableResId) {
		slider.setCustomThumbDrawable(drawableResId);
	}

	/**
	 * Registers a LabelFormatter to be used to format the value displayed in the bubble shown when
	 * the slider operates in discrete mode.
	 */
	public void setLabelFormatter(@NonNull LabelFormatter formatter) {
		slider.setLabelFormatter(formatter);
	}

	/**
	 * Sets the step size to use to mark the ticks.
	 */
	public void setStepSize(float stepSize) {
		slider.setStepSize(stepSize);
	}

	/**
	 * Sets the elevation of the thumb.
	 */
	public void setThumbElevation(float elevation) {
		slider.setThumbElevation(elevation);
	}

	/**
	 * Sets the elevation of the thumb from a dimension resource.
	 */
	public void setThumbElevationResource(int elevation) {
		slider.setThumbElevationResource(elevation);
	}

	/**
	 * Sets the radius of the thumb in pixels.
	 */
	public void setThumbRadius(int radius) {
		slider.setThumbRadius(radius);
	}

	/**
	 * Sets the radius of the thumb from a dimension resource.
	 */
	public void setThumbRadiusResource(int radius) {
		slider.setThumbRadiusResource(radius);
	}

	/**
	 * Sets the stroke color for the thumbs.
	 */
	public void setThumbStrokeColor(ColorStateList thumbStrokeColor) {
		slider.setThumbStrokeColor(thumbStrokeColor);
	}

	/**
	 * Sets the stroke color resource for the thumbs.
	 */
	public void setThumbStrokeColorResource(int thumbStrokeColorResourceId) {
		slider.setThumbStrokeColorResource(thumbStrokeColorResourceId);
	}

	/**
	 * Sets the stroke width for the thumb.
	 */
	public void setThumbStrokeWidth(float thumbStrokeWidth) {
		slider.setThumbStrokeWidth(thumbStrokeWidth);
	}

	/**
	 * Sets the stroke width dimension resource for the thumb.Both thumbStroke color and thumbStroke
	 * width must be set for a stroke to be drawn.
	 */
	public void setThumbStrokeWidthResource(int thumbStrokeWidthResourceId) {
		slider.setThumbStrokeWidthResource(thumbStrokeWidthResourceId);
	}

	/**
	 * Sets the color of the thumb.
	 */
	public void setThumbTintList(ColorStateList thumbColor) {
		slider.setThumbTintList(thumbColor);
	}

	/**
	 * Sets the value of the slider.
	 */
	public void setValue(float value) {
		slider.setValue(value);
	}

	/**
	 * Sets the slider's valueFrom value.
	 */
	public void setValueFrom(float valueFrom) {
		slider.setValueFrom(valueFrom);
		drawMinMaxText();
	}

	/**
	 * Sets the slider's valueTo value.
	 */
	public void setValueTo(float valueTo) {
		slider.setValueTo(valueTo);
		drawMinMaxText();
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
