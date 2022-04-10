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
import androidx.annotation.Nullable;

import app.misono.unit206.debug.Log2;
import app.misono.unit206.task.ObjectReference;

import java.util.LinkedList;
import java.util.List;

public class Calc {
	private static final String TAG = "Calc";

	private final ObjectReference<Value> mTmp;
	private final List<String> mListS;
	private final List<Value> mListV;

	private GetValue mGetValue;

	public interface GetValue {
		@NonNull
		Value getValue(@NonNull String name);
	}

	public Calc(@Nullable GetValue getValue) {
		this();
		mGetValue = getValue;
	}

	private Calc() {
		mListS = new LinkedList<>();
		mListV = new LinkedList<>();
		mTmp = new ObjectReference<>();
	}

	@NonNull
	public Value calc(@NonNull String s) {
		int idx = calc(s, 0);
		if (idx != s.length()) {
			log(s);
			log("remain:" + s.substring(idx));
			throw new RuntimeException("mismatch length." + idx + " " + s.length());
		}
		return getResult();
	}

	public int calc(@NonNull String s, int idx) {
		int len = s.length();
		for ( ; idx < len; ) {
			char c = s.charAt(idx);
			if (c == '[' || c == '-' || c == '+' || ('0' <= c && c <= '9')) {
				Value v = new Value();
				idx = v.set(s, idx);
				mListV.add(v);
				idx = operator(s, idx);
			} else if (c == '(') {
				Calc calc = new Calc();
				idx = calc.calc(s, idx + 1);
				char c1 = s.charAt(idx);
				if (c1 == ')') {
					idx++;
					mListV.add(calc.getResult());
					idx = operator(s, idx);
				} else {
					throw new RuntimeException("Syntax error1:" + c1);
				}
			} else if (c == '@') {
				idx = variable(s, idx);
			} else {
				break;
			}
		}
		for (int i = 0; i < mListS.size(); ) {
			String e = mListS.get(i);
			if (e.equals("*")) {
				Value v1 = mListV.get(i);
				Value v2 = mListV.get(i + 1);
				v2.multiply(v1);
				mListV.remove(i);
				mListS.remove(i);
			} else if (e.equals("/")) {
				Value v1 = mListV.get(i);
				Value v2 = mListV.get(i + 1);
				v2.divide(v1);
				mListV.remove(i);
				mListS.remove(i);
			} else {
				i++;
			}
		}
		for (int i = 0; i < mListS.size(); ) {
			String e = mListS.get(i);
			if (e.equals("+")) {
				Value v1 = mListV.get(i);
				Value v2 = mListV.get(i + 1);
				v2.add(v1);
				mListV.remove(i);
				mListS.remove(i);
			} else if (e.equals("-")) {
				Value v1 = mListV.get(i);
				Value v2 = mListV.get(i + 1);
				v2.sub(v1);
				mListV.remove(i);
				mListS.remove(i);
			} else {
				i++;
			}
		}
		return idx;
	}

	private int operator(@NonNull String s, int idx) {
		char c = s.charAt(idx);
		if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
			idx++;
			mListS.add("" + c);
		} else {
			throw new RuntimeException("Syntax error2:" + c);
		}
		return idx;
	}

	private int variable(@NonNull String s, int idx) {
		if (mGetValue == null) {
			log("remain:" + s.substring(idx));
			throw new RuntimeException("found variable but no-set GetValue...");
		}
		int len = s.length();
		int sidx = idx;
		int eidx = len;
		for ( ; idx < len; idx++) {
			char c = s.charAt(idx);
			if (!('A' <= c && c <= 'Z') && !('a' <= c && c <= 'z') && !('0' <= c && c <= '9')
					&& c != '.' && c != '_') {

				eidx = idx;
				break;
			}
		}
		String name = s.substring(sidx, eidx);
		log("found:" + name);
		mTmp.set(mGetValue.getValue(name));
		return eidx;
	}

	@NonNull
	public Value getResult() {
		if (mListV.size() != 1) {
			throw new RuntimeException("strange..." + mListV.size());
		}
		return mListV.get(0);
	}

	private void log(@NonNull String msg) {
		Log2.e(TAG, msg);
	}

}
