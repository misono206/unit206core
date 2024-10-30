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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import app.misono.unit206.callback.CallbackObjectT;

class TableAdapter<T extends TableCardItem> extends ListAdapter<T, TableViewHolder> {
	private static final String TAG = "TableAdapter";

	private final TableParam param;

	private CallbackObjectT<T> listener;

	TableAdapter(
		@NonNull TableParam param,
		@NonNull DiffUtil.ItemCallback<T> diffCallback
	) {
		super(diffCallback);
		this.param = param;
	}

	public void setListener(@Nullable CallbackObjectT<T> listener) {
		this.listener = listener;
	}

	@NonNull
	@Override
	public TableViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
		return new TableViewHolder(viewGroup);
	}

	@Override
	public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
		TableCardView parent = holder.getView();
		parent.removeAllViews();
		param.setBackgroundColor(parent, position);
		T item = getItem(position);
		Context context = parent.getContext();
		int x = 0;
		int n = param.getColumnCount();
		for (int i = 0; i < n; i++) {
			View view = item.createView(context, position, i, param.getTableTdParam(position, i));
			param.setPadding(view, position, i);
			ViewGroup.MarginLayoutParams p = param.createLayoutParams(position, i);
			p.leftMargin += x;
			parent.addView(view, p);
			x += p.width;	// TODO: padding????
		}
		if (listener != null) {
			parent.setOnClickListener(v -> {
				listener.callback(item);
			});
		}
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

}
