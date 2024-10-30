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

package app.misono.unit206.misc;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.File;
import java.io.FileInputStream;

@Deprecated		// use sound/WavHeader class instead.
public class WavReader {
	private static final int HEADER_SIZE = 44;
	private static final int HEADER_SIZE_EXTENDED = 0x44;
	private static final byte[] RIFF = "RIFF".getBytes();
	private static final byte[] WAVE = "WAVE".getBytes();
	private static final byte[] FMT  = "fmt ".getBytes();
	private static final byte[] DATA = "data".getBytes();

	public static final int WAVE_FORMAT_PCM = 0x0001;
	public static final int WAVE_FORMAT_IEEE_FLOAT = 0x0003;
	public static final int WAVE_FORMAT_ALAW = 0x0006;
	public static final int WAVE_FORMAT_MULAW = 0x0007;
	public static final int WAVE_FORMAT_EXTENSIBLE = 0xFFFE;

	private final File file;

	private int ch, hz, bytePsec, bytePsample, bitPwav, sizePcm, fmt;

	public WavReader(@NonNull File file) {
		this.file = file;
	}

	@WorkerThread
	public void readHeaderSync() throws Exception {
		byte[] b = new byte[HEADER_SIZE];
		int len = readBytes(b);
		if (len != HEADER_SIZE) {
			throw new RuntimeException("size is small. size=" + len);
		}

		if (!beq(b, 0, RIFF)) {
			throw new RuntimeException("not RIFF...");
		}
		if (!beq(b, 8, WAVE)) {
			throw new RuntimeException("not WAVE...");
		}
		if (!beq(b, 12, FMT)) {
			throw new RuntimeException("not FMT...");
		}
		fmt = Utils.read2le(b, 20);
		ch = Utils.read2le(b, 22);
		hz = Utils.read4le(b, 24);
		bytePsec = Utils.read4le(b, 28);
		bytePsample = Utils.read2le(b, 32);
		bitPwav = Utils.read2le(b, 34);
		sizePcm = Utils.read4le(b, 40);
	}

	public int getFormat() {
		return fmt;
	}

	public int getCh() {
		return ch;
	}

	public int getHz() {
		return hz;
	}

	public int getBytePerSec() {
		return bytePsec;
	}

	public int getBytePerSample() {
		return bytePsample;
	}

	public int getBitPerWav() {
		return bitPwav;
	}

	public int getPcmSize() {
		return sizePcm;
	}

	private boolean beq(@NonNull byte[] b, int idx, @NonNull byte[] d) {
		for (int i = 0; i < d.length; i++) {
			if (b[idx + i] != d[i]) {
				return false;
			}
		}
		return true;
	}

	@WorkerThread
	private int readBytes(@NonNull byte[] b) throws Exception {
		int len;
		try (
			FileInputStream is = new FileInputStream(file);
		) {
			len = is.read(b);
		}
		return len;
	}

}
