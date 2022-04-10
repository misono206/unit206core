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

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class HierarchyMap<E extends HierarchyItem> {
	protected final SparseArray<E> map;
	protected final HierarchyItem root;

	private int hashNext;

	public HierarchyMap() {
		map = new SparseArray<>();
		root = new HierarchyMutableItem();
	}

	@NonNull
	public HierarchyItem getRoot() {
		return root;
	}

	public int getNextHash() {
		return hashNext++;
	}

	public void addItem(@NonNull E item) {
		int hash = item.getHash();
		if (map.get(hash) != null) {
			throw new RuntimeException("Already exists hash:" + hash);
		} else {
			map.put(hash, item);
			if (hashNext <= hash) {
				hashNext = hash + 1;
			}
		}
	}

	public void updateItem(@NonNull E item) {
		int hash = item.getHash();
		map.put(hash, item);
		if (hashNext <= hash) {
			hashNext = hash + 1;
		}
	}

	@Nullable
	public E getItem(int hash) {
		return map.get(hash);
	}

	public void removeItem(int hash) {
		map.remove(hash);
	}

	public void removeItem(@Nullable E item) {
		if (item != null) {
			map.remove(item.getHash());
		}
	}

	public void setList(@NonNull List<E> list) {
		map.clear();
		hashNext = 0;
		for (E item : list) {
			addItem(item);
		}
	}

	public int getNest(int hash) {
		if (hash < 0) {
			return -1;
		}
		E item = map.get(hash);
		if (item == null) {
			return -1;
		}
		int parent = item.getParentHash();
		return getNest(parent) + 1;
	}

}
