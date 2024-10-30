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

package app.misono.unit206.special;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.misc.Utils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class OrphanResource {
	private static final String TAG = "OrphanResource";

	private final Set<String> setRef;
	private final Set<String> setFile;
	private final String[] endsWithAnalyze;

	private static final String[] ANALYZE_FILES_ENDSWITH = {
		".java",
		".xml",
		"/build.gradle",
	};

	public OrphanResource(@Nullable String[] endsWithAnalyze) {
		setRef = new HashSet<>();
		setFile = new HashSet<>();
		if (endsWithAnalyze != null) {
			this.endsWithAnalyze = endsWithAnalyze;
		} else {
			this.endsWithAnalyze = ANALYZE_FILES_ENDSWITH;
		}
	}

	@WorkerThread
	@NonNull
	public Set<String> checkResourceDir(@NonNull File inDir) throws Exception {
		setFile.clear();
		setRef.clear();
		checkResourceDirInternal(inDir);
		log("setFile:" + setFile.size());
		log("setRef:" + setRef.size());
		Set<String> rc = new HashSet<>(setFile);
		for (String ref : setRef) {
			rc.remove(ref);
		}
		return rc;
	}

	@WorkerThread
	private void checkResourceDirInternal(@NonNull File inDir) throws Exception {
		File[] files = inDir.listFiles();
		if (files != null) {
			for (File f : files) {
				String fname = f.getName();
				if (f.isDirectory()) {
					checkResourceDirInternal(f);
				} else {
					boolean analyze = false;
					for (String endsWith : endsWithAnalyze) {
						if (fname.endsWith(endsWith)) {
							analyze = true;
							break;
						}
					}
					if (analyze) {
						analyzeSource4Resources(f);
					}
				}
			}
		}
	}

	@WorkerThread
	private void analyzeSource4Resources(@NonNull File inFile) throws Exception {
		String path = inFile.getAbsolutePath();
		boolean fJava = path.endsWith(".java") || path.endsWith(".kt");
		boolean fXml = path.endsWith(".xml");
		int didx = path.indexOf("/res/drawable/");
		if (0 < didx) {
			path = path.substring(didx);
			path = path.replace("/res/", "R.");
			path = path.replace('/', '.');
			if (path.endsWith(".xml")) {
				path = path.substring(0, path.length() - 4);
			}
			setFile.add(path);
		} else if (path.endsWith("/res/values/strings.xml")) {
			byte[] b = Utils.readBytesWithException(inFile);
			String bin = new String(b);
			String[] lines = bin.split("\n");
			for (String line : lines) {
				int idx = line.indexOf("name=\"");
				if (0 < idx) {
					idx += 6;
					int eidx = line.indexOf('"', idx);
					if (idx < eidx) {
						String name = line.substring(idx, eidx);
						setFile.add("R.string." + name);
					}
				}
			}
		} else {
			byte[] b = Utils.readBytesWithException(inFile);
			String bin = new String(b);
			String[] lines = bin.split("\n");
			for (String line : lines) {
				if (fJava) {
					addResourceRef(line, "R.drawable.");
					addResourceRef(line, "R.string.");
				}
				if (fXml) {
					addResourceRef(line, "@drawable/");
					addResourceRef(line, "@string/");
				}
			}
		}
	}

	private void addResourceRef(@NonNull String line, @NonNull String prefixResource) {
		int len = prefixResource.length();
		int sidx = 0;
		for ( ; ; ) {
			int ridx = line.indexOf(prefixResource, sidx);
			if (ridx < 0) {
				break;
			}
			String rname = getRname(line, ridx);
			setRef.add(rname);
			sidx = ridx + len;
		}
	}

	@NonNull
	private String getRname(@NonNull String s, int idx) {
		int n = s.length();
		int end = n;
		for (int i = idx; i < n; i++) {
			char c = s.charAt(i);
			if (!Character.isAlphabetic(c)
				&& !Character.isDigit(c)
				&& c != '/'
				&& c != '@'
				&& c != '_'
				&& c != '.'
			) {
				end = i;
				break;
			}
		}
		String rc = s.substring(idx, end);
		rc = rc.replace("@drawable/", "R.drawable.");
		return rc.replace("@string/", "R.string.");
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
