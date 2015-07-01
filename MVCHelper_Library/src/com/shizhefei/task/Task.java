package com.shizhefei.task;

public interface Task<SUCCESS, FAIL> {

	/**
	 * 执行
	 * 
	 * @return
	 * @throws Exception
	 */
	public Data<SUCCESS, FAIL> execute(ProgressSender progressSender) throws Exception;

	/**
	 * 注意cancle 和 execute 有可能不在同一个线程，cancle可能在UI线程被调用
	 */
	public void cancle();

}
