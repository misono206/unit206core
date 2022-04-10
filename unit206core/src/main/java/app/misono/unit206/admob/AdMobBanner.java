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

import android.widget.FrameLayout;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.page.FrameAnimator;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AdMobBanner {
	private static final String TAG = "AdMobBanner";
	private static final int MSEC_AD_ANIME = 500;

	private static AdMobBanner me;

	private FrameAnimator mAnime;
	private boolean mLoaded;

	private AdMobBanner() {
		mAnime = new FrameAnimator(MSEC_AD_ANIME);
	}

	@MainThread
	public void load(@NonNull FrameLayout adbase, @NonNull FrameLayout parent, @NonNull AdView adView) {
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				log("onAdLoaded:");
				if (!mLoaded) {
					mLoaded = true;
					requestLayout(adbase, parent, adView);
				}
			}
		});
		adView.loadAd(adRequest);
	}

	@MainThread
	public void requestLayout(@NonNull FrameLayout adbase, @NonNull FrameLayout parent, @NonNull AdView adView) {
		mAnime.stop();
		mAnime.clear();
		int hAd = adView.getHeight();
		int wParent = adbase.getWidth();
		int hParent = adbase.getHeight();
		if (mLoaded) {
			hParent -= hAd;
			FrameLayout.LayoutParams p = (FrameLayout.LayoutParams)adView.getLayoutParams();
			if (p.width <= 0 || p.topMargin == 0) {
				if (p.width <= 0) {
					p.width = wParent;
				}
				if (p.topMargin == 0) {
					p.topMargin = adbase.getHeight();
				}
				adView.requestLayout();
			}
			FrameLayout.LayoutParams toAd = new FrameLayout.LayoutParams(wParent, hAd);
			toAd.topMargin = hParent;
			mAnime.addItem(adView, toAd);
		}
		FrameLayout.LayoutParams p2 = (FrameLayout.LayoutParams)parent.getLayoutParams();
		if (p2.width <= 0 || p2.height <= 0) {
			if (p2.width <= 0) {
				p2.width = wParent;
			}
			if (p2.height <= 0) {
				p2.height = adbase.getHeight();
			}
			parent.requestLayout();
		}
		FrameLayout.LayoutParams toParent = new FrameLayout.LayoutParams(wParent, hParent);
		mAnime.addItem(parent, toParent);
		mAnime.start();
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

	public static synchronized AdMobBanner getInstance() {
		if (me == null) {
			me = new AdMobBanner();
		}
		return me;
	}

}
