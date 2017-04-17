package com.shizhefei.mvc.http.okhttp;

import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.mvc.http.AbsHttpMethod;

import java.util.Map;
import java.util.Map.Entry;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class HttpMethod<METHOD extends HttpMethod> extends AbsHttpMethod<METHOD, Callback> {
    private static OkHttpClient defaultClient = new OkHttpClient();
    private Call call;

    public HttpMethod() {

    }

    public HttpMethod(String url) {
        this(defaultClient, url);
    }

    public HttpMethod(OkHttpClient httpClient, String url) {
        super(url);
        this.client = httpClient;
    }

    public static void setDefaultOkHttpClient(OkHttpClient okHttpClient) {
        defaultClient = okHttpClient;
    }

    public static OkHttpClient getDefaultOkHttpClient() {
        return defaultClient;
    }

    public HttpMethod setOkHttpClient(OkHttpClient client) {
        this.client = client;
        return this;
    }

    private OkHttpClient client;

    protected abstract Request.Builder buildRequset(String url, Map<String, Object> params);

    /**
     * 执行异步请求，执行的结果会自动调用sender的sender.sendData,执行失败或者出现异常会调用sender.sendError
     *
     * @param sender         mvchelper的datasource的ResponseSender
     * @param responseParser 数据解析器
     * @param <DATA>
     */
    public final <DATA> void executeAsync(ResponseSender<DATA> sender, final ResponseParser<DATA> responseParser) {
        executeAsync(new CallBackParser<DATA>(sender, responseParser));
    }

    /**
     * 执行异步请求,callback回调请求结果
     *
     * @param callback 请求的回调
     */
    @Override
    public final void executeAsync(Callback callback) {
        Request request = buildRequest();
        call = client.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 执行同步请求,解析Response
     *
     * @param responseParser 数据解析器
     * @param <DATA>
     * @return 返回请求后解析得到的数据
     * @throws Exception
     */
    public final <DATA> DATA executeSync(ResponseParser<DATA> responseParser) throws Exception {
        return responseParser.parse(executeSync());
    }

    /**
     * 执行同步请求
     *
     * @return 返回请求回来的 Response
     * @throws Exception
     */
    public final Response executeSync() throws Exception {
        Request request = buildRequest();
        call = client.newCall(request);
        return call.execute();
    }


    private Request buildRequest() {
        Request.Builder requestBuilder = buildRequset(getUrl(), getParams());
        Map<String, String> headers = getHeaders();
        for (Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
        }
        return requestBuilder.build();
    }

    @Override
    public void cancle() {
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    public boolean isRunning() {
        return call != null && !call.isExecuted();
    }
}
