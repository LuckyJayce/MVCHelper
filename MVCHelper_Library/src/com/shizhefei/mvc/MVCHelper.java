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

import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.shizhefei.mvc.ILoadViewFactory.FootViewAdder;
import com.shizhefei.mvc.ILoadViewFactory.ILoadMoreView;
import com.shizhefei.mvc.ILoadViewFactory.ILoadView;
import com.shizhefei.mvc.IRefreshView.OnRefreshListener;
import com.shizhefei.mvc.imp.DefaultLoadViewFactory;
import com.shizhefei.recyclerview.HFAdapter;
import com.shizhefei.recyclerview.HFRecyclerAdapter;
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
 * @author LuckyJayce
 * 
 * @param <DATA>
 */
public class MVCHelper<DATA> {
	private IDataAdapter<DATA> dataAdapter;
	private IRefreshView refreshView;
	private IDataSource<DATA> dataSource;
	private View contentView;
	private Context context;
	private MOnStateChangeListener<DATA> onStateChangeListener = new MOnStateChangeListener<DATA>();
	private MyAsyncTask<Void, Void, DATA> asyncTask;
	private long loadDataTime = -1;
	/**
	 * 是否还有更多数据。如果服务器返回的数据为空的话，就说明没有更多数据了，也就没必要自动加载更多数据
	 */
	private boolean hasMoreData = true;
	/*** 加载更多的时候是否事先检查网络是否可用。 */
	private boolean needCheckNetwork = true;
	private ILoadView mLoadView;
	private ILoadMoreView mLoadMoreView;
	public static ILoadViewFactory loadViewFactory = new DefaultLoadViewFactory();

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
		if (loadMoreView != null) {
			if (contentView instanceof ListView) {
				final ListView listView = (ListView) contentView;
				listView.setCacheColorHint(Color.TRANSPARENT);
				listView.setOnScrollListener(new ListViewOnScrollListener());
				listView.setOnItemSelectedListener(new ListViewOnItemSelectedListener());
				mLoadMoreView = loadMoreView;
				mLoadMoreView.init(new FootViewAdder() {

					@Override
					public View addFootView(int layoutId) {
						View view = LayoutInflater.from(context).inflate(layoutId, listView, false);
						return addFootView(view);
					}

					@Override
					public View addFootView(View view) {
						listView.addFooterView(view);
						return view;
					}
				}, onClickLoadMoreListener);
			} else if (contentView instanceof RecyclerView) {
				RecyclerView recyclerView = (RecyclerView) contentView;
				recyclerView.setOnScrollListener(new RecyclerViewOnScrollListener());
				mLoadMoreView = loadMoreView;
			}
		}
		mLoadView = loadView;
		mLoadView.init(refreshView.getSwitchView(), onClickRefresListener);
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
	 * 设置适配器，用于显示数据
	 * 
	 * @param adapter
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setAdapter(IDataAdapter<DATA> adapter) {
		if (contentView instanceof AbsListView) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				((AbsListView) contentView).setAdapter((ListAdapter) adapter);
			} else {
				try {
					Method method = contentView.getClass().getMethod("setAdapter", ListAdapter.class);
					method.invoke(contentView, adapter);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (contentView instanceof RecyclerView) {
			final RecyclerView recyclerView = (RecyclerView) contentView;
			Adapter<?> adapter2 = (Adapter<?>) adapter;
			if (mLoadMoreView != null) {
				final HFAdapter hfAdapter;
				if (adapter instanceof HFAdapter) {
					hfAdapter = (HFAdapter) adapter;
				} else {
					hfAdapter = new HFRecyclerAdapter(adapter2);
				}
				adapter2 = hfAdapter;
				mLoadMoreView.init(new FootViewAdder() {

					@Override
					public View addFootView(int layoutId) {
						View view = LayoutInflater.from(context).inflate(layoutId, recyclerView, false);
						return addFootView(view);
					}

					@Override
					public View addFootView(View view) {
						hfAdapter.addFooter(view);
						return view;
					}
				}, onClickLoadMoreListener);
			}
			recyclerView.setAdapter(adapter2);
		}
		this.dataAdapter = adapter;
	}

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
	 * @param onStateChangeListener
	 */
	public void setOnStateChangeListener(OnRefreshStateChangeListener<DATA> onRefreshStateChangeListener) {
		this.onStateChangeListener.setOnRefreshStateChangeListener(onRefreshStateChangeListener);
	}

	/**
	 * 设置状态监听，监听开始加载更多，加载更多成功
	 * 
	 * @param onStateChangeListener
	 */
	public void setOnStateChangeListener(OnLoadMoreStateChangeListener<DATA> onLoadMoreStateChangeListener) {
		this.onStateChangeListener.setOnLoadMoreStateChangeListener(onLoadMoreStateChangeListener);
	}

	/**
	 * 刷新，开启异步线程，并且显示加载中的界面，当数据加载完成自动还原成加载完成的布局，并且刷新列表数据
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void refresh() {
		if (dataAdapter == null || dataSource == null) {
			if (refreshView != null) {
				refreshView.showRefreshComplete();
			}
			return;
		}
		if (asyncTask != null && asyncTask.getStatus() != AsyncTask.Status.FINISHED) {
			asyncTask.cancel(true);
		}
		asyncTask = new MyAsyncTask<Void, Void, DATA>() {
			private volatile Exception e;

			protected void onPreExecute() {
				if (mLoadMoreView != null) {
					mLoadMoreView.showNormal();
				}
				if (dataAdapter.isEmpty()) {
					mLoadView.showLoading();
					refreshView.showRefreshComplete();
				} else {
					refreshView.showRefreshing();
				}
				onStateChangeListener.onStartRefresh(dataAdapter);
			};

			@Override
			protected DATA doInBackground(Void... params) {
				try {
					return dataSource.refresh();
				} catch (Exception e) {
					this.e = e;
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(DATA result) {
				super.onPostExecute(result);
				if (result == null) {
					if (dataAdapter.isEmpty()) {
						mLoadView.showFail(e);
					} else {
						mLoadView.tipFail(e);
					}
				} else {
					loadDataTime = System.currentTimeMillis();
					dataAdapter.notifyDataChanged(result, true);
					if (dataAdapter.isEmpty()) {
						mLoadView.showEmpty();
					} else {
						mLoadView.restore();
					}
					hasMoreData = dataSource.hasMore();
					if (mLoadMoreView != null) {
						if (hasMoreData) {
							mLoadMoreView.showNormal();
						} else {
							mLoadMoreView.showNomore();
						}
					}
				}
				onStateChangeListener.onEndRefresh(dataAdapter, result);
				refreshView.showRefreshComplete();
			};

		};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			asyncTask.execute();
		}
	}

	/**
	 * 加载更多，开启异步线程，并且显示加载中的界面，当数据加载完成自动还原成加载完成的布局，并且刷新列表数据
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void loadMore() {
		if (isLoading()) {
			return;
		}
		if (dataAdapter.isEmpty()) {
			refresh();
			return;
		}

		if (dataAdapter == null || dataSource == null) {
			if (refreshView != null) {
				refreshView.showRefreshComplete();
			}
			return;
		}
		if (asyncTask != null && asyncTask.getStatus() != AsyncTask.Status.FINISHED) {
			asyncTask.cancel(true);
		}
		asyncTask = new MyAsyncTask<Void, Void, DATA>() {
			private volatile Exception e;

			protected void onPreExecute() {
				onStateChangeListener.onStartLoadMore(dataAdapter);
				if (mLoadMoreView != null) {
					mLoadMoreView.showLoading();
				}
			}

			@Override
			protected DATA doInBackground(Void... params) {
				try {
					return dataSource.loadMore();
				} catch (Exception e) {
					this.e = e;
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(DATA result) {
				super.onPostExecute(result);
				if (result == null) {
					mLoadView.tipFail(e);
					if (mLoadMoreView != null) {
						mLoadMoreView.showFail(e);
					}
				} else {
					dataAdapter.notifyDataChanged(result, false);
					if (dataAdapter.isEmpty()) {
						mLoadView.showEmpty();
					} else {
						mLoadView.restore();
					}
					hasMoreData = dataSource.hasMore();
					if (mLoadMoreView != null) {
						if (hasMoreData) {
							mLoadMoreView.showNormal();
						} else {
							mLoadMoreView.showNomore();
						}
					}
				}
				onStateChangeListener.onEndLoadMore(dataAdapter, result);
			};
		};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			asyncTask.execute();
		}
	}

	/**
	 * 做销毁操作，比如关闭正在加载数据的异步线程等
	 */
	public void destory() {
		if (asyncTask != null && asyncTask.getStatus() != AsyncTask.Status.FINISHED) {
			asyncTask.cancel(true);
			asyncTask = null;
		}
	}

	private class MyAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
		private volatile boolean post;

		@Override
		protected Result doInBackground(Params... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Result result) {
			super.onPostExecute(result);
			post = true;
		}

		private boolean isLoading() {
			if (post) {
				return false;
			}
			return getStatus() != Status.FINISHED;
		}

	}

	/**
	 * 是否正在加载中
	 * 
	 * @return
	 */
	public boolean isLoading() {
		return asyncTask != null && asyncTask.isLoading();
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

	public IDataSource<DATA> getDataSource() {
		return dataSource;
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

	/**
	 * 滚动到底部自动加载更多数据
	 */
	private class ListViewOnScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView listView, int scrollState) {
			if (autoLoadMore) {
				if (hasMoreData) {
					if (!isLoading()) {
						if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && listView.getLastVisiblePosition() + 1 == listView.getCount()) {// 如果滚动到最后一行
							// 如果网络可以用
							if (needCheckNetwork && !NetworkUtils.hasNetwork(context)) {
								mLoadMoreView.showFail(new Exception("网络不可用"));
							} else {
								loadMore();
							}
						}
					}
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		}
	};

	/**
	 * 针对于电视 选择到了底部项的时候自动加载更多数据
	 */
	private class ListViewOnItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> listView, View view, int position, long id) {
			if (autoLoadMore) {
				if (hasMoreData) {
					if (!isLoading()) {
						if (listView.getLastVisiblePosition() + 1 == listView.getCount()) {// 如果滚动到最后一行
							// 如果网络可以用
							if (needCheckNetwork && !NetworkUtils.hasNetwork(context)) {
								mLoadMoreView.showFail(new Exception("网络不可用"));
							} else {
								loadMore();
							}
						}
					}
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	protected IRefreshView getRefreshView() {
		return refreshView;
	}

	/**
	 * 加载监听
	 * 
	 * @author zsy
	 *
	 * @param <DATA>
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

	/**
	 * 滑动监听
	 */
	private class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
		@Override
		public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState) {
			LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
			if (newState == RecyclerView.SCROLL_STATE_IDLE && layoutManager.findLastVisibleItemPosition() + 1 == layoutManager.getItemCount()) {// 如果滚动到最后一行
				if (autoLoadMore) {
					if (hasMoreData) {
						if (!isLoading()) {
							// 如果网络可以用
							if (needCheckNetwork && !NetworkUtils.hasNetwork(context)) {
								mLoadMoreView.showFail(new Exception("网络不可用"));
							} else {
								loadMore();
							}
						}
					}
				}

			}
		}

		@Override
		public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {

		}

	};
}