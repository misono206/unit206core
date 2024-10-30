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

package app.misono.unit206.page.stillcamera;

import android.Manifest;
import android.media.MediaActionSound;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import app.misono.unit206.callback.CallbackBitmap;
import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.Utils;
import app.misono.unit206.page.AbstractPage;
import app.misono.unit206.page.PageActivity;
import app.misono.unit206.page.PageManager;
import app.misono.unit206.task.Taskz;

import java.util.concurrent.Executor;

public abstract class AbstractStillcameraPage extends AbstractPage {
	private static final String TAG = "AbstractStillcameraPage";

	private final int codePerm;

	private CallbackCameraParameters callbackParams;
	private StillcameraControl camera;
	private MediaActionSound sound;
	private CallbackBitmap taken;
	private SurfaceView surface;
	private Runnable cameraStarted;
	private Runnable cameraStopped;
	private Runnable surfaceCreated;
	private Runnable surfaceDestroyed;
	private Runnable surfaceChanged;
	private Runnable denied, neverAsked;
	private Runnable beforeRequestPermission;
	private State state;
	private int wPicture, hPicture;
	private boolean readySurface;
	private int idCamera;
	private int zoom100, qualityJpeg, aspect100, degreeDevice;

	private enum State {
		IDLE,
		STARTED,
		PREVIEWING;
	}

	public AbstractStillcameraPage(
		@NonNull PageManager manager,
		@NonNull PageActivity activity,
		@NonNull FrameLayout parent,
		int codePerm,
		@Nullable Runnable clickBack
	) {
		super(manager, activity, parent, clickBack);
		this.codePerm = codePerm;
		zoom100 = 100;
		qualityJpeg = 95;
		aspect100 = 1280 * 100 / 720;
		state = State.IDLE;
	}

	private final SurfaceHolder.Callback cbSurface = new SurfaceHolder.Callback() {
		@Override
		public void surfaceCreated(SurfaceHolder surface) {
			log("surfaceCreated:called:");
			Taskz.call(() -> {	// for Android-11
				log("surfaceCreated:exec:");
				readySurface = false;
				Runnable cb = surfaceCreated;
				if (cb != null) {
					cb.run();
				}
				return null;
			});
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder surface) {
			log("surfaceDestroyed:called:" + state);
			Taskz.call(() -> {	// for Android-11
				log("surfaceDestroyed:exec:" + state);
				readySurface = false;
				switch (state) {
				case PREVIEWING:
					notReady();
					break;
				}
				Runnable cb = surfaceDestroyed;
				if (cb != null) {
					cb.run();
				}
				return null;
			});
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			log("surfaceChanged:called:" + width + "x" + height + ":" + state);
			Taskz.call(() -> {	// for Android-11
				log("surfaceChanged:exec:" + width + "x" + height + ":" + state);
				readySurface = true;
				switch (state) {
				case STARTED:
					checkReady();
					break;
				}
				Runnable cb = surfaceChanged;
				if (cb != null) {
					cb.run();
				}
				return null;
			});
		}
	};

	protected void setDeniedCallback(@Nullable Runnable denied) {
		this.denied = denied;
	}

	protected void setNeverAskedCallback(@Nullable Runnable neverAsked) {
		this.neverAsked = neverAsked;
	}

	protected void setTakenCallback(@NonNull CallbackBitmap taken) {
		this.taken = taken;
	}

	protected void setBeforeRequestPermissonCallback(@NonNull Runnable callabck) {
		beforeRequestPermission = callabck;
	}

	@MainThread
	protected void startPreview() {
		log("startPreview:" + state);
		switch (state) {
		case IDLE:
			checkReady();
			break;
		}
	}

	@MainThread
	protected void stopPreview() {
		log("stopPreview:" + state);
		switch (state) {
		case STARTED:
			state = State.IDLE;
			break;
		case PREVIEWING:
			stopCameraInternal();
			state = State.IDLE;
			break;
		}
	}

	protected void setDeviceDegree(int degree) {
		degreeDevice = degree;
	}

	protected void setSurfaceView(@NonNull SurfaceView surface) {
		if (this.surface != null) {
			this.surface.getHolder().removeCallback(cbSurface);
		}
		this.surface = surface;
		surface.getHolder().addCallback(cbSurface);
	}

	protected void setZoomRatio(int zoom100) {
		this.zoom100 = zoom100;
	}

	protected void setJpegQuality(int quality) {
		qualityJpeg = quality;
	}

	protected void setAspectRatio(int aspect100) {
		this.aspect100 = aspect100;
	}

	protected void setCameraId(int idCamera) {
		this.idCamera = idCamera;
	}

	protected void setSurfaceCreatedCallback(@Nullable Runnable callback) {
		surfaceCreated = callback;
	}

	protected void setSurfaceChangedCallback(@Nullable Runnable callback) {
		surfaceChanged = callback;
	}

	protected void setSurfaceDestroyedCallback(@Nullable Runnable callback) {
		surfaceDestroyed = callback;
	}

	protected void setSpecificPictureSize(int wPicture, int hPicture) {
		this.wPicture = wPicture;
		this.hPicture = hPicture;
	}

	protected void setCameraStarted(@Nullable Runnable callback) {
		cameraStarted = callback;
	}

	protected void setCameraStopped(@Nullable Runnable callback) {
		cameraStopped = callback;
	}

	protected void setCameraParametersCallback(@Nullable CallbackCameraParameters callbackParams) {
		this.callbackParams = callbackParams;
	}

	@Deprecated	// use Executor version
	protected void takePicture() {
		log("takePicture:" + state);
		switch (state) {
		case PREVIEWING:
			camera.takePicture(bitmap -> {
				// @MainThread now...
				stopCameraInternal();
				state = State.IDLE;
				if (taken != null) {
					taken.callback(bitmap);
				}
			});
			break;
		}
	}

	protected void takePicture(@NonNull Executor executor) {
		log("takePicture:" + state);
		switch (state) {
		case PREVIEWING:
			camera.takePictureTask(executor).addOnCompleteListener(task -> {
				stopCameraInternal();
				state = State.IDLE;
				if (task.isSuccessful()) {
					if (taken != null) {
						taken.callback(task.getResult());
					}
				} else{
					Taskz.printStackTrace2(task.getException());
				}
			});
			break;
		}
	}

	@Override
	public void onResume() {
		log("onResume:" + state);
		super.onResume();
		switch (state) {
		case STARTED:
			checkReady();
			break;
		}
	}

	@Override
	public void onPause() {
		log("onPause:" + state);
		super.onPause();
		switch (state) {
		case PREVIEWING:
			notReady();
			break;
		}
	}

	protected void beforeRequestPermissionOk() {
		requestPerm();
	}

	protected void beforeRequestPermissionCancel() {
		state = State.IDLE;
		onBackPressed();
	}

	private void checkRequestPerm() {
		if (beforeRequestPermission != null) {
			beforeRequestPermission.run();
		} else {
			requestPerm();
		}
	}

	private void requestPerm() {
		Utils.requestPermission(activity, codePerm, Manifest.permission.CAMERA, () -> {
			// granted
			log("requestPermission:granted:" + state);
			switch (state) {
			case STARTED:
				startCameraInternal();
				state = State.PREVIEWING;
				break;
			}
		}, () -> {
			//	denied
			switch (state) {
			case STARTED:
				if (denied != null) {
					denied.run();
				}
				break;
			}
		}, () -> {
			//	never asked
			switch (state) {
			case STARTED:
				if (neverAsked != null) {
					neverAsked.run();
				}
				break;
			}
		});
	}

	private boolean isGranted() {
		return Utils.isGrantedPermission(activity, Manifest.permission.CAMERA);
	}

	private boolean isReady() {
		return isActiveTopPage() && readySurface;
	}

	private void checkReady() {
		if (isReady()) {
			if (isGranted()) {
				startCameraInternal();
				state = State.PREVIEWING;
			} else {
				checkRequestPerm();
				state = State.STARTED;
			}
		} else {
			state = State.STARTED;
		}
	}

	private void notReady() {
		stopCameraInternal();
		state = State.STARTED;
	}

	@MainThread
	private void startCameraInternal() {
		log("startCameraInternal:");
		sound = new MediaActionSound();
		sound.load(MediaActionSound.SHUTTER_CLICK);
		camera = new StillcameraControl(aspect100);
		camera.setDeviceDegree(degreeDevice);
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		log("startCameraInternal:camera.start:");
		try {
			camera.start(idCamera, surface, zoom100, wPicture, hPicture, qualityJpeg, callbackParams);
			log("startCameraInternal:started:" + cameraStarted);
			if (cameraStarted != null) {
				cameraStarted.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequiresApi(21)
	@NonNull
	protected Size getPreviewSize() {
		return camera.getPreviewSize();
	}

	private void stopCameraInternal() {
		log("stopCameraInternal:");
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		camera.stop();
		camera = null;
		sound.release();
		sound = null;
		log("stopCameraInternal:" + cameraStopped);
		if (cameraStopped != null) {
			cameraStopped.run();
		}
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
