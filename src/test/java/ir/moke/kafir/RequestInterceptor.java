package ir.moke.kafir;

import ir.moke.kafir.http.Interceptor;

import java.net.URI;
import java.net.http.HttpRequest;

public class RequestInterceptor implements Interceptor {
    @Override
    public HttpRequest intercept(HttpRequest request) {
        URI uri = request.uri();
        String host = uri.getHost();
        String scheme = uri.getScheme();
        int port = uri.getPort();

        return HttpRequest.newBuilder().uri(URI.create("%s://%s:%s/get?id=1&age=22".formatted(scheme, host, port))).build();
    }
}
