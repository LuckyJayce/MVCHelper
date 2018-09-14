package com.shizhefei.task;

import android.os.Handler;
import android.os.Looper;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.IDataSource;
import com.shizhefei.mvc.ProgressSender;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

import java.util.concurrent.Executor;

/**
 * Created by luckyjayce on 2017/4/15.
 */

class TaskExecutors {

    public static <DATA> TaskExecutor<DATA> create(IDataSource<DATA> dataSource, boolean isExeRefresh, ICallback<DATA> callback) {
        return create(dataSource, isExeRefresh, callback, AsyncTaskV25.THREAD_POOL_EXECUTOR);
    }

    public static <DATA> TaskExecutor<DATA> create(IDataSource<DATA> dataSource, boolean isExeRefresh, ICallback<DATA> callback, Executor executor) {
        return new SyncDataSourceExecutor<>(dataSource, isExeRefresh, callback, executor);
    }

    public static <DATA> TaskExecutor<DATA> create(IAsyncDataSource<DATA> dataSource, boolean isExeRefresh, ICallback<DATA> callback) {
        return new AsyncDataSourceExecutor<>(dataSource, isExeRefresh, callback);
    }

    public static <DATA> TaskExecutor<DATA> create(ITask<DATA> task, ICallback<DATA> callback) {
        return create(task, callback, AsyncTaskV25.THREAD_POOL_EXECUTOR);
    }

    public static <DATA> TaskExecutor<DATA> create(ITask<DATA> task, ICallback<DATA> callback, Executor executor) {
        return new SyncTaskExecutor<>(task, callback, executor);
    }

    public static <DATA> TaskExecutor<DATA> create(IAsyncTask<DATA> task, ICallback<DATA> callback) {
        return new AsyncTaskExecutor<>(task, callback);
    }

    /**
     * IAsyncTask和IAsyncDataSource 抽象执行类
     *
     * @param <DATA>
     */
    private static abstract class AbsAsyncTaskExecutor<DATA> implements TaskExecutor<DATA> {
        private final ISuperTask<DATA> realTask;
        private ICallback<DATA> callback;
        private RequestHandle requestHandle;
        private TaskResponseSender<DATA> responseSender;

        public AbsAsyncTaskExecutor(ISuperTask<DATA> task, ICallback<DATA> callback) {
            this.realTask = task;
            this.callback = callback;
            if (callback == null) {
                responseSender = new TaskNoCallbackResponseSender<>();
            } else {
                responseSender = new TaskHasCallbackResponseSender<>();
            }
        }

        @Override
        public final RequestHandle execute() {
            responseSender.sendPreExecute(realTask, callback);
            try {
                requestHandle = executeImp(responseSender);
            } catch (Exception e) {
                responseSender.sendError(e);
            }
            return this;
        }

        protected abstract RequestHandle executeImp(ResponseSender<DATA> responseSender) throws Exception;

        @Override
        public void cancle() {
            if (requestHandle != null) {
                requestHandle.cancle();
            }
            responseSender.sendCancel();
        }


        @Override
        public ICallback<DATA> getCallback() {
            return callback;
        }

        @Override
        public ISuperTask<DATA> getTask() {
            return realTask;
        }

        @Override
        public boolean isRunning() {
            return responseSender.isRunning();
        }
    }

    /**
     * 同步task和IDataSource的抽象实现类
     *
     * @param <DATA>
     */
    private static abstract class AbsSyncTaskExecutor<DATA> extends AsyncTaskV25<Object, Object, DATA> implements TaskExecutor<DATA> {

        private final ISuperTask<DATA> realTask;
        private final TaskResponseSender<DATA> responseSender;
        private final Executor executor;
        private ICallback<DATA> callback;

        public AbsSyncTaskExecutor(ISuperTask<DATA> task, ICallback<DATA> callback, Executor executor) {
            super();
            this.callback = callback;
            this.realTask = task;
            this.executor = executor;
            if (callback == null) {
                responseSender = new TaskNoCallbackResponseSender<>();
            } else {
                responseSender = new TaskHasCallbackResponseSender<>();
            }
        }

        @Override
        public RequestHandle execute() {
            responseSender.sendPreExecute(realTask, callback);
            executeOnExecutor(executor, Boolean.TRUE);
            return this;
        }

        @Override
        protected DATA doInBackground(Object... params) {
            try {
                return executeImp(responseSender);
            } catch (Exception e) {
                responseSender.sendError(e);
            }
            return null;
        }

        protected abstract DATA executeImp(ProgressSender sender) throws Exception;

        @Override
        protected void onPostExecute(DATA result) {
            super.onPostExecute(result);
            responseSender.sendData(result);
        }

        @Override
        public void cancle() {
            cancelImp();
            cancel(true);
            responseSender.sendCancel();
        }

        protected abstract void cancelImp();

        @Override
        public boolean isRunning() {
            return responseSender.isRunning();
        }

        @Override
        public ICallback<DATA> getCallback() {
            return callback;
        }

        @Override
        public ISuperTask<DATA> getTask() {
            return realTask;
        }
    }

    /**
     * IAsyncDataSource执行类
     *
     * @param <DATA>
     */
    private static class AsyncDataSourceExecutor<DATA> extends AbsAsyncTaskExecutor<DATA> {
        private final boolean isExeRefresh;
        private IAsyncDataSource<DATA> dataSource;

        public AsyncDataSourceExecutor(IAsyncDataSource<DATA> dataSource, boolean isExeRefresh, ICallback<DATA> callback) {
            super(dataSource, callback);
            this.dataSource = dataSource;
            this.isExeRefresh = isExeRefresh;
        }

        @Override
        protected RequestHandle executeImp(ResponseSender<DATA> responseSender) throws Exception {
            if (isExeRefresh) {
                return dataSource.refresh(responseSender);
            }
            return dataSource.loadMore(responseSender);
        }

        @Override
        public boolean isExeRefresh() {
            return isExeRefresh;
        }
    }

    /**
     * IAsyncTask的执行类
     *
     * @param <DATA>
     */
    private static class AsyncTaskExecutor<DATA> extends AbsAsyncTaskExecutor<DATA> {
        private IAsyncTask<DATA> task;

        public AsyncTaskExecutor(IAsyncTask<DATA> task, ICallback<DATA> callback) {
            super(task, callback);
            this.task = task;
        }

        @Override
        protected RequestHandle executeImp(ResponseSender<DATA> responseSender) throws Exception {
            return task.execute(responseSender);
        }

        @Override
        public boolean isExeRefresh() {
            return true;
        }
    }

    /**
     * IDataSource的执行类
     *
     * @param <DATA>
     */
    private static class SyncDataSourceExecutor<DATA> extends AbsSyncTaskExecutor<DATA> {
        private final boolean isExeRefresh;
        private IDataSource<DATA> task;

        public SyncDataSourceExecutor(IDataSource<DATA> task, boolean isExeRefresh, ICallback<DATA> callback, Executor executor) {
            super(task, callback, executor);
            this.task = task;
            this.isExeRefresh = isExeRefresh;
        }

        @Override
        protected DATA executeImp(ProgressSender sender) throws Exception {
            if (isExeRefresh) {
                return task.refresh();
            }
            return task.loadMore();
        }

        @Override
        protected void cancelImp() {

        }

        @Override
        public boolean isExeRefresh() {
            return isExeRefresh;
        }
    }

    /**
     * ITask 的执行类
     *
     * @param <DATA>
     */
    private static class SyncTaskExecutor<DATA> extends AbsSyncTaskExecutor<DATA> {
        private ITask<DATA> task;

        public SyncTaskExecutor(ITask<DATA> task, ICallback<DATA> callback, Executor executor) {
            super(task, callback, executor);
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

        @Override
        public boolean isExeRefresh() {
            return true;
        }
    }

    private interface TaskResponseSender<DATA> extends ResponseSender<DATA> {
        void sendCancel();

        void sendPreExecute(Object realTask, ICallback<DATA> callback);

        boolean isRunning();
    }

    private static class TaskNoCallbackResponseSender<DATA> implements TaskResponseSender<DATA> {
        //isRunning可能被子线程改变值
        private volatile boolean isRunning;

        @Override
        public void sendPreExecute(Object realTask, ICallback<DATA> callback) {
            isRunning = true;
        }

        @Override
        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public void sendError(Exception exception) {
            isRunning = false;
        }

        @Override
        public void sendData(DATA data) {
            isRunning = false;
        }

        @Override
        public void sendProgress(long current, long total, Object extraData) {

        }

        @Override
        public void sendCancel() {
            isRunning = false;
        }
    }

    private static class TaskHasCallbackResponseSender<DATA> implements TaskResponseSender<DATA> {
        private Handler handler;
        //callback 和 realTask 只在主线程调用
        private ICallback<DATA> callback;
        private Object realTask;
        private boolean isRunning;

        public TaskHasCallbackResponseSender() {
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void sendPreExecute(Object realTask, ICallback<DATA> callback) {
            this.realTask = realTask;
            this.callback = callback;
            isRunning = true;
            onPreExecute();
        }

        @Override
        public void sendError(Exception exception) {
            onPostExecute(Code.EXCEPTION, exception, null);
        }

        @Override
        public void sendData(DATA data) {
            onPostExecute(Code.SUCCESS, null, data);
        }

        @Override
        public void sendCancel() {
            onPostExecute(Code.CANCEL, null, null);
        }

        @Override
        public void sendProgress(long current, long total, Object extraData) {
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

        @Override
        public boolean isRunning() {
            return isRunning;
        }

        private void onPreExecute() {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPreExecuteMainThread();
                    }
                });
            } else {
                onPreExecuteMainThread();
            }
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

        private void onPreExecuteMainThread() {
            ICallback<DATA> c = callback;
            if (c != null) {
                c.onPreExecute(realTask);
            }
        }

        private void onProgressMainThread(final int percent, final long current, final long total, final Object extraData) {
            ICallback<DATA> c = callback;
            if (c != null) {
                c.onProgress(realTask, percent, current, total, extraData);
            }
        }

        private void onPostExecuteMainThread(final Code code, final Exception exception, final DATA data) {
            if (!isRunning) {
                return;
            }
            isRunning = false;
            ICallback<DATA> c = callback;
            if (c != null) {
                c.onPostExecute(realTask, code, exception, data);
            }
            realTask = null;
            callback = null;
        }
    }
}
