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

package app.misono.unit206.sound;

import android.media.AudioFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import app.misono.unit206.misc.Utils;

import java.io.File;
import java.io.FileInputStream;

public class WavHeader {
	private static final int HEADER_SIZE = 44;
	private static final byte[] RIFF = "RIFF".getBytes();
	private static final byte[] WAVE = "WAVE".getBytes();
	private static final byte[] FMT  = "fmt ".getBytes();
	private static final byte[] DATA = "data".getBytes();

	public static final int WAVE_FORMAT_PCM = 0x0001;
	public static final int WAVE_FORMAT_IEEE_FLOAT = 0x0003;
	public static final int WAVE_FORMAT_ALAW = 0x0006;
	public static final int WAVE_FORMAT_MULAW = 0x0007;
	public static final int WAVE_FORMAT_EXTENSIBLE = 0xFFFE;

	private int ch, hz, bytePsec, bytePsample, bitPwav, sizePcm, fmt;

	public WavHeader() {
		// default: PCM16 44.1KHz stereo
		fmt = WAVE_FORMAT_PCM;
		ch = 2;
		hz = 44100;
		bitPwav = 16;
		calcParam();
	}

	private void calcParam() {
		bytePsample = ch * bitPwav / 8;
		bytePsec = hz * bytePsample;
	}

	@WorkerThread
	public void readHeaderSync(@NonNull File file) throws Exception {
		byte[] b = new byte[HEADER_SIZE];
		int len = readBytes(file, b);
		if (len != HEADER_SIZE) {
			throw new RuntimeException("size is small. size=" + len);
		}
		parseHeader(b);
	}

	public void parseHeader(@NonNull byte[] b) {
		if (b.length < HEADER_SIZE) {
			throw new RuntimeException("size is small.");
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

	private boolean beq(@NonNull byte[] b, int idx, @NonNull byte[] d) {
		for (int i = 0; i < d.length; i++) {
			if (b[idx + i] != d[i]) {
				return false;
			}
		}
		return true;
	}

	@WorkerThread
	private int readBytes(@NonNull File file, @NonNull byte[] b) throws Exception {
		int len;
		try (
			FileInputStream is = new FileInputStream(file);
		) {
			len = is.read(b);
		}
		return len;
	}

	@NonNull
	public byte[] setHeader(@Nullable byte[] header) {
		if (header != null) {
			int len = header.length;
			if (len < HEADER_SIZE) {
				throw new RuntimeException("too small header size:" + len);
			}
		} else {
			header = new byte[HEADER_SIZE];
		}

		System.arraycopy(RIFF, 0, header, 0, 4);
		Utils.write4le(header, 4, sizePcm + HEADER_SIZE - 8);
		System.arraycopy(WAVE, 0, header, 8, 4);
		System.arraycopy(FMT, 0, header, 12, 4);
		Utils.write4le(header, 16, 16);				// fmt chunk size
		Utils.write2le(header, 20, fmt);
		Utils.write2le(header, 22, ch);
		Utils.write4le(header, 24, hz);
		Utils.write4le(header, 28, bytePsec);
		Utils.write2le(header, 32, bytePsample);
		Utils.write2le(header, 34, bitPwav);
		System.arraycopy(DATA, 0, header, 36, 4);
		Utils.write4le(header, 40, sizePcm);

		return header;
	}

	@Deprecated		// use createHeader()
	@NonNull
	public byte[] getHeader() {
		return setHeader(null);
	}

	@NonNull
	public byte[] createHeader() {
		return setHeader(null);
	}

	public int getFormat() {
		return fmt;
	}

	public void setFormat(int fmt) {
		this.fmt = fmt;
	}

	/**
	 * Return AudioFormat.ENCODING_PCM_xxx value.
	 */
	public int getAudioFormat() {
		int rc;
		switch (fmt) {
		case WAVE_FORMAT_PCM:
			rc = AudioFormat.ENCODING_PCM_16BIT;
			break;
		case WAVE_FORMAT_IEEE_FLOAT:
			rc = AudioFormat.ENCODING_PCM_FLOAT;
			break;
		default:
			throw new RuntimeException("unknow format:" + fmt);
		}
		return rc;
	}

	public void setAudioFormat(int afmt) {
		switch (afmt) {
		case AudioFormat.ENCODING_PCM_16BIT:
			fmt = WAVE_FORMAT_PCM;
			break;
		case AudioFormat.ENCODING_PCM_FLOAT:
			fmt = WAVE_FORMAT_IEEE_FLOAT;
			break;
		default:
			throw new RuntimeException("unknow AudioFormat:" + afmt);
		}
	}

	public int getCh() {
		return ch;
	}

	public void setCh(int ch) {
		this.ch = ch;
		calcParam();
	}

	public int getHz() {
		return hz;
	}

	public void setHz(int hz) {
		this.hz = hz;
		calcParam();
	}

	public int getBitPerWav() {
		return bitPwav;
	}

	public void setBitPerWav(int bitPwav) {
		this.bitPwav = bitPwav;
		calcParam();
	}

	public int getPcmSize() {
		return sizePcm;
	}

	public void setPcmSize(int sizePcm) {
		this.sizePcm = sizePcm;
	}

	public int getBytePerSec() {
		return bytePsec;
	}

	public int getBytePerSample() {
		return bytePsample;
	}

	public int getHeaderSize() {
		return HEADER_SIZE;
	}

}
