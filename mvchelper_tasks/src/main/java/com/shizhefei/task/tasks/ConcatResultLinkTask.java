package com.shizhefei.task.tasks;


import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.Code;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ResponseSenderCallback;
import com.shizhefei.task.function.Func3;
import com.shizhefei.task.imp.SimpleCallback;

import java.lang.ref.WeakReference;

class ConcatResultLinkTask<D1, DATA> extends LinkTask<DATA> {
    private IAsyncTask<D1> task;
    private Func3<Code, Exception, D1, IAsyncTask<DATA>> func;
    private WeakReference<IAsyncTask<DATA>> linkNextTask;

    public ConcatResultLinkTask(IAsyncTask<D1> task, Func3<Code, Exception, D1, IAsyncTask<DATA>> func) {
        this.func = func;
        this.task = task;
    }

    @Override
    public RequestHandle execute(final ResponseSender<DATA> sender) throws Exception {
        final SimpleTaskHelper taskHelper = new SimpleTaskHelper();
        taskHelper.execute(task, new SimpleCallback<D1>() {
            @Override
            public void onProgress(Object task, int percent, long current, long total, Object extraData) {
                super.onProgress(task, percent, current, total, extraData);
                sender.sendProgress(current, total, extraData);
            }

            @Override
            public void onPostExecute(Object task, Code code, Exception exception, D1 d1) {
                switch (code) {
                    case SUCCESS:
                    case EXCEPTION:
                        try {
                            IAsyncTask<DATA> task2 = func.call(code, exception, d1);
                            linkNextTask = new WeakReference<>(task2);
                            taskHelper.execute(task2, new ResponseSenderCallback<>(sender));
                        } catch (Exception e) {
                            sender.sendError(e);
                        }
                        break;
                    case CANCEL:
                        func = null;
                        break;
                }
            }
        });
        return taskHelper;
    }

    @Override
    public String toString() {
        return "ConcatLinkTask->{task:" + task + ",linkNextTask:" + (linkNextTask == null ? null : linkNextTask.get() + "}");
    }
}