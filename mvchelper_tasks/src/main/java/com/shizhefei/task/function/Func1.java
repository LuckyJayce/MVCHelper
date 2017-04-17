package com.shizhefei.task.function;

public interface Func1<T, R> {
    R call(T data) throws Exception;
}