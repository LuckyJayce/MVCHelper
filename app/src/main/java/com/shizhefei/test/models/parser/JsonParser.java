package com.shizhefei.test.models.parser;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.shizhefei.mvc.http.NetworkExeption;
import com.shizhefei.mvc.http.okhttp.ResponseParser;

import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created by LuckyJayce on 2016/7/11.
 */
public abstract class JsonParser<DATA> implements ResponseParser<DATA> {

    private final Type type;

    public JsonParser(Class<?> clas) {
        type = getSuperclassTypeParameter(clas);
    }

    public JsonParser() {
        type = getSuperclassTypeParameter(getClass());
    }

    @Override
    public DATA parse(Response response) throws Exception {
        if (response.isSuccessful()) {
            return parse(response.body().charStream());
        }
        throw new NetworkExeption(response);
    }

    protected DATA parse(Reader json) throws Exception {
        DATA data = new Gson().fromJson(json, type);
        return data;
    }

    protected DATA parse(String json) throws Exception {
        DATA data = new Gson().fromJson(json, type);
        return data;
    }

    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }
}
