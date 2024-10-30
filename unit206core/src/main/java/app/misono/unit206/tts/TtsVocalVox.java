/*
 * Copyright 2023 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.tts;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import app.misono.unit206.debug.Log2;

import java.io.File;
import java.util.Locale;
import java.util.Set;

public class TtsVocalVox implements TtsEngine {
	private static final String TAG = "TtsVocalVox";

	public TtsVocalVox(@NonNull Context context) {
	}

	@Override
	@NonNull
	public Set<Locale> getAvailableLanguages() {
		throw new RuntimeException("not implement yet...");
	}

	@Override
	public void setLanguage(@NonNull Locale locale) {
		throw new RuntimeException("not implement yet...");
	}

	@Override
	@NonNull
	public Set<TtsVoice> getVoices() {
		throw new RuntimeException("not implement yet...");
	}

	@Override
	public void setVoice(@NonNull TtsVoice voice) {
		throw new RuntimeException("not implement yet...");
	}

	@Override
	public boolean setVoiceByName(@NonNull String name) {
		throw new RuntimeException("not implement yet...");
	}

	@Override
	@WorkerThread
	public void createWavSync(@NonNull String msg, @NonNull File file) throws Exception {
		throw new RuntimeException("not implement yet...");
	}

	@Override
	public void close() {
		throw new RuntimeException("not implement yet...");
	}

	@Override
	public void speak(@NonNull String msg, float rate) {
		throw new RuntimeException("not implement yet...");
	}

	@Override
	public void speak(@NonNull String msg) {
		throw new RuntimeException("not implement yet...");
	}

	@Override
	public void speakSync(@NonNull String msg, float rate) throws Exception {
		throw new RuntimeException("not implement yet...");
	}

	@Override
	public void speakSync(@NonNull String msg) throws Exception {
		throw new RuntimeException("not implement yet...");
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
