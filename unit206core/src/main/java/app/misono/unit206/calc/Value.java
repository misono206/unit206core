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

package app.misono.unit206.calc;

import androidx.annotation.NonNull;

public class Value {
	private float[] mValues;

	public Value() {
	}

	public void set(float v) {
		mValues = new float[1];
		mValues[0] = v;
	}

	public void set(float x, float y) {
		mValues = new float[2];
		mValues[0] = x;
		mValues[1] = y;
	}

	public void set(float[] fs) {
		mValues = fs;
	}

	public int set(@NonNull String s, int idx) {
		throw new RuntimeException("not supported set:");	// TODO
//		return idx;
	}

	public void multiply(@NonNull Value v) {
		throw new RuntimeException("not supported multiply:");	// TODO
	}

	public void divide(@NonNull Value v) {
		throw new RuntimeException("not supported divide:");	// TODO
	}

	public void add(@NonNull Value v) {
		throw new RuntimeException("not supported add:");	// TODO
	}

	public void sub(@NonNull Value v) {
		throw new RuntimeException("not supported sub:");	// TODO
	}

}
