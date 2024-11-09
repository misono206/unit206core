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
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.ThreadGate;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class TtsAndroid implements TtsEngine {
	private static final String TAG = "TtsAndroid";

	private TextToSpeech tts;
	private ThreadGate gateUtterance;

	private static class Voice1 implements TtsVoice {
		private final Voice voice;

		private Voice1(@NonNull Voice voice) {
			this.voice = voice;
		}

		@Override
		@NonNull
		public Object getVoiceInstance() {
			return voice;
		}
	}

	@WorkerThread
	public TtsAndroid(@NonNull Context context) throws InterruptedException {
		ThreadGate gate = new ThreadGate();
		tts = new TextToSpeech(context, status -> {
			log("status:" + status);
			gate.open();
		});
		gate.block();
		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
			@Override
			public void onStart(String idUtterance) {
				log("onStart:" + idUtterance);
			}

			@Override
			public void onDone(String idUtterance) {
				log("onDone:" + idUtterance);
				if (gateUtterance != null) {
					gateUtterance.open();
				}
			}

			@Override
			public void onError(String idUtterance) {
				log("onError:" + idUtterance);
			}
		});
	}

	@Override
	@NonNull
	public Set<Locale> getAvailableLanguages() {
		return tts.getAvailableLanguages();
	}

	@Override
	public void setLanguage(@NonNull Locale locale) {
		tts.setLanguage(locale);
	}

	@Override
	@NonNull
	public Set<TtsVoice> getVoices() {
		Set<TtsVoice> rc = new HashSet<>();
		Set<Voice> set = tts.getVoices();
		for (Voice voice : set) {
			rc.add(new Voice1(voice));
		}
		return rc;
	}

	@Override
	public void setVoice(@NonNull TtsVoice voice) {
		Voice1 v1 = (Voice1)voice;
		tts.setVoice(v1.voice);
	}

	@Override
	public boolean setVoiceByName(@NonNull String name) {
		boolean rc = false;
		Set<Voice> voices = tts.getVoices();
		for (Voice voice : voices) {
			if (TextUtils.equals(name, voice.getName())) {
				tts.setVoice(voice);
				rc = true;
				break;
			}
		}
		return rc;
	}

	@Override
	@WorkerThread
	public void createWavSync(@NonNull String msg, @NonNull File file) throws Exception {
		gateUtterance = new ThreadGate();
		tts.synthesizeToFile(msg, null, file, "1111");
		gateUtterance.block();
		gateUtterance = null;
	}

	@Override
	public void close() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
			tts = null;
		}
	}

	@Override
	public void speak(@NonNull String msg, float rate) {
		tts.setSpeechRate(rate);
		tts.speak(msg, TextToSpeech.QUEUE_ADD, null, "1234");
	}

	@Override
	public void speak(@NonNull String msg) {
		speak(msg, 1f);
	}

	@Override
	@WorkerThread
	public void speakSync(@NonNull String msg, float rate) throws Exception {
		tts.setSpeechRate(rate);
		gateUtterance = new ThreadGate();
		tts.speak(msg, TextToSpeech.QUEUE_ADD, null, "1234");
		gateUtterance.block();
		gateUtterance = null;
	}

	@Override
	@WorkerThread
	public void speakSync(@NonNull String msg) throws Exception {
		speakSync(msg, 1f);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
