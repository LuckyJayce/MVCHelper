package com.shizhefei.test.models.task;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.IAsyncTask;

/**
 * Created by luckyjayce on 2017/4/17.
 */

public class InitTokenTask implements IAsyncTask<Void> {
    public static  String userId;
    public static  String token;


    @Override
    public RequestHandle execute(ResponseSender<Void> sender) throws Exception {
        userId = "54896";
        token = "f09053jo";
        sender.sendData(null);
        return null;
    }
}
