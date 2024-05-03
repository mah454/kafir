package ir.moke.kafir.utils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static Object parseStringResponse(Method method, Class<?> returnType, String body) {
        if (String.class.isAssignableFrom(returnType)) {
            return String.valueOf(body);
        } else if (Void.class.isAssignableFrom(returnType)) {
            return Void.class;
        } else if (Boolean.class.isAssignableFrom(returnType) || boolean.class.isAssignableFrom(returnType)) {
            return Boolean.parseBoolean(body);
        } else if (Iterable.class.isAssignableFrom(returnType)) {
            return parseIterableJson(method, body);
        } else if (Map.class.isAssignableFrom(returnType)) {
            return JsonUtils.toMap(body);
        } else {
            return JsonUtils.toObject(body, returnType);
        }
    }

    @SuppressWarnings("unchecked")
    public static Object parseIterableJson(Method method, String body) {
        ParameterizedType parameterizedType = ReflectionUtils.getMethodGenericReturnType(method);
        Class<? extends Collection<?>> collectionType = (Class<? extends Collection<?>>) parameterizedType.getRawType();
        if (ReflectionUtils.isGenericType(parameterizedType.getActualTypeArguments()[0])) {
            Class<?> rawOfGeneric = ReflectionUtils.getRawOfGeneric(parameterizedType);
            return JsonUtils.toObject(body, collectionType, rawOfGeneric);
        } else {
            Class<?> genericType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
            return JsonUtils.toObject(body, collectionType, genericType);
        }
    }

    public static String parsePathParameters(String apiPath, Map<String, String> parameters) {
        final Pattern pattern = Pattern.compile("\\{.*?\\}");
        Matcher matcher = pattern.matcher(apiPath);

        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String str = matcher.group();
            String replacement = parameters.get(str.replace("{", "").replace("}", ""));
            matcher.appendReplacement(sb, replacement);
        }

        matcher.appendTail(sb);
        return sb.toString();
    }


    public static String parseQueryParameter(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        map.forEach((k, v) -> {
            sb.append(k).append("=").append(v).append("&");
        });
        if (sb.isEmpty()) return "";
        return sb.substring(0, sb.length() - 1);
    }
}
