package com.shizhefei.task;

/**
 * UI回调
 * 
 * @author LuckyJayce
 *
 * @param <SUCCESS>
 *            执行成功返回的数据类型
 * @param <FAIL>
 *            执行失败返回的数据类型
 */
public interface Callback<SUCCESS, FAIL> {

	/**
	 * 执行task之前的回调
	 */
	public void onPreExecute();

	/**
	 * 进度更新回调
	 * 
	 * @param percent
	 * @param current
	 * @param total
	 * @param exraData
	 */
	public void onProgressUpdate(int percent, long current, long total, Object exraData);

	/**
	 * 执行task结束的回调，通过code判断是什么情况结束task，（成功，失败，异常，取消）
	 * 
	 * @param code
	 *            返回码
	 * @param exception
	 *            异常信息（throw exception 时才有值）
	 * @param success
	 *            成功返回的数据
	 * @param fail
	 *            失败返回的数据
	 */
	public void onPostExecute(Code code, Exception exception, SUCCESS success, FAIL fail);

}