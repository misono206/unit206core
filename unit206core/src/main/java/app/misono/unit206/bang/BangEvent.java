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

package app.misono.unit206.bang;

import android.util.LongSparseArray;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class BangEvent<I extends BangItem> {
	private final List<I> list;

	public BangEvent(@NonNull LongSparseArray<I> hash) {
		List<I> list = new ArrayList<>();
		int n = hash.size();
		for (int i = 0; i < n; i++) {
			list.add(hash.valueAt(i));
		}
		this.list = Collections.unmodifiableList(list);
	}

	public BangEvent(@NonNull List<I> list) {
		this.list = Collections.unmodifiableList(list);
	}

	public int size() {
		return list.size();
	}

	@NonNull
	public List<I> getList() {
		return list;
	}

	@NonNull
	public List<I> getList(@NonNull Comparator<I> comparator) {
		List<I> rc = new ArrayList<>(list);
		Collections.sort(rc, comparator);
		return rc;
	}

	@NonNull
	public JSONArray createJson() throws JSONException {
		JSONArray rc = new JSONArray();
		for (I item : list) {
			rc.put(item.toJson());
		}
		return rc;
	}

}
