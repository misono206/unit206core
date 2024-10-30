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
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.google.android.material.textview.MaterialTextView;

public class LogView extends FrameLayout {
	MaterialTextView text;

	LinearLayout linear;
	ScrollView scroll;

	public LogView(@NonNull Context context) {
		super(context);
		init(context);
	}

	public LogView(@NonNull Context context, @NonNull AttributeSet attr) {
		super(context, attr);
		init(context);
	}

	private void init(@NonNull Context context) {
		linear = new LinearLayout(context);
		linear.setOrientation(LinearLayout.VERTICAL);

		text = new MaterialTextView(context);
		text.setTypeface(Typeface.MONOSPACE);
		linear.addView(text, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		scroll = new ScrollView(context);
		scroll.addView(linear, ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
		addView(scroll, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	@MainThread
	void setLogText(@NonNull String msg) {
		text.setText(msg);
	}

}

