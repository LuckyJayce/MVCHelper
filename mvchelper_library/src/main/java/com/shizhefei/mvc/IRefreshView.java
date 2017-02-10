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
package com.shizhefei.mvc;

import android.view.View;

public interface IRefreshView {

	/**
	 * 内容布局
	 * 
	 * @return
	 */
	View getContentView();

	/**
	 * 通过替换这个View实现切换失败，成功，无数据布局
	 * 
	 * @return
	 */
	View getSwitchView();

	/**
	 * 设置刷新事件
	 * 
	 * @param onRefreshListener
	 */
	void setOnRefreshListener(OnRefreshListener onRefreshListener);

	/**
	 * 显示刷新完成
	 */
	void showRefreshComplete();

	/**
	 * 显示正在刷新
	 */
	void showRefreshing();

	/**
	 * 刷新监听
	 * 
	 * @author LuckyJayce
	 *
	 */
	interface OnRefreshListener {
		void onRefresh();
	}

}
