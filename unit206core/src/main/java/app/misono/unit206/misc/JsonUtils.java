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

import android.graphics.Rect;
import android.util.LongSparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public static Set<String> jsonStringArrayToSet(@NonNull JSONArray ja) throws JSONException {
		Set<String> rc = new HashSet<>();
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
	public static int[] jsonIntArrayToArray(@NonNull JSONArray ja) throws JSONException {
		int n = ja.length();
		int[] rc = new int[n];
		for (int i = 0; i < n; i++) {
			rc[i] = ja.getInt(i);
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
	public static JSONArray toJSONArray(@NonNull Collection<String> collection) {
		JSONArray rc = new JSONArray();
		for (String s : collection) {
			rc.put(s);
		}
		return rc;
	}

	@NonNull
	public static JSONArray toJSONArray(@NonNull String[] array) {
		JSONArray rc = new JSONArray();
		for (String s : array) {
			rc.put(s);
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

	@NonNull
	public static JSONArray toJSONArray(@NonNull long[] ll) throws JSONException {
		JSONArray rc = new JSONArray();
		for (long l : ll) {
			rc.put(l);
		}
		return rc;
	}

	@NonNull
	public static JSONArray toJSONArray(@NonNull int[] ii) throws JSONException {
		JSONArray rc = new JSONArray();
		for (int i : ii) {
			rc.put(i);
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

	public static void put(@NonNull JSONObject json, @NonNull String key, boolean bool) {
		try {
			json.put(key, bool);
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
	public static Double getDouble(@Nullable JSONArray ja, int index) {
		Double rc = null;
		if (ja != null) {
			if (!ja.isNull(index)) {
				try {
					rc = ja.getDouble(index);
				} catch (JSONException e) {
					// nop
				}
			}
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

	@NonNull
	public static LongSparseArray<String> json2StringSparseArray(@NonNull JSONArray ja) throws JSONException {
		LongSparseArray<String> rc = new LongSparseArray<>();
		int n = ja.length();
		for (int i = 0; i < n; i++) {
			JSONArray ja2 = ja.getJSONArray(i);
			long uid = ja2.getLong(0);
			String s = ja2.getString(1);
			rc.append(uid, s);
		}
		return rc;
	}

	@NonNull
	public static JSONArray stringSparseArray2Json(@NonNull LongSparseArray<String> array) {
		JSONArray rc = new JSONArray();
		int n = array.size();
		for (int i = 0; i < n; i++) {
			long uid = array.keyAt(i);
			String s = array.valueAt(i);
			JSONArray ja = new JSONArray();
			ja.put(uid);
			ja.put(s);
			rc.put(ja);
		}
		return rc;
	}

	@NonNull
	public static JSONArray stringMap2Json(@NonNull Map<String, String> map) {
		Set<String> keys = map.keySet();
		JSONArray rc = new JSONArray();
		for (String key : keys) {
			String v = map.get(key);
			JSONArray ja = new JSONArray();
			ja.put(key);
			ja.put(v);
			rc.put(ja);
		}
		return rc;
	}

	@NonNull
	public static Map<String, String> json2StringMap(@NonNull JSONArray ja) throws Exception {
		Map<String, String> rc = new HashMap<>();
		int n = ja.length();
		for (int i = 0; i < n; i++) {
			JSONArray ja2 = ja.getJSONArray(i);
			String key = ja2.getString(0);
			String v = ja2.getString(1);
			rc.put(key, v);
		}
		return rc;
	}

	@NonNull
	public static JSONArray toJsonArray(@NonNull Rect rect) {
		JSONArray rc = new JSONArray();
		rc.put(rect.left);
		rc.put(rect.top);
		rc.put(rect.right);
		rc.put(rect.bottom);
		return rc;
	}

}
