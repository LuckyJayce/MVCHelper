package com.shizhefei.mvc;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import android.view.View;

import com.shizhefei.mvc.ILoadViewFactory.ILoadMoreView;
import com.shizhefei.mvc.ILoadViewFactory.ILoadView;

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
			mPtrFrame.setPtrHandler(new PtrHandler() {
				@Override
				public void onRefreshBegin(PtrFrameLayout frame) {
					if (onRefreshListener != null) {
						onRefreshListener.onRefresh();
					}
				}

				@Override
				public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
					return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
				}
			});
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
			mPtrFrame.autoRefresh(true, 10000000);
		}

		@Override
		public View getSwitchView() {
			return mPtrFrame;
		}

	}

}
