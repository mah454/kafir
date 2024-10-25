import ir.moke.kafir.http.Kafir;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class KafirTest {
    private static final BookService bookService = new Kafir.KafirBuilder()
            .setBaseUri("http://localhost:80/")
            .build(BookService.class);

    @Test
    public void checkPost() {
        HttpResponse<String> response = bookService.postItem(new Book("test book", "abcd1234"));
        int statusCode = response.statusCode();
        String body = response.body();
        Assertions.assertEquals(200, statusCode);

        System.out.println(body);
    }

    @Test
    public void checkAnnotationHeader() {
        HttpResponse<String> response = bookService.sendHeader();
        int statusCode = response.statusCode();
        String body = response.body();
        Assertions.assertEquals(200, statusCode);
        System.out.println(body);
    }

    @Test
    public void checkDynamicHeader() {
        Map<String,String> headers = new HashMap<>();
        headers.put("dynamic-1","d1");
        headers.put("dynamic-2","d2");
        BookService bookService = new Kafir.KafirBuilder()
                .setBaseUri("http://localhost:80/")
                .setHeaders(headers)
                .build(BookService.class);

        HttpResponse<String> response = bookService.sendHeader();
        int statusCode = response.statusCode();
        String body = response.body();
        Assertions.assertEquals(200, statusCode);
        System.out.println(body);
    }
}
