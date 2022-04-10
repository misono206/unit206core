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

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

public interface Page {
	@Deprecated
	@NonNull
	Context getContext();
	@NonNull
	PageActivity getPageActivity();
	@NonNull
	FrameLayout getBaseView();
	boolean isActiveToolbar();
	void addToParent(@Nullable Runnable done);
	void removeFromParent(@Nullable Runnable done);
	void onBackPressed();
	void changeLayout(int w, int h);
	void onResume();
	void onPause();
	@Nullable
	String refreshPage();
	boolean isHamburgerIcon();
	@NonNull
	String getTag();
	boolean onCreateOptionsMenu(Menu menu);
	boolean onOptionsItemSelected(@NonNull MenuItem item);
	void showSnackbar(@NonNull String msg);
	void showSnackbar(@StringRes int idMessage);
	@NonNull
	Task<Void> showSnackbarTask(@NonNull String msg);
	@NonNull
	Task<Void> showSnackbarTask(@StringRes int idMessage);
	@NonNull
	Snackbar showSnackProgress(@StringRes int idMessage);
	@NonNull
	Snackbar showSnackProgress(
		@StringRes int idMessage,
		@StringRes int idAction,
		@NonNull View.OnClickListener listener
	);
	@Nullable
	PagePref getPref();

}
