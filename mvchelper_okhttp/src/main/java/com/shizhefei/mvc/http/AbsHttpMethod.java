package com.shizhefei.mvc.http;

import com.shizhefei.mvc.RequestHandle;

import java.util.HashMap;
import java.util.Map;

public abstract class AbsHttpMethod<METHOD extends AbsHttpMethod,CALLBACK> implements RequestHandle {

	private String url;

	public AbsHttpMethod() {
	}

	public AbsHttpMethod(String url) {
		this.url = url;
	}

	public abstract void executeAsync(CALLBACK callback);

	private Map<String, Object> params = new HashMap<String, Object>();
	private Map<String, String> headers = new HashMap<String, String>();

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public METHOD addHeader(String key, String value) {
		this.headers.put(key, value);
		return (METHOD) this;
	}

	public METHOD addParam(String key, int value) {
		params.put(key, String.valueOf(value));
        return (METHOD) this;
	}

	public METHOD addParam(String key, boolean value) {
		params.put(key, String.valueOf(value));
        return (METHOD) this;
	}

	public METHOD addParam(String key, double value) {
		params.put(key, String.valueOf(value));
        return (METHOD) this;
	}

	public METHOD addParam(String key, float value) {
		params.put(key, String.valueOf(value));
        return (METHOD) this;
	}

	public METHOD addParam(String key, long value) {
		params.put(key, String.valueOf(value));
        return (METHOD) this;
	}

	public METHOD addParam(String key, String value) {
		params.put(key, value);
        return (METHOD) this;
	}

	public METHOD addParams(Map<String, ?> params) {
		this.params.putAll(params);
        return (METHOD) this;
	}
}
