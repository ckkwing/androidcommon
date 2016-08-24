package com.echen.androidcommon.HTTP;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import com.google.gson.internal.$Gson$Types;

import okhttp3.Request;

/**
 * Created by echen on 2016/7/18.
 */
public abstract class ResultCallback<T> {
    public Type type;

    public ResultCallback(){
        type = getSuperclassTypeParameter(getClass());
    }

    static Type getSuperclassTypeParameter(Class<?> subclass)
    {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class)
        {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    public abstract void onError(Request request, Exception e);

    public abstract void onResponse(T response);
}
