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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.misono.unit206.callback.CallbackInteger;
import app.misono.unit206.debug.Log2;

public final class RecyclerPagerListener extends RecyclerView.OnScrollListener {
	private static final String TAG = "RecyclerPagerListener";

	private final LinearLayoutManager manager;
	private final CallbackInteger listenerOnSelected;
	private final RecyclerView recycler;

	private boolean disableScroll;
	private boolean settling;
	private int indexDestination;
	private int dxLast;
	private int positionPresent;

	public RecyclerPagerListener(
		@NonNull RecyclerView recycler,
		@NonNull LinearLayoutManager manager,
		@Nullable CallbackInteger listenerOnSelected
	) {
		this.recycler = recycler;
		this.manager = manager;
		this.listenerOnSelected = listenerOnSelected;
		indexDestination = -1;
		positionPresent = -1;
	}

	@Override
	public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
		if (disableScroll) {
			if (newState == RecyclerView.SCROLL_STATE_IDLE) {
				disableScroll = false;
				dxLast = 0;
			}
		} else {
			switch (newState) {
			case RecyclerView.SCROLL_STATE_DRAGGING:
				dxLast = 0;
				settling = false;
				break;
			case RecyclerView.SCROLL_STATE_SETTLING:
			case RecyclerView.SCROLL_STATE_IDLE:
				settling = true;
				settling();
				break;
			}
		}
	}

	@Override
	public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
		dxLast += dx;
		if (settling) {
			settling();
		}
	}

	private void settling() {
		int position;
		if (dxLast < 0) {
			position = manager.findFirstVisibleItemPosition();
		} else {
			position = manager.findLastVisibleItemPosition();
		}
		if (indexDestination < 0) {
			recycler.smoothScrollToPosition(position);
		} else if (indexDestination == position) {
			// reached...
			indexDestination = -1;
		}
		dxLast = 0;
		if (positionPresent != position) {
			positionPresent = position;
			if (listenerOnSelected != null) {
				listenerOnSelected.callback(position);
			}
		}
	}

	public void setSmoothScrolling(int indexDestination) {
		this.indexDestination = indexDestination;
	}

	public void disableOnScroll() {
		disableScroll = true;
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
