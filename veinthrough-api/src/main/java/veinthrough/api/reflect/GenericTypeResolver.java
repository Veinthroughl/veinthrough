package veinthrough.api.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface GenericTypeResolver {
    /**
     * Method 1: by instance of a generic class
     */
    static String GenericTypeNameOf(Object object) {
        Class<?> clazz = object.getClass();

        // (1) Maybe: ...<T>
        // (2) Maybe: ...<java.lang.String>
        ParameterizedType parameterizedType = (ParameterizedType)clazz.getGenericSuperclass();

        // (1) Maybe: T
        // (2) Maybe: java.lang.String
        Type type = parameterizedType.getActualTypeArguments()[0];

        // (1) should use getTypeName()
        // (2) use getClass() is not correct, [?] type.getClass() will get java.lang.Class
//        return type.getClass().getTypeName();
        return type.getTypeName();
    }

    static Class<?> GenericTypeOf(Object object) throws ClassNotFoundException {
        return Class.forName(GenericTypeNameOf(object));
    }
}
