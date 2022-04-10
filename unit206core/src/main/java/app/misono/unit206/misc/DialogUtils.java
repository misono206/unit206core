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

import android.app.Activity;
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
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import app.misono.unit206.callback.CallbackString;
import app.misono.unit206.task.ObjectReference;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class DialogUtils {

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

	@NonNull
	public static AppCompatEditText createEditText(@NonNull Activity activity) {
		AppCompatEditText text = new AppCompatEditText(activity);
		text.setSingleLine();
		return text;
	}

	@NonNull
	public static AppCompatButton createButton(
		@NonNull Activity activity,
		@StringRes int idLabel,
		@NonNull String text
	) {
		AppCompatButton body = new AppCompatButton(activity);
		body.setText(text);
		return body;
	}

	public static void createViewLine(
		@NonNull LinearLayout base,
		@NonNull Activity activity,
		@StringRes int idLabel,
		@NonNull View view,
		@NonNull LinearLayout.LayoutParams pLabel
	) {
		LinearLayout line = new LinearLayout(activity);
		line.setOrientation(LinearLayout.HORIZONTAL);
		AppCompatTextView label = new AppCompatTextView(activity);
		label.setText(idLabel);
		label.setGravity(Gravity.END);
		line.addView(label, pLabel);
		line.addView(view);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	}

	@NonNull
	public static AppCompatEditText createEditTextLine(
		@NonNull LinearLayout base,
		@NonNull Activity activity,
		@StringRes int idLabel,
		@StringRes int idEditInit,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		AppCompatEditText body = createEditTextLine(base, activity, idLabel, pLabel, pBody);
		body.setText(idEditInit);
		return body;
	}

	@NonNull
	public static AppCompatEditText createEditTextLine(
		@NonNull LinearLayout base,
		@NonNull Activity activity,
		@StringRes int idLabel,
		@NonNull String value,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		AppCompatEditText body = createEditTextLine(base, activity, idLabel, pLabel, pBody);
		body.setText(value);
		return body;
	}

	@NonNull
	public static AppCompatEditText createEditTextLine(
		@NonNull LinearLayout base,
		@NonNull Activity activity,
		@StringRes int idLabel,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		String slabel = activity.getString(idLabel);
		return createEditTextLine(base, activity, slabel, pLabel, pBody);
	}

	@NonNull
	public static AppCompatEditText createEditTextLine(
		@NonNull LinearLayout base,
		@NonNull Activity activity,
		@NonNull String slabel,
		@NonNull String value,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		AppCompatEditText body = createEditTextLine(base, activity, slabel, pLabel, pBody);
		body.setText(value);
		return body;
	}

	@NonNull
	public static AppCompatEditText createEditTextLine(
		@NonNull LinearLayout base,
		@NonNull Activity activity,
		@NonNull CharSequence slabel,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(activity);
		line.setOrientation(LinearLayout.HORIZONTAL);
		AppCompatTextView label = new AppCompatTextView(activity);
		label.setText(slabel);
		label.setGravity(Gravity.END);
		AppCompatEditText body = DialogUtils.createEditText(activity);
		body.setSingleLine();
		line.addView(label, pLabel);
		line.addView(body, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		return body;
	}

	@NonNull
	public static AppCompatEditText createEditTextMultiple(
		@NonNull LinearLayout base,
		@NonNull Activity activity,
		@StringRes int idLabel,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(activity);
		line.setOrientation(LinearLayout.HORIZONTAL);
		AppCompatTextView label = new AppCompatTextView(activity);
		label.setText(idLabel);
		label.setGravity(Gravity.END);
		AppCompatEditText body = new AppCompatEditText(activity);
		line.addView(label, pLabel);
		line.addView(body, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		return body;
	}

	@NonNull
	public static AppCompatEditText createEditTextMultiple(
		@NonNull LinearLayout base,
		@NonNull Activity activity,
		@StringRes int idLabel,
		@NonNull String text,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(activity);
		line.setOrientation(LinearLayout.HORIZONTAL);
		AppCompatTextView label = new AppCompatTextView(activity);
		label.setText(idLabel);
		label.setGravity(Gravity.END);
		AppCompatEditText body = new AppCompatEditText(activity);
		body.setText(text);
		line.addView(label, pLabel);
		line.addView(body, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		return body;
	}

	@NonNull
	public static AppCompatButton createButtonLine(
		@NonNull LinearLayout base,
		@NonNull Activity activity,
		@StringRes int idLabel,
		@StringRes int idText,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(activity);
		line.setOrientation(LinearLayout.HORIZONTAL);
		AppCompatTextView label = new AppCompatTextView(activity);
		label.setText(idLabel);
		label.setGravity(Gravity.END);
		AppCompatButton body = new AppCompatButton(activity);
		body.setText(idText);
		line.addView(label, pLabel);
		line.addView(body, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		return body;
	}

	@NonNull
	public static AppCompatButton createButtonLine(
		@NonNull LinearLayout base,
		@NonNull Activity activity,
		@StringRes int idLabel,
		@NonNull String text,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(activity);
		line.setOrientation(LinearLayout.HORIZONTAL);
		AppCompatTextView label = new AppCompatTextView(activity);
		label.setText(idLabel);
		label.setGravity(Gravity.END);
		AppCompatButton body = new AppCompatButton(activity);
		body.setText(text);
		line.addView(label, pLabel);
		line.addView(body, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		return body;
	}

	@NonNull
	public static AppCompatCheckBox createCheckBoxLine(
		@NonNull LinearLayout base,
		@NonNull Activity activity,
		@StringRes int idLabel,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(activity);
		line.setOrientation(LinearLayout.HORIZONTAL);
		AppCompatCheckBox body = new AppCompatCheckBox(activity);
		LinearLayout linearBody = new LinearLayout(activity);
		linearBody.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
		linearBody.addView(body, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		line.addView(linearBody, pLabel);
		AppCompatTextView label = new AppCompatTextView(activity);
		label.setText(idLabel);
		label.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		line.addView(label, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		return body;
	}

	@NonNull
	public static AppCompatCheckBox createCheckBoxLine(
		@NonNull LinearLayout base,
		@NonNull Activity activity,
		@StringRes int idLabel,
		boolean checked,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		AppCompatCheckBox rc = createCheckBoxLine(base, activity, idLabel, pLabel, pBody);
		rc.setChecked(checked);
		return rc;
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

	public interface Selected {
		void onSelected(int selected);
	}

	@Deprecated
	public static void selectDialog(
		@NonNull Activity activity,
		@NonNull CharSequence[] items,
		@StringRes int idTitle,
		int idxSelect,
		@NonNull Selected listener,
		@Nullable Runnable onCancel
	) {
		selectDialog(activity, items, idTitle, 0, idxSelect, onCancel, listener);
	}

	@Deprecated
	public static void selectDialog(
		@NonNull Activity activity,
		@NonNull CharSequence[] items,
		@StringRes int idTitle,
		int idxSelect,
		@Nullable Runnable onCancel,
		@NonNull Selected listener
	) {
		selectDialog(activity, items, idTitle, 0, idxSelect, onCancel, listener);
	}

	public static void selectDialog(
		@NonNull Activity activity,
		@NonNull CharSequence[] items,
		@StringRes int idTitle,
		@DrawableRes int idIcon,
		int idxSelect,
		@Nullable Runnable onCancel,
		@NonNull Selected listener
	) {
		ObjectReference<Boolean> refOk = new ObjectReference<>(false);
		MaterialAlertDialogBuilder builder = newBuilder(activity);
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
				listener.onSelected(selected);
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
		builder.show();
	}

	@Deprecated
	public static void selectDialog(
		@NonNull Activity activity,
		@NonNull CharSequence[] items,
		@NonNull String title,
		int idxSelect,
		@Nullable Runnable onCancel,
		@NonNull Selected listener
	) {
		selectDialog(activity, items, title, 0, idxSelect, onCancel, listener);
	}

	public static void selectDialog(
		@NonNull Activity activity,
		@NonNull CharSequence[] items,
		@NonNull String title,
		@DrawableRes int idIcon,
		int idxSelect,
		@Nullable Runnable onCancel,
		@NonNull Selected listener
	) {
		ObjectReference<Boolean> refOk = new ObjectReference<>(false);
		MaterialAlertDialogBuilder builder = newBuilder(activity);
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
				listener.onSelected(selected);
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
		builder.show();
	}

	@Deprecated
	public static void selectDialog(
		@NonNull Activity activity,
		@NonNull CharSequence[] items,
		@StringRes int idTitle,
		int idxSelect,
		@NonNull Selected listener
	) {
		selectDialog(activity, items, idTitle, idxSelect, null, listener);
	}

	@Deprecated
	public static void selectDialog(
		@NonNull Activity activity,
		@NonNull CharSequence[] items,
		@StringRes int idTitle,
		@NonNull Selected listener
	) {
		selectDialog(activity, items, idTitle, -1, null, listener);
	}

	public interface Checked {
		void onChecked(boolean[] checked);
	}

	public static void checkDialog(
		@NonNull Activity activity,
		@NonNull CharSequence[] items,
		@StringRes int idTitle,
		@Nullable boolean[] checked,
		@NonNull Checked listener
	) {
		String title = activity.getString(idTitle);
		checkDialog(activity, items, title, checked, listener);
	}

	public static void checkDialog(
		@NonNull Activity activity,
		@NonNull CharSequence[] items,
		@NonNull String title,
		@Nullable boolean[] checked,
		@NonNull Checked listener
	) {
		MaterialAlertDialogBuilder builder = newBuilder(activity);
		builder.setTitle(title);
		final boolean[] cbChecked = checked != null ? checked : new boolean[items.length];
		builder.setMultiChoiceItems(items, checked, (dialog, which, isChecked) -> {
			cbChecked[which] = isChecked;
		});
		builder.setNegativeButton("CANCEL", null);
		builder.setPositiveButton("OK", (dialog, i) -> {
			listener.onChecked(cbChecked);
		});
		builder.show();
	}

	public static void messageDialog(
		@NonNull Activity activity,
		@StringRes int idMessage
	) {
		messageDialog(activity, 0, 0, idMessage, null);
	}

	public static void messageDialog(
		@NonNull Activity activity,
		@NonNull String msg
	) {
		messageDialog(activity, 0, 0, msg, null);
	}

	public static void messageDialog(
		@NonNull Activity activity,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@StringRes int idMessage,
		@Nullable Runnable dismiss
	) {
		MaterialAlertDialogBuilder builder = newBuilder(activity);
		if (idTitle != 0) {
			builder.setTitle(idTitle);
		}
		builder.setMessage(idMessage);
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		builder.setPositiveButton("OK", null);
		if (dismiss != null) {
			builder.setOnDismissListener(dialog -> {
				dismiss.run();
			});
		}
		builder.show();
	}

	public static void messageDialog(
		@NonNull Activity activity,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable CharSequence message,
		@Nullable Runnable dismiss
	) {
		MaterialAlertDialogBuilder builder = newBuilder(activity);
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
		builder.show();
	}

	@Deprecated // use messageDialogOkCancel().
	public static void messageDialogCancelOk(
		@NonNull Activity activity,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable CharSequence message,
		@Nullable Runnable dismiss,
		@NonNull Runnable ok
	) {
		MaterialAlertDialogBuilder builder = newBuilder(activity);
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
		builder.show();
	}

	private static boolean done001;
	public static void messageDialogOkCancel(
		@NonNull Activity activity,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable CharSequence message,
		@Nullable Runnable cancel,
		@NonNull Runnable ok
	) {
		done001 = false;
		MaterialAlertDialogBuilder builder = newBuilder(activity);
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
		builder.show();
	}

	@Deprecated // use messageDialogOkCancel().
	public static void messageDialogCancelOk(
		@NonNull Activity activity,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable CharSequence message,
		@StringRes int idOk,
		@StringRes int idCancel,
		@Nullable Runnable dismiss,
		@NonNull Runnable ok
	) {
		MaterialAlertDialogBuilder builder = newBuilder(activity);
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
		builder.show();
	}

	private static boolean done002;
	public static void messageDialogOkCancel(
		@NonNull Activity activity,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable CharSequence message,
		@StringRes int idOk,
		@StringRes int idCancel,
		@Nullable Runnable cancel,
		@NonNull Runnable ok
	) {
		done002 = false;
		MaterialAlertDialogBuilder builder = newBuilder(activity);
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
		builder.show();
	}

	@Deprecated
	public static void messageDialogCancelOk(
		@NonNull Activity activity,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@StringRes int idMessage,
		@NonNull Runnable ok
	) {
		MaterialAlertDialogBuilder builder = newBuilder(activity);
		if (idTitle != 0) {
			builder.setTitle(idTitle);
		}
		if (idIcon != 0) {
			builder.setIcon(idIcon);
		}
		builder.setMessage(idMessage);
		builder.setNegativeButton("CANCEL", null);
		builder.setPositiveButton("OK", (dialog, i) -> {
			ok.run();
		});
		builder.show();
	}

	public static void messageDialogCancelOkOther(
		@NonNull Activity activity,
		@DrawableRes int idIcon,
		@StringRes int idTitle,
		@Nullable CharSequence message,
		@StringRes int idOk,
		@StringRes int idCancel,
		@StringRes int idOther,
		@Nullable Runnable dismiss,
		@NonNull Runnable other,
		@NonNull Runnable ok
	) {
		MaterialAlertDialogBuilder builder = newBuilder(activity);
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
		builder.setNeutralButton(idOther, (dialog, i) -> {
			other.run();
		});
		if (dismiss != null) {
			builder.setOnDismissListener(dialog -> {
				dismiss.run();
			});
		}
		builder.show();
	}

	public static void inputTextDialog(
		@NonNull Activity activity,
		@StringRes int idTitle,
		@Nullable CallbackString onText
	) {
		inputTextDialog(activity, idTitle, null, null, onText);
	}

	public static void inputTextDialog(
		@NonNull Activity activity,
		@StringRes int idTitle,
		@Nullable Runnable cancel,
		@Nullable CallbackString onText
	) {
		inputTextDialog(activity, idTitle, null, cancel, onText);
	}

	public static void inputTextDialog(
		@NonNull Activity activity,
		@StringRes int idTitle,
		@Nullable String initText,
		@Nullable Runnable cancel,
		@Nullable CallbackString onText
	) {
		MaterialAlertDialogBuilder builder = newBuilder(activity);
		builder.setTitle(idTitle);
		AppCompatEditText view = createEditText(activity);
		if (initText != null) {
			view.setText(initText);
		}
		builder.setView(view);
		if (cancel != null) {
			builder.setNegativeButton("CANCEL", (dialog, which) -> {
				cancel.run();
			});
		}
		builder.setPositiveButton("OK", (dialog, which) -> {
			if (onText != null) {
				onText.callback(getString(view));
			}
		});
		builder.show();
	}

	public static void createTextViewLine(
		@NonNull LinearLayout base,
		@NonNull Activity activity,
		@StringRes int idLabel,
		@NonNull String text,
		@NonNull LinearLayout.LayoutParams pLabel,
		@NonNull LinearLayout.LayoutParams pBody
	) {
		LinearLayout line = new LinearLayout(activity);
		line.setOrientation(LinearLayout.HORIZONTAL);
		AppCompatTextView label = new AppCompatTextView(activity);
		label.setText(idLabel);
		label.setGravity(Gravity.END);
		AppCompatTextView body = new AppCompatTextView(activity);
		body.setTextColor(Color.BLACK);
		body.setText(text);
		line.addView(label, pLabel);
		line.addView(body, pBody);
		base.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	}

	@Deprecated
	@NonNull
	public static MaterialAlertDialogBuilder newBuilder(
		@NonNull Context context,
		@StyleRes int t
	) {
//		return new AlertDialog.Builder(activity, theme);	// goes black...
//		return new AlertDialog.Builder(activity);
		return new MaterialAlertDialogBuilder(context);
	}

	@NonNull
	public static MaterialAlertDialogBuilder newBuilder(@NonNull Context context) {
		return new MaterialAlertDialogBuilder(context);
	}

}
