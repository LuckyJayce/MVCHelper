package com.shizhefei.task.tasks;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.IAsyncTask;

class LinkProxyTask<DATA> extends LinkTask<DATA> {
    private IAsyncTask<DATA> task;

    public LinkProxyTask(IAsyncTask<DATA> task) {
        this.task = task;
    }

    @Override
    public RequestHandle execute(ResponseSender<DATA> sender) throws Exception {
        return task.execute(sender);
    }

    @Override
    public String toString() {
        return "LinkProxyTask->{" + task+"}";
    }
}