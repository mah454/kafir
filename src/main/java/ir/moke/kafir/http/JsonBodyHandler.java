package ir.moke.kafir.http;

import ir.moke.kafir.utils.Parser;
import ir.moke.kafir.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;

public class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {
    private final Method method;

    public JsonBodyHandler(Method method) {
        this.method = method;
        detectReturnType(method);
    }

    @Override
    public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
        HttpResponse.BodySubscriber<String> subscriber = HttpResponse.BodySubscribers.ofString(Charset.defaultCharset());
        return HttpResponse.BodySubscribers.mapping(subscriber, this::getResult);
    }

    @SuppressWarnings("unchecked")
    private T getResult(String s) {
        Class<?> returnType = detectReturnType(method);
        return (T) Parser.parseStringResponse(method, returnType, s);
    }

    private Class<?> detectReturnType(Method method) {
        try {
            // detect HttpResponse<?>
            ParameterizedType genericReturnType = ReflectionUtils.getMethodGenericReturnType(method);
            return (Class<?>) genericReturnType.getActualTypeArguments()[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
