/*
 * Copyright 2022 Atelier Misono, Inc. @ https://misono.app/
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

package app.misono.unit206.element.textlist;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import app.misono.unit206.R;
import app.misono.unit206.misc.RelativeRect;
import app.misono.unit206.view.IconCardView;

import com.google.android.material.card.MaterialCardView;

public class TextListCardView<I extends TextListItem> extends MaterialCardView {
	private static final String TAG = "TextListCardView";

	private final AppCompatTextView timeName;
	private final IconCardView vInfo;

	private boolean hasInfo;

	public TextListCardView(@NonNull Context ctx) {
		super(ctx);
		timeName = new AppCompatTextView(ctx);
		vInfo = new IconCardView(ctx);
		init();
	}

	public TextListCardView(@NonNull Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		timeName = new AppCompatTextView(ctx, attrs);
		vInfo = new IconCardView(ctx, attrs);
		init();
	}

	public TextListCardView(@NonNull Context ctx, AttributeSet attrs, int defStyleAttr) {
		super(ctx, attrs, defStyleAttr);
		timeName = new AppCompatTextView(ctx, attrs, defStyleAttr);
		vInfo = new IconCardView(ctx, attrs, defStyleAttr);
		init();
	}

	private void init() {
		timeName.setTypeface(Typeface.MONOSPACE);
		timeName.setSingleLine();
		timeName.setEllipsize(TextUtils.TruncateAt.END);
		LayoutParams params = new LayoutParams(
			LayoutParams.WRAP_CONTENT,
			LayoutParams.WRAP_CONTENT,
			Gravity.START | Gravity.TOP
		);
		addView(timeName, params);

		vInfo.setIcon(R.drawable.outline_info_24);
		addView(vInfo, new LayoutParams(0, 0, Gravity.END | Gravity.CENTER_VERTICAL));
		vInfo.setEnabled(false);

		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	void setItem(
		@NonNull I item,
		@NonNull TextListCardStyle<I> style,
		int typeView,
		@Nullable TextListSeeker<I> seeker
	) {
		RelativeRect relative = style.getRelativeRect(typeView);
		int h1 = relative.getItemHeight();
		MarginLayoutParams params = (MarginLayoutParams)getLayoutParams();
		boolean changedLayout = params.height != h1;
		float fh = (float)h1;
		float textSize = fh * 0.3f;
		Paint paint = new Paint();
		float radius = relative.getRadius();
		if (0 <= radius) {
			setRadius(radius);
		}
		if (changedLayout) {
			int leftm = relative.getLeftMargine();
			int topm = relative.getTopMargine();
			int rightm = relative.getRightMargine();
			int bottomm = relative.getBottomMargine();
			int w = relative.getItemWidth();
			int wText = w;
			if (hasInfo) {
				params = (MarginLayoutParams)vInfo.getLayoutParams();
				params.width = h1;
				params.height = h1;
				params.leftMargin = 0;
				params.topMargin = topm;
				params.rightMargin = rightm;
				params.bottomMargin = bottomm;
				vInfo.setLayoutParams(params);
				wText = w - h1 - rightm;
			}

			timeName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			params = (MarginLayoutParams)timeName.getLayoutParams();
			params.width = wText;
			params.height = h1;
			params.leftMargin = leftm;
			params.topMargin = topm;
			params.rightMargin = rightm;
			params.bottomMargin = bottomm;
			paint.setTextSize(textSize);
			paint.setTypeface(Typeface.MONOSPACE);
			params.topMargin = (int)(fh * 0.25f);
			params.leftMargin = (int)(fh * 0.2f);
			timeName.setLayoutParams(params);
		}

		timeName.setText(item.getText(seeker));

		int[] colors = style.getColors(typeView, item);
		timeName.setTextColor(colors[0]);
		setCardBackgroundColor(colors[1]);
	}

	void setInfoClickListener(@Nullable Runnable clicked) {
		boolean enable = clicked != null;
		hasInfo = enable;
		vInfo.setEnabled(enable);
		if (enable) {
			vInfo.setOnClickListener(v -> {
				clicked.run();
			});
		} else {
			vInfo.setOnClickListener(null);
		}
	}

}
