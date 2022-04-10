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
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.misc.JsonUtils;
import app.misono.unit206.misc.Utils;
import app.misono.unit206.page.AbstractPagePref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ColorPref extends AbstractPagePref {
	private static final String TAG = "ColorPref";
	private static final String KEY_LAB = "LAB";
	private static final String KEY_RGB = "RGB";
	private static final String KEY_HSL = "HSL";
	private static final String KEY_TYPE = "TYPE";

	static final int TYPE_LAB = 0;
	static final int TYPE_RGB = 1;
	static final int TYPE_HSL = 2;

	private double[] mLab;
	private float[] mHsl;
	private int mRgb;
	private int mType;

	public ColorPref(@NonNull Context context) {
		super(context, null, TAG);
		JSONObject json = loadPref();
		if (json != null) {
			mRgb = json.optInt(KEY_RGB, Color.GRAY);
			JSONArray ja = json.optJSONArray(KEY_LAB);
			if (ja != null) {
				try {
					mLab = JsonUtils.jsonDoubleArrayToArray(ja);
				} catch (JSONException e) {
					//nop
				}
			}
			ja = json.optJSONArray(KEY_HSL);
			if (ja != null) {
				try {
					mHsl = JsonUtils.jsonFloatArrayToArray(ja);
				} catch (JSONException e) {
					//nop
				}
			}
			mType = json.optInt(KEY_TYPE, TYPE_RGB);
		} else {
			mRgb = Color.GRAY;
			mType = TYPE_RGB;
		}
	}

	int getRgb() {
		return mRgb;
	}

	void setRgb(int rgb) {
		mRgb = rgb;
		mType = TYPE_RGB;
	}

	double[] getLab() {
		return mLab;
	}

	void setLab(double[] lab) {
		mLab = lab;
		mType = TYPE_LAB;
	}

	float[] getHsl() {
		return mHsl;
	}

	void setHsl(float[] hsl) {
		mHsl = hsl;
		mType = TYPE_HSL;
	}

	int getType() {
		return mType;
	}

	@Override
	public void restoreBundle(@NonNull Bundle bundle) {
		mRgb = bundle.getInt(KEY_RGB, mRgb);
	}

	@Override
	@Nullable
	public Bundle createBundle() {
		Bundle b = new Bundle();
		b.putInt(KEY_RGB, mRgb);
		return b;
	}

	@Override
	public void apply() {
		JSONObject json = new JSONObject();
		if (mLab != null) {
			try {
				json.put(KEY_LAB, JsonUtils.toJSONArray(mLab));
			} catch (JSONException e) {
				// nop
			}
		}
		if (mHsl != null) {
			try {
				json.put(KEY_HSL, JsonUtils.toJSONArray(mHsl));
			} catch (JSONException e) {
				// nop
			}
		}
		try {
			json.put(KEY_RGB, mRgb);
		} catch (JSONException e) {
			// nop
		}
		try {
			json.put(KEY_TYPE, mType);
		} catch (JSONException e) {
			// nop
		}
		applyPref(json);
	}

}
