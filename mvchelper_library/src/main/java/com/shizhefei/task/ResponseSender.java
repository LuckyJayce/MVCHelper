package com.shizhefei.task;

public interface ResponseSender<SUCCESS, FAIL> {

	public void sendError(Exception exception);

	public void sendData(SUCCESS data);

	public void sendFail(FAIL data);

}