package com.shizhefei.mvc;

import android.view.View;

import com.shizhefei.mvc.ILoadViewFactory.ILoadMoreView;
import com.shizhefei.mvc.ILoadViewFactory.ILoadView;
import com.shizhefei.view.coolrefreshview.CoolRefreshView;
import com.shizhefei.view.coolrefreshview.OnPullListener;
import com.shizhefei.view.coolrefreshview.SimpleOnPullListener;

/**
 * 注意 ：<br>
 * CoolRefreshView在 xml布局外面必须有一层layout
 *
 * @param <DATA>
 * @author zsy
 */
public class MVCCoolHelper<DATA> extends MVCHelper<DATA> {

    public MVCCoolHelper(CoolRefreshView coolRefreshView) {
        super(new RefreshView(coolRefreshView));
    }

    public MVCCoolHelper(CoolRefreshView coolRefreshView, ILoadView loadView, ILoadMoreView loadMoreView) {
        super(new RefreshView(coolRefreshView), loadView, loadMoreView);
    }

    private static class RefreshView implements IRefreshView {
        private CoolRefreshView coolRefreshView;

        public RefreshView(CoolRefreshView coolRefreshView) {
            this.coolRefreshView = coolRefreshView;
            coolRefreshView.addOnPullListener(onPullListener);
        }

        @Override
        public View getContentView() {
            return coolRefreshView.getContentView();
        }

        @Override
        public View getSwitchView() {
            return coolRefreshView;
        }

        private OnRefreshListener onRefreshListener;

        @Override
        public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
            this.onRefreshListener = onRefreshListener;
        }

        @Override
        public void showRefreshComplete() {
            coolRefreshView.setRefreshing(false);
        }

        @Override
        public void showRefreshing() {
            coolRefreshView.setRefreshing(true);
        }

        private OnPullListener onPullListener = new SimpleOnPullListener() {
            @Override
            public void onRefreshing(CoolRefreshView coolRefreshView) {
                if (onRefreshListener != null) {
                    onRefreshListener.onRefresh();
                }
            }
        };
    }
}
