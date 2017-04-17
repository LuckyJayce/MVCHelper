package com.shizhefei.task.datasource;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.IDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.ResponseSenderCallback;
import com.shizhefei.task.TaskHelper;

/**
 * Created by luckyjayce on 2017/4/17.
 */

class AsyncDataSource<DATA> implements IAsyncDataSource<DATA> {
    private IDataSource<DATA> dataSource;

    public AsyncDataSource(IDataSource<DATA> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public RequestHandle refresh(ResponseSender<DATA> sender) throws Exception {
        return TaskHelper.createExecutor(dataSource, true, new ResponseSenderCallback<>(sender)).execute();
    }

    @Override
    public RequestHandle loadMore(ResponseSender<DATA> sender) throws Exception {
        return TaskHelper.createExecutor(dataSource, false, new ResponseSenderCallback<>(sender)).execute();
    }

    @Override
    public boolean hasMore() {
        return dataSource.hasMore();
    }
}
