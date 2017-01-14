package io.questcompany.mustlist.util;

/**
 * Created by kimkkikki on 2017. 1. 10..
 * JSON Response Object
 */

public class JsonResponse {
    private String json;
    private int code;

    public JsonResponse() {
    }

    public JsonResponse(String json, int code) {
        this.json = json;
        this.code = code;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "JsonResponse{" +
                "json='" + json + '\'' +
                ", code=" + code +
                '}';
    }
}
