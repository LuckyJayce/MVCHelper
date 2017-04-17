package com.shizhefei.task;

import com.shizhefei.mvc.ResponseSender;

/**
 * Created by luckyjayce on 2017/4/17.
 */

public class ResponseSenderCallback<DATA> implements ICallback<DATA> {
    private ResponseSender<DATA> sender;
    private Code code;

    public ResponseSenderCallback(ResponseSender<DATA> sender) {
        this.sender = sender;
    }

    public Code getCode() {
        return code;
    }

    @Override
    public void onPreExecute(Object task) {

    }

    @Override
    public void onProgress(Object task, int percent, long current, long total, Object extraData) {
        sender.sendProgress(current, total, extraData);
    }

    @Override
    public void onPostExecute(Object task, Code code, Exception exception, DATA data) {
        this.code = code;
        switch (code) {
            case SUCCESS:
                sender.sendData(data);
                break;
            case EXCEPTION:
                sender.sendError(exception);
                break;
        }
    }
}
