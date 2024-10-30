/*
 * Copyright 2024 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.callback.CallbackBoolean;
import app.misono.unit206.debug.Log2;

public class TouchView extends View {
	private static final String TAG = "TouchView";

	private CallbackBoolean listener;

	public TouchView(@NonNull Context context) {
		super(context);
	}

	public TouchView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public TouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public TouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_DOWN:
			if (listener != null) {
				listener.callback(true);
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP:
			if (listener != null) {
				listener.callback(false);
			}
			break;
		}
		return false;
	}

	public void setTouchListener(@Nullable CallbackBoolean listener) {
		this.listener = listener;
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
