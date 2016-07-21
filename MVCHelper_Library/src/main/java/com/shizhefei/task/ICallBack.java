package com.shizhefei.task;

/**
 * task的回调
 * Created by LuckyJayce on 2016/7/17.
 */
public interface ICallBack<DATA> {

    /**
     * 执行task之前的回调
     */
    void onPreExecute(Object task);

    /**
     * 进度更新回调
     *
     * @param percent
     * @param current
     * @param total
     * @param extraData
     */
    void onProgress(Object task, int percent, long current, long total, Object extraData);

    /**
     * 执行完成的回调
     * @param task
     * @param code
     * @param exception
     * @param data
     */
    void onPostExecute(Object task, Code code, Exception exception, DATA data);
}
