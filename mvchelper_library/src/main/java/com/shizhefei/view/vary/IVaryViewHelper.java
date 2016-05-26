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

import android.content.Context;
import android.view.View;

public interface IVaryViewHelper {

	public abstract View getCurrentLayout();

	public abstract void restoreView();

	public abstract void showLayout(View view);

	public abstract void showLayout(int layoutId);

	public abstract View inflate(int layoutId);

	public abstract Context getContext();

	public abstract View getView();

}