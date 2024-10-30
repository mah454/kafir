package ir.moke.model;

import java.util.Map;

public class Response<T> {

    public int code;
    public T json;
    public Map<String,String> headers;

    public Response() {
    }

    public Response(int code, T json) {
        this.code = code;
        this.json = json;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", json=" + json +
                ", headers=" + headers +
                '}';
    }
}
