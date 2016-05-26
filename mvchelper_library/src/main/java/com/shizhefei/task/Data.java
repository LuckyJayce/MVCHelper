package com.shizhefei.task;

/**
 * 数据容器，用来装载成功，失败的数据
 * 
 * @author LuckyJayce
 *
 * @param <SUCCESS>
 * @param <FAIL>
 */
public class Data<SUCCESS, FAIL> {
	private SUCCESS success;
	private FAIL fail;
	private boolean isSuccess;

	private Data() {
		super();
	}

	private Data(FAIL fail) {
		super();
	}

	private void setSuccess(SUCCESS success) {
		this.success = success;
		this.isSuccess = true;
	}

	private void setFail(FAIL fail) {
		this.fail = fail;
		this.isSuccess = false;
	}

	public SUCCESS getSuccess() {
		return success;
	}

	public FAIL getFail() {
		return fail;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	/**
	 * 创建成功的返回数据
	 * 
	 * @param success
	 *            成功需要返回的数据
	 * @return
	 */
	public static <SUCCESS, FAIL> Data<SUCCESS, FAIL> madeSuccess(SUCCESS success) {
		Data<SUCCESS, FAIL> result = new Data<SUCCESS, FAIL>();
		result.setSuccess(success);
		return result;
	}

	/**
	 * 创建失败的返回数据
	 * 
	 * @param fail
	 *            失败需要返回的数据
	 * @return
	 */
	public static <SUCCESS, FAIL> Data<SUCCESS, FAIL> madeFail(FAIL fail) {
		Data<SUCCESS, FAIL> result = new Data<SUCCESS, FAIL>();
		result.setFail(fail);
		return result;
	}

}