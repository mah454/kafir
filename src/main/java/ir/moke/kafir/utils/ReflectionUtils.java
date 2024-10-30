package ir.moke.kafir.utils;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ReflectionUtils {

    public static Class<?> getClassOfMethod(Method method) {
        return method.getDeclaringClass();
    }

    public static boolean isGenericType(Method method) {
        Type returnType = method.getGenericReturnType();
        return isGenericType(returnType);
    }

    public static boolean isGenericType(Type type) {
        return type instanceof ParameterizedType ||
                type instanceof TypeVariable ||
                type instanceof GenericArrayType;
    }
}
