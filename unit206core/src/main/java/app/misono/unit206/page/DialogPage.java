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

package app.misono.unit206.page;

import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdView;

public final class DialogPage extends AbstractPage {
	private static final String TAG = "DialogPage";

	public DialogPage(
		@NonNull PageManager manager,
		@NonNull PageActivity activity,
		@NonNull FrameLayout adbase,
		@NonNull FrameLayout parent,
		@Nullable AdView adview,
		@Nullable Runnable clickBack
	) {
		super(manager, activity, adbase, parent, adview, clickBack);
	}

	@Override
	public void changeLayout(int w, int h) {

	}

	@Override
	@NonNull
	public String getTag() {
		return TAG;
	}

	@Override
	public String refreshPage() {
		return null;
	}

}
