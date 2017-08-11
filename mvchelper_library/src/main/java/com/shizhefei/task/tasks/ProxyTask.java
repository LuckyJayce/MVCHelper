package com.shizhefei.task.tasks;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.IAsyncTask;

/**
 * Created by luckyjayce on 2017/4/16.
 */

public abstract class ProxyTask<DATA> implements IAsyncTask<DATA> {

    @Override
    public final RequestHandle execute(ResponseSender<DATA> sender) throws Exception {
        return getTask().execute(sender);
    }

    protected abstract IAsyncTask<DATA> getTask();
}
