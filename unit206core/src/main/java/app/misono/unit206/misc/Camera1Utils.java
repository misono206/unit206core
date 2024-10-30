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

import android.hardware.Camera;

import androidx.annotation.NonNull;

import app.misono.unit206.debug.Log2;

import java.util.List;

public class Camera1Utils {
	private static final String TAG = "Camera1Utils";

	private Camera1Utils() {
	}

	public static void setDisplayOrientation(
		@NonNull Camera camera,
		@NonNull Camera.CameraInfo info,
		int degreeDevice
	) {
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
			camera.setDisplayOrientation((info.orientation + degreeDevice) % 360);
		} else {
			camera.setDisplayOrientation((info.orientation + degreeDevice + 180) % 360);
		}
	}

	public static int getCameraDegree(boolean inCamera, boolean portrait) {
		int rc;
		if (portrait) {
			if (inCamera) {
				rc = 270;
			} else {
				rc = 90;
			}
		} else {
			rc = 0;
		}
		return rc;
	}

	public static boolean setFocusModeIfSupported(
		@NonNull Camera.Parameters cp,
		@NonNull String modeFocus
	) {
		boolean rc = isSupportedFocusMode(cp, modeFocus);
		if (rc) {
			cp.setFocusMode(modeFocus);
		}
		return rc;
	}

	public static boolean isSupportedFocusMode(@NonNull Camera.Parameters cp, @NonNull String mode) {
		List<String> list = cp.getSupportedFocusModes();
		for (String s : list) {
			if (mode.contentEquals(s)) {
				return true;
			}
		}
		return false;
	}

	public static boolean setZoomIfSupportedNearly(
		@NonNull Camera.Parameters cp,
		int zoom
	) {
		boolean rc = cp.isZoomSupported();
		if (rc) {
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
		return rc;
	}

	public static boolean setPictureSizeIfSupported(
		@NonNull Camera.Parameters cp,
		int wPicture,
		int hPicture
	) {
		boolean rc = isSupportedPictureSize(cp, wPicture, hPicture);
		if (rc) {
			cp.setPictureSize(wPicture, hPicture);
		}
		return rc;
	}

	public static boolean isSupportedPictureSize(
		@NonNull Camera.Parameters cp,
		int wPicture,
		int hPicture
	) {
		List<Camera.Size> list = cp.getSupportedPictureSizes();
		for (Camera.Size s : list) {
			if (s.width == wPicture && s.height == hPicture) {
				return true;
			}
		}
		return false;
	}

	public static boolean setPreviewSizeIfSupported(
		@NonNull Camera.Parameters cp,
		int wPreview,
		int hPreview
	) {
		boolean rc = isSupportedPreviewSize(cp, wPreview, hPreview);
		if (rc) {
			cp.setPreviewSize(wPreview, hPreview);
		}
		return rc;
	}

	public static boolean isSupportedPreviewSize(
		@NonNull Camera.Parameters cp,
		int wPreview,
		int hPreview
	) {
		List<Camera.Size> list = cp.getSupportedPreviewSizes();
		for (Camera.Size s : list) {
			if (s.width == wPreview && s.height == hPreview) {
				return true;
			}
		}
		return false;
	}

	// TODO: ここから下は見直し. from KomaCamera1
	public static void setPreviewFpsRangeBest(@NonNull Camera.Parameters cp) {
		List<int[]> ranges = cp.getSupportedPreviewFpsRange();
		int n = ranges.size();
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < n; i++) {
			int[] range = ranges.get(i);
			if (max <= range[1]) {
				max = range[1];
				if (range[0] < min) {
					min = range[0];
				}
			}
		}
		log("fps:" + min + "," + max);
		cp.setPreviewFpsRange(min, max);
	}

	public static void setPreviewSizeSafe(@NonNull Camera.Parameters cp, int width, int height) {
		List<Camera.Size> sizes = cp.getSupportedPreviewSizes();
		int n = sizes.size();
		int i;
		for (i = 0; i < n; i++) {
			Camera.Size size = sizes.get(i);
			if (size.width == width && size.height == height) break;
		}
		if (i == n) {
			Camera.Size size = sizes.get(0);
			width = size.width;
			height = size.height;
		}
		cp.setPreviewSize(width, height);
	}

	public static void setColorEffectSafe(@NonNull Camera.Parameters cp, String effect) {
		List<String> slist = cp.getSupportedColorEffects();
		int n = slist.size();
		int i;
		for (i = 0; i < n; i++) {
			if (slist.get(i).equals(effect)) break;
		}
		if (i == n) {
			effect = slist.get(0);
		}
		cp.setColorEffect(effect);
	}

	public static void setFocusModeSafe(@NonNull Camera.Parameters cp, String focus) {
		List<String> slist = cp.getSupportedFocusModes();
		int n = slist.size();
		int i;
		for (i = 0; i < n; i++) {
			if (slist.get(i).equals(focus)) break;
		}
		if (i == n) {
			focus = slist.get(0);
		}
		cp.setFocusMode(focus);
	}

	public static void setZoom(@NonNull Camera.Parameters cp, int zoom) {
		if (cp.isZoomSupported()) {
			List<Integer> zlist = cp.getZoomRatios();
			int n = zlist.size();
			int i;
			for (i = 0; i < n; i++) {
				if (zlist.get(i).equals(zoom)) break;
			}
			cp.setZoom(i);
		}
	}

	private static void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
