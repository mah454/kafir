package ir.moke.kafir;

import ir.moke.kafir.annotation.GET;
import ir.moke.kafir.annotation.Header;
import ir.moke.kafir.annotation.HeaderParameter;
import ir.moke.kafir.annotation.POST;
import ir.moke.model.Book;
import ir.moke.model.Response;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Run docker container :
 * docker run -d -p 80:80 kennethreitz/httpbin
 */

@Header(parameters = {@HeaderParameter(key = "class-header", value = "class-header-value")})
public interface BookService {

    @POST("post")
    HttpResponse<String> checkHttpResponse(Book book);

    @POST("/post")
    HttpResponse<Response<Book>> checkHttpResponseGeneric(Book book);

    @POST("/post")
    HttpResponse<Response<List<Book>>> checkHttpResponseList(List<Book> books);

    @POST("/post")
    CompletableFuture<String> checkFuture(Book book);

    @POST("/post")
    CompletableFuture<Response<Book>> checkFutureHttpGeneric(Book book);

    @POST("/post")
    CompletableFuture<HttpResponse<Response<Book>>> checkFutureHttpResponseGeneric(Book book);

    @POST("/post")
    CompletableFuture<Response<List<Book>>> checkFutureList(List<Book> books);

    @GET("/get")
    HttpResponse<String> checkInterceptor();

    @GET("/get")
    @Header(parameters = {
            @HeaderParameter(key = "method-header-1", value = "method-header-value-1"),
            @HeaderParameter(key = "method-header-2", value = "method-header-value-2")
    })
    HttpResponse<String> checkHttpResponseHeaders();
}
