package com.shizhefei.test.models.datasource;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.shizhefei.mvc.RequestHandle;

public class VolleyRequestHandle implements RequestHandle {
	private Request<?> request;

	public VolleyRequestHandle(RequestQueue requestQueue, Request<?> request) {
		super();
		this.request = request;
	}

	@Override
	public void cancle() {
		request.cancel();
	}

	@Override
	public boolean isRunning() {
		return false;
	}
}