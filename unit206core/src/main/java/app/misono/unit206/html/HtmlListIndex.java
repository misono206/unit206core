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

/**
 * HTMLのリスト表示を複数ページに分割するもの.
 */
public class HtmlListIndex {
	private final BufferedWriter writer;
	private final HtmlListPage page;
	private final String htmlBottom;

	private int ccPage;

	@WorkerThread
	public HtmlListIndex(
		@NonNull File out,
		@NonNull String htmlIndexTop,
		@NonNull String htmlIndexBottom,
		@NonNull File dirPage,
		@NonNull String prefixPage,
		@NonNull String htmlPageTop,
		@NonNull String htmlPageBottom,
		int nCountPage
	) throws IOException {
		htmlBottom = htmlIndexBottom;
		writer = new BufferedWriter(new FileWriter(out));
		writer.append(htmlIndexTop);
		File dir = out.getParentFile();
		if (dir == null) {
			throw new NullPointerException("out directory is null...");
		}
		page = new HtmlListPage(dirPage, prefixPage, htmlPageTop, htmlPageBottom, nCountPage);
	}

	@WorkerThread
	public void writeHtmlItem(@NonNull HtmlItemCreator itemIndex, @NonNull HtmlItemCreator itemPage) throws IOException {
		if (page.writeItem(itemPage)) {
			// new html page created.
			pageClosed(itemIndex);
		}
	}

	@WorkerThread
	private void pageClosed(@NonNull HtmlItemCreator itemIndex) throws IOException {
		String html = itemIndex.createHtml(ccPage);
		writer.append(html);
		ccPage++;
	}

	public int getItemCount() {
		return page.getItemCount();
	}

	@NonNull
	public String createCurrentPageUrl() {
		return page.createCurrentUrl();
	}

	@NonNull
	public String createPreviousPageUrl() {
		return page.createPreviousUrl();
	}

	public void closeIndex(@NonNull HtmlItemCreator itemIndex) {
		try {
			if (page.closePage()) {
				pageClosed(itemIndex);
			}
			writer.append(htmlBottom);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Utils.closeSafely(writer);
	}

}
