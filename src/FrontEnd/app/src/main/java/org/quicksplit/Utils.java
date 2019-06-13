package org.quicksplit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.quicksplit.cache.Cacheable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Utils {

    public static Bitmap stringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Class typeToClass(Type type) {
        try {
            if (type instanceof ParameterizedType) {
                Type[] arguments = ((ParameterizedType) type).getActualTypeArguments();
                if (arguments.length > 0) {
                    Type argumentType = arguments[0];
                    return typeToClass(argumentType);
                }
            }
            return Class.forName(((Class) type).getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not find class with name " + type.getClass().getName());
        }
    }

    static boolean isCacheable(Annotation[] annotations){
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(Cacheable.class)) return true;
        }
        return false;
    }
}
