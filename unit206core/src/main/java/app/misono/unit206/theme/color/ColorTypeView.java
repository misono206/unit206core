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

package app.misono.unit206.theme.color;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;

import app.misono.unit206.page.FrameAnimator;
import app.misono.unit206.page.IAnimatorLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class ColorTypeView extends FrameLayout implements IAnimatorLayout {
	static final int FMT_INT = 0;
	static final int FMT_FLOAT = 1;

	private final FrameLayout.LayoutParams mToType;
	private final AppCompatTextView mType;
	private final OnChangedSeekbar mListener;
	private final List<ViewLine> mListLine;
//TODO	private final AppColors mColors;

	private FrameAnimator mAnime;

	interface OnChangedSeekbar {
		void onChanged(boolean fromUser);
	}

	ColorTypeView(@NonNull Context context, @NonNull String nameType, @Nullable OnChangedSeekbar listener) {
		super(context);
		mListener = listener;
		mListLine = new ArrayList<>();
//TODO		mColors = AppColors.getInstance();
		mType = new AppCompatTextView(context);
		mType.setText(nameType);
		mType.setTextColor(Color.BLACK);
		mType.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
		FrameLayout.LayoutParams paramType = new FrameLayout.LayoutParams(
			0,
			0,
			Gravity.START | Gravity.CENTER_VERTICAL
		);
		addView(mType, paramType);
		mToType = new FrameLayout.LayoutParams(0, 0, Gravity.START | Gravity.CENTER_VERTICAL);
	}

	private static class ViewLine {
		private final FrameLayout.LayoutParams mToLabel;
		private final FrameLayout.LayoutParams mToValue;
		private final FrameLayout.LayoutParams mToBar;
		private final AppCompatTextView mLabel;
		private final AppCompatTextView mValue;
		private final AppCompatSeekBar mBar;
		private final String mName;

		private ViewLine(
			@NonNull String name,
			@NonNull AppCompatTextView label,
			@NonNull AppCompatTextView value,
			@NonNull AppCompatSeekBar bar
		) {
			mName = name;
			mLabel = label;
			mValue = value;
			mBar = bar;
			mToLabel = new FrameLayout.LayoutParams(0, 0, Gravity.START | Gravity.TOP);
			mToValue = new FrameLayout.LayoutParams(0, 0, Gravity.START | Gravity.TOP);
			mToBar = new FrameLayout.LayoutParams(0, 0, Gravity.START | Gravity.TOP);
		}
	}

	void addLine(@NonNull String name, int min, int max, int fmt, float scale, @NonNull String format) {
		Context context = getContext();
		AppCompatTextView label = new AppCompatTextView(context);
		label.setText(name);
		label.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
		FrameLayout.LayoutParams paramLabel = new FrameLayout.LayoutParams(0, 0, Gravity.START | Gravity.TOP);
		addView(label, paramLabel);
		AppCompatTextView value = new AppCompatTextView(context);
		value.setText(String.valueOf(min));
		value.setGravity(Gravity.CENTER);
		FrameLayout.LayoutParams paramValue = new FrameLayout.LayoutParams(0, 0, Gravity.START | Gravity.TOP);
		addView(value, paramValue);
		AppCompatSeekBar bar = new AppCompatSeekBar(context);
		bar.setMax(max - min);
		bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				String v = "";
				switch (fmt) {
				case FMT_INT:
					int a = (int)((progress + min) * scale);
					v = String.format(Locale.US, format, a, a);
					break;
				case FMT_FLOAT:
					v = String.format(Locale.US, format, (progress + min) * scale);
					break;
				}
				value.setText(v);
				if (mListener != null) {
					mListener.onChanged(fromUser);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		FrameLayout.LayoutParams paramBar = new FrameLayout.LayoutParams(0, 0, Gravity.START | Gravity.TOP);
		addView(bar, paramBar);

		mListLine.add(new ViewLine(name, label, value, bar));
	}

	void setProgress(@NonNull String name, int progress) {
		for (ViewLine line : mListLine) {
			if (line.mName.equals(name)) {
				line.mBar.setProgress(progress);
				return;
			}
		}
		throw new RuntimeException("not found name:" + name);
	}

	@NonNull
	String getValue(@NonNull String name) {
		for (ViewLine line : mListLine) {
			if (line.mName.equals(name)) {
				return line.mValue.getText().toString();
			}
		}
		throw new RuntimeException("not found name:" + name);
	}

	@Override
	public void setFrameAnimator(@NonNull FrameAnimator animator) {
		mAnime = animator;
	}

	@Override
	public int addItems(boolean portrait, int width, int height) {
		int n = mListLine.size();
		int h1 = height / n;
		int pxText = h1 / 2;
		int w10 = width * 10 / 100;
		int w15 = width * 15 / 100;
		int w20 = width * 20 / 100;
		int w55 = width * 55 / 100;
//TODO		int colorText = mColors.getTextColor();
int colorText = Color.BLACK;

		mToType.width = w15;
		mToType.height = height;
		mType.setTextSize(TypedValue.COMPLEX_UNIT_PX, pxText);
		mType.setTextColor(colorText);
		mAnime.addItem(mType, mToType);

		int y = 0;
		for (int i = 0; i < n; i++) {
			int x = mToType.width;
			ViewLine line = mListLine.get(i);

			FrameLayout.LayoutParams toLabel = line.mToLabel;
			toLabel.width = w10;
			toLabel.height = h1;
			toLabel.leftMargin = x;
			toLabel.topMargin = y;
			mAnime.addItem(line.mLabel, toLabel);
			line.mLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, pxText);
			line.mLabel.setTextColor(colorText);
			x += toLabel.width;

			FrameLayout.LayoutParams toValue = line.mToValue;
			toValue.width = w20;
			toValue.height = h1;
			toValue.leftMargin = x;
			toValue.topMargin = y;
			mAnime.addItem(line.mValue, toValue);
			line.mValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, pxText * 0.8f);
			line.mValue.setTextColor(colorText);
			x += toValue.width;

			FrameLayout.LayoutParams toBar = line.mToBar;
			toBar.width = w55;
			toBar.height = h1;
			toBar.leftMargin = x;
			toBar.topMargin = y;
			mAnime.addItem(line.mBar, toBar);

			y += h1;
		}
		return 0;
	}

}
