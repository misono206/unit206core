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

package app.misono.unit206.page.imagecrop;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.callback.CallbackLayout;
import app.misono.unit206.debug.Log2;
import app.misono.unit206.page.AbstractPage;
import app.misono.unit206.page.PageActivity;
import app.misono.unit206.page.PageManager;

public class ImageCropPage extends AbstractPage {
	private static final String TAG = "ImageCropPage";

	private final ImageCropLayout layout;
	private final ImageCropView view;

	private CallbackLayout cbLayout;

	public ImageCropPage(
		@NonNull PageManager manager,
		@NonNull PageActivity activity,
		@NonNull FrameLayout parent,
		@Nullable Runnable clickBack,
		@NonNull ImageCropCallback cbCropped
	) {
		super(manager, activity, parent, clickBack);

		view = new ImageCropView(activity);
		view.setDoneCallback(() -> {
			cbCropped.callback(view.getCroppedBitmap());
		});
		mBase.addView(
			view,
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.MATCH_PARENT
		);
		layout = new ImageCropLayout();
		layout.layout(view);
	}

	@MainThread
	public void setCropArea(@NonNull RectF rect) {
		view.setCropArea(rect);
	}

	public void setGuideImage(@DrawableRes int idGuide) {
		view.setGuideImage(idGuide);
	}

	public void setTitle(@NonNull String title) {
		view.setTitle(title);
	}

	@MainThread
	public void setDoneIcon(@DrawableRes int idIcon) {
		view.setDoneIcon(idIcon);
	}

	@MainThread
	public void setDoneView(@Nullable View vDone) {
		view.setDoneView(vDone);
	}

	@MainThread
	public void setImageBitmap(@NonNull Bitmap bitmap) {
		view.setImageBitmap(bitmap);
	}

	@MainThread
	public void setLayoutCallback(@Nullable CallbackLayout layout) {
		cbLayout = layout;
	}

	@Nullable
	@Override
	public String refreshPage() {
		return "";
	}

	@NonNull
	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public void changeLayout(int wAdBase, int hAdBase) {
		if (cbLayout != null) {
			cbLayout.callback(wAdBase, hAdBase);
		}
		super.changeLayout(wAdBase, hAdBase);
		log("changeLayout:" + wAdBase + " " + hAdBase + " " + mPortrait);
		layout.setPixelSize(wAdBase, hAdBase);
		layout.layout(view);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
