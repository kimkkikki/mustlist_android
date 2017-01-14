package io.questcompany.mustlist.util;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by kimkkikki on 2016. 10. 12..
 * Http Send Util
 */

public class HttpUtil extends AsyncTask<String, String, String> {

    private static final String SERVER_URL = "http://questcompany.io/api";
//    private static final String SERVER_URL = "http://localhost:8000/api";
    private String requestUrl;
    private String body;
    private Method method;

    public enum Method {POST, GET}

    public HttpUtil(String requestUrl, Method method, String body) {
        this.requestUrl = requestUrl;
        this.method = method;
        this.body = body;
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
        JsonResponse response;
        try {
            URL url = new URL(SERVER_URL + requestUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("Content-Type", "application/json");
            if (Singleton.getInstance().getId() != null && Singleton.getInstance().getKey() != null) {
                connection.setRequestProperty("id", Singleton.getInstance().getId());
                connection.setRequestProperty("key", Singleton.getInstance().getKey());
            }

            if (method == Method.POST) {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
                if (body != null) {
                    outputStream.write(body.getBytes());
                }
                PrintWriter writer = new PrintWriter(outputStream);
                writer.close();
                outputStream.close();

            } else if (method == Method.GET) {
                connection.setRequestMethod("GET");
            }

            StringBuilder builder = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String outPutLine;
                while ((outPutLine = reader.readLine()) != null) {
                    if (!outPutLine.trim().equals("")) {
                        builder.append(outPutLine);
                    }
                }
                reader.close();
            } catch (FileNotFoundException e) {
                Log.d("HttpUtil", "send: FileNotFound " + e.getMessage());
            }

            Log.d("HttpUtil", "send: status code : " + connection.getResponseCode());

            response = new JsonResponse(builder.toString(), connection.getResponseCode());

            return response;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
