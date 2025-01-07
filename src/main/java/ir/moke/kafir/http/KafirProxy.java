package ir.moke.kafir.http;

import ir.moke.kafir.utils.HttpUtils;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

class KafirProxy implements InvocationHandler {
    private final Kafir.KafirBuilder kafirBuilder;

    public KafirProxy(Kafir.KafirBuilder builder) {
        this.kafirBuilder = builder;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            HttpClient httpClient = HttpUtils.buildHttpClient(kafirBuilder);
            HttpRequest httpRequest = HttpUtils.requestBuilder(kafirBuilder.getBaseUri(), method, args, kafirBuilder.getHeaders());
            Interceptor interceptor = kafirBuilder.getInterceptor();
            if (interceptor != null) {
                HttpRequest newRequest = interceptor.intercept(httpRequest);
                return HttpUtils.responseBuilder(method, newRequest, httpClient);
            } else {
                return HttpUtils.responseBuilder(method, httpRequest, httpClient);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
