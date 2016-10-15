package com.shizhefei.mvc.http.okhttp;

import com.shizhefei.mvc.ProgressSender;
import com.shizhefei.mvc.data.Data2;
import com.shizhefei.mvc.http.MimeUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PostFileMethod extends HttpMethod<PostFileMethod> {
    public PostFileMethod() {
    }

    private Map<String, Data2<String, RequestBody>> httpbodys = new HashMap<String, Data2<String, RequestBody>>();
    private CountingRequestBody.Listener listener;

    public PostFileMethod(String url) {
        super(url);
    }

    public PostFileMethod(OkHttpClient httpClient, String url) {
        super(httpClient, url);
    }

    public PostFileMethod addParam(String key, String fileName, RequestBody requestBody) {
        httpbodys.put(key, new Data2<String, RequestBody>(fileName, requestBody));
        return this;
    }

    public PostFileMethod addParam(String key, String fileName, File file) {
        String mime = MimeUtils.getFileMimeType(file);
        MediaType mediaType = MediaType.parse(mime);
        return addParam(key, fileName, RequestBody.create(mediaType, file));
    }

    public PostFileMethod addParam(String key, File file) {
        return addParam(key, file.getName(), file);
    }

    public void setProgressListener(CountingRequestBody.Listener listener) {
        this.listener = listener;
    }

    public void setProgressListener(final ProgressSender progressSender) {
        this.listener = new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength) {
                progressSender.sendProgress(bytesWritten, contentLength, null);
            }
        };
    }

    @Override
    protected Request.Builder buildRequset(String url, Map<String, Object> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (params != null) {
            for (Entry<String, ?> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        if (httpbodys != null) {
            for (Entry<String, Data2<String, RequestBody>> entry : httpbodys.entrySet()) {
                String key = entry.getKey();
                Data2<String, RequestBody> body = entry.getValue();
                builder.addFormDataPart(key, body.getValue1(), body.getValue2());
            }
        }
        RequestBody formBody = builder.build();
        if (listener != null) {
            formBody = new CountingRequestBody(formBody, listener);
        }
        return new Request.Builder().url(url).post(formBody);
    }
}
