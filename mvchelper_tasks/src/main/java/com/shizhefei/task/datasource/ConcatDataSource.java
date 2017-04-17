package com.shizhefei.task.datasource;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.Code;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ResponseSenderCallback;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.task.tasks.LinkTask;
import com.shizhefei.task.tasks.Tasks;

/**
 * Created by luckyjayce on 2017/4/17.
 */

class ConcatDataSource<D, DATA> implements IAsyncDataSource<DATA> {
    private IAsyncDataSource<DATA> dataSource;
    private IAsyncTask<D> asyncTask;
    private ResponseSenderCallback<DATA> successCallback;

    public ConcatDataSource(IAsyncTask<D> asyncTask, IAsyncDataSource<DATA> dataSource) {
        this.dataSource = dataSource;
        this.asyncTask = asyncTask;
    }

    @Override
    public RequestHandle refresh(ResponseSender<DATA> sender) throws Exception {
        if (successCallback != null && successCallback.getCode() == Code.SUCCESS) {
            return dataSource.refresh(sender);
        } else {
            LinkTask<DATA> task = Tasks.concatWith(asyncTask, Tasks.create(dataSource, true));
            return TaskHelper.createExecutor(task, successCallback = new ResponseSenderCallback<>(sender)).execute();
        }
    }

    @Override
    public RequestHandle loadMore(ResponseSender<DATA> sender) throws Exception {
        if (successCallback != null && successCallback.getCode() == Code.SUCCESS) {
            return dataSource.loadMore(sender);
        } else {
            LinkTask<DATA> task = Tasks.concatWith(asyncTask, Tasks.create(dataSource, true));
            return TaskHelper.createExecutor(task, successCallback = new ResponseSenderCallback<>(sender)).execute();
        }
    }

    @Override
    public boolean hasMore() {
        return dataSource.hasMore();
    }
}
