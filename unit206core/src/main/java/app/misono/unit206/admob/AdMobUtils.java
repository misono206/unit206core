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

package app.misono.unit206.admob;

import android.content.Context;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.misono.unit206.debug.Log2;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.List;

public class AdMobUtils {
	private static final String TAG = "AdMobUtils";

	private AdMobUtils() {
	}

	@MainThread
	public static void initialize(@NonNull Context context, @Nullable List<String> listTestDevice,
			@NonNull Runnable done) {

		MobileAds.initialize(context, initializationStatus -> {
			log("MobileAds:" + initializationStatus);
			if (listTestDevice != null) {
				RequestConfiguration config = new RequestConfiguration.Builder()
						.setTestDeviceIds(listTestDevice)
						.build();
				MobileAds.setRequestConfiguration(config);
			}
			done.run();
		});
	}

	public static void showInterstitial(@NonNull Context context, @NonNull String idInterstitial,
			@Nullable Runnable closed) {

		InterstitialAd ad = new InterstitialAd(context);
		ad.setAdUnitId(idInterstitial);
		ad.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				log("onAdLoaded:");
				ad.show();
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				log("onAdFailedToLoad:" + errorCode);
				if (closed != null) {
					closed.run();
				}
			}

			@Override
			public void onAdClosed() {
				log("onAdClosed:");
				if (closed != null) {
					closed.run();
				}
			}
		});
		ad.loadAd(new AdRequest.Builder().build());
	}

	private static void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
