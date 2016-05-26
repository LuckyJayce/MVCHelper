package com.shizhefei.mvc;

public interface ResponseSender<DATA> {

	public void sendError(Exception exception);

	public void sendData(DATA data);

}