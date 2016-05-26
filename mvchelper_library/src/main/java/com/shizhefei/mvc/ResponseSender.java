package com.shizhefei.mvc;

/**
 * 用于请求结束时发送数据或者发送异常
 * @param <DATA>
 */
public interface ResponseSender<DATA> {

	public void sendError(Exception exception);

	public void sendData(DATA data);

}