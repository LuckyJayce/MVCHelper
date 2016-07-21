package com.shizhefei.task;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

/**
 * 异步Task，不能直接执行超时任务，可以执行像OKhttp异步回调那样
 * Created by LuckyJayce on 2016/7/17.
 */
public interface IAsyncTask<DATA> extends ISuperTask<DATA> {
    RequestHandle execute(ResponseSender<DATA> sender) throws Exception;
}
