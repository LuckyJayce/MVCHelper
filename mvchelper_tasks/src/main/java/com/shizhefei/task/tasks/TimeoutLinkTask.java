package com.shizhefei.task.tasks;

import android.os.Handler;
import android.os.Looper;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.Code;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ResponseSenderCallback;


/**
 * Created by luckyjayce on 2017/7/20.
 */

public class TimeoutLinkTask<DATA> extends LinkTask<DATA> {
    private final Handler handler;
    private final int timeout;
    private IAsyncTask<DATA> task;
    private Code resultCode;

    public TimeoutLinkTask(IAsyncTask<DATA> task, int timeout) {
        this.task = task;
        this.timeout = timeout;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public RequestHandle execute(final ResponseSender<DATA> sender) throws Exception {
        final SimpleTaskHelper taskHelper = new SimpleTaskHelper();
        taskHelper.execute(task, new ResponseSenderCallback<DATA>(sender) {
            @Override
            public void onPostExecute(Object task, Code code, Exception exception, DATA data) {
                super.onPostExecute(task, code, exception, data);
                resultCode = code;
            }
        });
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (resultCode == null) {
                    sender.sendError(new TimeoutException());
                    taskHelper.cancle();
                }
            }
        };
        handler.postDelayed(runnable, timeout);
        return new RequestHandle() {
            @Override
            public void cancle() {
                taskHelper.cancle();
                handler.removeCallbacks(runnable);
            }

            @Override
            public boolean isRunning() {
                return false;
            }
        };
    }

    @Override
    public String toString() {
        return "TimeoutLinkTask->{task:" + task + "}";
    }

    public static class TimeoutException extends Exception {

    }
}
