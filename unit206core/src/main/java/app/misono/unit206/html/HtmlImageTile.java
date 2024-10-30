/*
 * Copyright 2024 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.html;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

public class HtmlImageTile {
	private final int x, y;
	private final int w, h;
	private final int noImage;

	public interface ImagePathCreator {
		@NonNull
		String createImagePath(int noImage);
	}

	public HtmlImageTile(int noImage, int x, int y, int w, int h) {
		this.noImage = noImage;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	@NonNull
	public String createImageTag(
		@Nullable String attrs,
		@NonNull ImagePathCreator creator
	) {
		String rc = String.format(
			Locale.US,
			"<div style='background-image:url(\"%s\"); background-position:-%dpx -%dpx; width:%dpx; height:%dpx; margin:0.5vw 0.5vw 0.5vw 0.5vw;'",
			creator.createImagePath(noImage),
			x,
			y,
			w,
			h
		);
		if (!TextUtils.isEmpty(attrs)) {
			rc += " ";
			rc += attrs;
		}
		rc += " />";
		return rc;
	}

	public int getImageNo() {
		return noImage;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}

}
