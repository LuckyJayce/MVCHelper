package com.shizhefei.task;


import com.shizhefei.mvc.ProgressSender;

/**
 * Created by LuckyJayce on 2016/7/17.
 */
public interface ITask<DATA> extends ISuperTask<DATA> {

    DATA execute(ProgressSender progressSender) throws Exception;

    void cancel();
}
