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

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import app.misono.unit206.R;
import app.misono.unit206.callback.CallbackInteger;
import app.misono.unit206.callback.CallbackString;
import app.misono.unit206.task.ObjectReference;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;
import java.util.List;

public class DialogUtils {

	private static boolean isCanceledOnTouchOutside = true;

	public static void setCanceledOnTouchOutside(boolean cancel) {
		isCanceledOnTouchOutside = cancel;
	}

	public static void setOutsideSetting(@NonNull Dialog d) {
		d.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
	}

	@NonNull
	public static LinearLayout.LayoutParams createParamBody() {
		LinearLayout.LayoutParams pBody = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT
		);
		pBody.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
		pBody.leftMargin = 10;	// TODO
		pBody.rightMargin = 10;	// TODO
		return pBody;
	}

	@NonNull
	public static LinearLayout.LayoutParams createParamLabel(int widthLabel) {
		LinearLayout.LayoutParams pLabel = new LinearLayout.LayoutParams(
			widthLabel,
			LinearLayout.LayoutParams.WRAP_CONTENT
		);
		pLabel.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
		pLabel.leftMargin = 10;		// TODO
		pLabel.rightMargin = 10;	// TODO
		return pLabel;
	}

	@NonNull
	public static String getString(@NonNull EditText edit) {
		String rc = "";
		CharSequence e = edit.getText();
		if (e != null) {
			rc = e.toString();
		}
		return rc;
	}

	@Nullable
	public static Integer getInteger(@NonNull EditText edit) {
		String s = getString(edit).trim();
		Integer rc = null;
		try {
			rc = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			// nop
		}
		return rc;
	}

	public static int optInteger(@NonNull EditText edit, int init) {
		String s = getString(edit).trim();
		int rc = init;
		try {
			rc = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			// nop
		}
		return rc;
	}

	@Nullable
	public static Long getLong(@NonNull EditText edit) {
		String s = getString(edit).trim();
		Long rc = null;
		try {
			rc = Long.parseLong(s);
		} catch (NumberFormatException e) {
			// nop
		}
		return rc;
	}

	public static long optLong(@NonNull EditText edit, long init) {
		String s = getString(edit).trim();
		long rc = init;
		try {
			rc = Long.parseLong(s);
		} catch (NumberFormatException e) {
			// nop
		}
		return rc;
	}

	@NonNull
	public static AppCompatEditText createEditText(@NonNull Context context) {
		AppCompatEditText text = new AppCompatEditText(context);
		text.setSingleLine();
		return text;
	}

	@NonNull
	public static MaterialButton createButton(
		@NonNull Context context,
		@StringRes int idLabel,
		@NonNull String text
	) {
		MaterialButton body = new MaterialButton(context);
		body.setText(text);
		return body;
	}

	public static void createViewLine(
		@NonNull LinearLayout base,
		@NonNull Context context,
		@StringRes int idLabel,
		@NonNull View view,
		@NonNull LinearLayout.LayoutParams pLabel
	) {
		LinearLayout line = new LinearLayout(context);
		line.setOrientation(LinearLayout.HORIZONTAL);
		MaterialTextView label = new MaterialTextView(context);
		label.setText(idLabel);
		label.setGravity(Gravity.END);
		line.addView(label, pLabel);
		line.addView(view);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	}

	@NonNull
	public static AppCompatEditText createEditTextLine(
		@NonNull LinearLayout base,
		@NonNull Context context,
		@StringRes int idLabel,
		@StringRes int idEditInit,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		AppCompatEditText body = createEditTextLine(base, context, idLabel, pLabel, pBody);
		body.setText(idEditInit);
		return body;
	}

	@NonNull
	public static AppCompatEditText createEditTextLine(
		@NonNull LinearLayout base,
		@NonNull Context context,
		@StringRes int idLabel,
		@NonNull String value,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		AppCompatEditText body = createEditTextLine(base, context, idLabel, pLabel, pBody);
		body.setText(value);
		return body;
	}

	@NonNull
	public static AppCompatEditText createEditTextLine(
		@NonNull LinearLayout base,
		@NonNull Context context,
		@StringRes int idLabel,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		String slabel = context.getString(idLabel);
		return createEditTextLine(base, context, slabel, pLabel, pBody);
	}

	@NonNull
	public static AppCompatEditText createEditTextLine(
		@NonNull LinearLayout base,
		@NonNull Context context,
		@NonNull String slabel,
		@NonNull String value,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		AppCompatEditText body = createEditTextLine(base, context, slabel, pLabel, pBody);
		body.setText(value);
		return body;
	}

	@NonNull
	public static AppCompatEditText createEditTextLine(
		@NonNull LinearLayout base,
		@NonNull Context context,
		@NonNull CharSequence slabel,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(context);
		line.setOrientation(LinearLayout.HORIZONTAL);
		MaterialTextView label = new MaterialTextView(context);
		label.setText(slabel);
		label.setGravity(Gravity.END);
		AppCompatEditText body = DialogUtils.createEditText(context);
		body.setSingleLine();
		line.addView(label, pLabel);
		line.addView(body, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		return body;
	}

	@NonNull
	public static AppCompatEditText createEditTextMultiple(
		@NonNull LinearLayout base,
		@NonNull Context context,
		@StringRes int idLabel,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(context);
		line.setOrientation(LinearLayout.HORIZONTAL);
		MaterialTextView label = new MaterialTextView(context);
		label.setText(idLabel);
		label.setGravity(Gravity.END);
		AppCompatEditText body = new AppCompatEditText(context);
		line.addView(label, pLabel);
		line.addView(body, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		return body;
	}

	@NonNull
	public static AppCompatEditText createEditTextMultiple(
		@NonNull LinearLayout base,
		@NonNull Context context,
		@StringRes int idLabel,
		@NonNull String text,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(context);
		line.setOrientation(LinearLayout.HORIZONTAL);
		MaterialTextView label = new MaterialTextView(context);
		label.setText(idLabel);
		label.setGravity(Gravity.END);
		AppCompatEditText body = new AppCompatEditText(context);
		body.setText(text);
		line.addView(label, pLabel);
		line.addView(body, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		return body;
	}

	@NonNull
	public static MaterialButton createButtonLine(
		@NonNull LinearLayout base,
		@NonNull Context context,
		@StringRes int idLabel,
		@StringRes int idText,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(context);
		line.setOrientation(LinearLayout.HORIZONTAL);
		MaterialTextView label = new MaterialTextView(context);
		label.setText(idLabel);
		label.setGravity(Gravity.END);
		MaterialButton body = new MaterialButton(context);
		body.setText(idText);
		line.addView(label, pLabel);
		line.addView(body, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		return body;
	}

	@NonNull
	public static MaterialButton createButtonLine(
		@NonNull LinearLayout base,
		@NonNull Context context,
		@StringRes int idLabel,
		@NonNull String text,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(context);
		line.setOrientation(LinearLayout.HORIZONTAL);
		MaterialTextView label = new MaterialTextView(context);
		label.setText(idLabel);
		label.setGravity(Gravity.END);
		MaterialButton body = new MaterialButton(context);
		body.setText(text);
		line.addView(label, pLabel);
		line.addView(body, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		return body;
	}

	@Deprecated		// use anothor createCheckBoxLine()
	@NonNull
	public static MaterialCheckBox createCheckBoxLine(
		@NonNull LinearLayout base,
		@NonNull Context context,
		@StringRes int idLabel,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		return createCheckBoxLine(base, context, idLabel, false, pLabel, pBody);
	}

	@NonNull
	public static MaterialCheckBox createCheckBoxLine(
		@NonNull LinearLayout base,
		@NonNull Context context,
		@StringRes int idLabel,
		boolean checked,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(context);
		line.setOrientation(LinearLayout.HORIZONTAL);
		MaterialCheckBox body = new MaterialCheckBox(context);
		body.setChecked(checked);
		LinearLayout linearBody = new LinearLayout(context);
		linearBody.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
		linearBody.addView(body, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		line.addView(linearBody, pLabel);
		MaterialTextView label = new MaterialTextView(context);
		label.setText(idLabel);
		label.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		line.addView(label, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		return body;
	}

	@NonNull
	public static String[] list2array(@NonNull List<String> list) {
		int n = list.size();
		String[] rc = new String[n];
		for (int i = 0; i < n; i++) {
			rc[i] = list.get(i);
		}
		return rc;
	}

	@Deprecated
	@NonNull
	public static AlertDialog selectDialog(
		@NonNull Context context,
		@NonNull CharSequence[] items,
		@StringRes int idTitle,
		int idxSelect,
		@NonNull CallbackInteger listener,
		@Nullable Runnable onCancel
	) {
		return selectDialog(context, items, idTitle, 0, idxSelect, onCancel, listener);
	}

	@Deprecated
	@NonNull
	public static AlertDialog selectDialog(
		@NonNull Context context,
		@NonNull CharSequence[] items,
		@StringRes int idTitle,
		int idxSelect,
		@Nullable Runnable onCancel,
		@NonNull CallbackInteger listener
	) {
		return selectDialog(context, items, idTitle, 0, idxSelect, onCancel, listener);
	}

	@Deprecated
	@NonNull
	public static AlertDialog selectDialog(
		@NonNull Context context,
		@NonNull CharSequence[] items,
		@StringRes int idTitle,
		@DrawableRes int idIcon,
		int idxSelect,
		@Nullable Runnable onCancel,
		@NonNull CallbackInteger listener
	) {
		ObjectReference<Boolean> refOk = new ObjectReference<>(false);
		MaterialAlertDialogBuilder builder = newBuilder(context);
		builder.setTitle(idTitle);
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		ObjectReference<Integer> select = new ObjectReference<>(idxSelect);
		builder.setSingleChoiceItems(items, idxSelect, (dialog, i) -> {
			select.set(i);
		});
		builder.setNegativeButton("CANCEL", null);
		builder.setPositiveButton("OK", (dialog, i) -> {
			int selected = select.get();
			if (0 <= selected) {
				listener.callback(selected);
				refOk.set(true);
			}
		});
		if (onCancel != null) {
			builder.setOnDismissListener(dialog -> {
				if (!refOk.get()) {
					onCancel.run();
				}
			});
		}
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@Deprecated
	@NonNull
	public static AlertDialog selectDialog(
		@NonNull Context context,
		@NonNull CharSequence[] items,
		@NonNull String title,
		int idxSelect,
		@Nullable Runnable onCancel,
		@NonNull CallbackInteger listener
	) {
		return selectDialog(context, items, title, 0, idxSelect, onCancel, listener);
	}

	@NonNull
	public static AlertDialog selectDialog(
		@NonNull Context context,
		@NonNull CharSequence[] items,
		@NonNull String title,
		@DrawableRes int idIcon,
		@StringRes int idOther,
		int idxSelect,
		@Nullable Runnable onCancel,
		@NonNull CallbackInteger listener,
		@Nullable Runnable onOther
	) {
		ObjectReference<Boolean> refOk = new ObjectReference<>(false);
		MaterialAlertDialogBuilder builder = newBuilder(context);
		builder.setTitle(title);
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		ObjectReference<Integer> select = new ObjectReference<>(idxSelect);
		builder.setSingleChoiceItems(items, idxSelect, (dialog, i) -> {
			select.set(i);
		});
		builder.setNegativeButton("CANCEL", null);
		builder.setPositiveButton("OK", (dialog, i) -> {
			int selected = select.get();
			if (0 <= selected) {
				listener.callback(selected);
				refOk.set(true);
			}
		});
		if (onCancel != null) {
			builder.setOnDismissListener(dialog -> {
				if (!refOk.get()) {
					onCancel.run();
				}
			});
		}
		if (idOther != 0 && onOther != null) {
			builder.setNeutralButton(idOther, (dialog, i) -> {
				refOk.set(true);
				onOther.run();
			});
		}
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@NonNull
	public static AlertDialog selectDialog(
		@NonNull Context context,
		@NonNull CharSequence[] items,
		@NonNull String title,
		@DrawableRes int idIcon,
		int idxSelect,
		@Nullable Runnable onCancel,
		@NonNull CallbackInteger listener
	) {
		return selectDialog(context, items, title, idIcon, 0, idxSelect, onCancel, listener, null);
	}

	@Deprecated
	@NonNull
	public static AlertDialog selectDialog(
		@NonNull Context context,
		@NonNull CharSequence[] items,
		@StringRes int idTitle,
		int idxSelect,
		@NonNull CallbackInteger listener
	) {
		return selectDialog(context, items, idTitle, idxSelect, null, listener);
	}

	@Deprecated
	@NonNull
	public static AlertDialog selectDialog(
		@NonNull Context context,
		@NonNull CharSequence[] items,
		@StringRes int idTitle,
		@NonNull CallbackInteger listener
	) {
		return selectDialog(context, items, idTitle, -1, null, listener);
	}

	@NonNull
	public static AlertDialog selectOkDialog(
		@NonNull Context context,
		@NonNull CharSequence[] items,
		@NonNull String title,
		@DrawableRes int idIcon,
		int idxSelect,
		@NonNull CallbackInteger listener
	) {
		ObjectReference<Boolean> refOk = new ObjectReference<>(false);
		MaterialAlertDialogBuilder builder = newBuilder(context);
		builder.setTitle(title);
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		ObjectReference<Integer> select = new ObjectReference<>(idxSelect);
		builder.setSingleChoiceItems(items, idxSelect, (dialog, i) -> {
			select.set(i);
		});
		builder.setPositiveButton("OK", (dialog, i) -> {
			int selected = select.get();
			if (0 <= selected) {
				listener.callback(selected);
				refOk.set(true);
			}
		});
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@Deprecated
	@NonNull
	public static AlertDialog checkDialog(
		@NonNull Context context,
		@NonNull CharSequence[] items,
		@StringRes int idTitle,
		@NonNull boolean[] checked,
		@NonNull Runnable ok
	) {
		String title = context.getString(idTitle);
		return checkDialog(context, items, title, checked, ok);
	}

	@Deprecated
	@NonNull
	public static AlertDialog checkDialog(
		@NonNull Context context,
		@NonNull CharSequence[] items,
		@NonNull String title,
		@NonNull boolean[] checked,
		@NonNull Runnable ok
	) {
		MaterialAlertDialogBuilder builder = newBuilder(context);
		builder.setTitle(title);
		builder.setMultiChoiceItems(items, checked, (dialog, which, isChecked) -> {
			checked[which] = isChecked;
		});
		builder.setNegativeButton("CANCEL", null);
		builder.setPositiveButton("OK", (dialog, i) -> {
			ok.run();
		});
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@NonNull
	public static AlertDialog checkDialog(
		@NonNull Context context,
		@NonNull CharSequence[] items,
		@NonNull String title,
		@DrawableRes int idIcon,
		@NonNull boolean[] checked,
		@NonNull Runnable ok
	) {
		MaterialAlertDialogBuilder builder = newBuilder(context);
		builder.setTitle(title);
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		builder.setMultiChoiceItems(items, checked, (dialog, which, isChecked) -> {
			checked[which] = isChecked;
		});
		builder.setNegativeButton("CANCEL", null);
		builder.setPositiveButton("OK", (dialog, i) -> {
			ok.run();
		});
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@NonNull
	public static AlertDialog messageDialog(
		@NonNull Context context,
		@StringRes int idMessage
	) {
		return messageDialog(context, 0, 0, idMessage, null);
	}

	@NonNull
	public static AlertDialog messageDialog(
		@NonNull Context context,
		@NonNull String msg
	) {
		return messageDialog(context, 0, 0, msg, null);
	}

	@NonNull
	public static AlertDialog messageDialog(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@StringRes int idMessage,
		@Nullable Runnable dismiss
	) {
		MaterialAlertDialogBuilder builder = newBuilder(context);
		if (idTitle != 0) {
			builder.setTitle(idTitle);
		}
		if (idMessage != 0) {
			builder.setMessage(idMessage);
		}
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		builder.setPositiveButton("OK", null);
		if (dismiss != null) {
			builder.setOnDismissListener(dialog -> {
				dismiss.run();
			});
		}
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@NonNull
	public static AlertDialog messageDialog(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable CharSequence message,
		@Nullable Runnable dismiss
	) {
		MaterialAlertDialogBuilder builder = newBuilder(context);
		if (idTitle != 0) {
			builder.setTitle(idTitle);
		}
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		builder.setMessage(message);
		builder.setPositiveButton("OK", null);
		if (dismiss != null) {
			builder.setOnDismissListener(dialog -> {
				dismiss.run();
			});
		}
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@Deprecated // use messageDialogOkCancel().
	@NonNull
	public static AlertDialog messageDialogCancelOk(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable CharSequence message,
		@Nullable Runnable dismiss,
		@NonNull Runnable ok
	) {
		MaterialAlertDialogBuilder builder = newBuilder(context);
		if (idTitle != 0) {
			builder.setTitle(idTitle);
		}
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		builder.setMessage(message);
		builder.setNegativeButton("CANCEL", null);
		builder.setPositiveButton("OK", (dialog, i) -> {
			ok.run();
		});
		if (dismiss != null) {
			builder.setOnDismissListener(dialog -> {
				dismiss.run();
			});
		}
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	private static boolean done001;

	@NonNull
	public static AlertDialog messageDialogOkCancel(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable CharSequence message,
		@Nullable Runnable cancel,
		@NonNull Runnable ok
	) {
		done001 = false;
		MaterialAlertDialogBuilder builder = newBuilder(context);
		if (idTitle != 0) {
			builder.setTitle(idTitle);
		}
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		builder.setMessage(message);
		builder.setNegativeButton("CANCEL", null);
		builder.setPositiveButton("OK", (dialog, i) -> {
			done001 = true;
			ok.run();
		});
		if (cancel != null) {
			builder.setOnDismissListener(dialog -> {
				if (!done001) {
					cancel.run();
				}
			});
		}
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@Deprecated // use messageDialogOkCancel().
	@NonNull
	public static AlertDialog messageDialogCancelOk(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable CharSequence message,
		@StringRes int idOk,
		@StringRes int idCancel,
		@Nullable Runnable dismiss,
		@NonNull Runnable ok
	) {
		MaterialAlertDialogBuilder builder = newBuilder(context);
		if (idTitle != 0) {
			builder.setTitle(idTitle);
		}
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		builder.setMessage(message);
		builder.setNegativeButton(idCancel, null);
		builder.setPositiveButton(idOk, (dialog, i) -> {
			ok.run();
		});
		if (dismiss != null) {
			builder.setOnDismissListener(dialog -> {
				dismiss.run();
			});
		}
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	private static boolean done002;

	@NonNull
	public static AlertDialog messageDialogOkCancel(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable CharSequence message,
		@StringRes int idOk,
		@StringRes int idCancel,
		@Nullable Runnable cancel,
		@NonNull Runnable ok
	) {
		done002 = false;
		MaterialAlertDialogBuilder builder = newBuilder(context);
		if (idTitle != 0) {
			builder.setTitle(idTitle);
		}
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		builder.setMessage(message);
		builder.setNegativeButton(idCancel, null);
		builder.setPositiveButton(idOk, (dialog, i) -> {
			done002 = true;
			ok.run();
		});
		if (cancel != null) {
			builder.setOnDismissListener(dialog -> {
				if (!done002) {
					cancel.run();
				}
			});
		}
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@Deprecated
	@NonNull
	public static AlertDialog messageDialogCancelOk(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@StringRes int idMessage,
		@NonNull Runnable ok
	) {
		MaterialAlertDialogBuilder builder = newBuilder(context);
		if (idTitle != 0) {
			builder.setTitle(idTitle);
		}
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		if (idMessage != 0) {
			builder.setMessage(idMessage);
		}
		builder.setNegativeButton("CANCEL", null);
		builder.setPositiveButton("OK", (dialog, i) -> {
			ok.run();
		});
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@NonNull
	public static AlertDialog messageDialogCancelOkOther(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable CharSequence message,
		@StringRes int idOk,
		@StringRes int idCancel,
		@StringRes int idOther,
		@Nullable Runnable dismiss,
		@NonNull Runnable other,
		@Nullable Runnable ok
	) {
		CharSequence title = null;
		if (idTitle != 0) {
			title = context.getString(idTitle);
		}
		CharSequence sOther = null;
		if (idOther != 0) {
			sOther = context.getString(idOther);
		}
		return messageDialogCancelOkOther(
			context,
			idIcon,
			title,
			message,
			idOk,
			idCancel,
			sOther,
			dismiss,
			other,
			ok
		);
	}

	@NonNull
	public static AlertDialog messageDialogCancelOkOther(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@Nullable CharSequence title,
		@Nullable CharSequence message,
		@StringRes int idOk,
		@StringRes int idCancel,
		@Nullable CharSequence sOther,
		@Nullable Runnable dismiss,
		@NonNull Runnable other,
		@Nullable Runnable ok
	) {
		MaterialAlertDialogBuilder builder = newBuilder(context);
		if (title != null) {
			builder.setTitle(title);
		}
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		builder.setMessage(message);
		if (idCancel != 0) {
			builder.setNegativeButton(idCancel, null);
		}
		if (idOk != 0) {
			if (ok != null) {
				builder.setPositiveButton(idOk, (dialog, i) -> {
					ok.run();
				});
			} else {
				builder.setPositiveButton(idOk, null);
			}
		}
		if (sOther != null) {
			builder.setNeutralButton(sOther, (dialog, i) -> {
				other.run();
			});
		}
		if (dismiss != null) {
			builder.setOnDismissListener(dialog -> {
				dismiss.run();
			});
		}
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@NonNull
	public static AlertDialog messageDialogCancelOkOther(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@Nullable CharSequence title,
		@Nullable CharSequence message,
		@StringRes int idOk,
		@StringRes int idCancel,
		@Nullable CharSequence sOther,
		@Nullable Runnable dismiss,
		@Nullable Runnable cancel,
		@NonNull Runnable other,
		@Nullable Runnable ok
	) {
		MaterialAlertDialogBuilder builder = newBuilder(context);
		if (title != null) {
			builder.setTitle(title);
		}
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		builder.setMessage(message);
		if (idCancel != 0) {
			if (cancel != null) {
				builder.setNegativeButton(idCancel, (dialog, i) -> {
					cancel.run();
				});
			} else {
				builder.setNegativeButton(idCancel, null);
			}
		}
		if (idOk != 0) {
			if (ok != null) {
				builder.setPositiveButton(idOk, (dialog, i) -> {
					ok.run();
				});
			} else {
				builder.setPositiveButton(idOk, null);
			}
		}
		if (sOther != null) {
			builder.setNeutralButton(sOther, (dialog, i) -> {
				other.run();
			});
		}
		if (dismiss != null) {
			builder.setOnDismissListener(dialog -> {
				dismiss.run();
			});
		}
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@NonNull
	public static AlertDialog inputTextDialog(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable CallbackString onText
	) {
		return inputTextDialog(
			context,
			idIcon,
			idTitle,
			null,
			null,
			R.string.cancel,
			R.string.ok,
			null,
			onText
		);
	}

	@NonNull
	public static AlertDialog inputTextDialog(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable Runnable cancel,
		@Nullable CallbackString onText
	) {
		return inputTextDialog(
			context,
			idIcon,
			idTitle,
			null,
			null,
			R.string.cancel,
			R.string.ok,
			cancel,
			onText
		);
	}

	@NonNull
	public static AlertDialog inputTextDialog(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable String initText,
		@Nullable String hint,
		@Nullable Runnable cancel,
		@Nullable CallbackString onText
	) {
		return inputTextDialog(
			context,
			idIcon,
			idTitle,
			initText,
			hint,
			R.string.cancel,
			R.string.ok,
			cancel,
			onText
		);
	}

	@NonNull
	public static AlertDialog inputTextDialog(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable String initText,
		@Nullable String hint,
		@StringRes int idCancel,
		@StringRes int idOk,
		@Nullable Runnable cancel,
		@Nullable CallbackString onText
	) {
		String title = context.getString(idTitle);
		return inputTextDialog(
			context,
			idIcon,
			title,
			initText,
			hint,
			idCancel,
			idOk,
			cancel,
			onText
		);
	}

	@NonNull
	public static AlertDialog inputTextDialog(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@NonNull String title,
		@Nullable String initText,
		@Nullable String hint,
		@StringRes int idCancel,
		@StringRes int idOk,
		@Nullable Runnable cancel,
		@Nullable CallbackString onText
	) {
		MaterialAlertDialogBuilder builder = newBuilder(context);
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		builder.setTitle(title);
		AppCompatEditText view = createEditText(context);
		if (initText != null) {
			view.setText(initText);
		}
		if (hint != null) {
			view.setHint(hint);
			view.setHintTextColor(Color.LTGRAY);
		}
		builder.setView(view);
		if (cancel != null) {
			builder.setNegativeButton(idCancel, (dialog, which) -> {
				cancel.run();
			});
		}
		builder.setPositiveButton(idOk, (dialog, which) -> {
			if (onText != null) {
				onText.callback(getString(view));
			}
		});
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@NonNull
	public static AlertDialog inputMultipleTextDialog(
		@NonNull Context context,
		@DrawableRes int idIcon,
		@NonNull String title,
		@Nullable String initText,
		@Nullable String hint,
		@StringRes int idCancel,
		@StringRes int idOk,
		@Nullable Runnable cancel,
		@Nullable CallbackString onText
	) {
		MaterialAlertDialogBuilder builder = newBuilder(context);
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		builder.setTitle(title);
		AppCompatEditText view = new AppCompatEditText(context);
		if (initText != null) {
			view.setText(initText);
		}
		if (hint != null) {
			view.setHint(hint);
		}
		builder.setView(view);
		if (cancel != null) {
			builder.setNegativeButton(idCancel, (dialog, which) -> {
				cancel.run();
			});
		}
		builder.setPositiveButton(idOk, (dialog, which) -> {
			if (onText != null) {
				onText.callback(getString(view));
			}
		});
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	public static void createTextViewLine(
		@NonNull LinearLayout base,
		@NonNull Context context,
		@StringRes int idLabel,
		@NonNull String text,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(context);
		line.setOrientation(LinearLayout.HORIZONTAL);
		MaterialTextView label = new MaterialTextView(context);
		label.setText(idLabel);
		label.setGravity(Gravity.END);
		MaterialTextView body = new MaterialTextView(context);
		body.setTextColor(Color.BLACK);
		body.setText(text);
		line.addView(label, pLabel);
		line.addView(body, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	}

	@NonNull
	public static AlertDialog showViewDialog(
		@NonNull Context context,
		@StringRes int idTitle,
		@DrawableRes int idIcon,
		@NonNull View view,
		@Nullable Runnable ok,
		@Nullable Runnable cancel,
		@Nullable Runnable dismiss,
		@Nullable Runnable neutral,
		@Nullable String sNeutral
	) {
		return showViewDialog(
			context,
			context.getString(idTitle),
			idIcon,
			view,
			ok,
			"OK",
			cancel,
			dismiss,
			neutral,
			sNeutral
		);
	}

	@NonNull
	public static AlertDialog showViewDialog(
		@NonNull Context context,
		@NonNull String title,
		@DrawableRes int idIcon,
		@NonNull View view,
		@Nullable Runnable ok,
		@Nullable String sOk,
		@Nullable Runnable cancel,
		@Nullable Runnable dismiss,
		@Nullable Runnable neutral,
		@Nullable String sNeutral
	) {
		MaterialAlertDialogBuilder builder = newBuilder(context);
		builder.setTitle(title);
		builder.setView(view);
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		if (dismiss != null) {
			builder.setOnDismissListener(dialog -> {
				dismiss.run();
			});
		}
		if (sOk != null) {
			builder.setPositiveButton(sOk, (dialog, which) -> {
				if (ok != null) {
					ok.run();
				}
			});
		}
		if (cancel != null) {
			builder.setNegativeButton("CANCEL", (dialog, which) -> {
				cancel.run();
			});
		}
		if (sNeutral != null) {
			builder.setNeutralButton(sNeutral, (dialog, which) -> {
				if (neutral != null) {
					neutral.run();
				}
			});
		}
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@NonNull
	public static AlertDialog show(@NonNull AlertDialog.Builder builder) {
		AlertDialog rc = builder.show();
		rc.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		return rc;
	}

	@NonNull
	public static MaterialAlertDialogBuilder newBuilder(@NonNull Context context) {
		return new MaterialAlertDialogBuilder(context);
	}

	public static void showTimePickerDialog(
		@NonNull Context context,
		@NonNull String title,
		int hhmm,
		@NonNull CallbackInteger callback
	) {
		TimePickerDialog d = new TimePickerDialog(context, (v, hour, minute) -> {
			callback.callback(hour * 100 + minute);
		}, hhmm / 100, hhmm % 100, true);
		d.setIcon(R.drawable.timer_48px);
		d.setTitle(title);
		d.show();
		d.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
	}

	public static void showDatePickerDialog(
		@NonNull Context context,
		@NonNull CallbackInteger callback	// date422
	) {
		Calendar cal = Calendar.getInstance();
		DatePickerDialog picker = new DatePickerDialog(context, (view, yy, mm, dd) -> {
			int date422 = yy * 10000 + (mm + 1) * 100 + dd;
			callback.callback(date422);
		}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		picker.show();
		picker.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
	}

	public static void showDatePickerDialog(
		@NonNull Context context,
		int date422,
		@NonNull CallbackInteger callback	// date422
	) {
		if (date422 == 0) {
			showDatePickerDialog(context, callback);
		} else {
			int y4 = date422 / 10000;
			int m2 = (date422 / 100) % 100;
			int d2 = date422 % 100;
			DatePickerDialog picker = new DatePickerDialog(context, (view, yy, mm, dd) -> {
				int rc = yy * 10000 + (mm + 1) * 100 + dd;
				callback.callback(rc);
			}, y4, m2 - 1, d2);
			picker.show();
			picker.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		}
	}

}
