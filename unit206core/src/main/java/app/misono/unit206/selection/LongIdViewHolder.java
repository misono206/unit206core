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

package app.misono.unit206.selection;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class LongIdViewHolder extends RecyclerView.ViewHolder implements LongId {
	public LongIdViewHolder(@NonNull SelectableCardView itemView) {
		super(itemView);
	}

	@Override
	public long getLongId() {
		return ((LongId)itemView).getLongId();
	}

	public void onViewAttachedToWindow() {
		((SelectableCardView)itemView).onViewAttachedToWindow();
	}

	public void onViewDetachedFromWindow() {
		((SelectableCardView)itemView).onViewDetachedFromWindow();
	}

}
