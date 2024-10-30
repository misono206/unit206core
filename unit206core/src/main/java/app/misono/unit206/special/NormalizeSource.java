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
import java.util.Map;

public class NormalizeSource {
	private static final String TAG = "NormalizeSource";
	private static final String[] REMOVE_FILES_ENDSWITH = {
		"/.git",
		"/.gradle",
		"/.idea",
		"/app/build",
	};
	private static final String[] ANALYZE_FILES_ENDSWITH = {
		".java",
		".xml",
		"/build.gradle",
	};

	private final Map<String, Boolean> mapInclude;
	private final String[] endsWithRemove;
	private final String[] endsWithAnalyze;

	public NormalizeSource(
		@Nullable String[] endsWithRemove,
		@Nullable String[] endsWithAnalyze,
		@NonNull Map<String, Boolean> mapInclude
	) {
		this.mapInclude = mapInclude;
		if (endsWithRemove != null) {
			this.endsWithRemove = endsWithRemove;
		} else {
			this.endsWithRemove = REMOVE_FILES_ENDSWITH;
		}
		if (endsWithAnalyze != null) {
			this.endsWithAnalyze = endsWithAnalyze;
		} else {
			this.endsWithAnalyze = ANALYZE_FILES_ENDSWITH;
		}
	}

	@WorkerThread
	public void createNormalSourceDir(
		@NonNull File dirIn,
		@NonNull File dirOut
	) throws Exception {
		File[] dirs = dirIn.listFiles();
		if (dirs != null) {
			for (File d : dirs) {
				log("do:" + d.getAbsolutePath());
				String fname = d.getName();
				File dOut = new File(dirOut, fname + "-normal");
				dOut.mkdirs();
				createNormalSource(d, dOut);
			}
		}
	}

	@WorkerThread
	private void createNormalSource(
		@NonNull File inDir,
		@NonNull File outDir
	) throws Exception {
		File[] files = inDir.listFiles();
		if (files != null) {
			for (File f : files) {
				String fname = f.getName();
				File out = new File(outDir, fname);
				String path = f.getAbsolutePath();
				boolean pass = false;
				for (String endsWith : endsWithRemove) {
					if (path.endsWith(endsWith)) {
						pass = true;
						break;
					}
				}
				if (!pass) {
					if (f.isDirectory()) {
						out.mkdirs();
						createNormalSource(f, out);
					} else {
						boolean analyze = false;
						for (String endsWith : endsWithAnalyze) {
							if (path.endsWith(endsWith)) {
								analyze = true;
								break;
							}
						}
						if (analyze) {
							analyzeSource(f, out);
						} else {
							Utils.copyFileToFile(f, out);
						}
					}
				}
			}
		}
	}

	@WorkerThread
	private void analyzeSource(
		@NonNull File in,
		@NonNull File out
	) throws Exception {
		StringBuilder sb = new StringBuilder();
		byte[] b = Utils.readBytesWithException(in);
		String bin = new String(b);
		String[] lines = bin.split("\n");
		int state = 0;
		String execLabel = null;
		for (String line : lines) {
			int idx = line.indexOf("//### ");
			if (0 <= idx) {
				String s = line.substring(idx + 6).trim();
				String[] sp = s.split(" ");
				String label = sp[0];
				String sStart = sp[1];
				boolean bStart = false;
				boolean bElse = false;
				boolean bEnd = false;
				switch (sStart) {
				case "start":
					bStart = true;
					break;
				case "else":
					bElse = true;
					break;
				case "end":
					bEnd = true;
					break;
				default:
					log("file:" + in.getAbsolutePath());
					throw new RuntimeException("unknown line:" + line);
				}
				Boolean onoff = mapInclude.get(label);
				if (onoff == null) {
					log("path:" + in.getAbsolutePath());
					log("line:" + line);
					throw new RuntimeException("unknown label:" + label);
				}
				switch (state) {
				case 0:		// copy lines
					if (bStart) {
						execLabel = label;
						state = 1;
					}
					break;
				case 1:		// remove lines
					if (label.contentEquals(execLabel)) {
						if (bStart) {
							log("path:" + in.getAbsolutePath());
							log("line:" + line);
							throw new RuntimeException("duplicated enter:" + label);
						} else if (bElse) {
							state = 2;
						} else if (bEnd) {
							execLabel = null;
							state = 0;
						}
					}
					break;
				case 2:		// copy lines in else
					if (label.contentEquals(execLabel)) {
						if (bEnd) {
							execLabel = null;
							state = 0;
						}
					}
					break;
				default:
					throw new RuntimeException("unknown state:" + state);
				}
			} else {
				if (state == 0) {
					sb.append(line);
					sb.append("\n");
				}
			}
		}
		Utils.writeToFile(out, sb.toString().getBytes());
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}