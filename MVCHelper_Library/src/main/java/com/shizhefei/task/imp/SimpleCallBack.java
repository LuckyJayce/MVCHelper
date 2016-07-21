package com.shizhefei.task.imp;

import com.shizhefei.task.ICallBack;

/**
 * Created by LuckyJayce on 2016/7/17.
 * ICallback的空实现.
 *
 */
public abstract class SimpleCallBack<DATA> implements ICallBack<DATA> {
    @Override
    public void onPreExecute(Object task) {

    }

    @Override
    public void onProgress(Object task, int percent, long current, long total, Object extraData) {

    }
}
