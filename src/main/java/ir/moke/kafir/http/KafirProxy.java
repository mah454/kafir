package ir.moke.kafir.http;

import ir.moke.kafir.annotation.*;
import ir.moke.kafir.utils.JsonUtils;
import ir.moke.kafir.utils.Parser;
import ir.moke.kafir.utils.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

class KafirProxy implements InvocationHandler {
    private final String baseUri;
    private final HttpClient client;
    private final Map<String, String> headers = new HashMap<>();

    public KafirProxy(Kafir.KafirBuilder builder) {
        baseUri = builder.getBaseUri();
        Optional.ofNullable(builder.getHeaders()).ifPresent(headers::putAll);
        HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
        Optional.ofNullable(builder.getAuthenticator()).ifPresent(httpClientBuilder::authenticator);
        Optional.ofNullable(builder.getVersion()).ifPresent(httpClientBuilder::version);
        Optional.ofNullable(builder.getConnectionTimeout()).ifPresent(httpClientBuilder::connectTimeout);
        Optional.ofNullable(builder.getExecutorService()).ifPresent(httpClientBuilder::executor);
        Optional.ofNullable(builder.getSslContext()).ifPresent(httpClientBuilder::sslContext);

        client = httpClientBuilder.build();
    }

    private static HttpRequest.BodyPublisher initializeBodyPublisher(Method method, Object[] args) {
        Object o = extractRequestBody(method, args);
        if (o == null) return HttpRequest.BodyPublishers.noBody();
        return HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(o));
    }

    private static Object extractRequestBody(Method method, Object[] args) {
        for (int i = 0; i < method.getParameters().length; i++) {
            if (method.getParameters()[i].getDeclaredAnnotations().length == 0) {
                return args[i];
            }
        }
        return null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        setAnnotationHeaders(method);

        StringBuilder queryParameters = new StringBuilder();
        Map<String, String> pathParameters = new HashMap<>();
        String apiPath = "";
        Class<?> returnType = ReflectionUtils.getReturnTypeClass(method);
        HttpMethod methodType = HttpMethod.GET;
        if (method.isAnnotationPresent(GET.class)) {
            apiPath = method.getDeclaredAnnotation(GET.class).value();
        } else if (method.isAnnotationPresent(POST.class)) {
            apiPath = method.getDeclaredAnnotation(POST.class).value();
            methodType = HttpMethod.POST;
        } else if (method.isAnnotationPresent(PUT.class)) {
            apiPath = method.getDeclaredAnnotation(PUT.class).value();
            methodType = HttpMethod.PUT;
        } else if (method.isAnnotationPresent(DELETE.class)) {
            apiPath = method.getDeclaredAnnotation(DELETE.class).value();
            methodType = HttpMethod.DELETE;
        }

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Parameter parameter = method.getParameters()[i];
                Object arg = args[i];
                if (arg != null) {
                    if (parameter.isAnnotationPresent(QueryParameter.class)) {
                        String paramKey = parameter.getDeclaredAnnotation(QueryParameter.class).value();
                        if (Iterable.class.isAssignableFrom(arg.getClass())) {
                            for (Object item : (Iterable<?>) arg) {
                                queryParameters.append(paramKey).append("=").append(String.valueOf(item).trim()).append("&");
                            }
                        } else {
                            queryParameters.append(paramKey).append("=").append(String.valueOf(arg).trim()).append("&");
                        }
                    }

                    if (parameter.isAnnotationPresent(PathParameter.class)) {
                        String value = parameter.getDeclaredAnnotation(PathParameter.class).value();
                        pathParameters.put(value, String.valueOf(arg).trim());
                    }
                }
            }
        }

        String formatedUri = "";
        if (queryParameters.isEmpty() && pathParameters.isEmpty()) {
            formatedUri = baseUri + apiPath;
        } else {
            formatedUri = Parser.parsePathParameters(baseUri + apiPath, pathParameters) + "?" + queryParameters;
        }

        URI uri = URI.create(formatedUri);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(uri);
        if (!headers.isEmpty()) headers.forEach(requestBuilder::setHeader);
        requestBuilder = switch (methodType) {
            case GET -> requestBuilder.GET();
            case POST -> requestBuilder.POST(initializeBodyPublisher(method, args));
            case PUT -> requestBuilder.PUT(initializeBodyPublisher(method, args));
            case DELETE -> requestBuilder.DELETE();
        };

        HttpRequest request = requestBuilder.build();
        if (Future.class.isAssignableFrom(returnType)) {
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
//                    .thenApply(item -> Parser.parseStringResponse(method, returnType, item.body()));
        } else if (HttpResponse.class.isAssignableFrom(returnType)) {
            return client.send(request, new JsonBodyHandler<>(method));
        } else {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = httpResponse.body();
            return Parser.parseStringResponse(method, returnType, body);
        }
    }

    private void setAnnotationHeaders(Method method) {
        Class<?> clazz = ReflectionUtils.getClassOfMethod(method);
        if (clazz.isAnnotationPresent(Header.class)) {
            Header headerAnnotation = clazz.getDeclaredAnnotation(Header.class);
            for (HeaderParameter headerParams : headerAnnotation.parameters()) {
                String key = headerParams.key();
                String value = headerParams.value();
                headers.put(key, value);
            }
        }


        if (method.isAnnotationPresent(Header.class)) {
            Header methodHeaders = method.getDeclaredAnnotation(Header.class);
            for (HeaderParameter headerParams : methodHeaders.parameters()) {
                String key = headerParams.key();
                String value = headerParams.value();
                headers.put(key, value);
            }
        }
    }
}
