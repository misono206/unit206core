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

import androidx.annotation.NonNull;

public final class ByteArrayReader {
	private byte[] buf;
	private int index;

	public ByteArrayReader(@NonNull byte[] buf) {
		this.buf = buf;
		index = 0;
	}

	public int getRemain() {
		return buf.length - index;
	}

	public byte readByte() {
		return buf[index++];
	}

	public short readShort() {
		short rc = 0;
		rc += (long)(buf[index++] & 0xff) <<  8;
		rc += (long)(buf[index++] & 0xff);
		return rc;
	}

	public int readInt() {
		int rc = 0;
		rc += (buf[index++] & 0xff) << 24;
		rc += (buf[index++] & 0xff) << 16;
		rc += (buf[index++] & 0xff) <<  8;
		rc +=  buf[index++] & 0xff;
		return rc;
	}

	public long readLong() {
		long rc = 0;
		rc += (long)(buf[index++] & 0xff) << 56;
		rc += (long)(buf[index++] & 0xff) << 48;
		rc += (long)(buf[index++] & 0xff) << 40;
		rc += (long)(buf[index++] & 0xff) << 32;
		rc += (long)(buf[index++] & 0xff) << 24;
		rc += (long)(buf[index++] & 0xff) << 16;
		rc += (long)(buf[index++] & 0xff) <<  8;
		rc += (long)(buf[index++] & 0xff);
		return rc;
	}

	@NonNull
	public byte[] readByteArray() {
		int len = readInt();
		byte[] rc = new byte[len];
		System.arraycopy(buf, index, rc, 0, len);
		index += len;
		return rc;
	}

	@NonNull
	public String readString() {
		return new String(readByteArray());
	}

}
