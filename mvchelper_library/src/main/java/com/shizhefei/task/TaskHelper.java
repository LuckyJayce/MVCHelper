package com.shizhefei.task;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.IDataSource;
import com.shizhefei.mvc.ProgressSender;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.mvc.data.Data2;
import com.shizhefei.task.imp.MemoryCacheStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LuckyJayce on 2016/7/17.
 * TaskHelper用于执行多个Task
 *
 * @param <BASE_DATA> 基础类型，execute的task的数据类型都继承于它，不是它的子类型的数据的task不能被执行
 *                    为什么要定义它？主要用于registerCallBack 注册全局的callback数据的返回的数据不用强制转化，
 */
public class TaskHelper<BASE_DATA> {

    private Handler handler;
    private ICacheStore cacheStore;
    private Set<ICallback<BASE_DATA>> callBacks = new LinkedHashSet<>();
    private Set<TaskImp> taskImps = new LinkedHashSet<>();

    public TaskHelper() {
        this(new MemoryCacheStore(100));
    }

    public TaskHelper(ICacheStore cacheStore) {
        this.cacheStore = cacheStore;
        handler = new Handler(Looper.getMainLooper());
    }

    public ICacheStore getCacheStore() {
        return cacheStore;
    }

    public <DATA extends BASE_DATA> TaskHandle execute(ITask<DATA> task, ICallback<DATA> callBack) {
        return executeCache(task, callBack, null);
    }

    public <DATA extends BASE_DATA> TaskHandle execute(final IDataSource<DATA> dataSource, ICallback<DATA> callBack) {
        return executeCache(dataSource, callBack, null);
    }

    public <DATA extends BASE_DATA> TaskHandle execute(final IAsyncDataSource<DATA> dataSource, ICallback<DATA> callBack) {
        return executeCache(dataSource, callBack, null);
    }

    public <DATA extends BASE_DATA> TaskHandle execute(IAsyncTask<DATA> task, ICallback<DATA> callBack) {
        return executeCache(task, callBack, null);
    }

    public <DATA extends BASE_DATA> TaskHandle executeCache(IAsyncDataSource<DATA> task, ICallback<DATA> callBack, ICacheConfig<DATA> cacheConfig) {
        TaskHandle requestHandle = checkTask(cacheConfig, task, callBack);
        if (requestHandle != null) {
            return requestHandle;
        }
        AsyncDataSourceImp taskImp = new AsyncDataSourceImp<>(cacheConfig, callBack, task);
        taskImps.add(taskImp);
        taskImp.execute();
        return new TaskHandle(TaskHandle.TYPE_RUN, task, callBack, taskImp);
    }

    public <DATA extends BASE_DATA> TaskHandle executeCache(IAsyncTask<DATA> task, ICallback<DATA> callBack, ICacheConfig<DATA> cacheConfig) {
        TaskHandle requestHandle = checkTask(cacheConfig, task, callBack);
        if (requestHandle != null) {
            return requestHandle;
        }
        AsyncTaskImp taskImp = new AsyncTaskImp<>(cacheConfig, callBack, task);
        taskImps.add(taskImp);
        taskImp.execute();
        return new TaskHandle(TaskHandle.TYPE_RUN, task, callBack, taskImp);
    }

    public <DATA extends BASE_DATA> TaskHandle executeCache(IDataSource<DATA> task, ICallback<DATA> callBack, ICacheConfig<DATA> cacheConfig) {
        TaskHandle requestHandle = checkTask(cacheConfig, task, callBack);
        if (requestHandle != null) {
            return requestHandle;
        }
        SyncDataSourceImp<DATA> taskImp = new SyncDataSourceImp<>(cacheConfig, callBack, task);
        taskImps.add(taskImp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            taskImp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            taskImp.execute();
        }
        return new TaskHandle(TaskHandle.TYPE_RUN, task, callBack, taskImp);
    }

    public <DATA extends BASE_DATA> TaskHandle executeCache(ITask<DATA> task, ICallback<DATA> callBack, ICacheConfig<DATA> cacheConfig) {
        TaskHandle requestHandle = checkTask(cacheConfig, task, callBack);
        if (requestHandle != null) {
            return requestHandle;
        }
        SyncTaskImp<DATA> taskImp = new SyncTaskImp<>(cacheConfig, callBack, task);
        taskImps.add(taskImp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            taskImp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            taskImp.execute();
        }
        return new TaskHandle(TaskHandle.TYPE_RUN, task, callBack, taskImp);
    }

    private TaskImp getTaskImpByTask(Object task) {
        for (TaskImp taskImp : taskImps) {
            List<Data2<Object, ICallback<? extends BASE_DATA>>> calls = taskImp.getCallbacks();
            for (Data2<Object, ICallback<? extends BASE_DATA>> data : calls) {
                if (task.equals(data.getValue1())) {
                    return taskImp;
                }
            }
        }
        return null;
    }

    private TaskImp getTaskImpByTask(String taskKey) {
        for (TaskImp entry : taskImps) {
            if (taskKey.equals(entry.getTaskKey())) {
                return entry;
            }
        }
        return null;
    }


    private <DATA extends BASE_DATA> TaskHandle checkTask(ICacheConfig<DATA> cacheConfig, Object task, ICallback<DATA> callBack) {
        if (task == null) {
            throw new RuntimeException("task不能为空");
        }
        if (cacheConfig != null) {
            String taskKey = cacheConfig.getTaskKey(task);
            if (TextUtils.isEmpty(taskKey)) {
                throw new RuntimeException("ICacheConfig 返回的taskkey不能为空");
            }
            TaskImp taskImp = getTaskImpByTask(taskKey);
            if (taskImp != null) {
                callBack.onPreExecute(task);
                taskImp.addCallBack(task, callBack);
                return new TaskHandle(TaskHandle.TYPE_ATTACH, task, callBack, taskImp);
            }
        }
        TaskHandle requestHandle = loadCache(cacheConfig, task, callBack);
        if (requestHandle != null) {
            return requestHandle;
        }
        return null;
    }

    private <DATA extends BASE_DATA> TaskHandle loadCache(ICacheConfig<DATA> cacheConfig, Object task, ICallback<DATA> callBack) {
        if (cacheConfig != null) {
            String taskKey = cacheConfig.getTaskKey(task);
            ICacheStore.Cache cache = cacheStore.getCache(taskKey);
            if (cache != null) {
                DATA data = (DATA) cache.data;
                if (cacheConfig.isUsefulCacheData(task, cache.requestTime, cache.saveTime, data)) {
                    callBack.onPreExecute(task);
                    callBack.onPostExecute(task, Code.SUCCESS, null, data);
                    return new TaskHandle(TaskHandle.TYPE_CACHE, task, callBack, null);
                }
            }
            return null;
        }
        return null;
    }

    public void cancel(Object task) {
        if (task == null) {
            return;
        }
        if (task instanceof TaskHandle) {
            TaskHandle handle = (TaskHandle) task;
            handle.cancelTaskIfMoreCallbackUnregisterCallback();
            return;
        }
        TaskImp taskImp = getTaskImpByTask(task);
        if (taskImp != null) {
            taskImp.cancle();
        }
    }

    public void cancelTaskIfMoreCallbackUnregisterCallback(Object task) {
        if (task == null) {
            return;
        }
        if (task instanceof TaskHandle) {
            TaskHandle handle = (TaskHandle) task;
            handle.cancelTaskIfMoreCallbackUnregisterCallback();
            return;
        }
        for (TaskImp taskImp : taskImps) {
            List<Data2<Object, ICallback>> calls = taskImp.getCallbacks();
            Iterator<Data2<Object, ICallback>> iterator = calls.iterator();
            while (iterator.hasNext()) {
                Data2<Object, ICallback> data = iterator.next();
                Object ct = data.getValue1();
                if (task.equals(ct)) {
                    if (calls.size() == 1) {
                        taskImp.cancle();
                    } else {
                        iterator.remove();
                        data.getValue2().onPostExecute(ct, Code.CANCEL, null, null);
                    }
                    return;
                }
            }
        }
    }

    public void registerCallBack(ICallback<BASE_DATA> callback) {
        callBacks.add(callback);
    }

    public void unregisterCallBack(ICallback<BASE_DATA> callback) {
        callBacks.remove(callback);
    }

    public void cancelAll() {
        for (TaskImp entry : taskImps) {
            entry.cancle();
        }
        taskImps.clear();
    }

    public void destroy() {
        cancelAll();
        handler.removeCallbacksAndMessages(null);
    }


    /**
     * 执行类的接口
     */
    interface TaskImp<DATA> extends RequestHandle {

        /**
         * 可以被添加回调，多个相同taskKey的task同时执行的话只运行最先的一个task，后面的task通过addCallBack添加回调
         *
         * @param task     执行的对象
         * @param callBack 回调
         */
        void addCallBack(Object task, ICallback<DATA> callBack);

        /**
         * 获取所有回调，包括第一个task也在里面
         *
         * @return 所有回调
         */
        List<Data2<Object, ICallback<DATA>>> getCallbacks();

        /**
         * 返回taskKey
         *
         * @return 返回taskKey
         */
        String getTaskKey();
    }

    /**
     * IAsyncTask的执行类
     *
     * @param <DATA>
     */
    private class AsyncTaskImp<DATA extends BASE_DATA> extends AbsAsyncTaskImp<DATA> {
        private IAsyncTask<DATA> task;

        public AsyncTaskImp(ICacheConfig<DATA> cacheConfig, ICallback<DATA> callback, IAsyncTask<DATA> task) {
            super(cacheConfig, callback, task);
            this.task = task;
        }

        @Override
        protected RequestHandle executeImp(ResponseSender<DATA> responseSender) throws Exception {
            return task.execute(responseSender);
        }
    }

    /**
     * IAsyncDataSource执行类
     *
     * @param <DATA>
     */
    private class AsyncDataSourceImp<DATA extends BASE_DATA> extends AbsAsyncTaskImp<DATA> {
        private IAsyncDataSource<DATA> dataSource;

        public AsyncDataSourceImp(ICacheConfig<DATA> cacheConfig, ICallback<DATA> callback, IAsyncDataSource<DATA> dataSource) {
            super(cacheConfig, callback, dataSource);
            this.dataSource = dataSource;
        }

        @Override
        protected RequestHandle executeImp(ResponseSender<DATA> responseSender) throws Exception {
            return dataSource.refresh(responseSender);
        }
    }

    /**
     * IAsyncTask和IAsyncDataSource 抽象执行类
     *
     * @param <DATA>
     */
    private abstract class AbsAsyncTaskImp<DATA extends BASE_DATA> implements ResponseSender<DATA>, TaskImp<DATA> {
        private final Object realTask;
        private final ICacheConfig<DATA> cacheConfig;
        private final long requestTime;
        private String taskKey;
        private RequestHandle requestHandle;
        private List<Data2<Object, ICallback<DATA>>> selfCallBacks = new ArrayList<>(2);
        private boolean isRunning;

        public AbsAsyncTaskImp(ICacheConfig<DATA> cacheConfig, ICallback<DATA> callback, Object task) {
            this.isRunning = true;
            this.realTask = task;
            this.cacheConfig = cacheConfig;
            if (cacheConfig != null) {
                taskKey = cacheConfig.getTaskKey(task);
            }
            this.requestTime = System.currentTimeMillis();
            selfCallBacks.add(new Data2<>(realTask, callback));
        }

        public void execute() {
            for (Data2<Object, ICallback<DATA>> data2 : selfCallBacks) {
                data2.getValue2().onPreExecute(data2.getValue1());
            }
            for (ICallback<BASE_DATA> callback : callBacks) {
                callback.onPreExecute(realTask);
            }
            try {
                requestHandle = executeImp(this);
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    Log.d("TaskHelper", realTask.toString() + e);
                } else {
                    e.printStackTrace();
                }
                sendError(e);
            }
        }

        @Override
        public String getTaskKey() {
            return taskKey;
        }

        protected abstract RequestHandle executeImp(ResponseSender<DATA> responseSender) throws Exception;

        @Override
        public void sendError(final Exception exception) {
            onPostExecute(Code.EXCEPTION, exception, null);
        }

        @Override
        public void sendData(final DATA data) {
            onPostExecute(Code.SUCCESS, null, data);
        }

        @Override
        public void sendProgress(final long current, final long total, final Object extraData) {
            final int percent;
            if (current == 0) {
                percent = 0;
            } else if (total == 0) {
                percent = 0;
            } else {
                percent = (int) (100 * current / total);
            }
            onProgress(percent, current, total, extraData);
        }

        private void onProgress(final int percent, final long current, final long total, final Object extraData) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onProgressMainThread(percent, current, total, extraData);
                    }
                });
            } else {
                onProgressMainThread(percent, current, total, extraData);
            }
        }

        private void onProgressMainThread(final int percent, final long current, final long total, final Object extraData) {
            for (Data2<Object, ICallback<DATA>> data2 : selfCallBacks) {
                data2.getValue2().onProgress(data2.getValue1(), percent, current, total, extraData);
            }
            for (ICallback<BASE_DATA> callback : callBacks) {
                callback.onProgress(realTask, percent, current, total, extraData);
            }
        }

        private void onPostExecute(final Code code, final Exception exception, final DATA data) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecuteMainThread(code, exception, data);
                    }
                });
            } else {
                onPostExecuteMainThread(code, exception, data);
            }
        }

        private void onPostExecuteMainThread(final Code code, final Exception exception, final DATA data) {
            if (!isRunning) {
                return;
            }
            isRunning = false;
            taskImps.remove(this);
            if (code == Code.SUCCESS && cacheConfig != null) {
                long saveTime = System.currentTimeMillis();
                if (cacheConfig.isNeedSave(realTask, requestTime, saveTime, data)) {
                    String taskKey = cacheConfig.getTaskKey(realTask);
                    cacheStore.saveCache(taskKey, requestTime, saveTime, data);
                }
            }
            for (Data2<Object, ICallback<DATA>> data2 : selfCallBacks) {
                data2.getValue2().onPostExecute(data2.getValue1(), code, exception, data);
            }
            for (ICallback<BASE_DATA> callback : callBacks) {
                callback.onPostExecute(realTask, code, exception, data);
            }
        }

        @Override
        public void cancle() {
            if (requestHandle != null) {
                requestHandle.cancle();
            }
            onPostExecute(Code.CANCEL, null, null);
        }

        @Override
        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public void addCallBack(Object task, ICallback<DATA> callBack) {
            selfCallBacks.add(new Data2<>(task, callBack));
        }

        @Override
        public List<Data2<Object, ICallback<DATA>>> getCallbacks() {
            return selfCallBacks;
        }
    }

    /**
     * ITask 的执行类
     *
     * @param <DATA>
     */
    private class SyncTaskImp<DATA> extends AbsSyncTaskImp {
        private ITask<DATA> task;

        public SyncTaskImp(ICacheConfig<DATA> cacheConfig, ICallback<DATA> call, ITask<DATA> task) {
            super(cacheConfig, call, task);
            this.task = task;
        }

        @Override
        protected DATA executeImp(ProgressSender sender) throws Exception {
            return task.execute(sender);
        }

        @Override
        protected void cancelImp() {
            task.cancel();
        }
    }

    /**
     * IDataSource的执行类
     *
     * @param <DATA>
     */
    private class SyncDataSourceImp<DATA> extends AbsSyncTaskImp {
        private IDataSource<DATA> task;

        public SyncDataSourceImp(ICacheConfig<DATA> cacheConfig, ICallback<DATA> call, IDataSource<DATA> task) {
            super(cacheConfig, call, task);
            this.task = task;
        }

        @Override
        protected DATA executeImp(ProgressSender sender) throws Exception {
            return task.refresh();
        }

        @Override
        protected void cancelImp() {

        }
    }

    /**
     * 同步task和IDataSource的抽象实现类
     *
     * @param <DATA>
     */
    private abstract class AbsSyncTaskImp<DATA extends BASE_DATA> extends AsyncTask<Object, Object, DATA> implements TaskImp<DATA> {

        private final Object realTask;
        private final ICacheConfig<DATA> cacheConfig;
        private final long requestTime;
        private volatile Exception e;
        private List<Data2<Object, ICallback<DATA>>> selfCallBacks = new ArrayList<>(2);
        private boolean isRunning;
        private String taskKey;

        public AbsSyncTaskImp(ICacheConfig<DATA> cacheConfig, ICallback<DATA> call, Object task) {
            super();
            this.isRunning = true;
            this.realTask = task;
            this.cacheConfig = cacheConfig;
            this.requestTime = System.currentTimeMillis();
            if (cacheConfig != null) {
                this.taskKey = cacheConfig.getTaskKey(task);
            }
            selfCallBacks.add(new Data2<>(realTask, call));
            for (Data2<Object, ICallback<DATA>> data2 : selfCallBacks) {
                data2.getValue2().onPreExecute(data2.getValue1());
            }
            for (ICallback<BASE_DATA> callback : callBacks) {
                callback.onPreExecute(realTask);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected DATA doInBackground(Object... params) {
            try {
                ProgressSender progressSender = new ProgressSender() {
                    @Override
                    public void sendProgress(long current, long total, Object exraData) {
                        publishProgress(current, total, exraData);
                    }
                };
                return executeImp(progressSender);
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    Log.d("TaskHelper", realTask.toString() + e);
                } else {
                    e.printStackTrace();
                }
                this.e = e;
            }
            return null;
        }

        protected abstract DATA executeImp(ProgressSender sender) throws Exception;

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            Long current = (Long) values[0];
            Long total = (Long) values[1];
            Object extraData = values[2];
            int percent;
            if (current == 0) {
                percent = 0;
            } else if (total == 0) {
                percent = 0;
            } else {
                percent = (int) (100 * current / total);
            }
            for (Data2<Object, ICallback<DATA>> data2 : selfCallBacks) {
                data2.getValue2().onProgress(data2.getValue1(), percent, current, total, extraData);
            }
            for (ICallback<BASE_DATA> callback : callBacks) {
                callback.onProgress(realTask, percent, current, total, extraData);
            }
        }

        @Override
        protected void onPostExecute(DATA result) {
            super.onPostExecute(result);
            if (isRunning) {
                isRunning = false;
                taskImps.remove(this);
                if (this.e != null) {
                    for (Data2<Object, ICallback<DATA>> data2 : selfCallBacks) {
                        data2.getValue2().onPostExecute(data2.getValue1(), Code.EXCEPTION, e, null);
                    }
                    for (ICallback<BASE_DATA> callback : callBacks) {
                        callback.onPostExecute(realTask, Code.EXCEPTION, e, null);
                    }
                } else {
                    if (cacheConfig != null) {
                        long saveTime = System.currentTimeMillis();
                        if (cacheConfig.isNeedSave(realTask, requestTime, saveTime, result)) {
                            String taskKey = cacheConfig.getTaskKey(realTask);
                            cacheStore.saveCache(taskKey, requestTime, saveTime, result);
                        }
                    }
                    for (Data2<Object, ICallback<DATA>> data2 : selfCallBacks) {
                        data2.getValue2().onPostExecute(data2.getValue1(), Code.SUCCESS, null, result);
                    }
                    for (ICallback<BASE_DATA> callback : callBacks) {
                        callback.onPostExecute(realTask, Code.SUCCESS, null, result);
                    }
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (isRunning) {
                isRunning = false;
                taskImps.remove(this);
                for (Data2<Object, ICallback<DATA>> data2 : selfCallBacks) {
                    data2.getValue2().onPostExecute(data2.getValue1(), Code.CANCEL, null, null);
                }
                for (ICallback<BASE_DATA> callback : callBacks) {
                    callback.onPostExecute(realTask, Code.CANCEL, null, null);
                }
            }
        }

        @Override
        public void cancle() {
            cancelImp();
            cancel(true);
            if (isRunning) {
                isRunning = false;
                taskImps.remove(this);
                for (Data2<Object, ICallback<DATA>> data2 : selfCallBacks) {
                    data2.getValue2().onPostExecute(data2.getValue1(), Code.CANCEL, null, null);
                }
                for (ICallback<BASE_DATA> callback : callBacks) {
                    callback.onPostExecute(realTask, Code.CANCEL, null, null);
                }
            }
        }

        protected abstract void cancelImp();

        @Override
        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public void addCallBack(Object task, ICallback<DATA> callBack) {
            selfCallBacks.add(new Data2<>(task, callBack));
        }

        @Override
        public List<Data2<Object, ICallback<DATA>>> getCallbacks() {
            return selfCallBacks;
        }

        @Override
        public String getTaskKey() {
            return taskKey;
        }
    }
}
