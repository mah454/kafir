package ir.moke.kafir.http;

import java.net.http.HttpRequest;

public interface Interceptor {

    HttpRequest intercept(HttpRequest request);
}
