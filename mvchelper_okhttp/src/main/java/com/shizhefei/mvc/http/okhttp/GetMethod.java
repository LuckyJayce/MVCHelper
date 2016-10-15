package com.shizhefei.mvc.http.okhttp;

import com.shizhefei.mvc.http.UrlBuilder;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class GetMethod extends HttpMethod<GetMethod> {
    public GetMethod() {

    }

    public GetMethod(String url) {
        super(url);
    }

    public GetMethod(OkHttpClient httpClient, String url) {
        super(httpClient, url);
    }

    @Override
    protected Request.Builder buildRequset(String url, Map<String, Object> params) {
        url = new UrlBuilder(url).params(params).build();
        return new Request.Builder().url(url).get();
    }

}
