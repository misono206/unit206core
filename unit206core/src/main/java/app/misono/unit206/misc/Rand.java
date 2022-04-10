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
import androidx.annotation.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public final class Rand {
	public static long randLong(@Nullable byte[] seed) {
		long rand;
		for ( ; ; ) {
			rand = randLongInternal(seed);
			if (rand != 0 && rand != 1 && rand != -1L) {	// special
				break;
			}
		}
		return rand;
	}

	private static long randLongInternal(@Nullable byte[] seed) {
		MessageDigest ctx = null;
		long rand = 0;
		try {
			ctx = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (ctx != null) {
			if (seed != null) {
				ctx.update(seed);
			}
			ctx.update(UUID.randomUUID().toString().getBytes());
			long tick = System.currentTimeMillis();
			byte[] tmp = new byte[4];
			tmp[0] = (byte)(tick >> 24);
			tmp[1] = (byte)(tick >> 16);
			tmp[2] = (byte)(tick >>  8);
			tmp[3] = (byte) tick;
			ctx.update(tmp);
			byte[] digest = ctx.digest();
			rand = ((long)(digest[0] & 0xff) << 56)
			     + ((long)(digest[1] & 0xff) << 48)
			     + ((long)(digest[2] & 0xff) << 40)
			     + ((long)(digest[3] & 0xff) << 32)
			     + ((long)(digest[4] & 0xff) << 24)
			     + ((long)(digest[5] & 0xff) << 16)
			     + ((long)(digest[6] & 0xff) <<  8)
			     +  (long)(digest[7] & 0xff);
		}
		return rand;
	}

	public static int calcHashInt(@NonNull byte[] buf) {
		MessageDigest ctx = null;
		int hash = 0;
		try {
			ctx = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (ctx != null) {
			ctx.update(buf);
			byte[] digest = ctx.digest();
			hash = ((digest[0] & 0xff) << 24)
			     + ((digest[1] & 0xff) << 16)
			     + ((digest[2] & 0xff) <<  8)
			     +  (digest[3] & 0xff);
		}
		return hash;
	}

}
