# 常见疑惑集锦


## 列表不需要加载更多？
1.mvcHelper.setAdapter(adapter) 改为 mvcHelper.setAdapter(adapter, null);（第二个参数是ViewHandler处理加载更多和滑动事件）
2.DataSource只实现refresh

## ListView的滑动监听触发不了？
因为mvcHelper里面要通过滑动事件触发自动加载更多，
你如果需要设置滑动事可以mvcHelper.setAdapter之后 从listView取出监听，然后设置自己的监听的时候，调用取出的mvcHelper的滑动监听事件。（代理一层）

## 在执行获取数据非网络的加载更多，关闭网络没有列表滑到底部没有触发加载更多
因为默认是滑动到底部 检查是否有网络，没网络就不需要触发加载更多的操作。
mvcHelper.setNeedCheckNetwork(boolean needCheckNetwork) needCheckNetwork设置为false就不检查网络。

## 不想滑动到底部加载更多要怎么控制
mvcHelper.setAutoLoadMore(boolean autoLoadMore) autoLoadMore 设置为false滑动到底部就不自定触发加载更多

## ListView的Adapter一定要实现IDataAdapter么？
可以不用
你可以使用mvcHelper.setAdapter2(viewAdapter，dataAdapter);前面一个是listView的adapter，后面一个实现IDataAdapter。

## 怎么自定义失败加载中等布局？
实现ILoadViewFractory，可以参照类库中DefaultLoadViewFactory的默认写法
然后在应用启动的时候设置全局的 MVCHelper.setLoadViewFractory(new LoadViewFractory());
就这样，就会显示你自定义的布局

## 怎么在特定的界面使用特定的加载的布局切换
使用这个构造参数传入即可
public MVCHelper(IRefreshView refreshView, ILoadView loadView, ILoadMoreView loadMoreView)

## 什么时候使用MVCHelper什么时候使用TaskHelper
用加载中，加载失败等界面切换或者刷新功能的时候用MVCHelper比较方便
界面操作比较少的用TaskHelper比较方便

## 什么时候实现IDataSource什么时候使用ITask
建议获取数据的逻辑实现IDataSource，执行任务的时候用task比如保存数据等，
当然TaskHelper能够执行IDataSource和task
MVCHelper能够执行IDataSource和task

## HFAdapter和HFRecyclerAdapter分别用来做什么的
1.HFAdapter是实现 可以添加Header和Footer的 RecyclerView的Adapter
可以设置item的点击事件和长按事件 OnItemClickListener，OnItemLongClickListener

2.HFRecyclerAdapter继承于HFAdapter通过 HFRecyclerAdapter(Adapter adapter)构造传入实际的adapter，原本没有添加头部功能的adapter 通过HFRecyclerAdapter就可以有添加头部的功能了。MVCHelper加载更多的Footer就是通过这种装饰模式的方式实现。

