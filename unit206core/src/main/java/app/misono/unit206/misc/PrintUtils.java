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

import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.webkit.WebView;

import androidx.annotation.NonNull;

public class PrintUtils {
	@Deprecated		// use createAttrA4Color350()
	@NonNull
	public static PrintAttributes createAttrA4Color300(boolean landscape) {
		if (landscape) {
			return createAttrA4Color300Landscape();
		} else {
			return createAttrA4Color300Portrait();
		}
	}

	@NonNull
	public static PrintAttributes createAttrA4Color350(boolean landscape) {
		if (landscape) {
			return createAttrA4Color350Landscape();
		} else {
			return createAttrA4Color350Portrait();
		}
	}

	@Deprecated		// use createAttrA4Color350Portrait()
	@NonNull
	public static PrintAttributes createAttrA4Color300Portrait() {
		return createAttrA4Color300(PrintAttributes.MediaSize.ISO_A4.asPortrait());
	}

	@NonNull
	public static PrintAttributes createAttrA4Color350Portrait() {
		return createAttrA4Color350(PrintAttributes.MediaSize.ISO_A4.asPortrait());
	}

	@Deprecated		// use createAttrA4Color350Landscape()
	@NonNull
	public static PrintAttributes createAttrA4Color300Landscape() {
		return createAttrA4Color300(PrintAttributes.MediaSize.ISO_A4.asLandscape());
	}

	@NonNull
	public static PrintAttributes createAttrA4Color350Landscape() {
		return createAttrA4Color350(PrintAttributes.MediaSize.ISO_A4.asLandscape());
	}

	@NonNull
	private static PrintAttributes createAttrA4Color300(@NonNull PrintAttributes.MediaSize size) {
		return new PrintAttributes.Builder()
			.setMediaSize(size)
			.setResolution(new PrintAttributes.Resolution("300dpi", "300dpi", 300, 300))
			.setColorMode(PrintAttributes.COLOR_MODE_COLOR)
			.setMinMargins(PrintAttributes.Margins.NO_MARGINS)
			.build();
	}

	@NonNull
	private static PrintAttributes createAttrA4Color350(@NonNull PrintAttributes.MediaSize size) {
		return new PrintAttributes.Builder()
			.setMediaSize(size)
			.setResolution(new PrintAttributes.Resolution("350dpi", "350dpi", 350, 350))
			.setColorMode(PrintAttributes.COLOR_MODE_COLOR)
			.setMinMargins(PrintAttributes.Margins.NO_MARGINS)
			.build();
	}

	@NonNull
	public static PrintDocumentAdapter createAdapter(@NonNull WebView webView, @NonNull String fname) {
		return webView.createPrintDocumentAdapter(fname);
	}

}
