package com.shizhefei.mvc;

/**
 * 异步数据源（比如Volley，OkHttp等异步请求使用）
 * @param <DATA>
 */
public interface IAsyncDataSource<DATA> {
    /**
     * 获取刷新的数据
     *
     * @param sender 用于请求结束时发送数据给MVCHelper，MVCHelper再通知IDataAdapter调用notifyDataChenge方法
     * @return 用于提供外部取消请求的处理.比如执行refresh还没请求结束又执行refresh，就会通过上次的RequestHandle取消上次的请求.MVCHelper的destroy也会用这个取消请求
     * @throws Exception
     */
    public RequestHandle refresh(ResponseSender<DATA> sender) throws Exception;

    /**
     * 获取加载更多的数据
     *
     * @param sender 用于请求结束时发送数据给MVCHelper，MVCHelper再通知IDataAdapter调用notifyDataChenge方法
     * @return 用于提供外部取消请求的处理.比如执行refresh还没请求结束又执行refresh，就会通过上次的RequestHandle取消上次的请求.MVCHelper的destroy也会用这个取消请求
     * @throws Exception
     */
    public RequestHandle loadMore(ResponseSender<DATA> sender) throws Exception;

    /**
     * 是否还可以继续加载更多
     *
     * @return
     */
    public boolean hasMore();

}
