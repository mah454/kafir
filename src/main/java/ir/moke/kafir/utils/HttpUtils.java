package ir.moke.kafir.utils;

import ir.moke.kafir.annotation.*;
import ir.moke.kafir.http.HttpMethod;
import ir.moke.kafir.http.JsonBodyHandler;
import ir.moke.kafir.http.Kafir;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils {
    public static HttpClient buildHttpClient(Kafir.KafirBuilder builder) {
        HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
        Optional.ofNullable(builder.getAuthenticator()).ifPresent(httpClientBuilder::authenticator);
        Optional.ofNullable(builder.getVersion()).ifPresent(httpClientBuilder::version);
        Optional.ofNullable(builder.getConnectionTimeout()).ifPresent(httpClientBuilder::connectTimeout);
        Optional.ofNullable(builder.getExecutorService()).ifPresent(httpClientBuilder::executor);
        Optional.ofNullable(builder.getSslContext()).ifPresent(httpClientBuilder::sslContext);

        return httpClientBuilder.build();
    }

    public static HttpRequest requestBuilder(String baseUri, Method method, Object[] args, Map<String, String> dynamicHeaders) {
        StringBuilder queryParameters = new StringBuilder();
        Map<String, String> pathParameters = new HashMap<>();
        String apiPath = "";
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

//        if (!apiPath.startsWith("/")) throw new RuntimeException("Api path [%s] should be started with \"/\"".formatted(apiPath));

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

        String formatedUri;
        if (queryParameters.isEmpty() && pathParameters.isEmpty()) {
            formatedUri = baseUri + apiPath;
        } else {
            formatedUri = parsePathParameters(baseUri + apiPath, pathParameters) + "?" + (queryParameters.toString().endsWith("&") ? queryParameters.substring(0, queryParameters.length() - 1) : queryParameters);
        }

        URI uri = URI.create(formatedUri);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(uri);
        requestBuilder = switch (methodType) {
            case GET -> requestBuilder.GET();
            case POST -> requestBuilder.POST(initializeBodyPublisher(method, args));
            case PUT -> requestBuilder.PUT(initializeBodyPublisher(method, args));
            case DELETE -> requestBuilder.DELETE();
        };


        if (dynamicHeaders != null && !dynamicHeaders.isEmpty()) dynamicHeaders.forEach(requestBuilder::setHeader);

        return requestBuilder.build();
    }

    public static Object responseBuilder(Method method, HttpRequest httpRequest, HttpClient httpClient) throws IOException, InterruptedException {
        CompletableFuture<HttpResponse<Object>> future = httpClient.sendAsync(httpRequest, new JsonBodyHandler<>(method));
        Class<?> returnType = method.getReturnType();
        if (Future.class.isAssignableFrom(returnType)) {
            ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
            if (ReflectionUtils.isGenericType(parameterizedType.getActualTypeArguments()[0])) {
                ParameterizedType pt = (ParameterizedType) parameterizedType.getActualTypeArguments()[0];
                if (HttpResponse.class.isAssignableFrom((Class<?>) pt.getRawType())) {
                    // Used for HttpResponse<?>
                    return future;
                } else {
                    // Used for generic types
                    return future.thenApply(HttpResponse::body);
                }
            } else {
                // Used for any non-generic types like primitives
                return future.thenApply(HttpResponse::body);
            }
        } else if (HttpResponse.class.isAssignableFrom(returnType)) {
            return httpClient.send(httpRequest, new JsonBodyHandler<>(method));
        } else {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String body = httpResponse.body();
            return Parser.parseStringResponse(method, body);
        }
    }

    private static Map<String, String> setAnnotationHeaders(Method method) {
        Map<String, String> headers = new HashMap<>();
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
        return headers;
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
