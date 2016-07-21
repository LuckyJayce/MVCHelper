package com.shizhefei.task;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.data.Data2;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

public class TaskHandle implements RequestHandle {
    private final Object task;
    private final ICallback callBack;
    private final int type;
    private WeakReference<TaskHelper.TaskImp> taskImpWeakReference;
    public static final int TYPE_RUN = 1;
    public static final int TYPE_CACHE = 2;
    public static final int TYPE_ATTACH = 3;

    public TaskHandle(int type, Object exeTask, ICallback callBack, TaskHelper.TaskImp taskImp) {
        this.task = exeTask;
        this.callBack = callBack;
        this.type = type;
        if (taskImp != null) {
            taskImpWeakReference = new WeakReference<>(taskImp);
        }
    }

    @Override
    public void cancle() {
        if (taskImpWeakReference == null) {
            return;
        }
        TaskHelper.TaskImp taskImp = taskImpWeakReference.get();
        if (taskImp != null) {
            taskImp.cancle();
        }
    }

    public void cancelTaskIfMoreCallbackUnregisterCallback() {
        if (taskImpWeakReference == null) {
            return;
        }
        TaskHelper.TaskImp taskImp = taskImpWeakReference.get();
        if (taskImp == null) {
            return;
        }
        List<Data2<Object, ICallback>> calls = taskImp.getCallbacks();
        Iterator<Data2<Object, ICallback>> iterator = calls.iterator();
        while (iterator.hasNext()) {
            Data2<Object, ICallback> data = iterator.next();
            Object ct = data.getValue1();
            if (task.equals(ct)) {
                if (calls.size() == 1) {
                    taskImp.cancle();
                } else {
                    iterator.remove();
                    data.getValue2().onPostExecute(ct, Code.CANCEL, null, null);
                }
                return;
            }
        }
    }

    public int getRunType() {
        return type;
    }

    @Override
    public boolean isRunning() {
        if (taskImpWeakReference == null) {
            return false;
        }
        TaskHelper.TaskImp taskImp = taskImpWeakReference.get();
        if (taskImp != null) {
            return taskImp.isRunning();
        }
        return false;
    }
}