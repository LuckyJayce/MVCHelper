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

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.shizhefei.mvc.ILoadViewFactory.ILoadMoreView;
import com.shizhefei.mvc.ILoadViewFactory.ILoadView;
import com.shizhefei.mvc.IRefreshView.OnRefreshListener;
import com.shizhefei.mvc.imp.DefaultLoadViewFactory;
import com.shizhefei.mvc.viewhandler.ListViewHandler;
import com.shizhefei.mvc.viewhandler.RecyclerViewHandler;
import com.shizhefei.mvc.viewhandler.ViewHandler;
import com.shizhefei.task.Code;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ISuperTask;
import com.shizhefei.task.ITask;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.task.imp.SimpleCallback;
import com.shizhefei.utils.NetworkUtils;

/**
 * <h1>下拉刷新，上滑加载更多的控件的辅助类</h1><br>
 * <br>
 * 刷新，加载更多规则<br>
 * 当用户下拉刷新时，会取消掉当前的刷新，以及加载更多的任务<br>
 * 当用户加载更多的时候，如果有已经正在刷新或加载更多是不会再执行加载更多的操作。<br>
 * <br>
 * 注意:记得在Activity的Ondestroy方法调用destory <br>
 * 要添加 android.permission.ACCESS_NETWORK_STATE 权限，这个用来检测是否有网络
 *
 * @param <DATA>
 * @author LuckyJayce
 */
public class MVCHelper<DATA> {
    private IDataAdapter<DATA> dataAdapter;
    private IRefreshView refreshView;
    private ISuperTask<DATA> dataSource;
    private View contentView;
    private Context context;
    private MOnStateChangeListener<DATA> onStateChangeListener = new MOnStateChangeListener<DATA>();
    private RequestHandle requestHandle;
    private TaskHelper<DATA> taskHelper;
    private long loadDataTime = -1;
    /**
     * 是否还有更多数据。如果服务器返回的数据为空的话，就说明没有更多数据了，也就没必要自动加载更多数据
     */
    private boolean hasMoreData = true;
    /***
     * 加载更多的时候是否事先检查网络是否可用。
     */
    private boolean needCheckNetwork = true;
    private ILoadView mLoadView;
    private ILoadMoreView mLoadMoreView;
    public static ILoadViewFactory loadViewFactory = new DefaultLoadViewFactory();

    private ListViewHandler listViewHandler = new ListViewHandler();

    private RecyclerViewHandler recyclerViewHandler = new RecyclerViewHandler();
    private Handler handler;

    public MVCHelper(IRefreshView refreshView) {
        this(refreshView, loadViewFactory.madeLoadView(), loadViewFactory.madeLoadMoreView());
    }

    public MVCHelper(IRefreshView refreshView, ILoadView loadView) {
        this(refreshView, loadView, null);
    }

    public MVCHelper(IRefreshView refreshView, ILoadView loadView, ILoadMoreView loadMoreView) {
        super();
        this.context = refreshView.getContentView().getContext().getApplicationContext();
        this.autoLoadMore = true;
        this.refreshView = refreshView;
        contentView = refreshView.getContentView();
        contentView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        refreshView.setOnRefreshListener(onRefreshListener);
        mLoadView = loadView;
        mLoadMoreView = loadMoreView;
        taskHelper = new TaskHelper<>();
        mLoadView.init(refreshView.getSwitchView(), onClickRefresListener);
        handler = new Handler();
    }

    /**
     * 设置LoadView的factory，用于创建使用者自定义的加载失败，加载中，加载更多等布局
     *
     * @param fractory
     */
    public static void setLoadViewFractory(ILoadViewFactory fractory) {
        loadViewFactory = fractory;
    }

    /**
     * 如果不是网络请求的业务可以把这个设置为false
     *
     * @param needCheckNetwork
     */
    public void setNeedCheckNetwork(boolean needCheckNetwork) {
        this.needCheckNetwork = needCheckNetwork;
    }

    /**
     * 设置数据源，用于加载数据
     *
     * @param dataSource
     */
    public void setDataSource(IDataSource<DATA> dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 设置数据源，用于加载数据
     *
     * @param asyncDataSource
     */
    public void setDataSource(IAsyncDataSource<DATA> asyncDataSource) {
        this.dataSource = asyncDataSource;
    }

    /**
     * 设置数据源，用于加载数据
     *
     * @param task
     */
    public void setDataSource(IAsyncTask<DATA> task) {
        this.dataSource = task;
    }

    /**
     * 设置数据源，用于加载数据
     *
     * @param task
     */
    public void setDataSource(ITask<DATA> task) {
        this.dataSource = task;
    }

    /**
     * 设置适配器，用于显示数据
     *
     * @param adapter 既是IDataAdapter，也是ListView，RecyclerView等view的适配器
     *                如果ContentView是ListView那么viewAdapter就要继承于ListAdapter，如果是RecyclerView，那么viewAdapter就要继承于RecyclerView.Adapter
     */
    public void setAdapter(IDataAdapter<DATA> adapter) {
        setAdapter2(adapter, adapter);
    }

    /**
     * 设置适配器，用于显示数据
     *
     * @param adapter     既是IDataAdapter，也是ListView，RecyclerView等view的适配器
     *                    如果ContentView是ListView那么viewAdapter就要继承于ListAdapter，如果是RecyclerView，那么viewAdapter就要继承于RecyclerView.Adapter
     * @param viewHandler 用于设置view的适配器,用于添加加载更多的FootView,滑动事件触发加载更多
     */
    public void setAdapter(IDataAdapter<DATA> adapter, ViewHandler viewHandler) {
        setAdapter2(adapter, adapter, viewHandler);
    }

    /**
     * 分别设置两种适配器，viewAdapter设置到对应的view上，IDataAdapter 处理显示的逻辑
     *
     * @param viewAdapter ListView，RecyclerView等view的适配器
     *                    如果ContentView是ListView那么viewAdapter就要继承于ListAdapter，如果是RecyclerView，那么viewAdapter就要继承于RecyclerView.Adapter
     * @param dataAdapter 接收数据，并显示数据的适配器
     */
    public void setAdapter2(Object viewAdapter, IDataAdapter<DATA> dataAdapter) {
        if (contentView instanceof ListView) {
            setAdapter2(viewAdapter, dataAdapter, listViewHandler);
        } else if (contentView instanceof RecyclerView) {
            setAdapter2(viewAdapter, dataAdapter, recyclerViewHandler);
        } else {
            setAdapter2(viewAdapter, dataAdapter, null);
        }
    }

    /**
     * 分别设置两种适配器，viewAdapter设置到对应的view上，IDataAdapter 处理显示的逻辑
     *
     * @param viewAdapter 如果ContentView是ListView那么viewAdapter就要继承于ListAdapter，如果是RecyclerView，那么viewAdapter就要继承于RecyclerView.Adapter
     * @param dataAdapter 数据显示adapter
     * @param viewHandler 用于设置view的适配器,用于添加加载更多的FootView,滑动事件触发加载更多
     */
    public void setAdapter2(Object viewAdapter, IDataAdapter<DATA> dataAdapter, ViewHandler viewHandler) {
        hasInitLoadMoreView = false;
        if (viewHandler != null) {
            View view = getContentView();
            hasInitLoadMoreView = viewHandler.handleSetAdapter(view, viewAdapter, mLoadMoreView, onClickLoadMoreListener);
            viewHandler.setOnScrollBottomListener(view, onScrollBottomListener);
        }
        this.dataAdapter = dataAdapter;
    }

    private boolean hasInitLoadMoreView = false;

    /**
     * 设置状态监听，监听开始刷新，刷新成功，开始加载更多，加载更多成功
     *
     * @param onStateChangeListener
     */
    public void setOnStateChangeListener(OnStateChangeListener<DATA> onStateChangeListener) {
        this.onStateChangeListener.setOnStateChangeListener(onStateChangeListener);
    }

    /**
     * 设置状态监听，监听开始刷新，刷新成功
     *
     * @param onRefreshStateChangeListener
     */
    public void setOnStateChangeListener(OnRefreshStateChangeListener<DATA> onRefreshStateChangeListener) {
        this.onStateChangeListener.setOnRefreshStateChangeListener(onRefreshStateChangeListener);
    }

    /**
     * 设置状态监听，监听开始加载更多，加载更多成功
     *
     * @param onLoadMoreStateChangeListener
     */
    public void setOnStateChangeListener(OnLoadMoreStateChangeListener<DATA> onLoadMoreStateChangeListener) {
        this.onStateChangeListener.setOnLoadMoreStateChangeListener(onLoadMoreStateChangeListener);
    }

    /**
     * 刷新，开启异步线程，并且显示加载中的界面，当数据加载完成自动还原成加载完成的布局，并且刷新列表数据
     */
    public void refresh() {
        if (dataAdapter == null || (dataSource == null)) {
            if (refreshView != null) {
                refreshView.showRefreshComplete();
            }
            return;
        }
        if (requestHandle != null) {
            requestHandle.cancle();
            requestHandle = null;
        }
        if (dataSource instanceof IDataSource) {
            requestHandle = TaskHelper.createExecutor((IDataSource<DATA>) dataSource, true, refreshCallback).execute();
        } else if (dataSource instanceof IAsyncDataSource) {
            requestHandle = TaskHelper.createExecutor((IAsyncDataSource<DATA>) dataSource, true, refreshCallback).execute();
        } else if (dataSource instanceof ITask) {
            requestHandle = TaskHelper.createExecutor((ITask<DATA>) dataSource, refreshCallback).execute();
        } else {
            requestHandle = TaskHelper.createExecutor((IAsyncTask<DATA>) dataSource, refreshCallback).execute();
        }
    }

    public void loadMore() {
        if (isLoading()) {
            return;
        }
        if (dataAdapter.isEmpty()) {
            refresh();
            return;
        }
        if (dataAdapter == null || (dataSource == null)) {
            if (refreshView != null) {
                refreshView.showRefreshComplete();
            }
            return;
        }
        if (requestHandle != null) {
            requestHandle.cancle();
            requestHandle = null;
        }
        if (dataSource instanceof IDataSource) {
            requestHandle = TaskHelper.createExecutor((IDataSource<DATA>) dataSource, false, loadMoreCallback).execute();
        } else if (dataSource instanceof IAsyncDataSource) {
            requestHandle = TaskHelper.createExecutor((IAsyncDataSource<DATA>) dataSource, false, loadMoreCallback).execute();
        }
    }

    /**
     * 做销毁操作，比如关闭正在加载数据的异步线程等
     */
    public void destory() {
        if (requestHandle != null) {
            requestHandle.cancle();
            requestHandle = null;
        }
        taskHelper.destroy();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 是否正在加载中
     *
     * @return
     */
    public boolean isLoading() {
        return requestHandle != null;
    }

    private SimpleCallback<DATA> refreshCallback = new SimpleCallback<DATA>() {
        public Runnable showRefreshing;

        @Override
        public void onPreExecute(Object task) {
            if (hasInitLoadMoreView && mLoadMoreView != null) {
                mLoadMoreView.showNormal();
            }
            if (task instanceof IDataCacheLoader) {
                @SuppressWarnings("unchecked")
                IDataCacheLoader<DATA> cacheLoader = (IDataCacheLoader<DATA>) task;
                DATA data = cacheLoader.loadCache(dataAdapter.isEmpty());
                if (data != null) {
                    dataAdapter.notifyDataChanged(data, true);
                }
            }
            if (dataAdapter.isEmpty()) {
                mLoadView.showLoading();
            } else {
                mLoadView.restore();
            }
            handler.post(showRefreshing = new Runnable() {

                @Override
                public void run() {
                    if (dataAdapter.isEmpty()) {
                        refreshView.showRefreshComplete();
                    } else {
                        refreshView.showRefreshing();
                    }
                }
            });
            if (onStateChangeListener != null) {
                onStateChangeListener.onStartRefresh(dataAdapter);
            }
        }

        @Override
        public void onPostExecute(Object task, Code code, Exception exception, DATA data) {
            handler.removeCallbacks(showRefreshing);
            refreshView.showRefreshComplete();
            if (code == Code.SUCCESS && data == null) {
                code = Code.EXCEPTION;
                exception = new Exception("数据不能返回null");
            }
            switch (code) {
                case SUCCESS:
                    requestHandle = null;
                    loadDataTime = System.currentTimeMillis();
                    dataAdapter.notifyDataChanged(data, true);
                    if (dataAdapter.isEmpty()) {
                        mLoadView.showEmpty();
                    } else {
                        mLoadView.restore();
                    }
                    hasMoreData = hasMore(task);
                    if (hasInitLoadMoreView && mLoadMoreView != null) {
                        if (!hasMoreData) {
                            mLoadMoreView.showNormal();
                        } else {
                            mLoadMoreView.showNomore();
                        }
                    }
                    break;
                case EXCEPTION:
                    requestHandle = null;
                    if (dataAdapter.isEmpty()) {
                        mLoadView.showFail(exception);
                    } else {
                        mLoadView.tipFail(exception);
                    }
                    break;
                case CANCEL:
                    break;
            }
            if (onStateChangeListener != null) {
                onStateChangeListener.onEndRefresh(dataAdapter, data);
            }
        }
    };

    private SimpleCallback<DATA> loadMoreCallback = new SimpleCallback<DATA>() {
        @Override
        public void onPreExecute(Object task) {
            super.onPreExecute(task);
            if (onStateChangeListener != null) {
                onStateChangeListener.onStartLoadMore(dataAdapter);
            }
            if (hasInitLoadMoreView && mLoadMoreView != null) {
                mLoadMoreView.showLoading();
            }
        }

        @Override
        public void onPostExecute(Object task, Code code, Exception exception, DATA data) {
            if (code == Code.SUCCESS && data == null) {
                code = Code.EXCEPTION;
                exception = new Exception("数据不能返回null");
            }
            switch (code) {
                case SUCCESS:
                    requestHandle = null;
                    dataAdapter.notifyDataChanged(data, false);
                    if (dataAdapter.isEmpty()) {
                        mLoadView.showEmpty();
                    } else {
                        mLoadView.restore();
                    }
                    hasMoreData = hasMore(task);
                    if (hasInitLoadMoreView && mLoadMoreView != null) {
                        if (hasMoreData) {
                            mLoadMoreView.showNormal();
                        } else {
                            mLoadMoreView.showNomore();
                        }
                    }
                    break;
                case EXCEPTION:
                    requestHandle = null;
                    mLoadView.tipFail(exception);
                    if (hasInitLoadMoreView && mLoadMoreView != null) {
                        mLoadMoreView.showFail(exception);
                    }
                    break;
                case CANCEL:
                    break;
            }
            if (onStateChangeListener != null) {
                onStateChangeListener.onEndLoadMore(dataAdapter, data);
            }
        }
    };

    private boolean hasMore(Object dataSource) {
        if (dataSource instanceof IAsyncDataSource) {
            return ((IAsyncDataSource) dataSource).hasMore();
        } else if (dataSource instanceof IDataSource) {
            return ((IDataSource) dataSource).hasMore();
        }
        return false;
    }

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {

        @Override
        public void onRefresh() {
            refresh();
        }
    };

    @SuppressWarnings("unchecked")
    public <T extends View> T getContentView() {
        return (T) refreshView.getContentView();
    }

    /**
     * 获取上次刷新数据的时间（数据成功的加载），如果数据没有加载成功过，那么返回-1
     *
     * @return
     */
    public long getLoadDataTime() {
        return loadDataTime;
    }

    public IDataAdapter<DATA> getAdapter() {
        return dataAdapter;
    }

    public Object getDataSource() {
        return (IDataSource<DATA>) dataSource;
    }

    public ILoadView getLoadView() {
        return mLoadView;
    }

    public ILoadMoreView getLoadMoreView() {
        return mLoadMoreView;
    }

    public void setAutoLoadMore(boolean autoLoadMore) {
        this.autoLoadMore = autoLoadMore;
    }

    private boolean autoLoadMore = true;

    public boolean isAutoLoadMore() {
        return autoLoadMore;
    }

    private OnClickListener onClickLoadMoreListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            loadMore();
        }
    };

    private OnClickListener onClickRefresListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            refresh();
        }
    };

    protected IRefreshView getRefreshView() {
        return refreshView;
    }

    /**
     * 加载监听
     *
     * @param <DATA>
     * @author zsy
     */
    private static class MOnStateChangeListener<DATA> implements OnStateChangeListener<DATA> {
        private OnStateChangeListener<DATA> onStateChangeListener;
        private OnRefreshStateChangeListener<DATA> onRefreshStateChangeListener;
        private OnLoadMoreStateChangeListener<DATA> onLoadMoreStateChangeListener;

        public void setOnStateChangeListener(OnStateChangeListener<DATA> onStateChangeListener) {
            this.onStateChangeListener = onStateChangeListener;
        }

        public void setOnRefreshStateChangeListener(OnRefreshStateChangeListener<DATA> onRefreshStateChangeListener) {
            this.onRefreshStateChangeListener = onRefreshStateChangeListener;
        }

        public void setOnLoadMoreStateChangeListener(OnLoadMoreStateChangeListener<DATA> onLoadMoreStateChangeListener) {
            this.onLoadMoreStateChangeListener = onLoadMoreStateChangeListener;
        }

        @Override
        public void onStartRefresh(IDataAdapter<DATA> adapter) {
            if (onStateChangeListener != null) {
                onStateChangeListener.onStartRefresh(adapter);
            } else if (onRefreshStateChangeListener != null) {
                onRefreshStateChangeListener.onStartRefresh(adapter);
            }
        }

        @Override
        public void onEndRefresh(IDataAdapter<DATA> adapter, DATA result) {
            if (onStateChangeListener != null) {
                onStateChangeListener.onEndRefresh(adapter, result);
            } else if (onRefreshStateChangeListener != null) {
                onRefreshStateChangeListener.onEndRefresh(adapter, result);
            }
        }

        @Override
        public void onStartLoadMore(IDataAdapter<DATA> adapter) {
            if (onStateChangeListener != null) {
                onStateChangeListener.onStartLoadMore(adapter);
            } else if (onLoadMoreStateChangeListener != null) {
                onLoadMoreStateChangeListener.onStartLoadMore(adapter);
            }
        }

        @Override
        public void onEndLoadMore(IDataAdapter<DATA> adapter, DATA result) {
            if (onStateChangeListener != null) {
                onStateChangeListener.onEndLoadMore(adapter, result);
            } else if (onLoadMoreStateChangeListener != null) {
                onLoadMoreStateChangeListener.onEndLoadMore(adapter, result);
            }
        }

    }

    private OnScrollBottomListener onScrollBottomListener = new OnScrollBottomListener() {

        @Override
        public void onScorllBootom() {
            if (autoLoadMore && hasMoreData && !isLoading()) {
                // 如果网络可以用
                if (needCheckNetwork && !NetworkUtils.hasNetwork(context)) {
                    mLoadMoreView.showFail(new Exception("网络不可用"));
                } else {
                    loadMore();
                }
            }
        }
    };

    public interface OnScrollBottomListener {
        void onScorllBootom();
    }
}
