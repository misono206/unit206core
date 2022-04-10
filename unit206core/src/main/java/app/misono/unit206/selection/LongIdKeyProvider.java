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

package app.misono.unit206.selection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import app.misono.unit206.debug.Log2;

import java.util.ArrayList;
import java.util.List;

public class LongIdKeyProvider extends ItemKeyProvider<Long> {
	private static final String TAG = "LongIdKeyProvider";

	private List<Long> mList;

	public LongIdKeyProvider() {
		super(SCOPE_MAPPED);
		mList = new ArrayList<>();
	}

	public <T extends LongId> void setLongIdList(@NonNull List<T> list) {
		mList.clear();
		for (LongId item : list) {
			mList.add(item.getLongId());
		}
	}

	@NonNull
	public List<Long> getIdList() {
		return mList;
	}

	@Nullable
	@Override
	public Long getKey(int position) {
//		log("getKey:" + position + " rc:" + mList.get(position));
		return mList.get(position);
	}

	@Override
	public int getPosition(@NonNull Long key) {
//		log("getPosition:" + key + " rc:" + mList.indexOf(key));
		return mList.indexOf(key);	// -1: RecyclerView.NO_POSITION
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
