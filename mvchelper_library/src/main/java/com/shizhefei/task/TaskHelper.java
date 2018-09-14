package com.shizhefei.task;

import android.text.TextUtils;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.IDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.task.imp.MemoryCacheStore;
import com.shizhefei.utils.MVCLogUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * Created by LuckyJayce on 2016/7/17.
 * TaskHelper用于执行多个Task
 * 可以缓存数据
 *
 * @param <BASE_DATA> 基础类型，execute的task的数据类型都继承于它，不是它的子类型的数据的task不能被执行
 *                    为什么要定义它？主要用于registerCallBack 注册全局的callback数据的返回的数据不用强制转化，
 */
public class TaskHelper<BASE_DATA> implements RequestHandle {

    private ICacheStore cacheStore;
    private Set<ICallback<BASE_DATA>> registerCallBacks = new LinkedHashSet<>();
    private List<MultiTaskBindProxyCallBack<?, BASE_DATA>> taskImps = new LinkedList<>();
    private Executor executor;

    public TaskHelper() {
        this(new MemoryCacheStore(100));
    }

    public TaskHelper(ICacheStore cacheStore) {
        this.cacheStore = cacheStore;
        this.executor = AsyncTaskV25.THREAD_POOL_EXECUTOR;
    }

    public ICacheStore getCacheStore() {
        return cacheStore;
    }

    /**
     * 提供设置执行ITask的线程池Executor
     * @param executor
     */
    public void setThreadExecutor(Executor executor){
        this.executor = executor;
    }

//    public <DATA extends BASE_DATA> TaskHandle execute(ITask<DATA> task, ICallback<? super DATA> callBack) {
//        return executeImp(null, task, true, callBack);
//    }

    public <DATA extends BASE_DATA> TaskHandle execute(ITask<DATA> task, ICallback<DATA> callBack) {
        return executeImp(null, task, true, callBack);
    }

    public <DATA extends BASE_DATA> TaskHandle execute(final IDataSource<DATA> dataSource, ICallback<DATA> callBack) {
        return executeImp(null, dataSource, true, callBack);
    }

    public <DATA extends BASE_DATA> TaskHandle execute(final IDataSource<DATA> dataSource, boolean isExeRefresh, ICallback<DATA> callBack) {
        return executeImp(null, dataSource, isExeRefresh, callBack);
    }

    public <DATA extends BASE_DATA> TaskHandle execute(final IAsyncDataSource<DATA> dataSource, ICallback<DATA> callBack) {
        return executeImp(null, dataSource, true, callBack);
    }

    public <DATA extends BASE_DATA> TaskHandle execute(final IAsyncDataSource<DATA> dataSource, boolean isExeRefresh, ICallback<DATA> callBack) {
        return executeImp(null, dataSource, isExeRefresh, callBack);
    }

    public <DATA extends BASE_DATA> TaskHandle execute(IAsyncTask<DATA> task, ICallback<DATA> callBack) {
        return executeImp(null, task, true, callBack);
    }

    public <DATA extends BASE_DATA> TaskHandle executeCache(IAsyncDataSource<DATA> task, ICallback<DATA> callBack, ICacheConfig<DATA> cacheConfig) {
        return executeImp(cacheConfig, task, true, callBack);
    }

    public <DATA extends BASE_DATA> TaskHandle executeCache(ITask<DATA> task, ICallback<DATA> callBack, ICacheConfig<DATA> cacheConfig) {
        return executeImp(cacheConfig, task, true, callBack);
    }

    public <DATA extends BASE_DATA> TaskHandle executeCache(IAsyncTask<DATA> task, ICallback<DATA> callBack, ICacheConfig<DATA> cacheConfig) {
        return executeImp(cacheConfig, task, true, callBack);
    }

    public <DATA extends BASE_DATA> TaskHandle executeCache(IDataSource<DATA> task, ICallback<DATA> callBack, ICacheConfig<DATA> cacheConfig) {
        return executeImp(cacheConfig, task, true, callBack);
    }

    private <DATA extends BASE_DATA> MultiTaskBindProxyCallBack<DATA, BASE_DATA> getTaskImpByTask(String taskKey) {
        for (MultiTaskBindProxyCallBack<?, BASE_DATA> multiTaskBindProxyCallBack : taskImps) {
            if (taskKey.equals(multiTaskBindProxyCallBack.taskKey)) {
                return (MultiTaskBindProxyCallBack<DATA, BASE_DATA>) multiTaskBindProxyCallBack;
            }
        }
        return null;
    }

    private <DATA extends BASE_DATA> TaskHandle executeImp(ICacheConfig<DATA> cacheConfig, ISuperTask<DATA> task, boolean isExeRefresh, ICallback<DATA> callBack) {
        if (task == null) {
            throw new RuntimeException("task不能为空");
        }
        if (cacheConfig != null) {
            String taskKey = cacheConfig.getTaskKey(task);
            if (TextUtils.isEmpty(taskKey)) {
                throw new RuntimeException("ICacheConfig 返回的taskkey不能为空");
            }
            MultiTaskBindProxyCallBack<DATA, BASE_DATA> multiTaskBindProxyCallBack = getTaskImpByTask(taskKey);
            if (multiTaskBindProxyCallBack != null) {
                callBack.onPreExecute(task);
                multiTaskBindProxyCallBack.addCallBack(task, callBack);
                return new TaskHandle(TaskHandle.TYPE_ATTACH, task, multiTaskBindProxyCallBack);
            }
        }
        TaskHandle requestHandle = loadCache(cacheConfig, task, callBack);
        if (requestHandle == null) {
            MultiTaskBindProxyCallBack<DATA, BASE_DATA> multiTaskBindProxyCallBack = new MultiTaskBindProxyCallBack<>(cacheConfig, task, callBack, registerCallBacks, taskImps, cacheStore);
            ICallback<DATA> callback = new LogCallback<>(multiTaskBindProxyCallBack);
            TaskExecutor<DATA> taskExecutor;
            if (task instanceof IDataSource) {
                taskExecutor = TaskExecutors.create((IDataSource<DATA>) task, isExeRefresh, callback, executor);
            } else if (task instanceof IAsyncDataSource) {
                taskExecutor = TaskExecutors.create((IAsyncDataSource<DATA>) task, isExeRefresh, callback);
            } else if (task instanceof ITask) {
                taskExecutor = TaskExecutors.create((ITask<DATA>) task, callback, executor);
            } else {
                taskExecutor = TaskExecutors.create((IAsyncTask<DATA>) task, callback);
            }
            multiTaskBindProxyCallBack.taskExecutor = taskExecutor;
            taskImps.add(multiTaskBindProxyCallBack);
            taskExecutor.execute();
            requestHandle = new TaskHandle(TaskHandle.TYPE_RUN, task, multiTaskBindProxyCallBack);
        }
        return requestHandle;
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
                    return new TaskHandle(TaskHandle.TYPE_CACHE, callBack, null);
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
            handle.cancle();
            return;
        }
        for (MultiTaskBindProxyCallBack<?, BASE_DATA> taskImp : taskImps) {
            if (taskImp.cancel(task)) {
                break;
            }
        }
    }

    public void cancelAllWithTaskKey(String taskKey) {
        if (TextUtils.isEmpty(taskKey)) {
            return;
        }
        MultiTaskBindProxyCallBack taskImp = getTaskImpByTask(taskKey);
        if (taskImp != null) {
            taskImp.cancelAllTaskBinder();
        }
    }

    public void registerCallBack(ICallback<BASE_DATA> callback) {
        registerCallBacks.add(callback);
    }

    public void unRegisterCallBack(ICallback<BASE_DATA> callback) {
        registerCallBacks.remove(callback);
    }

    public void cancelAll() {
        if (taskImps.isEmpty()) {
            return;
        }
        //这里创建一个临时的map，主要原因是Set循环的时候不能remove操作，否则会报ConcurrentModificationException
        //TaskImp里面会调用到Set的remove
        HashSet<MultiTaskBindProxyCallBack<?, BASE_DATA>> temp = new HashSet<>(taskImps);
        for (MultiTaskBindProxyCallBack<?, BASE_DATA> entry : temp) {
            entry.cancelAllTaskBinder();
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


    static class MultiTaskBindProxyCallBack<DATA extends BASE_DATA, BASE_DATA> implements ICallback<DATA> {
        private ICallback<DATA> callback;
        private long requestTime;
        private Set<ICallback<BASE_DATA>> registerCallbacks;
        private Map<Object, ICallback<DATA>> bindCallBacks = new LinkedHashMap<>();
        private List<MultiTaskBindProxyCallBack<?, BASE_DATA>> taskImps;
        private ICacheStore cacheStore;
        private String taskKey;
        private ICacheConfig<DATA> cacheConfig;
        private Object realTask;
        private TaskExecutor<DATA> taskExecutor;

        public MultiTaskBindProxyCallBack(ICacheConfig<DATA> cacheConfig, Object realTask, ICallback<DATA> callback, Set<ICallback<BASE_DATA>> registerCallbacks, List<MultiTaskBindProxyCallBack<?, BASE_DATA>> taskImps, ICacheStore cacheStore) {
            this.cacheConfig = cacheConfig;
            this.requestTime = System.currentTimeMillis();
            if (cacheConfig != null) {
                this.taskKey = cacheConfig.getTaskKey(realTask);
            }
            this.callback = callback;
            this.registerCallbacks = registerCallbacks;
            this.taskImps = taskImps;
            this.cacheStore = cacheStore;
            this.realTask = realTask;
        }

        @Override
        public void onPreExecute(Object task) {
            for (Map.Entry<Object, ICallback<DATA>> callbackEntry : bindCallBacks.entrySet()) {
                Object aTask = callbackEntry.getKey();
                ICallback<DATA> aCallback = callbackEntry.getValue();
                aCallback.onPreExecute(aTask);
            }
            if (callback != null) {
                callback.onPreExecute(task);
            }
            for (ICallback<BASE_DATA> callback : registerCallbacks) {
                callback.onPreExecute(task);
            }
        }

        @Override
        public void onProgress(Object task, int percent, long current, long total, Object extraData) {
            for (Map.Entry<Object, ICallback<DATA>> callbackEntry : bindCallBacks.entrySet()) {
                Object aTask = callbackEntry.getKey();
                ICallback<DATA> aCallback = callbackEntry.getValue();
                aCallback.onProgress(aTask, percent, current, total, extraData);
            }
            if (callback != null) {
                callback.onProgress(task, percent, current, total, extraData);
            }
            for (ICallback<BASE_DATA> callback : registerCallbacks) {
                callback.onProgress(task, percent, current, total, extraData);
            }
        }

        @Override
        public void onPostExecute(Object task, Code code, Exception exception, DATA data) {
            for (Map.Entry<Object, ICallback<DATA>> taskCallbackEntry : bindCallBacks.entrySet()) {
                Object aTask = taskCallbackEntry.getKey();
                ICallback<DATA> aCallback = taskCallbackEntry.getValue();
                aCallback.onPostExecute(aTask, code, exception, data);
            }
            if (callback != null) {
                callback.onPostExecute(task, code, exception, data);
            }
            for (ICallback<BASE_DATA> callback : registerCallbacks) {
                callback.onPostExecute(task, code, exception, data);
            }
            taskImps.remove(this);
            if (code == Code.SUCCESS) {
                if (cacheConfig != null) {
                    long saveTime = System.currentTimeMillis();
                    if (cacheConfig.isNeedSave(task, requestTime, saveTime, data)) {
                        String taskKey = cacheConfig.getTaskKey(task);
                        cacheStore.saveCache(taskKey, requestTime, saveTime, data);
                    }
                }
            }
            this.cacheStore = null;
            this.registerCallbacks = null;
            this.cacheConfig = null;
            this.taskExecutor = null;
            this.taskImps = null;
            this.callback = null;
            this.bindCallBacks = null;
            this.realTask = null;
            this.taskKey = null;
        }

        public Map<Object, ICallback<DATA>> getRegisterCallbacks() {
            return bindCallBacks;
        }

        public void addCallBack(Object task, ICallback<DATA> callBack) {
            if (bindCallBacks != null) {
                bindCallBacks.put(task, callBack);
            }
        }

        public void cancelAllTaskBinder() {
            if (taskExecutor != null) {
                taskExecutor.cancle();
            }
        }

        public boolean cancel(Object task) {
            Map<Object, ICallback<DATA>> callbacks = getRegisterCallbacks();
            if (callbacks == null) {
                return false;
            }
            if (task.equals(realTask)) {
                if (!callbacks.isEmpty()) {
                    if (callback != null) {
                        callback.onPostExecute(realTask, Code.CANCEL, null, null);
                    }
                    callback = null;
                } else {
                    cancelAllTaskBinder();
                }
                return true;
            } else {
                Iterator<? extends Map.Entry<Object, ? extends ICallback<? extends BASE_DATA>>> iterator = callbacks.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Object, ? extends ICallback<? extends BASE_DATA>> data = iterator.next();
                    Object ct = data.getKey();
                    if (task.equals(ct)) {
                        iterator.remove();
                        data.getValue().onPostExecute(ct, Code.CANCEL, null, null);
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean isRunning() {
            if (taskExecutor != null) {
                return taskExecutor.isRunning();
            }
            return false;
        }
    }

    public static <DATA> TaskExecutor<DATA> createExecutor(IDataSource<DATA> dataSource, boolean isExeRefresh, ICallback<DATA> callback) {
        return TaskExecutors.create(dataSource, isExeRefresh, new LogCallback<>(callback));
    }

    public static <DATA> TaskExecutor<DATA> createExecutor(IAsyncDataSource<DATA> dataSource, boolean isExeRefresh, ICallback<DATA> callback) {
        return TaskExecutors.create(dataSource, isExeRefresh, new LogCallback<>(callback));
    }

    public static <DATA> TaskExecutor<DATA> createExecutor(ITask<DATA> task, ICallback<DATA> callback) {
        return TaskExecutors.create(task, new LogCallback<>(callback));
    }

    public static <DATA> TaskExecutor<DATA> createExecutor(IAsyncTask<DATA> task, ICallback<DATA> callback) {
        return TaskExecutors.create(task, new LogCallback<>(callback));
    }

    public static <DATA> TaskExecutor<DATA> createExecutorWithoutLog(ITask<DATA> task, ICallback<DATA> callback) {
        return TaskExecutors.create(task, callback);
    }

    public static <DATA> TaskExecutor<DATA> createExecutorWithoutLog(IAsyncTask<DATA> task, ICallback<DATA> callback) {
        return TaskExecutors.create(task, callback);
    }

    public static <DATA> TaskExecutor<DATA> create(IDataSource<DATA> dataSource, boolean isExeRefresh, ICallback<DATA> callback, Executor executor) {
        return TaskExecutors.create(dataSource, isExeRefresh, callback, executor);
    }

    public static <DATA> TaskExecutor<DATA> create(ITask<DATA> task, ICallback<DATA> callback, Executor executor) {
        return TaskExecutors.create(task, callback, executor);
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
                MVCLogUtil.d("{} task={} code={}  data={}", "执行结果", task, code, data);
            } else {
                MVCLogUtil.e(exception, "{} task={} code={} data={}", "执行结果", task, code, data);
            }
            if (callback != null) {
                callback.onPostExecute(task, code, exception, data);
            }
        }
    }

}
