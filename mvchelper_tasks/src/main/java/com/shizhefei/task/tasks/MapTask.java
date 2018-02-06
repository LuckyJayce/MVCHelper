package com.shizhefei.task.tasks;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.Code;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.function.Func1;
import com.shizhefei.task.imp.SimpleCallback;

/**
 * Created by luckyjayce on 2017/8/22.
 */

class MapTask<DATA, D> extends LinkTask<DATA> {

    private final IAsyncTask<D> task;
    private final Func1<D, DATA> func;

    public MapTask(IAsyncTask<D> task, Func1<D, DATA> func) {
        super();
        this.task = task;
        this.func = func;
    }

    @Override
    public RequestHandle execute(final ResponseSender<DATA> sender) throws Exception {
        final SimpleTaskHelper taskHelper = new SimpleTaskHelper();
        taskHelper.execute(task, new SimpleCallback<D>() {
            @Override
            public void onProgress(Object task, int percent, long current, long total, Object extraData) {
                super.onProgress(task, percent, current, total, extraData);
                sender.sendProgress(current, total, extraData);
            }

            @Override
            public void onPostExecute(Object task, Code code, Exception exception, D d1) {
                switch (code) {
                    case SUCCESS:
                        try {
                            DATA data = func.call(d1);
                            sender.sendData(data);
                        } catch (Exception e) {
                            sender.sendError(e);
                        }
                        break;
                    case EXCEPTION:
                        sender.sendError(exception);
                        break;
                    case CANCEL:
                        break;
                }
            }
        });
        return taskHelper;
    }
}
