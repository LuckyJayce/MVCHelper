package com.shizhefei.task.datasource;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.IDataSource;
import com.shizhefei.task.IAsyncTask;

/**
 * Created by luckyjayce on 2017/4/17.
 */

public class DataSources {

    public static <DATA> IAsyncDataSource<DATA> concatWith(IAsyncTask<Void> task, IAsyncDataSource<DATA> dataSource) {
        return new ConcatDataSource<>(task, dataSource);
    }

    public static <DATA> IAsyncDataSource<DATA> concatWith(IAsyncTask<Void> task, IDataSource<DATA> dataSource) {
        return new ConcatDataSource<>(task, async(dataSource));
    }

    public static <DATA> IAsyncDataSource<DATA> async(IDataSource<DATA> dataSource) {
        return new AsyncDataSource<>(dataSource);
    }
}
