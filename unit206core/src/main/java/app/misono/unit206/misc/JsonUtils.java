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

package app.misono.unit206.misc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

	@NonNull
	public static List<String> jsonStringArrayToList(@NonNull JSONArray ja) throws JSONException {
		List<String> rc = new ArrayList<>();
		int n = ja.length();
		for (int i = 0; i < n; i++) {
			rc.add(ja.getString(i));
		}
		return rc;
	}

	@NonNull
	public static String[] jsonStringArrayToArray(@NonNull JSONArray ja) throws JSONException {
		int n = ja.length();
		String[] rc = new String[n];
		for (int i = 0; i < n; i++) {
			rc[i] = ja.getString(i);
		}
		return rc;
	}

	@NonNull
	public static long[] jsonLongArrayToArray(@NonNull JSONArray ja) throws JSONException {
		int n = ja.length();
		long[] rc = new long[n];
		for (int i = 0; i < n; i++) {
			rc[i] = ja.getLong(i);
		}
		return rc;
	}

	@NonNull
	public static double[] jsonDoubleArrayToArray(@NonNull JSONArray ja) throws JSONException {
		int n = ja.length();
		double[] rc = new double[n];
		for (int i = 0; i < n; i++) {
			rc[i] = ja.getDouble(i);
		}
		return rc;
	}

	@NonNull
	public static float[] jsonFloatArrayToArray(@NonNull JSONArray ja) throws JSONException {
		int n = ja.length();
		float[] rc = new float[n];
		for (int i = 0; i < n; i++) {
			rc[i] = (float)ja.getDouble(i);
		}
		return rc;
	}

	@NonNull
	public static JSONArray toJSONArray(@NonNull double[] dd) throws JSONException {
		JSONArray rc = new JSONArray();
		for (double d : dd) {
			rc.put(d);
		}
		return rc;
	}

	@NonNull
	public static JSONArray toJSONArray(@NonNull float[] ff) throws JSONException {
		JSONArray rc = new JSONArray();
		for (float f : ff) {
			rc.put(f);
		}
		return rc;
	}

	public static void putArray(@NonNull JSONObject json, @NonNull String key, @NonNull double[] array) {
		try {
			json.put(key, toJSONArray(array));
		} catch (JSONException e) {
			// nop
		}
	}

	public static void putArray(@NonNull JSONObject json, @NonNull String key, @NonNull float[] array) {
		try {
			json.put(key, toJSONArray(array));
		} catch (JSONException e) {
			// nop
		}
	}

	public static void put(@NonNull JSONObject json, @NonNull String key, int value) {
		try {
			json.put(key, value);
		} catch (JSONException e) {
			// nop
		}
	}

	public static void put(@NonNull JSONObject json, @NonNull String key, @Nullable String s) {
		try {
			json.put(key, s);
		} catch (JSONException e) {
			// nop
		}
	}

	public static int getInt(@Nullable JSONObject json, @NonNull String key, int fallback) {
		int rc = fallback;
		if (json != null) {
			rc = json.optInt(key, fallback);
		}
		return rc;
	}

	@Nullable
	public static String getString(
		@Nullable JSONObject json,
		@NonNull String key,
		@Nullable String fallback
	) {
		String rc = fallback;
		if (json != null) {
			if (fallback == null) {
				rc = json.optString(key);
			} else {
				rc = json.optString(key, fallback);
			}
		}
		return rc;
	}

}
