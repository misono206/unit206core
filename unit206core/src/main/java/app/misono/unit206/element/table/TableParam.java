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

package app.misono.unit206.element.table;

import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TableParam {
	private final Builder builder;

	public static class Builder {
		private final LongSparseArray<TableTdParam> paramSingle;
		private final SparseArray<TableTdParam> paramRow, paramColum;
		private final TableTdParam[] paramTr;
		private final int nLine;

		private int[] colorBg;

		public Builder(int nColum, int nLine, @NonNull float[] rWidth) {
			this.nLine = nLine;
			paramSingle = new LongSparseArray<>();
			paramRow = new SparseArray<>();
			paramColum = new SparseArray<>();
			paramTr = new TableTdParam[nColum];
			for (int i = 0; i < nColum; i++) {
				paramTr[i] = new TableTdParam.Builder().build();
			}
			setWidthRatio(rWidth);
		}

		@NonNull
		public Builder setBackgroundColor(int colorBg) {
			this.colorBg = new int[1];
			this.colorBg[0] = colorBg;
			return this;
		}

		@NonNull
		public Builder setBackgroundColor(@NonNull int[] colorBg) {
			this.colorBg = colorBg.length == 0 ? null : colorBg;
			return this;
		}

		@NonNull
		public Builder setWidthRatio(@NonNull float[] rWidth) {
			for (int i = 0; i < rWidth.length; i++) {
				paramTr[i].setWidthRatio(rWidth[i]);
			}
			return this;
		}

		@NonNull
		public Builder setMarginRatio(float rMargin) {
			for (TableTdParam td : paramTr) {
				td.setMarginRatio(rMargin);
			}
			return this;
		}

		@NonNull
		public Builder setMarginRatio(
			float rMarginLeft,
			float rMarginTop,
			float rMarginRight,
			float rMarginBottom
		) {
			for (TableTdParam td : paramTr) {
				td.setMarginRatio(rMarginLeft, rMarginTop, rMarginRight, rMarginBottom);
			}
			return this;
		}

		@NonNull
		public Builder setPaddingRatio(float rPadding) {
			for (TableTdParam td : paramTr) {
				td.setPaddingRatio(rPadding);
			}
			return this;
		}

		@NonNull
		public Builder setPaddingRatio(
			float rPaddingLeft,
			float rPaddingTop,
			float rPaddingRight,
			float rPaddingBottom
		) {
			for (TableTdParam td : paramTr) {
				td.setPaddingRatio(rPaddingLeft, rPaddingTop, rPaddingRight, rPaddingBottom);
			}
			return this;
		}

		@NonNull
		public Builder setParamSingle(int row, int colum, @NonNull TableTdParam param) {
			long key = calcSingleKey(row, colum);
			paramSingle.append(key, param);
			return this;
		}

		@NonNull
		public Builder setParamRow(int row, @NonNull TableTdParam param) {
			paramRow.append(row, param);
			return this;
		}

		@NonNull
		public Builder setParamColum(int colum, @NonNull TableTdParam param) {
			paramColum.append(colum, param);
			return this;
		}

		@NonNull
		public TableParam build() {
			return new TableParam(this);
		}
	}

	private static long calcSingleKey(int row, int colum) {
		return (((long)row) << 32) + colum;
	}

	private TableParam(@NonNull Builder builder) {
		this.builder = builder;
	}

	@NonNull
	public TableTdParam getTableTdParam(int row, int colum) {
		long key = calcSingleKey(row, colum);
		TableTdParam rc = builder.paramSingle.get(key);
		if (rc == null) {
			rc = builder.paramRow.get(row);
			if (rc == null) {
				rc = builder.paramColum.get(colum);
				if (rc == null) {
					rc = builder.paramTr[colum];
				}
			}
		}
		return rc;
	}

	public void setPixel(int pxWidth, int pxHeight) {
		int min = Math.min(pxWidth, pxHeight);
		int pxHeight1 = min / builder.nLine;
		int n = builder.paramSingle.size();
		for (int i = 0; i < n; i++) {
			TableTdParam p = builder.paramSingle.valueAt(i);
			p.setPixel(pxWidth, pxHeight1);
		}
		n = builder.paramRow.size();
		for (int i = 0; i < n; i++) {
			TableTdParam p = builder.paramRow.valueAt(i);
			p.setPixel(pxWidth, pxHeight1);
		}
		n = builder.paramColum.size();
		for (int i = 0; i < n; i++) {
			TableTdParam p = builder.paramColum.valueAt(i);
			p.setPixel(pxWidth, pxHeight1);
		}
		for (int i = 0; i < builder.paramTr.length; i++) {
			TableTdParam p = builder.paramTr[i];
			p.setPixel(pxWidth, pxHeight1);
		}
	}

	public int getColumnCount() {
		return builder.paramTr.length;
	}

	@Nullable
	public int[] getBackgroundColor() {
		return builder.colorBg;
	}

	public void setBackgroundColor(@NonNull View view, int row) {
		int [] colorBg = builder.colorBg;
		if (colorBg != null) {
			view.setBackgroundColor(colorBg[row % colorBg.length]);
		}
	}

	@NonNull
	public ViewGroup.MarginLayoutParams createLayoutParams(int row, int column) {
		TableTdParam p = getTableTdParam(row, column);
		return p.createLayoutParams();
	}

	public void setPadding(@NonNull View view, int row, int column) {
		TableTdParam p = getTableTdParam(row, column);
		p.setPadding(view);
	}

}
