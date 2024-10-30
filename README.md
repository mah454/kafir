## Kafir Rest Client

API base http client (replacement for retrofit or jersey-client)     
Used only pure java HttpClient api .

### Usage

**Add dependency:**

```xml

<dependency>
    <groupId>ir.moke</groupId>
    <artifactId>kafir</artifactId>
    <version>1.4</version>
</dependency>
```

Simple Usage:   
```java
import ir.moke.kafir.annotation.PathParameter;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// You can use global header
@Header(parameters = {@HeaderParameter(key = "global-header", value = "global-header-value")})
public class PersonService {
    
    @POST("person/add")
    // You can use special header
    @Header(parameters = {@HeaderParameter(key = "special-header", value = "special-header-value")})
    HttpResponse<Person> addNewPerson(Person person);

    @POST("person/add")
    CompletableFuture<Person> addNewPersonAsync(Person person);

    @POST("person/search")
    List<Person> search(@QueryParameter("username") String username,
                        @QueryParameter("id") Long id);

    @DELETE("person/remove")
    HttpResponse<Void> search(@QueryParameter("username") String username);

    @GET("person/{username}")
    Person search(@PathParameter("username") String username);

    /*
     * Http response 200
     * */
    @GET("exists/{username}")
    HttpResponse<Void> exists(@PathParameter("username") String username);
}
```

Now use interface like this :

```java
import java.net.http.HttpResponse;

public class MainClass {

    public static void main(String[] args) {
        Map<String,String> dynamicHeaders = new HashMap<>();
        /*
         * dynamicHeaders.put(key,value);
         */
        PersonService personService = new Kafir.KafirBuilder()
                .setBaseUri("http://127.0.0.1:8080/api/v1")
                .setVersion(HttpClient.Version.HTTP_2)
                // .setHeaders(headers) this is optional 
                .build(PersonService.class);

        Person person = new Person();
        person.setName("ali");
        HttpResponse<Person> httpResponse = personService.addNewPerson(person);
        System.out.println(httpResponse.statusCode());
        System.out.println(httpResponse.body().getId());
    }
}
```

also you can use interceptor. 
only implement and use like this :   
```java
public class RequestInterceptor implements Interceptor {
    @Override
    public HttpRequest intercept(HttpRequest request) {
        URI uri = request.uri();
        String host = uri.getHost();
        String scheme = uri.getScheme();
        int port = uri.getPort();

        return HttpRequest.newBuilder().uri(URI.create("%s://%s:%s/get?id=1&age=22".formatted(scheme, host, port))).build();
    }
}
```
