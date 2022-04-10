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

package app.misono.unit206.misc;

import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(19)
public class PrintUtils {
	@NonNull
	public static PrintAttributes createAttrA4Color300() {
		return new PrintAttributes.Builder()
			.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
			.setResolution(new PrintAttributes.Resolution("300dpi", "300dpi", 300, 300))
			.setColorMode(PrintAttributes.COLOR_MODE_COLOR)
			.setMinMargins(PrintAttributes.Margins.NO_MARGINS)
			.build();
	}

	@NonNull
	public static PrintDocumentAdapter createAdapter(@NonNull WebView webView, @NonNull String fname) {
		PrintDocumentAdapter adapter;
		if (21 <= Build.VERSION.SDK_INT) {
			adapter = webView.createPrintDocumentAdapter(fname);
		} else {
			adapter = webView.createPrintDocumentAdapter();
		}
		return adapter;
	}

}
