package com.shizhefei.task;

/**
 * 任务
 * 
 * @author LuckyJayce
 *
 * @param <SUCCESS>
 *            成功的数据类型
 * @param <FAIL>
 *            失败的数据类型
 */
public interface Task<SUCCESS, FAIL> extends SuperTask<SUCCESS, FAIL> {

	/**
	 * 执行后台任务
	 * 
	 * @param progressSender
	 *            进度更新发送者
	 * @return
	 * @throws Exception
	 */
	public Data<SUCCESS, FAIL> execute(ProgressSender progressSender) throws Exception;

	/**
	 * 注意cancle 和 execute 有可能不在同一个线程，cancle可能在UI线程被调用
	 */
	public void cancle();

}
