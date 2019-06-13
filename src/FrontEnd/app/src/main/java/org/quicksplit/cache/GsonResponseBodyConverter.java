package org.quicksplit.cache;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

import static org.quicksplit.Utils.typeToClass;

public final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private final GsonResponseListener listener;
    private final Class clsType;
    private final boolean isCacheable;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter, Type type, Annotation[] annotations, GsonResponseListener listener) {
        this.gson = gson;
        this.adapter = adapter;
        this.listener = listener;

        clsType = typeToClass(type);
        isCacheable = isCacheable(annotations);
    }

    @Override public T convert(ResponseBody value) throws IOException {
        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        try {
            T response = adapter.read(jsonReader);
            if (listener != null && isCacheable) listener.onCacheableResponse(clsType, response);
            return response;
        } finally {
            value.close();
        }
    }

    boolean isCacheable(Annotation[] annotations){
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(Cacheable.class)) return true;
        }
        return false;
    }
}
