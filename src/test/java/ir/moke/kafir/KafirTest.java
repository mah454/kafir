package ir.moke.kafir;

import ir.moke.kafir.http.Kafir;
import ir.moke.model.Book;
import ir.moke.model.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KafirTest {

    @Test
    public void checkHttpResponseTest() {
        BookService bookService = new Kafir.KafirBuilder()
                .setBaseUri("http://localhost:80/")
                .build(BookService.class);
        HttpResponse<String> response = bookService.checkHttpResponse(new Book("test book", "abcd1234"));
        int statusCode = response.statusCode();
        String body = response.body();
        Assertions.assertEquals(200, statusCode);

        System.out.println(body);
    }

    @Test
    public void checkHttpResponseGenericTest() {
        BookService bookService = new Kafir.KafirBuilder()
                .setBaseUri("http://localhost:80/")
                .build(BookService.class);
        HttpResponse<Response<Book>> response = bookService.checkHttpResponseGeneric(new Book("test book", "abcd1234"));
        int statusCode = response.statusCode();
        Response<Book> bookResponse = response.body();
        Assertions.assertEquals(200, statusCode);

        System.out.println(bookResponse);
    }

    @Test
    public void checkHttpResponseListTest() {
        Book book1 = new Book("B1", "1");
        Book book2 = new Book("B1", "2");
        List<Book> bookList = List.of(book1, book2);

        BookService bookService = new Kafir.KafirBuilder()
                .setBaseUri("http://localhost:80/")
                .build(BookService.class);
        HttpResponse<Response<List<Book>>> response = bookService.checkHttpResponseList(bookList);
        int statusCode = response.statusCode();
        Response<List<Book>> listResponse = response.body();
        Assertions.assertEquals(200, statusCode);

        System.out.println(listResponse);
    }

    @Test
    public void checkInterceptorTest() {
        BookService bookService = new Kafir.KafirBuilder()
                .setBaseUri("http://localhost:80/")
                .setInterceptor(new RequestInterceptor())
                .build(BookService.class);
        HttpResponse<String> response = bookService.checkInterceptor();
        int statusCode = response.statusCode();
        String body = response.body();
        Assertions.assertEquals(200, statusCode);

        System.out.println(body);
    }

    @Test
    public void checkHttpResponseHeadersTest() {
        BookService bookService = new Kafir.KafirBuilder()
                .setBaseUri("http://localhost:80/")
                .build(BookService.class);
        HttpResponse<String> response = bookService.checkHttpResponseHeaders();
        int statusCode = response.statusCode();
        String body = response.body();
        Assertions.assertEquals(200, statusCode);
        System.out.println(body);
    }

    @Test
    public void checkDynamicHeaderTest() {
        Map<String, String> headers = new HashMap<>();
        headers.put("dynamic-1", "d1");
        headers.put("dynamic-2", "d2");
        BookService bookService = new Kafir.KafirBuilder()
                .setBaseUri("http://localhost:80/")
                .setHeaders(headers)
                .build(BookService.class);

        HttpResponse<String> response = bookService.checkHttpResponseHeaders();
        int statusCode = response.statusCode();
        String body = response.body();
        Assertions.assertEquals(200, statusCode);
        System.out.println(body);
    }

    @Test
    public void checkFutureTest() {
        Book book = new Book("B1", "1");
        BookService bookService = new Kafir.KafirBuilder()
                .setBaseUri("http://localhost:80/")
                .build(BookService.class);

        bookService.checkFuture(book)
                .thenAccept(System.out::println)
                .join();
    }

    @Test
    public void checkFutureHttpResponseGenericTest() {
        Book book = new Book("B1", "1");
        BookService bookService = new Kafir.KafirBuilder()
                .setBaseUri("http://localhost:80/")
                .build(BookService.class);

        HttpResponse<Response<Book>> httpResponse = bookService.checkFutureHttpResponseGeneric(book)
                .join();

        System.out.println(httpResponse.body());
    }

    @Test
    public void checkFutureListTest() {
        Book book1 = new Book("B1", "1");
        Book book2 = new Book("B1", "2");
        List<Book> bookList = List.of(book1, book2);

        BookService bookService = new Kafir.KafirBuilder()
                .setBaseUri("http://localhost:80/")
                .build(BookService.class);

        Response<List<Book>> response = bookService.checkFutureList(bookList).join();
        System.out.println(response);
    }

    @Test
    public void checkFutureHttpGenericTest() {
        Book book = new Book("B1", "1");

        BookService bookService = new Kafir.KafirBuilder()
                .setBaseUri("http://localhost:80/")
                .build(BookService.class);

        Response<Book> response = bookService.checkFutureHttpGeneric(book).join();
        System.out.println(response);
    }
}
