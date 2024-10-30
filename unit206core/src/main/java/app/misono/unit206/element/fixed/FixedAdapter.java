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

package app.misono.unit206.element.fixed;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Deprecated		// create the your adapter class.
public abstract class FixedAdapter<
	C extends FixedCardView<C, L, I>,
	L extends FixedCardLayout<C, L, I>,
	I extends FixedItem,
	V extends FixedViewHolder<C, L, I>
> extends ListAdapter<I, V> {
	private static final String TAG = "FixedAdapter";

	private final Runnable refresh;
	private final Set<V> pool;

	private L layout;

	public FixedAdapter(DiffUtil.ItemCallback<I> callbackItem, @Nullable Runnable refresh) {
		super(callbackItem);
		this.refresh = refresh;
		pool = new HashSet<>();
	}

	public FixedAdapter(AsyncDifferConfig<I> config, @Nullable Runnable refresh) {
		super(config);
		this.refresh = refresh;
		pool = new HashSet<>();
	}

	@Override
	public void onBindViewHolder(@NonNull V holder, int position) {
		C card = holder.getView();
		I item = getItem(position);
		card.setItem(item);
		holder.setLongId(item.getLongId());
		layout.layoutCard(card, position);
	}

	@Override
	public void onViewAttachedToWindow(@NonNull V holder) {
		super.onViewAttachedToWindow(holder);
		pool.add(holder);
	}

	@Override
	public void onViewDetachedFromWindow(@NonNull V holder) {
		super.onViewDetachedFromWindow(holder);
		pool.remove(holder);
	}

	@MainThread
	@Nullable
	public V getViewHolder(long id) {
		for (V holder : pool) {
			if (holder.getLongId() == id) return holder;
		}
		return null;
	}

	@Override
	public void submitList(@Nullable List<I> list) {
		if (list == null || list.size() == getItemCount()) {
			super.submitList(list);
		} else {
			super.submitList(Collections.emptyList(), () -> {
				super.submitList(list, refresh);
			});
		}
	}

	@Override
	public void submitList(@Nullable List<I> list, Runnable commitCallback) {
		if (list == null || list.size() == getItemCount()) {
			super.submitList(list, commitCallback);
		} else {
			super.submitList(Collections.emptyList(), () -> {
				super.submitList(list, () -> {
					if (refresh != null) {
						refresh.run();
					}
					if (commitCallback != null) {
						commitCallback.run();
					}
				});
			});
		}
	}

	@MainThread
	@NonNull
	public Set<V> getAttachedViewHolders() {
		return new HashSet<>(pool);
	}

	public void layoutCard(@NonNull L layout) {
		this.layout = layout;
	}

}
