/*
 * Copyright 2022 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.page.log;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.page.AbstractPagePref;
import app.misono.unit206.page.Page;

class LogPref extends AbstractPagePref {
	private static final String TAG = "LogPref";
	private static final String KEY_STATE = "STATE";

	// tempolary
	private int state;

	LogPref(@NonNull Context context, @NonNull Page page) {
		super(context, page, TAG);
/*
		JSONObject json = loadPref();
		if (json != null) {
			try {
				content = json.getInt(KEY_STATE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
*/
	}

	int getState() {
		return state;
	}

	void setState(int state) {
		log("setState:" + state);
		this.state = state;
	}

	@Override
	public void restoreBundle(@NonNull Bundle bundle) {
		log("restoreBundle:");
		state = bundle.getInt(KEY_STATE, state);
	}

	@Override
	@Nullable
	public Bundle createBundle() {
		log("createBundle:");
		Bundle bundle = new Bundle();
		bundle.putInt(KEY_STATE, state);
		return bundle;
	}

	@Override
	public void apply() {
/*
		JSONObject json = new JSONObject();
		try {
			json.put(KEY_CONTENT, content);
			applyPref(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
*/
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
