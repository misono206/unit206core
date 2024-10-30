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

package app.misono.unit206.lineage;

import androidx.annotation.NonNull;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.Utils;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

public class LineageItemMutable implements LineageItem, LineageWrapperMutable {
	private static final String	TAG	= "LineageItemMutable";
	private static final int VERSION = 0;

	private Set<Long> parents;
	private String name;
	private String description;
	private long uid;

	// temporary
	private Set<Long> children;

	public LineageItemMutable() {
		name = "???";
		description = "";
		parents = new HashSet<>();
		uid = Utils.createUidHash();
	}

	public LineageItemMutable(@NonNull String name) {
		uid = Utils.createUidHash();
		this.name = name;
		description = "";
		parents = new HashSet<>();
	}

	public LineageItemMutable(@NonNull JSONArray in) throws JSONException {
		parents = new HashSet<>();
		int ver = in.getInt(0);
		switch (ver) {
		case 0:
			readVersion0(in);
			break;
		default:
			log("unknown version:" + ver);
		}
	}

	private void readVersion0(@NonNull JSONArray ja) throws JSONException {
		int idx = 1;
		uid = ja.getLong(idx++);
		name = ja.getString(idx++);
		description = ja.getString(idx++);
		JSONArray ja2 = ja.getJSONArray(idx);
		int n = ja2.length();
		for (int i = 0; i < n; i++) {
			parents.add(ja2.getLong(i));
		}
	}

	@NonNull
	public JSONArray toJson() {
		JSONArray rc = new JSONArray();
		rc.put(VERSION);
		rc.put(uid);
		rc.put(name);
		rc.put(description);
		JSONArray ja = new JSONArray();
		for (long parent : parents) {
			ja.put(parent);
		}
		rc.put(ja);
		return rc;
	}

	@Override
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	@Override
	@NonNull
	public LineageItemMutable clone() {
		try {
			return (LineageItemMutable)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@NonNull
	public <M extends LineageWrapperMutable> M deepClone() {
		M rc = (M)clone();
		parents = new HashSet<>(parents);
		if (children != null) {
			children = new HashSet<>(children);
		}
		return rc;
	}

	@Override
	@NonNull
	public String getName() {
		return name;
	}

	public void setName(@NonNull String name) {
		this.name = name;
	}

	@Override
	@NonNull
	public String getDescription() {
		return description;
	}

	public void setDescription(@NonNull String description) {
		this.description = description;
	}

	@Override
	@NonNull
	public Set<Long> getParents() {
		return new HashSet<>(parents);
	}

	@Override
	public int getParentSize() {
		return parents.size();
	}

	@Override
	public boolean isParent(long uid) {
		return parents.contains(uid);
	}

	public void addParent(long hashParent) {
		parents.add(hashParent);
	}

	@Override
	@NonNull
	public Set<Long> getChildren() {
		return new HashSet<>(children);
	}

	@Override
	public void addChild(long uidChild) {
		if (children == null) {
			children = new HashSet<>();
		}
		children.add(uidChild);
	}

	@Override
	public int getChildSize() {
		if (children != null) {
			return children.size();
		} else {
			return 0;
		}
	}

	public boolean removeParent(long hashParent) {
		return parents.remove(hashParent);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
