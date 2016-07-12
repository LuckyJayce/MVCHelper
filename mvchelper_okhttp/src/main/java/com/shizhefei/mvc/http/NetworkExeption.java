package com.shizhefei.mvc.http;


import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetworkExeption extends Exception {
    private static final long serialVersionUID = 2316643815390526053L;
    private final Response response;
    private int httpCode;
    private String url;

    public NetworkExeption(Response response) {
        super();
        this.response = response;
        this.httpCode = response.code();
        Request request = response.request();
        if (request != null) {
            HttpUrl httpUrl = request.url();
            if (httpUrl != null) {
                this.url = httpUrl.toString();
            }
        }
    }

    public NetworkExeption(String url, Response response) {
        super();
        this.response = response;
        this.httpCode = response.code();
        this.url = url;
    }

    public Response getResponse() {
        return response;
    }

    private String message;

    @Override
    public String getMessage() {
        if (message == null) {
            message = toMessage();
        }
        return message;
    }

    public String getUrl() {
        return url;
    }

    public int getHttpCode() {
        return httpCode;
    }

    private final String toMessage() {
        ResponseBody body = response.body();
        StringBuilder builder = new StringBuilder("url:").append(url);
        builder.append(" code:").append(httpCode);
        builder.append(" message:").append(response.message());
        if (body != null) {
            try {
                builder.append(" body:").append(body.string());
                body.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }

}
