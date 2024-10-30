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

import androidx.annotation.NonNull;

import app.misono.unit206.debug.Log2;

public class ResamplerFloat {
	private static final String TAG = "ResamplerFloat";

	private boolean isRemain;
	private float rv;
	private float rx;		// usually minus or zero value.

	public ResamplerFloat() {
		init();
	}

	public void init() {
		isRemain = false;
	}

	public int resample(
		float pitch,			// inHz / outHz
		@NonNull float[] in,
		int inOffset,
		int inLength,
		@NonNull float[] out,
		int outOffset
	) {
		int inMax = inOffset + inLength;
		int idx = outOffset;
		if (inLength != 0) {
			float ix = inOffset + rx;
			if (!isRemain) {
				rx = 0;
				out[idx++] = in[inOffset];
				ix += pitch;
			}
			for ( ; idx < out.length; idx++) {
				int iix = (int)ix;
				if (inMax <= iix + 1) {
					break;
				}
				float fix = ix - iix;
				float b = in[iix];
				out[idx] = b + (in[iix + 1] - b) * fix;
				ix += pitch;
			}
			rx = ix - (inOffset + inLength);
			rv = in[inOffset + inLength - 1];
//			isRemain = true;
			isRemain = false;		// TODO
		}
		return idx - outOffset;
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
