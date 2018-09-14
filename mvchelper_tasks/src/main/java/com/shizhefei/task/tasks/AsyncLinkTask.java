package com.shizhefei.task.tasks;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.IDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ISuperTask;
import com.shizhefei.task.ITask;
import com.shizhefei.task.ResponseSenderCallback;
import com.shizhefei.task.TaskHelper;

class AsyncLinkTask<DATA> extends LinkTask<DATA> {
    private final boolean isExeRefresh;
    private ISuperTask<DATA> task;

    public AsyncLinkTask(ISuperTask<DATA> task, boolean isExeRefresh) {
        this.task = task;
        this.isExeRefresh = isExeRefresh;
    }

    @Override
    public RequestHandle execute(final ResponseSender<DATA> sender) throws Exception {
        if (task instanceof ITask) {
            return TaskHelper.createExecutor((ITask<DATA>) task, new ResponseSenderCallback<>(sender)).execute();
        } else if (task instanceof IAsyncTask) {
            return TaskHelper.createExecutor((IAsyncTask<DATA>) task, new ResponseSenderCallback<>(sender)).execute();
        } else if (task instanceof IDataSource) {
            return TaskHelper.createExecutor((IDataSource<DATA>) task, isExeRefresh, new ResponseSenderCallback<>(sender)).execute();
        } else if (task instanceof IAsyncDataSource) {
            return TaskHelper.createExecutor((IAsyncDataSource<DATA>) task, isExeRefresh, new ResponseSenderCallback<>(sender)).execute();
        }
        return null;
    }

    @Override
    public String toString() {
        return "AsyncLinkTask->{"+task+"}";
    }
}
