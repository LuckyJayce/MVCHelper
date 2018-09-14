package com.shizhefei.task.datasource;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.IDataSource;
import com.shizhefei.task.IAsyncTask;

/**
 * Created by luckyjayce on 2017/4/17.
 */

public class DataSources {

    /**
     * 先执行task，再执行dataSource，该函数会返回新的dataSource
     * @param task
     * @param dataSource
     * @param <DATA>
     * @return
     */
    public static <DATA> IAsyncDataSource<DATA> concatWith(IAsyncTask<Void> task, IAsyncDataSource<DATA> dataSource) {
        return new ConcatDataSource<>(task, dataSource);
    }

    /**
     * 先执行task，再执行dataSource，该函数会返回新的dataSource
     * @param task
     * @param dataSource
     * @param <DATA>
     * @return
     */
    public static <DATA> IAsyncDataSource<DATA> concatWith(IAsyncTask<Void> task, IDataSource<DATA> dataSource) {
        return new ConcatDataSource<>(task, async(dataSource));
    }

    /**
     * 同步dataSource变为异步dataSource
     * @param dataSource
     * @param <DATA>
     * @return
     */
    public static <DATA> IAsyncDataSource<DATA> async(IDataSource<DATA> dataSource) {
        return new AsyncDataSource<>(dataSource);
    }
}
