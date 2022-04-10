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

package app.misono.unit206.page.steelcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.Size;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import app.misono.unit206.callback.CallbackBitmap;
import app.misono.unit206.debug.Log2;

import java.io.IOException;
import java.util.List;

class SteelcameraControl {
	private static final String TAG = "SteelcameraControl";

	private final int aspect100;

	private Camera.CameraInfo info;
	private Camera camera;

	SteelcameraControl(int aspect100) {
		this.aspect100 = aspect100;
	}

	void start(
		int idCamera,
		@NonNull SurfaceView surfaceView,
		int zoom,
		int wPicture,
		int hPicture,
		int qualityJpeg,
		@Nullable CallbackCameraParameters callback
	) {
		log("start:");
		camera = Camera.open(idCamera);
		info = new Camera.CameraInfo();
		Camera.getCameraInfo(idCamera, info);
		try {
			if (idCamera == 0) {
				camera.setDisplayOrientation(info.orientation);
			} else {
				camera.setDisplayOrientation((info.orientation + 180) % 360);	// TODO:
			}
			Camera.Parameters cp = camera.getParameters();
			if (idCamera == 0) { // 設定するとインカメ(id=1)で動作しない
				cp.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			}
			cp.setJpegQuality(qualityJpeg);
			setZoom(cp, zoom);
			setPictureSize(cp, wPicture, hPicture);
			if (callback != null) {
				callback.callback(cp);
			}
			camera.setParameters(cp);
			camera.setPreviewDisplay(surfaceView.getHolder());
			camera.startPreview();
			camera.setErrorCallback((error, camera) -> {
				log("onError:" + error);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setPictureSize(@NonNull Camera.Parameters cp, int width, int height) {
		List<Camera.Size> list;
		Camera.Size size;

		if (width == 0 || height == 0) {
			// set picture size
			list = cp.getSupportedPictureSizes();
			for (Camera.Size s : list) {
				int aspect = s.width * 100 / s.height;
				if (aspect == aspect100 || aspect100 == 0) {
					if (width == 0 || width < s.width) {
						width = s.width;
						height = s.height;
					}
				}
			}
		}
		if (width != 0) {
			log("picture:" + width + " " + height);
			cp.setPictureSize(width, height);
		}

		// set preview size
		list = cp.getSupportedPreviewSizes();
		size = null;
		for (Camera.Size s : list) {
			int aspect = s.width * 100 / s.height;
			if (aspect == aspect100 || aspect100 == 0) {
				if (size == null || size.width < s.width) {
					size = s;
				}
			}
		}
		if (size != null) {
			log("preview:" + size.width + " " + size.height);
			cp.setPreviewSize(size.width, size.height);
		}
	}

	private void setZoom(@NonNull Camera.Parameters cp, int zoom) {
		if (cp.isZoomSupported()) {
			List<Integer> zlist = cp.getZoomRatios();
			int n = zlist.size();
			int i;
			int index = 0;
			int vMatch = 0;
			for (i = 0; i < n; i++) {
				int v = zlist.get(i);
				if (v <= zoom && vMatch < v) {
					index = i;
					vMatch = v;
				}
			}
			if (vMatch != 0) {
				cp.setZoom(index);
				log("setZoom:" + zlist.get(index));
			}
		}
	}

	void stop() {
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
		}
		camera = null;
	}

	@RequiresApi(21)
	@NonNull
	Size getPreviewSize() {
		Camera.Parameters cp = camera.getParameters();
		Camera.Size size = cp.getPreviewSize();
		return new Size(size.width, size.height);
	}

	void takePicture(@NonNull CallbackBitmap taken) {
		camera.takePicture(
			null,
			null,
			null,
			(data, c) -> {
				// jpeg
				log("take:" + data.length);
				Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length, null);
				Matrix matrix = new Matrix();
				matrix.postRotate(info.orientation);
				b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
				log("take:" + b.getWidth() + " " + b.getHeight());
				taken.callback(b);
			}
		);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
