package ir.moke.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.moke.kafir.utils.JsonUtils;
import ir.moke.model.Book;
import ir.moke.model.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class JsonTest {

    @Test
    public void serializeJson() {
        Book book = new Book("test","1234");
        Response<Book> response = new Response<>(200,book);
        String json = JsonUtils.toJson(response);
        System.out.println(json);
        Assertions.assertTrue(json.contains("test"));
        Assertions.assertTrue(json.contains("1234"));
    }

    @Test
    public void deserializeJson() throws JsonProcessingException {
        String json = "{\"code\":200,\"data\":{\"name\":\"test\",\"isbn\":\"1234\"}}";

        // Create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Deserialize JSON to Response<Book> object using TypeReference
//        Response<Book> response = objectMapper.readValue(json, new TypeReference<Response<Book>>() {});

//        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Response.class, Book.class);
        Response<Book> response = JsonUtils.toObject(json,Response.class);

//        Response<Book> response = objectMapper.readValue(json, javaType);
        System.out.println(response);
    }

    private interface Sample {
        Response<List<Book>> getResponse() ;
    }

    public static void main(String[] args) throws NoSuchMethodException {
        Method method = Sample.class.getMethod("getResponse");
        ParameterizedType returnType = (ParameterizedType) method.getGenericReturnType();
        Type rawType = returnType.getRawType();
        System.out.println("Raw Type: " + rawType); // Should print Call

        // Getting the actual type arguments
        Type[] actualTypeArguments = returnType.getActualTypeArguments();
        System.out.println("Actual Type Arguments:");
        for (Type type : actualTypeArguments) {

            System.out.println(type); // This will show Response<Book> but Book is still a raw type
        }
    }
}
