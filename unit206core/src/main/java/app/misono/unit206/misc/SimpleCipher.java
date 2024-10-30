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

import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public final class SimpleCipher {
	private static final String TAG = "SimpleCipher";

	private static final String ALGORITHM = "ARC4";
	private static final int SIZE_SEED = 4;

	private static SecretKeySpec keyspecCommon;

	private SecretKeySpec keyspec;
	private Cipher cipher;
	private Random rand;

	public static void setKey(@NonNull byte[] key) {
		keyspecCommon = new SecretKeySpec(key, ALGORITHM);
	}

	public SimpleCipher() {
		keyspec = keyspecCommon;
		try {
			cipher = Cipher.getInstance(ALGORITHM);
			rand = new Random(SystemClock.elapsedRealtime());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	public SimpleCipher(@NonNull byte[] key) {
		this();
		keyspec = new SecretKeySpec(key, ALGORITHM);
	}

	@WorkerThread
	@NonNull
	public byte[] encrypt(@NonNull byte[] data) throws Exception {
		byte[] rc = new byte[data.length + SIZE_SEED];
		byte[] seed = new byte[SIZE_SEED];
		rand.nextBytes(seed);
		seed[1] = SIZE_SEED;
		cipher.init(Cipher.ENCRYPT_MODE, keyspec);
		cipher.update(seed, 0, SIZE_SEED, rc, 0);
		cipher.doFinal(data, 0, data.length, rc, SIZE_SEED);
		return rc;
	}

	@WorkerThread
	@NonNull
	public byte[] decrypt(@NonNull byte[] data) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, keyspec);
		byte[] poff = cipher.update(data, 0, 2);
		int offset = poff[1] & 0xff;
		cipher.update(data, 2, offset - 2);
		return cipher.doFinal(data, offset, data.length - offset);
	}

}
