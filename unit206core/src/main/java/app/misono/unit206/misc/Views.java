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

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import app.misono.unit206.task.Taskz;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;

public final class Views {
	public static void hideKeyboard(@NonNull View view) {
		Context context = view.getContext();
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	@MainThread
	@NonNull
	public static Snackbar showSnackLong(@NonNull View view, @NonNull Throwable e) {
		Snackbar snack = Snackbar.make(view, e.toString(), Snackbar.LENGTH_LONG);
		snack.show();
		return snack;
	}

	@AnyThread
	@NonNull
	public static Task<Snackbar> showSnackLongWorker(@NonNull View view, @NonNull Throwable e) {
		return Taskz.call(TaskExecutors.MAIN_THREAD, () -> {
			return showSnackLong(view, e);
		});
	}

	@MainThread
	@NonNull
	public static Snackbar showSnackLong(@NonNull View view, @NonNull String msg) {
		Snackbar snack = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
		snack.show();
		return snack;
	}

	@AnyThread
	@NonNull
	public static Task<Snackbar> showSnackLongWorker(@NonNull View view, @NonNull String msg) {
		return Taskz.call(TaskExecutors.MAIN_THREAD, () -> {
			return showSnackLong(view, msg);
		});
	}

	@MainThread
	@NonNull
	public static Snackbar showSnackLong(@NonNull View view, @StringRes int idMessage) {
		Snackbar snack = Snackbar.make(view, idMessage, Snackbar.LENGTH_LONG);
		snack.show();
		return snack;
	}

	@AnyThread
	@NonNull
	public static Task<Snackbar> showSnackLongWorker(@NonNull View view, @StringRes int idMessage) {
		return Taskz.call(TaskExecutors.MAIN_THREAD, () -> {
			return showSnackLong(view, idMessage);
		});
	}

	@MainThread
	@NonNull
	public static Snackbar showSnackProgress(@NonNull View view, @NonNull String msg) {
		Snackbar snack = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE);
		snack.show();
		return snack;
	}

	@AnyThread
	@NonNull
	public static Task<Snackbar> showSnackProgressWorker(@NonNull View view, @NonNull String msg) {
		return Taskz.call(TaskExecutors.MAIN_THREAD, () -> {
			return showSnackProgress(view, msg);
		});
	}

	@MainThread
	@NonNull
	public static Snackbar showSnackProgress(@NonNull View view, @StringRes int idMessage) {
		Snackbar snack = Snackbar.make(view, idMessage, Snackbar.LENGTH_INDEFINITE);
		snack.show();
		return snack;
	}

	@AnyThread
	@NonNull
	public static Task<Snackbar> showSnackProgressWorker(@NonNull View view, @StringRes int idMessage) {
		return Taskz.call(TaskExecutors.MAIN_THREAD, () -> {
			return showSnackProgress(view, idMessage);
		});
	}

	@MainThread
	@NonNull
	public static Snackbar showSnackProgress(
		@NonNull View view,
		@StringRes int idMessage,
		@StringRes int idAction,
		@NonNull View.OnClickListener listener
	) {
		Snackbar snack = Snackbar.make(view, idMessage, Snackbar.LENGTH_INDEFINITE);
		snack.setAction(idAction, listener);
		snack.show();
		return snack;
	}

	@MainThread
	@NonNull
	public static Snackbar showSnackProgress(
		@NonNull View view,
		@NonNull String msg,
		@StringRes int idAction,
		@NonNull View.OnClickListener listener
	) {
		Snackbar snack = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE);
		snack.setAction(idAction, listener);
		snack.show();
		return snack;
	}

	@AnyThread
	@NonNull
	public static Task<Snackbar> showSnackProgressWorker(
		@NonNull View view,
		@StringRes int idMessage,
		@StringRes int idAction,
		@NonNull View.OnClickListener listener
	) {
		return Taskz.call(TaskExecutors.MAIN_THREAD, () -> {
			return showSnackProgress(view, idMessage, idAction, listener);
		});
	}

	@AnyThread
	public static void dismissSnackWorker(@Nullable Snackbar snack) {
		if (snack != null) {
			Taskz.call(TaskExecutors.MAIN_THREAD, () -> {
				snack.dismiss();
				return null;
			});
		}
	}

	@NonNull
	public static FrameLayout.LayoutParams newFrameMatchParams() {
		return new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
	}

}
