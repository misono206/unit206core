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

package app.misono.unit206.element.table;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class TableTdParam {
	private final Builder builder;

	private int pxWidth, pxHeight;

	public static class Builder {
		private float rWidth;
		private float rMarginLeft, rMarginTop, rMarginRight, rMarginBottom;
		private float rPaddingLeft, rPaddingTop, rPaddingRight, rPaddingBottom;

		public Builder() {
		}

		@NonNull
		public Builder setWidthRatio(float rWidth) {
			this.rWidth = rWidth;
			return this;
		}

		@NonNull
		public Builder setMarginRatio(float rMargin) {
			this.rMarginLeft = rMargin;
			this.rMarginTop = rMargin;
			this.rMarginRight = rMargin;
			this.rMarginBottom = rMargin;
			return this;
		}

		@NonNull
		public Builder setMarginRatio(
			float rMarginLeft,
			float rMarginTop,
			float rMarginRight,
			float rMarginBottom
		) {
			this.rMarginLeft = rMarginLeft;
			this.rMarginTop = rMarginTop;
			this.rMarginRight = rMarginRight;
			this.rMarginBottom = rMarginBottom;
			return this;
		}

		@NonNull
		public Builder setPaddingRatio(float rPadding) {
			this.rPaddingLeft = rPadding;
			this.rPaddingTop = rPadding;
			this.rPaddingRight = rPadding;
			this.rPaddingBottom = rPadding;
			return this;
		}

		@NonNull
		public Builder setPaddingRatio(
			float rPaddingLeft,
			float rPaddingTop,
			float rPaddingRight,
			float rPaddingBottom
		) {
			this.rPaddingLeft = rPaddingLeft;
			this.rPaddingTop = rPaddingTop;
			this.rPaddingRight = rPaddingRight;
			this.rPaddingBottom = rPaddingBottom;
			return this;
		}

		@NonNull
		public TableTdParam build() {
			return new TableTdParam(this);
		}
	}

	public void setPixel(int pxWidth, int pxHeight) {
		this.pxWidth = pxWidth;
		this.pxHeight = pxHeight;
	}

	private TableTdParam(@NonNull Builder builder) {
		this.builder = builder;
	}

	public int getWidth() {
		return (int)(pxWidth * builder.rWidth);
	}

	void setWidthRatio(float rWidth) {
		builder.rWidth = rWidth;
	}

	public int getHeight() {
		return pxHeight;
	}

	void setMarginRatio(float rMargin) {
		builder.rMarginLeft = rMargin;
		builder.rMarginTop = rMargin;
		builder.rMarginRight = rMargin;
		builder.rMarginBottom = rMargin;
	}

	void setMarginRatio(float rMarginLeft, float rMarginTop, float rMarginRight, float rMarginBottom) {
		builder.rMarginLeft = rMarginLeft;
		builder.rMarginTop = rMarginTop;
		builder.rMarginRight = rMarginRight;
		builder.rMarginBottom = rMarginBottom;
	}

	public int getMarginLeft() {
		return (int)(pxHeight * builder.rMarginLeft);
	}

	void setMarginLeftRatio(float rMarginLeft) {
		builder.rMarginLeft = rMarginLeft;
	}

	public int getMarginTop() {
		return (int)(pxHeight * builder.rMarginTop);
	}

	void setMarginTopRatio(float rMarginTop) {
		builder.rMarginTop = rMarginTop;
	}

	public int getMarginRight() {
		return (int)(pxHeight * builder.rMarginRight);
	}

	void setMarginRightRatio(float rMarginRight) {
		builder.rMarginRight = rMarginRight;
	}

	public int getMarginBottom() {
		return (int)(pxHeight * builder.rMarginBottom);
	}

	void setMarginBottomRatio(float rMarginBottom) {
		builder.rMarginBottom = rMarginBottom;
	}

	void setPaddingRatio(float rPadding) {
		builder.rPaddingLeft = rPadding;
		builder.rPaddingTop = rPadding;
		builder.rPaddingRight = rPadding;
		builder.rPaddingBottom = rPadding;
	}

	void setPaddingRatio(float rPaddingLeft, float rPaddingTop, float rPaddingRight, float rPaddingBottom) {
		builder.rPaddingLeft = rPaddingLeft;
		builder.rPaddingTop = rPaddingTop;
		builder.rPaddingRight = rPaddingRight;
		builder.rPaddingBottom = rPaddingBottom;
	}

	public int getPaddingLeft() {
		return (int)(pxHeight * builder.rPaddingLeft);
	}

	void setPaddingLeftRatio(float rPaddingLeft) {
		builder.rPaddingLeft = rPaddingLeft;
	}

	public int getPaddingTop() {
		return (int)(pxHeight * builder.rPaddingTop);
	}

	void setPaddingTopRatio(float rPaddingTop) {
		builder.rPaddingTop = rPaddingTop;
	}

	public int getPaddingRight() {
		return (int)(pxHeight * builder.rPaddingRight);
	}

	void setPaddingRightRatio(float rPaddingRight) {
		builder.rPaddingRight = rPaddingRight;
	}

	public int getPaddingBottom() {
		return (int)(pxHeight * builder.rPaddingBottom);
	}

	void setPaddingBottomRatio(float rPaddingBottom) {
		builder.rPaddingBottom = rPaddingBottom;
	}

	@NonNull
	public ViewGroup.MarginLayoutParams createLayoutParams() {
		ViewGroup.MarginLayoutParams rc = new ViewGroup.MarginLayoutParams(getWidth(), getHeight());
		rc.setMargins(getMarginLeft(), getMarginTop(), getMarginRight(), getMarginBottom());
		return rc;
	}

	public void setPadding(@NonNull View view) {
		view.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
	}

}
