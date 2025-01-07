package ir.moke;

import ir.moke.kafir.annotation.POST;
import ir.moke.kafir.annotation.QueryParameter;
import ir.moke.kafir.http.Kafir;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class PodmanTest {

    public static void main(String[] args) {
        Podman postman = new Kafir.KafirBuilder().setBaseUri("http://127.0.0.1:9000/v5/libpod/").setVersion(HttpClient.Version.HTTP_2).build(Podman.class);

        System.out.println("Before");
        postman.imagePullAsync("registry.docker.ir/busybox")
                .thenAcceptAsync(System.out::println);
        System.out.println("After");
        sleep();
    }

    private static void sleep() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private interface Podman {

        @POST("images/pull")
        CompletableFuture<HttpResponse<String>> imagePullAsync(@QueryParameter("reference") String reference);

    }
}