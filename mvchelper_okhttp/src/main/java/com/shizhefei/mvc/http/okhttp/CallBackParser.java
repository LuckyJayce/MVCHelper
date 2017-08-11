package com.shizhefei.mvc.http.okhttp;

import com.shizhefei.mvc.ResponseSender;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CallBackParser<DATA> implements Callback, ResponseParser<DATA> {
    private final ResponseParser<DATA> responseParser;
    private ResponseSender<DATA> sender;

    public CallBackParser(ResponseSender<DATA> sender, ResponseParser<DATA> responseParser) {
        this.sender = sender;
        this.responseParser = responseParser;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        sender.sendError(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            DATA data = parse(response);
            sender.sendData(data);
        } catch (Exception e) {
            sender.sendError(e);
        }
    }

    @Override
    public DATA parse(Response response) throws Exception {
        return responseParser.parse(response);
    }
}