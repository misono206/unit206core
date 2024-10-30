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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.callback.CallbackInteger;
import app.misono.unit206.debug.Log2;

public class LandscapeDeviceAngle {
	private static final String TAG = "LandscapeDeviceAngle";

	private final SensorEventListener listener;
	private final SensorManager sensor;
	private final float[] rR, values;

	private CallbackInteger cbDegree;
	private float[] rGravity, rGeomagnetic;

	public LandscapeDeviceAngle(@NonNull Context context) {
		rR = new float[9];
		values = new float[3];
		sensor = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		listener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				switch (event.sensor.getType()) {
				case Sensor.TYPE_MAGNETIC_FIELD:
					rGeomagnetic = event.values.clone();
					break;
				case Sensor.TYPE_ACCELEROMETER:
					rGravity = event.values.clone();
					break;
				}
				if (rGeomagnetic != null && rGravity != null) {
					SensorManager.getRotationMatrix(
						rR,
						null,
						rGravity,
						rGeomagnetic
					);
					SensorManager.getOrientation(rR, values);
					int roll = (int)(values[2] * 180 / Math.PI);
					int degree = Math.abs(roll) - 90;
					if (cbDegree != null) {
						cbDegree.callback(degree);
					}
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
	}

	@Nullable
	public float[] getAccelometer() {
		float[] rc = rGravity;
		if (rc != null) {
			return rc.clone();
		} else {
			return null;
		}
	}

	@Nullable
	public float[] getGeomagnetic() {
		float[] rc = rGeomagnetic;
		if (rc != null) {
			return rc.clone();
		} else {
			return null;
		}
	}

	public void setEventCallback(@Nullable CallbackInteger callback) {
		rGravity = null;
		rGeomagnetic = null;
		cbDegree = callback;
		if (callback != null) {
			sensor.registerListener(
				listener,
				sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME
			);
			sensor.registerListener(
				listener,
				sensor.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_GAME
			);
		} else {
			sensor.unregisterListener(listener);
		}
	}

	private static void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
