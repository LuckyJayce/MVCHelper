package com.shizhefei.task.tasks;


import com.shizhefei.task.Code;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.function.Func1;
import com.shizhefei.task.function.Func2;
import com.shizhefei.task.function.Func3;

public abstract class LinkTask<D> implements IAsyncTask<D> {

    public <D2, DATA> LinkTask<DATA> combine(IAsyncTask<D2> task2, Func2<D, D2, DATA> func) {
        return Tasks.combine(this, task2, func);
    }

    public <DATA> LinkTask<DATA> concatMap(Func1<D, IAsyncTask<DATA>> func1) {
        return Tasks.concatMap(this, func1);
    }

    public <DATA> LinkTask<DATA> concatMap(Func3<Code, Exception, D, IAsyncTask<DATA>> func) {
        return Tasks.concatMap(this, func);
    }

    public <DATA> LinkTask<DATA> concatWith(IAsyncTask<DATA> task2) {
        return Tasks.concatWith(this, task2);
    }
//
//    public LinkTask<D> retry(Func2<IAsyncTask<D>, Exception, IAsyncTask<D>> func2) {
//        return Tasks.retry(this, func2);
//    }
//
//    public LinkTask<D> retry() {
//        return Tasks.retry(this);
//    }
//
//    public LinkTask<D> retry(int maxTimes) {
//        return Tasks.retry(this, maxTimes);
//    }
//
//    public ISuperTask<?>[] getChildTasks(){
//        return null;
//    }
//
//    public ISuperTask<?>[] getTasks(){
//        return null;
//    }
//
//    public ISuperTask<?>[] getCurrentChildTasks(){
//        return null;
//    }
//
//    public ISuperTask<?>[] getCurrentTasks(){
//        return null;
//    }
//
//    public Throwable[] getThrowables(){
//        return null;
//    }
}
