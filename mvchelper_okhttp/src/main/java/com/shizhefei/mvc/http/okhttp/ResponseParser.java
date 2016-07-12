package com.shizhefei.mvc.http.okhttp;

import okhttp3.Response;

/**
 * 数据解析器
 * Created by LuckyJayce on 2016/7/10.
 */
public interface ResponseParser<DATA> {
    DATA parse(Response response) throws  Exception;
}
