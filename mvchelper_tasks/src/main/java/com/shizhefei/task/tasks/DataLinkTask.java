package com.shizhefei.task.tasks;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

class DataLinkTask<DATA> extends LinkTask<DATA> {
    private DATA data;

    public DataLinkTask(DATA data) {
        this.data = data;
    }

    @Override
    public RequestHandle execute(ResponseSender<DATA> sender) throws Exception {
        sender.sendData(data);
        return null;
    }

    @Override
    public String toString() {
        return "DataLinkTask->{data:" + data+"}";
    }
}