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

package app.misono.unit206.page;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.element.Element;
import app.misono.unit206.misc.SavedPreferences;
import app.misono.unit206.misc.UnitPref;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractPagePref implements PagePref {
	private static final String TAG = "AbstractPagePref";

	private final SavedPreferences pref;
	private final Set<UnitPref> units;
	private final String tagPref;
	private final Page page;

	protected AbstractPagePref(@NonNull Context context, @Nullable Page page, @NonNull String tagPref) {
		pref = new SavedPreferences(context);
		this.page = page;
		this.tagPref = tagPref;
		units = new HashSet<>();
	}

	@Override
	@NonNull
	public String getKey() {
		if (page != null) {
			return getKey(page.getTag(), tagPref);
		} else {
			return getKey(null, tagPref);
		}
	}

	@NonNull
	public static String getKey(@Nullable String tagPage, @NonNull String tagPref) {
		if (tagPage != null) {
			return tagPage + "@" + tagPref;
		} else {
			return tagPref;
		}
	}

	@Nullable
	protected JSONObject loadPref() {
		JSONObject json = null;
		try {
			String s = pref.getString(getKey(), null);
			if (s != null) {
				json = new JSONObject(s);
			}
		} catch (JSONException e) {
			// nop
		}
		return json;
	}

	public static boolean loadPrefBoolean(
		@NonNull Context context,
		@Nullable String tagPage,
		@NonNull String tagPref,
		@NonNull String key
	) {
		boolean rc = false;
		JSONObject json = null;
		try {
			SavedPreferences pref = new SavedPreferences(context);
			String s = pref.getString(getKey(tagPage, tagPref), null);
			if (s != null) {
				json = new JSONObject(s);
				rc = json.optBoolean(key, false);
			}
		} catch (JSONException e) {
			// nop
		}
		return rc;
	}

	protected void applyPref(@NonNull JSONObject json) {
		pref.putString(getKey(), json.toString());
		pref.apply();
	}

	protected void addUnitPref(@NonNull UnitPref prefUnit) {
		units.add(prefUnit);
	}

	protected void addElement(@NonNull Element element) {
		UnitPref prefUnit = element.getUnitPref();
		if (prefUnit != null) {
			units.add(prefUnit);
		}
	}

}
