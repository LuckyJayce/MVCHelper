package com.shizhefei.task.tasks;

import android.os.Handler;
import android.os.Looper;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.IAsyncTask;


/**
 * Created by luckyjayce on 2017/7/20.
 */

class DelayLinkTask<DATA> extends LinkTask<DATA> {

    private final IAsyncTask<DATA> task;
    private final int delay;
    private Handler handler;

    public DelayLinkTask(IAsyncTask<DATA> task, int delay) {
        super();
        this.task = task;
        this.delay = delay;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public RequestHandle execute(final ResponseSender<DATA> sender) throws Exception {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    task.execute(sender);
                } catch (Exception e) {
                    sender.sendError(e);
                }
            }
        };
        handler.postDelayed(runnable, delay);
        return new RequestHandle() {
            @Override
            public void cancle() {
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
        return "DelayLinkTask->{" + task + ",delay:" + delay + "}";
    }
}
