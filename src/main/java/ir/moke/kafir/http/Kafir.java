package ir.moke.kafir.http;

import javax.net.ssl.SSLContext;
import java.lang.reflect.Proxy;
import java.net.Authenticator;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;

public class Kafir {

    public String baseUri;
    public HttpClient.Version version;
    public Authenticator authenticator;
    public ExecutorService executorService;
    public SSLContext sslContext;
    public Duration connectionTimeout;

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
        private String baseUri;
        private HttpClient.Version version;
        private Authenticator authenticator;
        private ExecutorService executorService;
        private SSLContext sslContext;
        private Duration connectionTimeout;

        public KafirBuilder setBaseUri(String baseUri) {
            this.baseUri = baseUri;
            return this;
        }

        public KafirBuilder setVersion(HttpClient.Version version) {
            this.version = version;
            return this;
        }

        public KafirBuilder setAuthenticator(Authenticator authenticator) {
            this.authenticator = authenticator;
            return this;
        }

        public KafirBuilder setExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public KafirBuilder setSslContext(SSLContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        public KafirBuilder setConnectionTimeout(Duration connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        protected String getBaseUri() {
            return baseUri;
        }

        protected HttpClient.Version getVersion() {
            return version;
        }

        protected Authenticator getAuthenticator() {
            return authenticator;
        }

        protected ExecutorService getExecutorService() {
            return executorService;
        }

        protected SSLContext getSslContext() {
            return sslContext;
        }

        protected Duration getConnectionTimeout() {
            return connectionTimeout;
        }

        @SuppressWarnings("unchecked")
        public <T> T build(Class<T> t) {
            return (T) Proxy.newProxyInstance(t.getClassLoader(), new Class[]{t}, new KafirProxy(this));
        }
    }
}
