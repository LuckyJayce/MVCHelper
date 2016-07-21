package com.shizhefei.task;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

/**
 * Created by LuckyJayce on 2016/7/17.
 */
public interface IAsyncTask<DATA> extends ISuperTask<DATA> {
    RequestHandle execute(ResponseSender<DATA> sender) throws Exception;
}
