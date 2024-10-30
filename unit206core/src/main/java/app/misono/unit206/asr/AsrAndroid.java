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

package app.misono.unit206.asr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.ThreadGate;
import app.misono.unit206.task.Taskz;

import java.io.Closeable;
import java.util.List;
import java.util.Locale;

public class AsrAndroid implements Closeable {
	private static final String TAG = "AsrAndroid";

	private final String locale;		// en-US
	private final String namePackage;

	private SpeechRecognizer recognizer;
	private ThreadGate gate;
	private String asrText;
	private int asrError;

	@MainThread
	public AsrAndroid(@NonNull Context context) {
		this(context, Locale.getDefault().toString());
	}

	@MainThread
	public AsrAndroid(@NonNull Context context, @NonNull String locale) {
		this.locale = locale;
		namePackage = context.getPackageName();
		recognizer = SpeechRecognizer.createSpeechRecognizer(context);
		recognizer.setRecognitionListener(new RecognitionListener() {
			@Override
			public void onReadyForSpeech(Bundle params) {
				log("onReadyForSpeech:");
			}

			@Override
			public void onBeginningOfSpeech() {
				log("onBeginningOfSpeech:");
			}

			@Override
			public void onRmsChanged(float rmsdB) {
			}

			@Override
			public void onBufferReceived(byte[] buffer) {
			}

			@Override
			public void onEndOfSpeech() {
				log("onEndOfSpeech:");
			}

			@Override
			public void onError(int error) {
				log("onError:" + error);
				asrError = error;
				gate.open();
			}

			@Override
			public void onResults(Bundle results) {
				log("onResults:");
				List<String> list = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
				if (list != null) {
					for (String s : list) {
						log("result:" + s);
					}
					asrText = list.get(0);
					gate.open();
				}
			}

			@Override
			public void onPartialResults(Bundle partialResults) {
				log("onPartialResults:");
			}

			@Override
			public void onEvent(int eventType, Bundle params) {
				log("onEvent:" + eventType);
			}
		});
	}

	@WorkerThread
	@NonNull
	public String speechRecognition() throws Exception {
		asrText = null;
		asrError = -1;
		gate = new ThreadGate();
		Taskz.call(() -> {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, namePackage);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);
			recognizer.startListening(intent);
			return null;
		});
		gate.block();
		if (asrText == null) {
			throw new RuntimeException("SpeechRecognizer error:" + asrError);
		}
		return asrText;
	}

	@Override
	public void close() {
		if (recognizer != null) {
			recognizer.destroy();
			recognizer = null;
		}
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
