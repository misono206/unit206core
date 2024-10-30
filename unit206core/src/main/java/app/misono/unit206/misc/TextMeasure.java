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

public class TextMeasure {
	private final float w, h, baseline;

	public TextMeasure(float w, float h, float baseline) {
		this.w = w;
		this.h = h;
		this.baseline = baseline;
	}

	public float getWidth() {
		return w;
	}

	public float getHeight() {
		return h;
	}

	public float getBaseline() {
		return baseline;
	}

	/**
	 * Get center and baseline difference.
	 * You can draw by text's center Y-position.
	 * canvas.drawText(text, x, y + tm.getCenterOffset(), paint);
	 */
	public float getCenterOffset() {
		return baseline - h / 2;
	}

}
