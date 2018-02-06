package com.shizhefei.task;


import com.shizhefei.mvc.ProgressSender;

/**
 * 同步task，直接执行耗时的任务，直接返回数据
 * Created by LuckyJayce on 2016/7/17.
 */
public interface ITask<DATA> extends ISuperTask<DATA> {

    DATA execute(ProgressSender progressSender) throws Exception;

    void cancel();
}
