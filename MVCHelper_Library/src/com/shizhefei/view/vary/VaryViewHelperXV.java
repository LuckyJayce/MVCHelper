/*
Copyright 2015 shizhefei（LuckyJayce）
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.shizhefei.view.vary;

import android.view.View;

/**
 * 用于切换布局,用一个新的布局覆盖在原布局之上,原先的布局被切换时候就被掩藏，restoreView时显示
 * 
 * @author LuckyJayce
 *
 */
public class VaryViewHelperXV extends VaryViewHelperX {

	public VaryViewHelperXV(View view) {
		super(view);
		this.view = view;
	}

	private View view;

	@Override
	public void showLayout(View view) {
		super.showLayout(view);
		this.view.setVisibility(View.GONE);
	}

	@Override
	public void showLayout(int layoutId) {
		super.showLayout(layoutId);
		this.view.setVisibility(View.GONE);
	}

	@Override
	public void restoreView() {
		super.restoreView();
		this.view.setVisibility(View.VISIBLE);
	}
}
