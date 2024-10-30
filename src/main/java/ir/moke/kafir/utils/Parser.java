package ir.moke.kafir.utils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.http.HttpResponse;
import java.util.concurrent.Future;

public class Parser {
    public static Object parseStringResponse(Method method, String body) {
        Class<?> returnType = method.getReturnType();

        if (HttpResponse.class.isAssignableFrom(returnType)) {
            ParameterizedType pt = (ParameterizedType) method.getGenericReturnType();
            return handleHttpResponse(pt, body);
        } else if (Future.class.isAssignableFrom(returnType)) {
            ParameterizedType pt = (ParameterizedType) method.getGenericReturnType();
            if (ReflectionUtils.isGenericType(pt.getActualTypeArguments()[0])) {
                pt = (ParameterizedType) pt.getActualTypeArguments()[0];
                if (HttpResponse.class.isAssignableFrom((Class<?>) pt.getRawType())) {
                    return handleHttpResponse(pt, body);
                } else {
                    return JsonUtils.toObject(body, pt.getTypeName());
                }
            } else {
                return parseTypes((Class<?>) pt.getActualTypeArguments()[0], body);
            }
        } else {
            return parseTypes(returnType, body);
        }
    }

    public static Object handleHttpResponse(ParameterizedType pt, String body) {
        Type actualTypeArgument = pt.getActualTypeArguments()[0];
        if (ReflectionUtils.isGenericType(actualTypeArgument)) {
            return JsonUtils.toObject(body, actualTypeArgument.getTypeName());
        } else {
            return parseTypes((Class<?>) actualTypeArgument, body);
        }
    }

    private static Object parseTypes(Class<?> returnType, String body) {
        if (String.class.isAssignableFrom(returnType)) {
            return body;
        } else if (Void.class.isAssignableFrom(returnType)) {
            return Void.class;
        } else if (boolean.class.isAssignableFrom(returnType) || Boolean.class.isAssignableFrom(returnType)) {
            return Boolean.parseBoolean(body);
        } else if (int.class.isAssignableFrom(returnType) || Integer.class.isAssignableFrom(returnType)) {
            return Integer.parseInt(body);
        } else if (long.class.isAssignableFrom(returnType) || Long.class.isAssignableFrom(returnType)) {
            return Long.parseLong(body);
        } else if (double.class.isAssignableFrom(returnType) || Double.class.isAssignableFrom(returnType)) {
            return Double.parseDouble(body);
        } else if (float.class.isAssignableFrom(returnType) || Float.class.isAssignableFrom(returnType)) {
            return Float.parseFloat(body);
        } else if (BigInteger.class.isAssignableFrom(returnType)) {
            return new BigInteger(body);
        } else if (BigDecimal.class.isAssignableFrom(returnType)) {
            return new BigDecimal(body);
        } else if (ReflectionUtils.isGenericType(returnType)) {
            return JsonUtils.toObject(body, returnType.getTypeName());
        } else {
            return JsonUtils.toObject(body, returnType);
        }
    }
}
