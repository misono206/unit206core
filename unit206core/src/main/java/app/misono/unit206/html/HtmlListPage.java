/*
 * Copyright 2024 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.html;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import app.misono.unit206.misc.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

class HtmlListPage {
	private final String prefix;
	private final String topHtml, bottomHtml;
	private final File dir;
	private final int nCount;

	private BufferedWriter writer;
	private int ccHtml, ccItem;

	HtmlListPage(
		@NonNull File dir,
		@NonNull String prefix,
		@NonNull String topHtml,
		@NonNull String bottomHtml,
		int nCount
	) {
		this.dir = dir;
		this.prefix = prefix;
		this.topHtml = topHtml;
		this.bottomHtml = bottomHtml;
		this.nCount = nCount;
	}

	int getItemCount() {
		return ccItem;
	}

	/**
	 * write item.
	 * @return true if file was closed.
	 */
	@WorkerThread
	boolean writeItem(@NonNull HtmlItemCreator item) throws IOException {
		boolean rc = reCreateWriterIfNeed();
		String body = item.createHtml(ccItem);
		writer.append(body);
		ccItem++;
		return rc;
	}

	@WorkerThread
	void writeHtml(@NonNull String html) throws IOException {
		writer.append(html);
	}

	@NonNull
	String createCurrentUrl() {
		return String.format(Locale.US, "%s-%04d.html", prefix, ccHtml);
	}

	@NonNull
	String createPreviousUrl() {
		return String.format(Locale.US, "%s-%04d.html", prefix, ccHtml - 1);
	}

	private boolean reCreateWriterIfNeed() throws IOException {
		if (ccItem % nCount == 0) {
			closePage();
			String fname = createCurrentUrl();
			File file = new File(dir, fname);
			writer = new BufferedWriter(new FileWriter(file));
			writer.append(topHtml);
			ccHtml++;
			return ccItem != 0;
		}
		return false;
	}

	boolean closePage() {
		boolean rc = false;
		if (writer != null) {
			rc = ccItem % nCount != 0;
			try {
				writer.append(bottomHtml);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Utils.closeSafely(writer);
			writer = null;
		}
		return rc;
	}

}
