package com.shizhefei.mvc;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.AbsListView;

import com.shizhefei.mvc.ILoadViewFactory.ILoadMoreView;
import com.shizhefei.mvc.ILoadViewFactory.ILoadView;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 注意 ：<br>
 * <2>PtrClassicFrameLayout 必须有Parent
 * 
 * @author zsy
 *
 * @param <DATA>
 */
public class MVCUltraHelper<DATA> extends MVCHelper<DATA> {

	public MVCUltraHelper(PtrClassicFrameLayout ptrClassicFrameLayout) {
		super(new RefreshView(ptrClassicFrameLayout));
	}

	public MVCUltraHelper(PtrClassicFrameLayout ptrClassicFrameLayout, ILoadView loadView, ILoadMoreView loadMoreView) {
		super(new RefreshView(ptrClassicFrameLayout), loadView, loadMoreView);
	}

	private static class RefreshView implements IRefreshView {
		private PtrFrameLayout mPtrFrame;

		public RefreshView(PtrFrameLayout ptrClassicFrameLayout) {
			super();
			this.mPtrFrame = ptrClassicFrameLayout;
			if (mPtrFrame.getParent() == null) {
				throw new RuntimeException("PtrClassicFrameLayout 必须有Parent");
			}
			mPtrFrame.setPtrHandler(ptrHandler);
		}

		@Override
		public View getContentView() {
			return mPtrFrame.getContentView();
		}

		@Override
		public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
			this.onRefreshListener = onRefreshListener;
		}

		private OnRefreshListener onRefreshListener;

		@Override
		public void showRefreshComplete() {
			mPtrFrame.refreshComplete();
		}

		@Override
		public void showRefreshing() {
			mPtrFrame.setPtrHandler(null);
			mPtrFrame.autoRefresh(true,150);
			mPtrFrame.setPtrHandler(ptrHandler);
		}

		@Override
		public View getSwitchView() {
			return mPtrFrame;
		}

		private PtrHandler ptrHandler = new PtrHandler() {
			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				if (onRefreshListener != null) {
					onRefreshListener.onRefresh();
				}
			}

			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return checkContentCanBePulledDown(frame, content, header);
			}
		};

	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static boolean canChildScrollUp(View mTarget) {
		if (Build.VERSION.SDK_INT < 14) {
			if (mTarget instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) mTarget;
				return absListView.getChildCount() > 0
						&& (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
			} else {
				return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
			}
		} else {
			return ViewCompat.canScrollVertically(mTarget, -1);
		}
	}

	/**
	 * Default implement for check can perform pull to refresh
	 *
	 * @param frame
	 * @param content
	 * @param header
	 * @return
	 */
	public static boolean checkContentCanBePulledDown(PtrFrameLayout frame, View content, View header) {
		return !canChildScrollUp(content);
	}

}
