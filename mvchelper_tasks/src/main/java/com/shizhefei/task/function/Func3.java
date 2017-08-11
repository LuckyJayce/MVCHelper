package com.shizhefei.task.function;

/**
 * Created by luckyjayce on 2017/4/16.
 */

public interface Func3<D1, D2, D3, R> {
    R call(D1 d1, D2 d2, D3 d3) throws Exception;
}
