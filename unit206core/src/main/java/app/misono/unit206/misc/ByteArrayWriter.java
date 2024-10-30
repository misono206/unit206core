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

import androidx.annotation.NonNull;

public final class ByteArrayWriter {
	private byte[] buf;
	private int len;

	public ByteArrayWriter() {
		buf = new byte[256];
		len = 0;
	}

	public void reset() {
		len = 0;
	}

	public int getLength() {
		return len;
	}

	public void writeByte(byte a) {
		reallocateIfNeed(1);
		buf[len++] = a;
	}

	public void writeShort(short a) {
		reallocateIfNeed(2);
		buf[len++] = (byte)(a >>  8);
		buf[len++] = (byte) a;
	}

	public void writeInt(int a) {
		reallocateIfNeed(4);
		buf[len++] = (byte)(a >> 24);
		buf[len++] = (byte)(a >> 16);
		buf[len++] = (byte)(a >>  8);
		buf[len++] = (byte) a;
	}

	public void writeLong(long a) {
		reallocateIfNeed(8);
		buf[len++] = (byte)(a >> 56);
		buf[len++] = (byte)(a >> 48);
		buf[len++] = (byte)(a >> 40);
		buf[len++] = (byte)(a >> 32);
		buf[len++] = (byte)(a >> 24);
		buf[len++] = (byte)(a >> 16);
		buf[len++] = (byte)(a >>  8);
		buf[len++] = (byte) a;
	}

	public void writeString(@NonNull String s) {
		writeByteArray(s.getBytes());
	}

	public void writeByteArray(@NonNull byte[] b) {
		writeInt(b.length);
		paste(b);
	}

	public void paste(@NonNull byte[] b) {
		reallocateIfNeed(b.length);
		System.arraycopy(b, 0, buf, len, b.length);
		len += b.length;
	}

	private void reallocateIfNeed(int need) {
		boolean realloc = false;
		int size = buf.length;
		for ( ; ; ) {
			if (need <= size - len) break;

			realloc = true;
			size *= 2;
		}
		if (realloc) {
			byte[] buf2= new byte[size];
			System.arraycopy(buf, 0, buf2, 0, len);
			buf = buf2;
		}
	}

	@NonNull
	public byte[] build() {
		byte[] rc = new byte[len];
		System.arraycopy(buf, 0, rc, 0, len);
		return rc;
	}

}
