package ir.moke.kafir.http;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    }

    @Override
    public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
        HttpResponse.BodySubscriber<String> subscriber = HttpResponse.BodySubscribers.ofString(Charset.defaultCharset());
        return HttpResponse.BodySubscribers.mapping(subscriber, this::getResult);
    }

    @SuppressWarnings("unchecked")
    private T getResult(String body) {
        try {
            return (T) Parser.parseStringResponse(method, body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
