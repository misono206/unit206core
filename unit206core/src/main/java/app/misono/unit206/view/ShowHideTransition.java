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

package app.misono.unit206.view;

import android.animation.LayoutTransition;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

public class ShowHideTransition {
	private final LayoutTransition transition;

	public ShowHideTransition(int msec) {
		transition = new LayoutTransition();
		transition.setDuration(msec);
		transition.addTransitionListener(new LayoutTransition.TransitionListener() {
			@Override
			public void startTransition(
				LayoutTransition transition,
				ViewGroup container,
				View view,
				int transitionType
			) {
				switch (transitionType) {
				case LayoutTransition.APPEARING:
					view.setVisibility(View.VISIBLE);
					break;
				case LayoutTransition.DISAPPEARING:
					view.setVisibility(View.GONE);
					break;
				}
			}

			@Override
			public void endTransition(
				LayoutTransition transition,
				ViewGroup container,
				View view,
				int transitionType
			) {
				// nop
			}
		});
	}

	public void show(@NonNull LinearLayout parent, @NonNull View target, boolean show) {
		if (show) {
			transition.showChild(parent, target, View.GONE);
		} else {
			transition.hideChild(parent, target, View.GONE);
		}
	}

	public void show(@NonNull LinearLayout parent, @NonNull View target) {
		transition.showChild(parent, target, View.GONE);
	}

	public void hide(@NonNull LinearLayout parent, @NonNull View target) {
		transition.hideChild(parent, target, View.GONE);
	}

}
