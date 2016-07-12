package com.shizhefei.test.models.parser;

import com.google.gson.internal.$Gson$Types;
import com.shizhefei.mvc.http.NetworkExeption;
import com.shizhefei.mvc.http.okhttp.ResponseParser;
import com.shizhefei.test.models.exception.BizException;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created by LuckyJayce on 2016/7/11.
 */
public abstract class MeizhiParser<DATA> implements ResponseParser<DATA> {

    @Override
    public final DATA parse(Response response) throws Exception {
        DATA data = parseImp(response);
        onParse(response, data);
        return data;
    }

    protected DATA parseImp(Response response) throws Exception {
        if (response.isSuccessful()) {
            String json = response.body().string();
            JSONObject jsonObject = new JSONObject(json);
            boolean error = jsonObject.getBoolean("error");
            if (!error) {
                String result = jsonObject.getString("results");
                DATA data = new JsonParser<DATA>(getClass()) {
                }.parse(result);
                return data;
            }
            throw new BizException();
        }
        throw new NetworkExeption(response);
    }

    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    protected abstract void onParse(Response responses, DATA data);
}
