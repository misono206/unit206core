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

import java.util.List;

public class FullOrder {
	private List<IOrder> list;

	public FullOrder() {
	}

	public void setOrderList(@NonNull List<IOrder> listSorted) {
		list = listSorted;
	}

	public int calcOrder(int indexInsert) {
		int n = list.size();
		int rc;
		if (n == 0) {
			rc = 0;
		} else if (indexInsert == 0) {
			rc = (Integer.MIN_VALUE + list.get(0).getOrder()) / 2;
		} else if (n <= indexInsert) {
			rc = (list.get(n - 1).getOrder() + Integer.MAX_VALUE) / 2;
		} else {
			rc = (list.get(indexInsert - 1).getOrder() + list.get(indexInsert).getOrder()) / 2;
		}
		return rc;
	}

}
