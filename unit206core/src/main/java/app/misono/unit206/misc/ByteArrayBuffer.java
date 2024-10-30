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

import java.util.ArrayList;
import java.util.List;

public class ByteArrayBuffer {
	private final List<Tuple> mList;

	public ByteArrayBuffer() {
		mList = new ArrayList<>();
	}

	public void add(@NonNull byte[] buf, int offset, int length) {
		if (length != 0) {
			mList.add(new Tuple(buf, offset, length));
		}
	}

	@NonNull
	public byte[] getByteArray() {
		if (mList.size() == 1) {
			Tuple t = mList.get(0);
			if (t.mOffset == 0 && t.mLength == t.mBuf.length) {
				return t.mBuf;
			}
		}
		int len = 0;
		for (Tuple t : mList) {
			len += t.mLength;
		}
		byte[] rc = new byte[len];
		int offset = 0;
		for (Tuple t : mList) {
			System.arraycopy(t.mBuf, t.mOffset, rc, offset, t.mLength);
			offset += t.mLength;
		}
		return rc;
	}

	private static class Tuple {
		private final byte[] mBuf;
		private final int mOffset;
		private final int mLength;

		private Tuple(@NonNull byte[] buf, int offset, int length) {
			mBuf = buf;
			mOffset = offset;
			mLength = length;
		}
	}

}
