package com.shizhefei.mvc;

import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.shizhefei.mvc.ILoadViewFactory.ILoadMoreView;
import com.shizhefei.mvc.ILoadViewFactory.ILoadView;

public class MVCPullrefshHelper<DATA> extends MVCHelper<DATA> {

	public MVCPullrefshHelper(PullToRefreshBase<?> pullToRefreshAdapterViewBase) {
		super(new RefreshView(pullToRefreshAdapterViewBase));
	}

	public MVCPullrefshHelper(PullToRefreshBase<?> pullToRefreshAdapterViewBase, ILoadView loadView, ILoadMoreView loadMoreView) {
		super(new RefreshView(pullToRefreshAdapterViewBase), loadView, loadMoreView);
	}

	private static class RefreshView implements IRefreshView {
		private PullToRefreshBase<? extends View> pullToRefreshAdapterViewBase;
		private OnRefreshListener211 onRefreshListener211 = new OnRefreshListener211();
		private OnRefreshListenerNone onRefreshListenerNone = new OnRefreshListenerNone();

		public RefreshView(PullToRefreshBase<? extends View> pullToRefreshAdapterViewBase) {
			this.pullToRefreshAdapterViewBase = pullToRefreshAdapterViewBase;
			pullToRefreshAdapterViewBase.setMode(Mode.PULL_FROM_START);
			pullToRefreshAdapterViewBase.setOnRefreshListener(onRefreshListener211);
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
			//避免触发刷新监听
			pullToRefreshAdapterViewBase.setOnRefreshListener(onRefreshListenerNone);
			pullToRefreshAdapterViewBase.setRefreshing();
			pullToRefreshAdapterViewBase.setOnRefreshListener(onRefreshListener211);
		}

		private class OnRefreshListenerNone<T extends View> implements OnRefreshListener2<T> {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<T> refreshView) {

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<T> refreshView) {

			}

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
