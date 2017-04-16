package com.shizhefei.task.tasks;


import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.IDataSource;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ITask;
import com.shizhefei.task.function.Func1;
import com.shizhefei.task.function.Func2;

/**
 * Created by luckyjayce on 2017/4/16.
 */

public class LinkTasks {

    public static <DATA> LinkTask<DATA> async(ITask<DATA> task) {
        return new AsyncLinkTask<>(task, true);
    }

    public static <DATA> LinkTask<DATA> just(DATA data) {
        return new DataLinkTask<>(data);
    }

    public static <DATA> LinkTask<DATA> create(ITask<DATA> task) {
        return new AsyncLinkTask<>(task, true);
    }

    public static <DATA> LinkTask<DATA> create(IAsyncTask<DATA> task) {
        if (task instanceof LinkTask) {
            return (LinkTask<DATA>) task;
        }
        return new LinkProxyTask<>(task);
    }

    public static <DATA> LinkTask<DATA> create(IDataSource<DATA> dataSource, boolean isExeRefresh) {
        return new AsyncLinkTask<>(dataSource, isExeRefresh);
    }

    public static <DATA> LinkTask<DATA> create(IAsyncDataSource<DATA> dataSource, boolean isExeRefresh) {
        return new AsyncLinkTask<>(dataSource, isExeRefresh);
    }

    public static <D, DATA> LinkTask<DATA> concatMap(IAsyncTask<D> task, Func1<D, IAsyncTask<DATA>> func) {
        return new ConcatLinkTask<>(task, func);
    }

    /**
     * 连接两个task
     *
     * @param task
     * @param task2
     * @param <D>
     * @param <DATA>
     * @return
     */
    public static <D, DATA> LinkTask<DATA> concatWith(IAsyncTask<D> task, final IAsyncTask<DATA> task2) {
        return new ConcatLinkTask<>(task, new Func1<D, IAsyncTask<DATA>>() {
            @Override
            public IAsyncTask<DATA> call(D data) throws Exception {
                return task2;
            }
        });
    }

    public static <D1, D2, DATA> LinkTask<DATA> combine(IAsyncTask<D1> task1, IAsyncTask<D2> task2, Func2<D1, D2, DATA> func) {
        return new CombineTask<>(task1, task2, func);
    }
}
