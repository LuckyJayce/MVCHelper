package com.shizhefei.task.tasks;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.task.Code;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ICallback;
import com.shizhefei.task.ITask;
import com.shizhefei.task.TaskExecutor;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.utils.MVCLogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LuckyJayce on 2016/7/17.
 * TaskHelper用于执行多个Task
 */
class SimpleTaskHelper<BASE_DATA> implements RequestHandle {

    private Map<ProxyCallBack<?, BASE_DATA>, RequestHandle> taskImps = new HashMap<>();

    public <DATA extends BASE_DATA> RequestHandle execute(ITask<DATA> task, ICallback<DATA> callBack) {
        return executeImp(task, callBack);
    }

    public <DATA extends BASE_DATA> RequestHandle execute(IAsyncTask<DATA> task, ICallback<DATA> callBack) {
        return executeImp(task, callBack);
    }

    private <DATA extends BASE_DATA> RequestHandle executeImp(Object task, ICallback<DATA> callBack) {
        ProxyCallBack<DATA, BASE_DATA> proxyCallBack = new ProxyCallBack<>(task, callBack, taskImps);
        TaskExecutor<DATA> taskExecutor;
        LogCallback<DATA> callback = new LogCallback<>(proxyCallBack);
        if (task instanceof ITask) {
            taskExecutor = TaskHelper.createExecutorWithoutLog((ITask<DATA>) task, callback);
        } else {
            taskExecutor = TaskHelper.createExecutorWithoutLog((IAsyncTask<DATA>) task, callback);
        }
        taskImps.put(proxyCallBack, taskExecutor);
        taskExecutor.execute();
        return taskExecutor;
    }

    public void cancelAll() {
        if (taskImps.isEmpty()) {
            return;
        }
        //这里创建一个临时的map，主要原因是Set循环的时候不能remove操作，否则会报ConcurrentModificationException
        //TaskImp里面会调用到Set的remove
        HashMap<ProxyCallBack<?, BASE_DATA>, RequestHandle> temp = new HashMap<>(taskImps);
        for (Map.Entry<ProxyCallBack<?, BASE_DATA>, RequestHandle> entry : temp.entrySet()) {
            entry.getValue().cancle();
        }
        taskImps.clear();
    }

    public void destroy() {
        cancelAll();
    }

    @Override
    public void cancle() {
        cancelAll();
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    static class ProxyCallBack<DATA extends BASE_DATA, BASE_DATA> implements ICallback<DATA> {
        private final Object task;
        private final ICallback<DATA> callback;
        private Map<ProxyCallBack<?, BASE_DATA>, RequestHandle> taskImps = new HashMap<>();

        public ProxyCallBack(Object task, ICallback<DATA> callback, Map<ProxyCallBack<?, BASE_DATA>, RequestHandle> taskImps) {
            this.task = task;
            this.callback = callback;
            this.taskImps = taskImps;
        }

        @Override
        public void onPreExecute(Object task) {
            if (callback != null) {
                callback.onPreExecute(task);
            }
        }

        @Override
        public void onProgress(Object task, int percent, long current, long total, Object extraData) {
            if (callback != null) {
                callback.onProgress(task, percent, current, total, extraData);
            }
        }

        @Override
        public void onPostExecute(Object task, Code code, Exception exception, DATA data) {
            if (callback != null) {
                callback.onPostExecute(task, code, exception, data);
            }
            taskImps.remove(this);
        }
    }

    private static class LogCallback<DATA> implements ICallback<DATA> {
        private ICallback<DATA> callback;

        public LogCallback(ICallback<DATA> callback) {
            this.callback = callback;
        }

        @Override
        public void onPreExecute(Object task) {
            if (callback != null) {
                callback.onPreExecute(task);
            }
        }

        @Override
        public void onProgress(Object task, int percent, long current, long total, Object extraData) {
            if (callback != null) {
                callback.onProgress(task, percent, current, total, extraData);
            }
        }

        @Override
        public void onPostExecute(Object task, Code code, Exception exception, DATA data) {
            if (exception == null) {
                MVCLogUtil.d("{} task={} code={} data={}", "过程结果", task, code, data);
            } else {
                MVCLogUtil.e("{} task={} code={} exception={}", "过程结果", task, code, exception);
            }
            if (callback != null) {
                callback.onPostExecute(task, code, exception, data);
            }
        }
    }
}
