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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonBody {

	public interface NewItem<T extends JsonArrayItem> {
		T newItem(int version, @NonNull JSONArray ja) throws JSONException;
	}

	@NonNull
	public static <T extends JsonArrayItem> List<T> toListItem(@NonNull JSONObject json,
			@NonNull NewItem<T> newItem) throws JSONException {

		List<T> rc = new ArrayList<>();
		int version = json.getInt("version");
		JSONArray ja = json.getJSONArray("body");
		int n = ja.length();
		for (int i = 0; i < n; i++) {
			JSONArray ja2 = ja.getJSONArray(i);
			T item = newItem.newItem(version, ja2);
			rc.add(item);
		}
		return rc;
	}

	@NonNull
	public static <T extends JsonArrayItem> JSONObject toJson(@NonNull List<T> list) throws JSONException {
		JSONObject json = new JSONObject();
		if (!list.isEmpty()) {
			json.put("version", list.get(0).getVersion());
			JSONArray ja = new JSONArray();
			for (T item : list) {
				ja.put(item.toJsonArray());
			}
			json.put("body", ja);
		}
		return json;
	}

}
