package io.questcompany.mustlist.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kimkkikki on 2016. 10. 12..
 * Http Send Util
 */

public class HttpUtil extends AsyncTask<String, String, String> {

    private static final String SERVER_URL = "https://questcompany.io/api";
//    private static final String SERVER_URL = "http://localhost:8000/api";
    private String requestUrl;
    private String body;
    private Method method;
    private Context context;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "HttpUtil";

    public enum Method {POST, GET, DELETE}

    public HttpUtil(String requestUrl, Method method, String body, Context context) {
        this.requestUrl = requestUrl;
        this.method = method;
        this.body = body;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            return new Gson().toJson(send(requestUrl, method, body));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JsonResponse send(String requestUrl, Method method, String body) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(SERVER_URL + requestUrl);

            // Setting Headers
            builder.addHeader("Content-Type", "application/json");
            if (Singleton.getInstance().getId() != null && Singleton.getInstance().getKey() != null) {
                builder.addHeader("id", Singleton.getInstance().getId());
                builder.addHeader("key", Singleton.getInstance().getKey());
                builder.addHeader("date", DateUtil.getTodayString(context));
            }

            // Setting Body
            if (method == Method.POST) {
                RequestBody okBody = RequestBody.create(JSON, body);
                builder.post(okBody);
            }

            if (method == Method.DELETE) {
                builder.delete();
            }

            Request request = builder.build();
            Log.d(TAG, "send request : " + request);
            Response response = client.newCall(request).execute();
            Log.d(TAG, "send response : " + response);

            if (response.isSuccessful()) {
                return new JsonResponse(response.body().string(), response.code());
            } else {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
