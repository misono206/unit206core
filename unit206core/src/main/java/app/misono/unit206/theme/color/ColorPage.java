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

import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import app.misono.unit206.R;
import app.misono.unit206.admob.AdMobBanner;
import app.misono.unit206.debug.Log2;
import app.misono.unit206.page.AbstractPage;
import app.misono.unit206.page.FrameAnimator;
import app.misono.unit206.page.PageActivity;
import app.misono.unit206.page.PageManager;
import app.misono.unit206.task.Taskz;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.Tasks;

import java.io.Closeable;

public class ColorPage extends AbstractPage implements Closeable {
	private static final String TAG = "ColorPage";

	private final FrameAnimator mAnime;
	private final ColorTypeView mLab;
	private final ColorTypeView mRgb;
	private final ColorTypeView mHsl;
	private final ColorBoxView mBox;
	private final ColorPref pref;
	private final Runnable mDone;

	public ColorPage(
		@NonNull PageManager manager,
		@NonNull PageActivity activity,
		@NonNull FrameLayout adbase,
		@NonNull FrameLayout parent,
		@Nullable AdView adview,
		@Nullable Runnable clickBack,
		@NonNull Runnable done
	) {
		super(manager, activity, adbase, parent, adview, clickBack);

		mDone = done;
		pref = new ColorPref(activity);
		setPref(pref);
		mBase.setBackgroundColor(Color.LTGRAY);
		mAnime = new FrameAnimator(500);

		mBox = new ColorBoxView(activity);
		mBox.setFrameAnimator(mAnime);
		FrameLayout.LayoutParams paramBox = new FrameLayout.LayoutParams(0, 0, Gravity.START | Gravity.TOP);
		mBase.addView(mBox, paramBox);

		mLab = new ColorTypeView(activity, "LAB", this::updateLab);
		mLab.setFrameAnimator(mAnime);
		mLab.setBackgroundColor(Color.rgb(181, 183, 206));
		mLab.addLine("L", 0, 100, ColorTypeView.FMT_INT, 1, "%d");
		mLab.addLine("A", -128, 127, ColorTypeView.FMT_INT, 1, "%d");
		mLab.addLine("B", -128, 127, ColorTypeView.FMT_INT, 1, "%d");
		FrameLayout.LayoutParams paramLab = new FrameLayout.LayoutParams(0, 0, Gravity.START | Gravity.TOP);
		mBase.addView(mLab, paramLab);

		mRgb = new ColorTypeView(activity, "RGB", this::updateRgb);
		mRgb.setFrameAnimator(mAnime);
		mRgb.setBackgroundColor(Color.rgb(218, 216, 158));
		mRgb.addLine("R", 0, 255, ColorTypeView.FMT_INT, 1, "%d[%02X]");
		mRgb.addLine("G", 0, 255, ColorTypeView.FMT_INT, 1, "%d[%02X]");
		mRgb.addLine("B", 0, 255, ColorTypeView.FMT_INT, 1, "%d[%02X]");
		FrameLayout.LayoutParams paramRgb = new FrameLayout.LayoutParams(0, 0, Gravity.START | Gravity.TOP);
		mBase.addView(mRgb, paramRgb);

		mHsl = new ColorTypeView(activity, "HSL", this::updateHsl);
		mHsl.setFrameAnimator(mAnime);
		mHsl.setBackgroundColor(Color.rgb(181, 183, 206));
		mHsl.addLine("H", 0, 360, ColorTypeView.FMT_INT, 1, "%d");
		mHsl.addLine("S", 0, 100, ColorTypeView.FMT_FLOAT, 0.01f, "%4.2f");
		mHsl.addLine("L", 0, 100, ColorTypeView.FMT_FLOAT, 0.01f, "%4.2f");
		FrameLayout.LayoutParams paramHsl = new FrameLayout.LayoutParams(0, 0, Gravity.START | Gravity.TOP);
		mBase.addView(mHsl, paramHsl);

		switch (pref.getType()) {
		case ColorPref.TYPE_LAB:
			setLab(pref.getLab());
			break;
		case ColorPref.TYPE_RGB:
			setRgb(pref.getRgb());
			break;
		case ColorPref.TYPE_HSL:
			setHsl(pref.getHsl());
			break;
		}
	}

	private void updateLab(boolean fromUser) {
		int l = toInt(mLab.getValue("L"), 0);
		int a = toInt(mLab.getValue("A"), 0);
		int b = toInt(mLab.getValue("B"), 0);
		if (fromUser) {
			double[] lab = new double[3];
			lab[0] = l;
			lab[1] = a;
			lab[2] = b;
			pref.setLab(lab);
			int rgb = ColorUtils.LABToColor(l, a, b);
			mBox.setRgb(rgb);
			letRgb(rgb);
			float[] hsl = new float[3];
			ColorUtils.colorToHSL(rgb, hsl);
			letHsl(hsl);
		}
	}

	@NonNull
	private String firstString(@NonNull String s) {
		String[] sp = s.split("\\[");
		return sp[0];
	}

	private void updateRgb(boolean fromUser) {
		int r = toInt(firstString(mRgb.getValue("R")), 0);
		int g = toInt(firstString(mRgb.getValue("G")), 0);
		int b = toInt(firstString(mRgb.getValue("B")), 0);
		if (fromUser) {
			int rgb = 0xff000000 | (r << 16) | (g << 8) | b;
			pref.setRgb(rgb);
			mBox.setRgb(rgb);
			double[] lab = new double[3];
			ColorUtils.colorToLAB(rgb, lab);
			letLab(lab);
			float[] hsl = new float[3];
			ColorUtils.colorToHSL(rgb, hsl);
			letHsl(hsl);
		}
	}

	private void updateHsl(boolean fromUser) {
		float[] hsl = new float[3];
		hsl[0] = toFloat(mHsl.getValue("H"), 0);
		hsl[1] = toFloat(mHsl.getValue("S"), 0);
		hsl[2] = toFloat(mHsl.getValue("L"), 0);
		if (fromUser) {
			pref.setHsl(hsl);
			int rgb = ColorUtils.HSLToColor(hsl);
			mBox.setRgb(rgb);
			letRgb(rgb);
			double[] lab = new double[3];
			ColorUtils.colorToLAB(rgb, lab);
			letLab(lab);
		}
	}

	private void letLab(double[] lab) {
		int l = (int)lab[0];
		int a = (int)lab[1] + 128;
		int b = (int)lab[2] + 128;
		mLab.setProgress("L", l);
		mLab.setProgress("A", a);
		mLab.setProgress("B", b);
	}

	private void letRgb(int rgb) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		mRgb.setProgress("R", r);
		mRgb.setProgress("G", g);
		mRgb.setProgress("B", b);
	}

	private void letHsl(float[] hsl) {
		int h = (int)hsl[0];
		int s = (int)(hsl[1] * 100);
		int l = (int)(hsl[2] * 100);
		mHsl.setProgress("H", h);
		mHsl.setProgress("S", s);
		mHsl.setProgress("L", l);
	}

	private int toInt(@NonNull String s, int defaultValue) {
		int rc = defaultValue;
		try {
			rc = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			// nop
		}
		return rc;
	}

	private float toFloat(@NonNull String s, float defaultValue) {
		float rc = defaultValue;
		try {
			rc = Float.parseFloat(s);
		} catch (NumberFormatException e) {
			// nop
		}
		return rc;
	}

	public void setLab(double[] lab) {
		pref.setLab(lab);
		letLab(lab);
		int rgb = ColorUtils.LABToColor(lab[0], lab[1], lab[2]);
		mBox.setRgb(rgb);
		letRgb(rgb);
		float[] hsl = new float[3];
		ColorUtils.colorToHSL(rgb, hsl);
		letHsl(hsl);
	}

	public void setRgb(int rgb) {
		pref.setRgb(rgb);
		mBox.setRgb(rgb);
		letRgb(rgb);
		double[] lab = new double[3];
		ColorUtils.colorToLAB(rgb, lab);
		letLab(lab);
		float[] hsl = new float[3];
		ColorUtils.colorToHSL(rgb, hsl);
		letHsl(hsl);
	}

	public void setHsl(float[] hsl) {
		pref.setHsl(hsl);
		letHsl(hsl);
		int rgb = ColorUtils.HSLToColor(hsl);
		mBox.setRgb(rgb);
		letRgb(rgb);
		double[] lab = new double[3];
		ColorUtils.colorToLAB(rgb, lab);
		letLab(lab);
	}

	public int getRgb() {
		switch (pref.getType()) {
		case ColorPref.TYPE_LAB:
			double[] lab = pref.getLab();
			return ColorUtils.LABToColor(lab[0], lab[1], lab[2]);
		case ColorPref.TYPE_RGB:
			return pref.getRgb();
		case ColorPref.TYPE_HSL:
			float[] hsl = pref.getHsl();
			return ColorUtils.HSLToColor(hsl);
		}
		throw new RuntimeException("unknown color type..." + pref.getType());
	}

	@Override
	public void changeLayout(int wAdBase, int hAdBase) {
		super.changeLayout(wAdBase, hAdBase);
		log("changeLayout:" + wAdBase + " " + hAdBase + " " + mPortrait);
		AdMobBanner.getInstance().requestLayout(mAdBase, mParent, mAdView);
		Tasks.call(() -> {
			mAnime.stop();
			mAnime.clear();
			int w = mBase.getWidth();
			int h = mBase.getHeight();
			if (mPortrait) {
				int wBox = w;
				int hBox = mBox.addItems(mPortrait, wBox, 0);
				FrameLayout.LayoutParams toBox = new FrameLayout.LayoutParams(wBox, hBox, Gravity.START | Gravity.TOP);
				mAnime.addItem(mBox, toBox);

				int h1 = (h - hBox) / 3;

				mLab.addItems(mPortrait, w, h1);
				FrameLayout.LayoutParams toLab = new FrameLayout.LayoutParams(w, h1, Gravity.START | Gravity.TOP);
				toLab.leftMargin = 0;
				toLab.topMargin = hBox;
				mAnime.addItem(mLab, toLab);

				mRgb.addItems(mPortrait, w, h1);
				FrameLayout.LayoutParams toRgb = new FrameLayout.LayoutParams(w, h1, Gravity.START | Gravity.TOP);
				toRgb.leftMargin = 0;
				toRgb.topMargin = hBox + h1;
				mAnime.addItem(mRgb, toRgb);

				mHsl.addItems(mPortrait, w, h1);
				FrameLayout.LayoutParams toHsl = new FrameLayout.LayoutParams(w, h1, Gravity.START | Gravity.TOP);
				toHsl.leftMargin = 0;
				toHsl.topMargin = hBox + h1 * 2;
				mAnime.addItem(mHsl, toHsl);
			} else {
				int wBox = w / 3;
				int hBox = h;
				mBox.addItems(mPortrait, wBox, hBox);
				FrameLayout.LayoutParams toBox = new FrameLayout.LayoutParams(wBox, hBox, Gravity.START | Gravity.TOP);
				mAnime.addItem(mBox, toBox);

				int w1 = w - wBox;
				int h1 = h / 3;

				mLab.addItems(mPortrait, w1, h1);
				FrameLayout.LayoutParams toLab = new FrameLayout.LayoutParams(w1, h1, Gravity.START | Gravity.TOP);
				toLab.leftMargin = wBox;
				toLab.topMargin = 0;
				mAnime.addItem(mLab, toLab);

				mRgb.addItems(mPortrait, w1, h1);
				FrameLayout.LayoutParams toRgb = new FrameLayout.LayoutParams(w1, h1, Gravity.START | Gravity.TOP);
				toRgb.leftMargin = wBox;
				toRgb.topMargin = h1;
				mAnime.addItem(mRgb, toRgb);

				mHsl.addItems(mPortrait, w1, h1);
				FrameLayout.LayoutParams toHsl = new FrameLayout.LayoutParams(w1, h1, Gravity.START | Gravity.TOP);
				toHsl.leftMargin = wBox;
				toHsl.topMargin = h1 * 2;
				mAnime.addItem(mHsl, toHsl);
			}
			mAnime.start();
			return null;
		}).addOnFailureListener(Taskz::printStackTrace2);
	}

	@Override
	public boolean isHamburgerIcon() {
		return false;
	}

	@Override
	public String refreshPage() {
		activity.invalidateOptionsMenu();
		return activity.getString(R.string.color_picker);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		log("onCreateOptionsMenu:");
		activity.getMenuInflater().inflate(R.menu.menu_color, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.color_action_done) {
			mDone.run();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void close() {
	}

	@NonNull
	@Override
	public String getTag() {
		return TAG;
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
