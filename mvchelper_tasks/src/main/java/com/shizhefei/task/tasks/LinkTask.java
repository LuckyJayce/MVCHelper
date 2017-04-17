package com.shizhefei.task.tasks;

import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.function.Func1;
import com.shizhefei.task.function.Func2;

public abstract class LinkTask<D> implements IAsyncTask<D> {

    public <D2, DATA> LinkTask<DATA> combine(IAsyncTask<D2> task2, Func2<D, D2, DATA> func) {
        return Tasks.combine(this, task2, func);
    }

    public <DATA> LinkTask<DATA> concatMap(Func1<D, IAsyncTask<DATA>> func1) {
        return Tasks.concatMap(this, func1);
    }

    public <DATA> LinkTask<DATA> concatWith(IAsyncTask<DATA> task2) {
        return Tasks.concatWith(this, task2);
    }
}
