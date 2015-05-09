package com.shizhefei.mvc;

import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.shizhefei.mvc.ILoadViewFactory.ILoadMoreView;
import com.shizhefei.mvc.ILoadViewFactory.ILoadView;

public class MVCPullrefshHelper<DATA> extends MVCHelper<DATA> {

	public MVCPullrefshHelper(PullToRefreshAdapterViewBase<?> pullToRefreshAdapterViewBase) {
		super(new RefreshView(pullToRefreshAdapterViewBase));
	}

	public MVCPullrefshHelper(PullToRefreshAdapterViewBase<?> pullToRefreshAdapterViewBase, ILoadView loadView, ILoadMoreView loadMoreView) {
		super(new RefreshView(pullToRefreshAdapterViewBase), loadView, loadMoreView);
	}

	private static class RefreshView implements IRefreshView {
		private PullToRefreshAdapterViewBase<? extends View> pullToRefreshAdapterViewBase;

		public RefreshView(PullToRefreshAdapterViewBase<? extends View> pullToRefreshAdapterViewBase) {
			this.pullToRefreshAdapterViewBase = pullToRefreshAdapterViewBase;
			pullToRefreshAdapterViewBase.setMode(Mode.PULL_FROM_START);
			pullToRefreshAdapterViewBase.setOnRefreshListener(new OnRefreshListener211());
		}

		@Override
		public View getContentView() {
			return pullToRefreshAdapterViewBase.getRefreshableView();
		}

		@Override
		public void showRefreshComplete() {
			pullToRefreshAdapterViewBase.onRefreshComplete();
		}

		@Override
		public void showRefreshing() {
			pullToRefreshAdapterViewBase.showHeadRefreshing();
		}

		private class OnRefreshListener211<T extends View> implements OnRefreshListener2<T> {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<T> refreshView) {
				if (onRefreshListener != null) {
					onRefreshListener.onRefresh();
				}

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<T> refreshView) {

			}

		}

		private OnRefreshListener onRefreshListener;

		@Override
		public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
			this.onRefreshListener = onRefreshListener;
		}

		@Override
		public View getSwitchView() {
			return pullToRefreshAdapterViewBase.getRefreshableView();
		}

	}

}
