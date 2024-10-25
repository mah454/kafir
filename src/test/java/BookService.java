import ir.moke.kafir.annotation.GET;
import ir.moke.kafir.annotation.Header;
import ir.moke.kafir.annotation.HeaderParameter;
import ir.moke.kafir.annotation.POST;

import java.net.http.HttpResponse;

/**
 * Run docker container :
 * docker run -d -p 80:80 kennethreitz/httpbin
 */

@Header(parameters = {@HeaderParameter(key = "class-header", value = "class-header-value")})
public interface BookService {

    @POST("/post")
    HttpResponse<String> postItem(Book book);

    @GET("/get")
    @Header(parameters = {@HeaderParameter(key = "method-header", value = "method-header-value")})
    HttpResponse<String> sendHeader();
}
