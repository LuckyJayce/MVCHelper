package com.shizhefei.task.tasks;


import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.Code;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.function.Func2;
import com.shizhefei.task.function.LogFuncs;
import com.shizhefei.task.imp.SimpleCallback;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by luckyjayce on 2017/7/20.
 */
class RetryLinkTask<DATA> extends LinkTask<DATA> {
    private final Func2<IAsyncTask<DATA>, Exception, IAsyncTask<DATA>> retryFunc;
    private IAsyncTask<DATA> preTask;
    private Queue<IAsyncTask<DATA>> tasks;
    private IAsyncTask<DATA> task;
    private int maxRetryTimes;

    public RetryLinkTask(String logTag, IAsyncTask<DATA> task, int maxRetryTimes) {
        this(task, LogFuncs.made(logTag, new RetryTimesFunc<DATA>(maxRetryTimes)));
        this.maxRetryTimes = maxRetryTimes;
    }

    public RetryLinkTask(IAsyncTask<DATA> task, Func2<IAsyncTask<DATA>, Exception, IAsyncTask<DATA>> retryFunc) {
        this.task = task;
        this.preTask = task;
        this.retryFunc = retryFunc;
        tasks = new LinkedList<>();
    }

    @Override
    public RequestHandle execute(final ResponseSender<DATA> sender) throws Exception {
        final SimpleTaskHelper taskHelper = new SimpleTaskHelper();
        taskHelper.execute(task, new SimpleCallback<DATA>() {

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
                        try {
                            IAsyncTask<DATA> retry = retryFunc.call(preTask, exception);
                            if (retry != null) {
                                tasks.add(retry);
                                preTask = retry;
                                taskHelper.execute(retry, this);
                            } else {
                                sender.sendError(exception);
                            }
                        } catch (Exception e) {
                            sender.sendError(e);
                        }
                        break;
                    case CANCEL:
                        break;
                }
            }
        });
        return taskHelper;
    }

    @Override
    public String toString() {
        if (maxRetryTimes > 0) {
            return "RetryLinkTask->{task:" + task + ",current:" + preTask + ",maxRetryTimes:" + maxRetryTimes + "}";
        }
        return "RetryLinkTask->{task:" + task + ",current:" + preTask + ",tasks:" + tasks + "}";
    }

    private static class RetryTimesFunc<DATA> implements Func2<IAsyncTask<DATA>, Exception, IAsyncTask<DATA>> {
        private int times = 0;
        private int maxTimes;

        public RetryTimesFunc(int maxTimes) {
            this.maxTimes = maxTimes;
        }

        @Override
        public IAsyncTask<DATA> call(IAsyncTask<DATA> task, Exception e) throws Exception {
            if (times < maxTimes) {
                times++;
                return task;
            }
            return null;
        }
    }
}
