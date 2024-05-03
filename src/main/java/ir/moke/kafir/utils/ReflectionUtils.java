package ir.moke.kafir.utils;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ReflectionUtils {

    public static Type getReturnType(Method method) {
        try {
            Class<?>[] parameterTypes = Arrays.stream(method.getParameters())
                    .map(Parameter::getType)
                    .toArray(Class<?>[]::new);

            return method.getDeclaringClass()
                    .getDeclaredMethod(method.getName(), parameterTypes)
                    .getReturnType();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getReturnTypeClass(Method method) {
        try {
            Class<?>[] parameterTypes = Arrays.stream(method.getParameters())
                    .map(Parameter::getType)
                    .toArray(Class<?>[]::new);

            return method.getDeclaringClass()
                    .getDeclaredMethod(method.getName(), parameterTypes)
                    .getReturnType();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ParameterizedType getMethodGenericReturnType(Method method) {
        return (ParameterizedType) method.getGenericReturnType();
    }

    public static Class<?> getRawOfGeneric(ParameterizedType parameterizedType) {
        return (Class<?>) ((ParameterizedType) parameterizedType.getActualTypeArguments()[0]).getRawType();
    }

    public static ParameterizedType getGenericIndexType(ParameterizedType types, int index) {
        return (ParameterizedType) types.getActualTypeArguments()[index];
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

    public static boolean isCollection(ParameterizedType actualTypeArgument) {
        return Collection.class.isAssignableFrom((Class<?>) actualTypeArgument.getRawType());
    }

    public static boolean isMap(ParameterizedType parameterizedType) {
//        return Map.class.isAssignableFrom((Class<?>) ((ParameterizedType) actualTypeArgument.getActualTypeArguments()[0]).getRawType());
        return Map.class.isAssignableFrom((Class<?>) parameterizedType.getRawType());
    }
}
