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

import android.util.LongSparseArray;

import androidx.annotation.NonNull;

import app.misono.unit206.debug.Log2;

import java.util.HashSet;
import java.util.Set;

public class LineageUtils {
	private static final String TAG = "LineageUtils";

	@NonNull
	public static Set<Long> getUpstreamParents(@NonNull LongSparseArray<LineageItem> array, long uid) {
		Set<Long> rc = new HashSet<>();
		LineageItem item = array.get(uid);
		if (item != null) {
			Set<Long> parent = item.getParents();
			for (Long uidParent : parent) {
				if (!rc.contains(uidParent)) {
					rc.add(uidParent);
					Set<Long> setParent = getUpstreamParents(array, uidParent);
					rc.addAll(setParent);
				}
			}
		}
		return rc;
	}

	@NonNull
	public static <
		M extends LineageWrapperMutable,
		I extends LineageWrapper
	> LongSparseArray<M> setChildren(LongSparseArray<I> array) {
		LongSparseArray<M> rc = new LongSparseArray<>();
		int n = array.size();
		for (int i = 0; i < n; i++) {
			I item = array.valueAt(i);
			M mutable = item.deepClone();
			rc.append(mutable.getUid(), mutable);
		}
		n = rc.size();
		for (int i = 0; i < n; i++) {
			M item = rc.valueAt(i);
			long uid = item.getUid();
			Set<Long> parents = item.getParents();
			for (long uidParent : parents) {
				M mutable = rc.get(uidParent);
				if (mutable != null) {
					mutable.addChild(uid);
				}
			}
		}
		return rc;
	}

	@NonNull
	private static String getIndent(int nest) {
		StringBuilder rc = new StringBuilder();
		for (int i = 0; i < nest; i++) {
			rc.append("  ");
		}
		return rc.toString();
	}

	public static void dumpLineage(
		@NonNull LongSparseArray<LineageWrapperMutable> array,
		@NonNull Set<Long> done,
		@NonNull LineageWrapperMutable item,
		int nest
	) {
		long uid = item.getUid();
		done.add(uid);
		int nChild = item.getChildSize();
		Log2.e(TAG, getIndent(nest) + uid + " " + item.getName() + " children:" + nChild);
		if (nChild != 0) {
			Set<Long> children = item.getChildren();
			if (0 <= nest) {
				for (long uidChild : children) {
					LineageWrapperMutable child = array.get(uidChild);
					dumpLineage(array, done, child, nest + 1);
				}
			} else {
				// for orphan
				String indent = getIndent(nest + 1);
				for (long uidChild : children) {
					LineageWrapperMutable child = array.get(uidChild);
					Log2.e(TAG, indent + uidChild + " " + child.getName() + " children:" + child.getChildSize());
				}
			}
		}
	}

	public static void dumpLineage(@NonNull LongSparseArray<LineageWrapper> array) {
		Log2.e(TAG, "array:" + array.size());
		LongSparseArray<LineageWrapperMutable> tmp = setChildren(array);
		Log2.e(TAG, "tmp:" + tmp.size());
		Set<Long> done = new HashSet<>();
		int n = tmp.size();
		for (int i = 0; i < n; i++) {
			LineageWrapperMutable item = tmp.valueAt(i);
			Log2.e(TAG, "xxx: " + item.getName() + " parentSize:" + item.getParentSize());
			if (item.getParentSize() == 0) {
				// root item.
				dumpLineage(tmp, done, item, 0);
			}
		}
		if (done.size() != n) {
			Log2.e(TAG, "=== orphan:");
			for (int i = 0; i < n; i++) {
				LineageWrapperMutable item = tmp.valueAt(i);
				if (!done.contains(item.getUid())) {
					dumpLineage(tmp, done, item, -1);
				}
			}
		}
	}

}
