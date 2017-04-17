package com.shizhefei.test.models.task;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.IAsyncTask;

/**
 * Created by luckyjayce on 2017/4/17.
 */

public class InitTokenTask implements IAsyncTask<Void> {
    private int time;

    @Override
    public RequestHandle execute(ResponseSender<Void> sender) throws Exception {
        time++;
        if (time > 2) {
            sender.sendData(null);
        } else {
            sender.sendError(new Exception("初始化异常"));
        }
        return null;
    }
}
