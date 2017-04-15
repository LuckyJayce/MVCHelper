package com.shizhefei.task;

import com.shizhefei.mvc.RequestHandle;

/**
 * 执行类的接口
 */
public interface TaskExecutor<DATA> extends RequestHandle {

    RequestHandle execute();

    ICallback<DATA> getCallback();

    Object getTask();

    boolean isExeRefresh();
}