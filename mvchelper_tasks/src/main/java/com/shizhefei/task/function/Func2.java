package com.shizhefei.task.function;

/**
 * Created by luckyjayce on 2017/4/16.
 */

public interface Func2<D1, D2, R> {
    R call(D1 d1, D2 d2) throws Exception;
}
