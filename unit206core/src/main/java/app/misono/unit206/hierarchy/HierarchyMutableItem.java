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

package app.misono.unit206.hierarchy;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.debug.Log2;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

public class HierarchyMutableItem implements HierarchyItem {
	private static final String TAG = "HierarchyMutableItem";

	private final Set<Integer> children;

	private boolean updated;
	private int parent;
	private int hash;

	public HierarchyMutableItem() {
		children = new HashSet<>();
		parent = -1;
		hash = -1;
	}

	protected void setJSONArray(@NonNull JSONArray ja) throws JSONException {
		int n = ja.length();
		if (n == 0) {
			throw new RuntimeException("not found body...");
		} else {
			children.clear();
			parent = -1;
			int idx = 0;
			hash = ja.getInt(idx++);
			if (hash < 0) {
				throw new RuntimeException("illegal hash value:" + hash);
			}
			parent = ja.getInt(idx++);
		}
	}

	public void setParce(@NonNull Parcel parcel) {
		throw new RuntimeException("not supported Parcel yet...");
	}

	@Override
	@NonNull
	public HierarchyMutableItem clone() {
		try {
			return (HierarchyMutableItem)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public void setHash(int hash) {
		this.hash = hash;
	}

	public void addChild(@NonNull HierarchyMutableItem child) {
		if (child.parent != hash) {
			child.parent = hash;
			child.updated = true;
		}
		updated |= children.add(child.hash);
	}

	public void removeChild(@Nullable HierarchyMutableItem child) {
		if (child != null) {
			if (child.parent == hash) {
				child.parent = -1;
				child.updated = true;
			}
			updated |= children.remove(child.hash);
		}
	}

	public boolean isUpdated() {
		return updated;
	}

	@Override
	public boolean isParentHash(int hashParent) {
		return parent == hashParent;
	}

	@Override
	public int getParentHash() {
		return parent;
	}

	@Override
	@NonNull
	public Set<Integer> getChildrenHash() {
		return new HashSet<>(children);
	}

	@Override
	public int getHash() {
		return hash;
	}

	@Override
	@NonNull
	public JSONArray toJson() {
		if (hash < 0) {
			throw new RuntimeException("invalid hash:" + hash);
		}
		JSONArray rc = new JSONArray();
		rc.put(hash);
		rc.put(parent);
		return rc;
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
