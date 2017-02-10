package com.shizhefei.mvc.viewhandler;

import android.view.View;
import android.view.View.OnClickListener;

import com.shizhefei.mvc.ILoadViewFactory.ILoadMoreView;
import com.shizhefei.mvc.MVCHelper.OnScrollBottomListener;

/**
 * 用于设置view的适配器,用于添加加载更多的FootView,滑动事件触发加载更多
 */
public interface ViewHandler {

	/**
	 *
	 * @param contentView
	 * @param viewAdapter
	 * @param loadMoreView
	 * @param onClickLoadMoreListener
     * @return 是否有 init ILoadMoreView
     */
	boolean handleSetAdapter(View contentView, Object viewAdapter, ILoadMoreView loadMoreView, OnClickListener onClickLoadMoreListener);

	void setOnScrollBottomListener(View contentView, OnScrollBottomListener onScrollBottomListener);

}
