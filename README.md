## Kafir Rest Client

API base http client (replacement for retrofit or jersey-client)     
Used only pure java HttpClient api .
#### Usage

```java
import ir.moke.kafir.annotation.PathParameter;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PersonService {
    @POST("person/add")
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
        PersonService personService = new Kafir.KafirBuilder()
                .setBaseUri("http://127.0.0.1:8080/api/v1")
                .setVersion(HttpClient.Version.HTTP_2)
                .build(PersonService.class);

        Person person = new Person();
        person.setName("ali");
        HttpResponse<Person> httpResponse = personService.addNewPerson(personService);
        System.out.println(httpResponse.statusCode());
        System.out.println(httpResponse.body().getId());
    }
}
```