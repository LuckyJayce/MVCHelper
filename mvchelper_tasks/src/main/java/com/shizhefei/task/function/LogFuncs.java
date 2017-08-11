package com.shizhefei.task.function;

import com.shizhefei.utils.MVCLogUtil;

/**
 * Created by luckyjayce on 2017/7/22.
 */

public class LogFuncs {

    public static <T, R> Func1<T, R> made(String tag, Func1<T, R> func) {
        return new LogFunc1<>(tag, func);
    }

    public static <D1, D2, R> Func2<D1, D2, R> made(String tag, Func2<D1, D2, R> func) {
        return new LogFunc2<>(tag, func);
    }

    public static <D1, D2, D3, R> Func3<D1, D2, D3, R> made(String tag, Func3<D1, D2, D3, R> func) {
        return new LogFunc3<>(tag, func);
    }

    private static class LogFunc1<T, R> implements Func1<T, R> {
        private final String tag;
        private Func1<T, R> func1;

        public LogFunc1(String tag, Func1<T, R> func1) {
            this.tag = tag;
            this.func1 = func1;
        }

        @Override
        public R call(T data) throws Exception {
            MVCLogUtil.d("{} LogFunc1 tag={} start 参数data={}", "LogFunc1", tag, data);
            R r = func1.call(data);
            MVCLogUtil.d("{} tag={} end 返回值={}", "LogFunc1", tag, r);
            return r;
        }
    }

    private static class LogFunc2<D1, D2, R> implements Func2<D1, D2, R> {
        private final String tag;
        private Func2<D1, D2, R> func1;

        public LogFunc2(String tag, Func2<D1, D2, R> func1) {
            this.tag = tag;
            this.func1 = func1;
        }


        @Override
        public R call(D1 d1, D2 d2) throws Exception {
            MVCLogUtil.d("{} tag={} start 参数d1={} 参数d2={}", "LogFunc2", tag, d1, d2);
            R r = func1.call(d1, d2);
            MVCLogUtil.d("{} tag={} end 返回值={}", "LogFunc2", tag, r);
            return r;
        }
    }

    private static class LogFunc3<D1, D2, D3, R> implements Func3<D1, D2, D3, R> {
        private final String tag;
        private Func3<D1, D2, D3, R> func1;

        public LogFunc3(String tag, Func3<D1, D2, D3, R> func1) {
            this.tag = tag;
            this.func1 = func1;
        }


        @Override
        public R call(D1 d1, D2 d2, D3 d3) throws Exception {
            MVCLogUtil.d("{} tag={} start 参数d1={} 参数d2={} 参数d3={}", "LogFunc3", tag, d1, d2, d3);
            R r = func1.call(d1, d2, d3);
            MVCLogUtil.d("{} tag={} end 返回值={}", "LogFunc3", tag, r);
            return r;
        }
    }
}
