package ir.moke.reflection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.moke.kafir.utils.JsonUtils;
import ir.moke.model.Book;
import ir.moke.model.Response;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionTest {

//    private static final String json = "{\"code\":200,\"json\":[{\"name\":\"test\",\"isbn\":\"1234\"},{\"name\":\"test\",\"isbn\":\"1234\"}]}";
//    private static final String json = "{\"A\":[\"a1\",\"a2\",\"a3\"],\"B\":[\"b1\",\"b2\",\"b3\"]}";
//    private static final String json = "{\"A\":[{\"name\":\"a1\",\"isbn\":\"1\"},{\"name\":\"a2\",\"isbn\":\"2\"}],\"B\":[{\"name\":\"b1\",\"isbn\":\"1\"},{\"name\":\"b2\",\"isbn\":\"2\"}]}";
    private static final String json = "[{\"name\":\"A\",\"isbn\":\"1\"},{\"name\":\"B\",\"isbn\":\"2\"}]";
    private static final ObjectMapper objMapper = new ObjectMapper();

    public static void main(String[] args) throws NoSuchMethodException, JsonProcessingException {
        Method method = ModelClass.class.getDeclaredMethod("checkList");
        if (isGenericType(method)) {
            ParameterizedType parameterizedType = getMethodGenericReturnType(method);
            JavaType javaType = objMapper.getTypeFactory().constructFromCanonical(parameterizedType.getTypeName());
            List<Book> aa = objMapper.readValue(json, javaType);
            System.out.println(aa);
        }

    }

    public static boolean isGenericType(Method method) {
        Type returnType = method.getGenericReturnType();
        return isGenericType(returnType);
    }

    public static boolean isGenericType(Type type) {
        return type instanceof ParameterizedType ||
                type instanceof TypeVariable ||
                type instanceof GenericArrayType;
    }

    public static ParameterizedType getMethodGenericReturnType(Method method) {
        return (ParameterizedType) method.getGenericReturnType();
    }

    interface ModelClass {
        Response<Book> checkGeneric();
        List<Book> checkList();
        Map<String,List<String>> checkMapList();
        Map<String,List<Book>> checkMapListObject();
    }
}
