package com.shizhefei.task;

import com.shizhefei.mvc.RequestHandle;

import java.lang.ref.WeakReference;

/**
 * 用于取消task，判断task是否执行
 */
public class TaskHandle implements RequestHandle {
    private final WeakReference<Object> taskReference;
    private final int type;
    private WeakReference<TaskHelper.MultiTaskBindProxyCallBack> taskImpWeakReference;
    public static final int TYPE_RUN = 1;
    public static final int TYPE_CACHE = 2;
    public static final int TYPE_ATTACH = 3;

    public TaskHandle(int type, Object exeTask, TaskHelper.MultiTaskBindProxyCallBack taskImp) {
        this.type = type;
        this.taskReference = new WeakReference<>(exeTask);
        if (taskImp != null) {
            taskImpWeakReference = new WeakReference<>(taskImp);
        }
    }

    @Override
    public void cancle() {
        if (taskImpWeakReference == null) {
            return;
        }
        TaskHelper.MultiTaskBindProxyCallBack taskImp = taskImpWeakReference.get();
        Object task = taskReference.get();
        if (taskImp == null || task == null) {
            return;
        }
        taskImp.cancel(task);
    }

//    public void cancelAllTaskBinder() {
//        if (taskImpWeakReference == null) {
//            return;
//        }
//        TaskHelper.MultiTaskBindProxyCallBack taskImp = taskImpWeakReference.get();
//        if (taskImp != null) {
//            taskImp.cancelAllTaskBinder();
//        }
//    }

    public int getRunType() {
        return type;
    }

    @Override
    public boolean isRunning() {
        if (taskImpWeakReference == null) {
            return false;
        }
        TaskHelper.MultiTaskBindProxyCallBack taskImp = taskImpWeakReference.get();
        if (taskImp != null) {
            return taskImp.isRunning();
        }
        return false;
    }
}