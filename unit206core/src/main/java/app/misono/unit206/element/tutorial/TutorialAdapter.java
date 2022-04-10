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

package app.misono.unit206.element.tutorial;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

class TutorialAdapter<T extends TutorialItem, V extends View> extends ListAdapter<T, TutorialViewHolder> {
	private static final String TAG = "TutorialAdapter";

	private final TutorialElement.ViewCreator<T, V> creator;
	private final TutorialCardLayout<V> layout;

	private int wCard;
	private int hCard;

	TutorialAdapter(
		@NonNull DiffUtil.ItemCallback<T> diffCallback,
		@NonNull TutorialElement.ViewCreator<T, V> creator,
		@NonNull TutorialCardLayout<V> layout
	) {
		super(diffCallback);
		this.creator = creator;
		this.layout = layout;
	}

	@NonNull
	@Override
	public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
		return new TutorialViewHolder(viewGroup);
	}

	@Override
	public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
		TutorialCardView parent = holder.getView();
		parent.removeAllViews();
		T item = getItem(position);
		Context context = parent.getContext();
		V view = creator.createView(context, item);
		layout.layout(view);
		parent.addView(view, wCard, hCard);
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	public void setCardPixelSize(int wCard, int hCard) {
		this.wCard = wCard;
		this.hCard = hCard;
		layout.setPixelSize(wCard, hCard);
	}

}
