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
import android.view.View.OnClickListener;

/**
 * 布局切换工厂
 * 
 * @author LuckyJayce
 *
 */
public interface ILoadViewFactory {

	public ILoadMoreView madeLoadMoreView();

	public ILoadView madeLoadView();

	/**
	 * 切换加载中，加载失败等布局
	 * 
	 * @author LuckyJayce
	 *
	 */
	public static interface ILoadView {

		/**
		 * 初始化
		 * 
		 * @param mListView
		 * @param onClickRefreshListener
		 *            ，刷新的点击事件，需要点击刷新的按钮都可以设置这个监听
		 */
		public void init(View switchView, OnClickListener onClickRefreshListener);

		/**
		 * 显示加载中
		 */
		public void showLoading();

		/**
		 * 显示加载失败
		 * @param e 
		 */
		public void showFail(Exception e);

		/**
		 * 显示空数据布局
		 */
		public void showEmpty();

		/**
		 * 有数据的时候，toast提示失败
		 * @param e 
		 */
		public void tipFail(Exception e);

		/**
		 * 显示原先的布局
		 */
		public void restore();

	}

	/**
	 * ListView底部加载更多的布局切换
	 * 
	 * @author LuckyJayce
	 *
	 */
	public interface ILoadMoreView {

		/**
		 * 初始化
		 * 
		 * @param mListView
		 * @param onClickLoadMoreListener
		 *            加载更多的点击事件，需要点击调用加载更多的按钮都可以设置这个监听
		 */
		public void init(FootViewAdder footViewHolder, OnClickListener onClickLoadMoreListener);

		/**
		 * 显示普通保布局
		 */
		public void showNormal();

		/**
		 * 显示已经加载完成，没有更多数据的布局
		 */
		public void showNomore();

		/**
		 * 显示正在加载中的布局
		 */
		public void showLoading();

		/**
		 * 显示加载失败的布局
		 * @param e 
		 */
		public void showFail(Exception e);

	}

	public static interface FootViewAdder {

		public View addFootView(View view);

		public View addFootView(int layoutId);
		
		public View getContentView();

	}

}
