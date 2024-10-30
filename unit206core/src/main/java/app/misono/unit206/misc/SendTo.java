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

package app.misono.unit206.misc;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.DrawableRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;

import app.misono.unit206.callback.CallbackException;
import app.misono.unit206.callback.CallbackString;
import app.misono.unit206.debug.Log2;
import app.misono.unit206.page.PageActivity;
import app.misono.unit206.task.ObjectReference;
import app.misono.unit206.task.Taskz;
import app.misono.unit206.theme.AppStyle;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class SendTo {
	private static final String TAG = "SendTo";

	public enum Status {
		NO_MAILER,
		MAILER_ONLY_1,
		SHOW_CHOOSER,
	}

	/**
	 * Choose a mailer and send E-mail.
	 *
	 * @return null is no mailer exists.
	 */
	@MainThread
	@NonNull
	public static Status sendEmailChooserDialog(
		@NonNull PageActivity activity,
		@NonNull CharSequence title,
		@DrawableRes int iconTitle,
		@Nullable String[] to,
		@Nullable String[] cc,
		@Nullable String subject,
		@Nullable String message,
		@NonNull AppStyle style,
		int wDisp,
		@Nullable Uri uriAttachment,
		@Nullable CallbackString cbSuccess,
		@Nullable Runnable cbCancel,
		@Nullable CallbackException cbError
	) {
		ObjectReference<AlertDialog> refDialog = new ObjectReference<>();
		ObjectReference<Boolean> refDone = new ObjectReference<>(false);

		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:"));
		PackageManager manager = activity.getPackageManager();
		List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
		switch (infos.size()) {
		case 0:
			return Status.NO_MAILER;
		case 1:
			String name = infos.get(0).activityInfo.packageName;
			sendEmail(
				activity,
				name,
				to,
				cc,
				subject,
				message,
				uriAttachment,
				() -> {
					if (!refDone.get()) {
						refDone.set(true);
						if (cbSuccess != null) {
							cbSuccess.callback(name);
						}
					}
				},
				e -> {
					if (!refDone.get()) {
						refDone.set(true);
						if (cbError != null) {
							cbError.callback(e);
						}
					}
				}
			);
			return Status.MAILER_ONLY_1;
		default:
			int size = wDisp / 8;
			int m = (int)(size * 0.25f);
			int m2 = m / 2;
			float sizeText = size * 0.4f;
			ScrollView scroll = new ScrollView(activity);
			LinearLayout linear = new LinearLayout(activity);
			linear.setOrientation(LinearLayout.VERTICAL);
			FrameLayout.LayoutParams ps = new FrameLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
			);
			ps.topMargin = m * 2;
			ps.bottomMargin = m * 2;
			scroll.addView(linear, ps);
			List<MaterialCardView> cards = new ArrayList<>();
			for (ResolveInfo info : infos) {
				String namePackage = info.activityInfo.packageName;
				try {
					PackageInfo pInfo = manager.getPackageInfo(namePackage, 0);
					Drawable icon = pInfo.applicationInfo.loadIcon(manager);
					CharSequence nameMailer = pInfo.applicationInfo.loadLabel(manager);
					LinearLayout line = new LinearLayout(activity);
					line.setOrientation(LinearLayout.HORIZONTAL);
					AppCompatImageView image = new AppCompatImageView(activity);
					image.setAdjustViewBounds(true);
					image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					image.setImageDrawable(icon);
					line.addView(image, size, size);
					MaterialTextView text = new MaterialTextView(activity);
					text.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
					text.setTextColor(style.getOsBlackColor());
					text.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeText);
					text.setText(nameMailer);
					LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						size
					);
					p2.leftMargin = m;
					line.addView(text, p2);
					FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						size
					);
					p.topMargin = m2;
					p.leftMargin = m * 2;
					p.bottomMargin = m2;
					MaterialCardView card = new MaterialCardView(activity);
					cards.add(card);
					card.addView(line, p);
					card.setStrokeColor(Color.GRAY);
					card.setStrokeWidth(1);
					card.setRadius(size * 0.1f);
					card.setOnClickListener(v -> {
						log("clicked...");
						if (!refDone.get()) {
							refDone.set(true);
							for (MaterialCardView c : cards) {
								if (c != v) {
									c.setClickable(false);
								}
							}
							Utils.sleep(300, () -> {		// for animation
								sendEmail(
									activity,
									namePackage,
									to,
									cc,
									subject,
									message,
									uriAttachment,
									() -> {
										// success
										refDialog.get().dismiss();
										if (cbSuccess != null) {
											cbSuccess.callback(namePackage);
										}
									},
									e -> {
										refDialog.get().dismiss();
										if (cbError != null) {
											cbError.callback(e);
										}
									}
								);
							});
						}
					});
					LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						size + m
					);
					p3.topMargin = m2;
					p3.leftMargin = m;
					p3.rightMargin = m;
					p3.bottomMargin = m2;
					linear.addView(card, p3);
				} catch (PackageManager.NameNotFoundException e) {
					e.printStackTrace();
				}
			}
			MaterialAlertDialogBuilder builder = DialogUtils.newBuilder(activity);
			builder.setTitle(title);
			builder.setIcon(iconTitle);
			builder.setView(scroll);
			builder.setOnDismissListener(dialog -> {
				if (!refDone.get()) {
					refDone.set(true);
					if (cbCancel != null) {
						cbCancel.run();
					}
				}
			});
			refDialog.set(DialogUtils.show(builder));
			return Status.SHOW_CHOOSER;
		}
	}

	public static void sendGmail(
		@NonNull Activity activity,
		@Nullable String[] to,
		@Nullable String[] cc,
		@Nullable String subject,
		@Nullable String message,
		@Nullable Uri uriAttachment,
		@Nullable Runnable successSync,
		@Nullable CallbackException errorSync
	) {
		sendEmail(
			activity,
			"com.google.android.gm",
			to,
			cc,
			subject,
			message,
			uriAttachment,
			successSync,
			errorSync
		);
	}

	public static void sendEmail(
		@NonNull Activity activity,
		@NonNull String namePackageMailer,
		@Nullable String[] to,
		@Nullable String[] cc,
		@Nullable String subject,
		@Nullable String message,
		@Nullable Uri uriAttachment,
		@Nullable Runnable successSync,
		@Nullable CallbackException errorSync
	) {
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:"));
		intent.setPackage(namePackageMailer);
		if (to != null) {
			intent.putExtra(Intent.EXTRA_EMAIL, to);
		}
		if (cc != null) {
			intent.putExtra(Intent.EXTRA_CC, cc);
		}
		if (subject != null) {
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		}
		if (message != null) {
			intent.putExtra(Intent.EXTRA_TEXT, message);
		}
		if (uriAttachment != null) {
			activity.grantUriPermission(
				namePackageMailer,
				uriAttachment,
				Intent.FLAG_GRANT_READ_URI_PERMISSION
			);
			intent.putExtra(Intent.EXTRA_STREAM, uriAttachment);
		}
		try {
			activity.startActivity(intent);
			if (successSync != null) {
				successSync.run();
			}
		} catch (Exception e) {
			if (errorSync != null) {
				errorSync.callback(e);
			} else {
				Taskz.printStackTrace2(e);
			}
		}
	}

	private static void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}

