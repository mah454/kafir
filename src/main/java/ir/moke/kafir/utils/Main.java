package ir.moke.kafir.utils;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("name","aa");
        map.put("age",null);

        System.out.println(JsonUtils.toJson(map));
    }
}
