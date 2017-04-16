package com.shizhefei.task.tasks;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.IDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.Code;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ISuperTask;
import com.shizhefei.task.ITask;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.task.imp.SimpleCallback;

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
            return TaskHelper.createExecutor((ITask<DATA>) task, new MyCallback<>(sender)).execute();
        } else if (task instanceof IAsyncTask) {
            return TaskHelper.createExecutor((IAsyncTask<DATA>) task, new MyCallback<>(sender)).execute();
        } else if (task instanceof IDataSource) {
            return TaskHelper.createExecutor((IDataSource<DATA>) task, isExeRefresh, new MyCallback<>(sender)).execute();
        } else if (task instanceof IAsyncDataSource) {
            return TaskHelper.createExecutor((IAsyncDataSource<DATA>) task, isExeRefresh, new MyCallback<>(sender)).execute();
        }
        return null;
    }

    private static class MyCallback<DATA> extends SimpleCallback<DATA> {
        private ResponseSender<DATA> sender;

        public MyCallback(ResponseSender<DATA> sender) {
            this.sender = sender;
        }

        @Override
        public void onProgress(Object task, int percent, long current, long total, Object extraData) {
            super.onProgress(task, percent, current, total, extraData);
            sender.sendProgress(current, total, extraData);
        }

        @Override
        public void onPostExecute(Object task, Code code, Exception exception, DATA data) {
            switch (code) {
                case SUCCESS:
                    sender.sendData(data);
                    break;
                case EXCEPTION:
                    sender.sendError(exception);
                    break;
                case CANCEL:
                    break;
            }
        }
    }
}
