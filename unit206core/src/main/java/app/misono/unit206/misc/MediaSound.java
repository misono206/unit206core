/*
 * Copyright 2017 Atelier Misono, Inc. @ https://misono.app/
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

import android.media.MediaActionSound;

import java.io.Closeable;

/**
 *	Provides MediaActionSound function.
 */
public final class MediaSound implements Closeable {
	private MediaActionSound actSound;

	public MediaSound() {
		actSound = new MediaActionSound();
	}

	public void playStartSound() {
		actSound.play(MediaActionSound.START_VIDEO_RECORDING);
	}

	public void playStopSound() {
		actSound.play(MediaActionSound.STOP_VIDEO_RECORDING);
	}

	@Override
	public void close() {
		if (actSound != null) {
			actSound.release();
			actSound = null;
		}
	}

}
