/*
 * Copyright 2020 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.misc;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

public class SavedPreferences {
	private static final String TAG = "SavedPreferences";

	private final SharedPreferences mPref;

	private SharedPreferences.Editor mEditor;

	public SavedPreferences(@NonNull Context context) {
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@NonNull
	private SharedPreferences.Editor getEditor() {
		if (mEditor == null) {
			mEditor = mPref.edit();
		}
		return mEditor;
	}

	public void apply() {
		if (mEditor != null) {
			mEditor.apply();
			mEditor = null;
		}
	}

	public void putString(@NonNull String key, @Nullable String value) {
		getEditor().putString(key, value);
	}

	public void putStringSet(@NonNull String key, @Nullable Set<String> value) {
		getEditor().putStringSet(key, value);
	}

	public void putInt(@NonNull String key, int value) {
		getEditor().putInt(key, value);
	}

	public void putLong(@NonNull String key, long value) {
		getEditor().putLong(key, value);
	}

	public void putBoolean(@NonNull String key, boolean value) {
		getEditor().putBoolean(key, value);
	}

	public void putFloat(@NonNull String key, float value) {
		getEditor().putFloat(key, value);
	}

	public void putFloatArray(@NonNull String key, @NonNull float[] value) throws JSONException {
		JSONArray ja = new JSONArray();
		for (float d : value) {
			ja.put(d);
		}
		getEditor().putString(key, ja.toString());
	}

	public void putDouble(@NonNull String key, double value) {
		getEditor().putString(key, String.valueOf(value));
	}

	public void putDoubleArray(@NonNull String key, @NonNull double[] value) throws JSONException {
		JSONArray ja = new JSONArray();
		for (double d : value) {
			ja.put(d);
		}
		getEditor().putString(key, ja.toString());
	}

	public void remove(@NonNull String key) {
		getEditor().remove(key);
	}

	@NonNull
	public String getString(@NonNull String key, @Nullable String defaultValue) {
		return mPref.getString(key, defaultValue);
	}

	@Nullable
	public Set<String> getStringSet(@NonNull String key, @Nullable Set<String> defaultValue) {
		return mPref.getStringSet(key, defaultValue);
	}

	public int getInt(@NonNull String key, int defaultValue) {
		return mPref.getInt(key, defaultValue);
	}

	public long getLong(@NonNull String key, long defaultValue) {
		return mPref.getLong(key, defaultValue);
	}

	public boolean getBoolean(@NonNull String key, boolean defaultValue) {
		return mPref.getBoolean(key, defaultValue);
	}

	public float getFloat(@NonNull String key, float defaultValue) {
		return mPref.getFloat(key, defaultValue);
	}

	@Nullable
	public float[] getFloatArray(@NonNull String key) {
		float[] rc = null;
		String s = mPref.getString(key, null);
		if (s != null) {
			try {
				JSONArray ja = new JSONArray(s);
				int n = ja.length();
				float[] tmp = new float[n];
				for (int i = 0; i < n; i++) {
					tmp[i] = (float)ja.getDouble(i);
				}
				rc = tmp;
			} catch (JSONException e) {
				// nop
			}
		}
		return rc;
	}

	public double getDouble(@NonNull String key, double defaultValue) {
		double rc = defaultValue;
		String s = mPref.getString(key, null);
		if (s != null) {
			try {
				rc = Double.parseDouble(s);
			} catch (NumberFormatException e) {
				// nop
			}
		}
		return rc;
	}

	@Nullable
	public double[] getDoubleArray(@NonNull String key) {
		double[] rc = null;
		String s = mPref.getString(key, null);
		if (s != null) {
			try {
				JSONArray ja = new JSONArray(s);
				int n = ja.length();
				double[] tmp = new double[n];
				for (int i = 0; i < n; i++) {
					tmp[i] = ja.getDouble(i);
				}
				rc = tmp;
			} catch (JSONException e) {
				// nop
			}
		}
		return rc;
	}

	public boolean contains(@NonNull String key) {
		return mPref.contains(key);
	}

}
