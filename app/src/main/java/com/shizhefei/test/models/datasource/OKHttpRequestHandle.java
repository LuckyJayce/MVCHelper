package com.shizhefei.test.models.datasource;

import com.shizhefei.mvc.RequestHandle;
import com.squareup.okhttp.Call;


public class OKHttpRequestHandle implements RequestHandle {

    private final Call call;

    public OKHttpRequestHandle(Call call) {
        super();
        this.call = call;
    }

    @Override
    public void cancle() {
        call.cancel();
    }

    @Override
    public boolean isRunning() {
        return false;
    }
}