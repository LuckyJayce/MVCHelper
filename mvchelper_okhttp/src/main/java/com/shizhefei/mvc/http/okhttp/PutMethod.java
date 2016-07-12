package com.shizhefei.mvc.http.okhttp;

import java.util.Map;
import java.util.Map.Entry;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PutMethod extends HttpMethod<PutMethod> {
	public PutMethod() {
	}

	public PutMethod(String url) {
		super(url);
	}

	public PutMethod(OkHttpClient httpClient, String url) {
		super(httpClient, url);
	}

	@Override
	protected Request.Builder buildRequset(String url, Map<String, Object> params) {
		FormBody.Builder builder = new FormBody.Builder();
		if (params != null) {
			for (Entry<String, ?> entry : params.entrySet()) {
				builder.add(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}
		RequestBody formBody = builder.build();
		return new Request.Builder().url(url).put(formBody);
	}

}
