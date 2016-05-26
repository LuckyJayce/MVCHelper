package com.shizhefei.task;

/**
 * 状态码
 * 
 * @author LuckyJayce
 *
 */
public enum Code {
	/** 成功 */
	SUCESS,
	/** 失败 */
	FAIL,
	/** task执行的时候抛出异常 */
	EXCEPTION,
	/** task被取消 */
	CANCLE;
}