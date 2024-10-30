/*
 * Copyright 2020 Atelier Misono, Inc. @ https://misono.app/
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

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.task.Taskz;

public class BlockClick {
	private static final String TAG = "BlockClick";

	private final View block;

	public BlockClick(
		@NonNull FrameLayout size,
		@NonNull View block,
		@Nullable Runnable onSizeChanged
	) {
		this.block = block;
		View view = new View(size.getContext()) {
			@Override
			protected void onSizeChanged(int w, int h, int oldw, int oldh) {
				if (onSizeChanged != null) {
//					log("onSizeChanged:" + w + " " + h);
					Taskz.call(() -> {
						onSizeChanged.run();
						return null;
					});
				}
			}
		};
		view.setBackgroundColor(0);
		size.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
	}

	public BlockClick(
		@NonNull FrameLayout blockAndSize,
		@Nullable Runnable onSizeChanged
	) {
		this(blockAndSize, blockAndSize, onSizeChanged);
	}

	public void block() {
		log("block:");
		block.setOnClickListener(null);
	}

	public void unblock() {
		log("unblock:");
		block.setClickable(false);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
