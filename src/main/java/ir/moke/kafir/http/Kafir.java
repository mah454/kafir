package ir.moke.kafir.http;

import javax.net.ssl.SSLContext;
import java.lang.reflect.Proxy;
import java.net.Authenticator;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class Kafir {

    public String baseUri;
    public HttpClient.Version version;
    public Authenticator authenticator;
    public ExecutorService executorService;
    public SSLContext sslContext;
    public Duration connectionTimeout;
    public Map<String, String> headers;

    private Kafir() {
    }

    public Kafir(KafirBuilder kafirBuilder) {
        this.baseUri = kafirBuilder.baseUri;
        this.version = kafirBuilder.version;
        this.authenticator = kafirBuilder.authenticator;
        this.executorService = kafirBuilder.executorService;
        this.sslContext = kafirBuilder.sslContext;
        this.connectionTimeout = kafirBuilder.connectionTimeout;
    }

    public static class KafirBuilder {
        private Map<String, String> headers;
        private Interceptor interceptor;
        private String baseUri;
        private HttpClient.Version version;
        private Authenticator authenticator;
        private ExecutorService executorService;
        private SSLContext sslContext;
        private Duration connectionTimeout;

        public String getBaseUri() {
            return baseUri;
        }

        public KafirBuilder setBaseUri(String baseUri) {
//            this.baseUri = baseUri.trim().replaceAll("^/+|/+$", "");
            this.baseUri = baseUri;
            return this;
        }

        public HttpClient.Version getVersion() {
            return version;
        }

        public KafirBuilder setVersion(HttpClient.Version version) {
            this.version = version;
            return this;
        }

        public Authenticator getAuthenticator() {
            return authenticator;
        }

        public KafirBuilder setAuthenticator(Authenticator authenticator) {
            this.authenticator = authenticator;
            return this;
        }

        public ExecutorService getExecutorService() {
            return executorService;
        }

        public KafirBuilder setExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public SSLContext getSslContext() {
            return sslContext;
        }

        public KafirBuilder setSslContext(SSLContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        public Duration getConnectionTimeout() {
            return connectionTimeout;
        }

        public KafirBuilder setConnectionTimeout(Duration connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public KafirBuilder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Interceptor getInterceptor() {
            return interceptor;
        }

        public KafirBuilder setInterceptor(Interceptor interceptor) {
            this.interceptor = interceptor;
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> T build(Class<T> t) {
            return (T) Proxy.newProxyInstance(t.getClassLoader(), new Class[]{t}, new KafirProxy(this));
        }
    }
}
