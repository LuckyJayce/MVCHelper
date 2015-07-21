package com.shizhefei.test.models.datasource;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.shizhefei.mvc.RequestHandle;

public class VolleyRequestHandle implements RequestHandle {
	private RequestQueue requestQueue;

	public VolleyRequestHandle(RequestQueue requestQueue, Request<?> request) {
		super();
		this.requestQueue = requestQueue;
		request.setTag(this);
	}

	@Override
	public void cancle() {
		requestQueue.cancelAll(this);
	}

	@Override
	public boolean isRunning() {
		return false;
	}
}