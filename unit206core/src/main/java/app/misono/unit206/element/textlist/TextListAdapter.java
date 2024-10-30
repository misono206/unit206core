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

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class TextListAdapter<I extends TextListItem> extends ListAdapter<I, TextListViewHolder> {
	private static final String TAG = "TextListAdapter";

	private final TextListCardStyle<I> style;
	private final TextListCallback<I> cbClicked;
	private final TextListCallback<I> cbInfo;
	private final TextListSeeker<I> seeker;

	private float radius;
	private int typeView;

	public TextListAdapter(
		@NonNull TextListCardStyle<I> style,
		@Nullable TextListSeeker<I> seeker,
		@Nullable TextListCallback<I> cbClicked,
		@Nullable TextListCallback<I> cbInfo
	) {
		super(new DiffUtil.ItemCallback<I>() {
			@Override
			public boolean areItemsTheSame(@NonNull I item1, @NonNull I item2) {
				return item1.getLongId() == item2.getLongId();
			}

			@Override
			public boolean areContentsTheSame(@NonNull I item1, @NonNull I item2) {
				return item1.getModifiedTime() == item2.getModifiedTime();
			}
		});
		this.style = style;
		this.seeker = seeker;
		this.cbClicked = cbClicked;
		this.cbInfo = cbInfo;
		radius = -1f;
	}

	@NonNull
	@Override
	public TextListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
		return new TextListViewHolder(viewGroup, viewType);
	}

	@Override
	public void onBindViewHolder(@NonNull TextListViewHolder holder, int position) {
		I item = getItem(position);
		if (item != null) {
			TextListCardView<I> view = holder.getView();
			view.setItem(item, style, holder.getViewType(), seeker);
			if (cbClicked != null) {
				view.setOnClickListener(v -> {
					cbClicked.callback(item);
				});
			}
			if (cbInfo != null && item.hasInfo()) {
				view.setInfoClickListener(() -> {
					cbInfo.callback(item);
				});
			}
		}
	}

	@Override
	public int getItemViewType(int position) {
		return typeView;
	}

	public void setViewType(int typeView) {
		this.typeView = typeView;
	}

	public void changeLayout(int w, int h) {
		style.changeLayout(w, h);
	}
}
