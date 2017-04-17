package com.shizhefei.task.tasks;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.Code;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.task.function.Func1;
import com.shizhefei.task.imp.SimpleCallback;

class ConcatLinkTask<D1, DATA> extends LinkTask<DATA> {
    private IAsyncTask<D1> task;
    private Func1<D1, IAsyncTask<DATA>> func1;
    private ProxyRequestHandle proxyRequestHandle;

    public ConcatLinkTask(IAsyncTask<D1> task, Func1<D1, IAsyncTask<DATA>> func1) {
        this.func1 = func1;
        this.task = task;
        proxyRequestHandle = new ProxyRequestHandle();
    }

    @Override
    public RequestHandle execute(final ResponseSender<DATA> sender) throws Exception {
        proxyRequestHandle.requestHandle = TaskHelper.createExecutor(task, new SimpleCallback<D1>() {
            @Override
            public void onProgress(Object task, int percent, long current, long total, Object extraData) {
                super.onProgress(task, percent, current, total, extraData);
                sender.sendProgress(current, total, extraData);
            }

            @Override
            public void onPostExecute(Object task, Code code, Exception exception, D1 d1) {
                switch (code) {
                    case SUCCESS:
                        try {
                            IAsyncTask<DATA> task2 = func1.call(d1);
                            proxyRequestHandle.requestHandle = task2.execute(sender);
                        } catch (Exception e) {
                            sender.sendError(e);
                            e.printStackTrace();
                        }
                        break;
                    case EXCEPTION:
                        sender.sendError(exception);
                        break;
                    case CANCEL:
                        func1 = null;
                        break;
                }
            }
        }).execute();
        return proxyRequestHandle;
    }


    private static class ProxyRequestHandle implements RequestHandle {
        private RequestHandle requestHandle;

        @Override
        public void cancle() {
            if (requestHandle != null) {
                requestHandle.cancle();
            }
        }

        @Override
        public boolean isRunning() {
            if (requestHandle != null) {
                return requestHandle.isRunning();
            }
            return false;
        }
    }
}