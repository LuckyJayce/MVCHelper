package com.shizhefei.task;

/**
 * task回调
 * Created by LuckyJayce on 2016/7/17.
 */
public interface ICallback<DATA> {

    /**
     * 执行task之前的回调
     */
    public void onPreExecute(Object task);

    /**
     * 进度更新回调
     *
     * @param percent
     * @param current
     * @param total
     * @param extraData
     */
    public void onProgress(Object task, int percent, long current, long total, Object extraData);

    /**
     * 执行完成的回调
     *
     * @param task
     * @param code      失败，异常，取消
     * @param exception
     * @param data
     */
    public void onPostExecute(Object task, Code code, Exception exception, DATA data);
}
