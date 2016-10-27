package io.questcompany.mustlist.util;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kimkkikki on 2016. 10. 12..
 * Http Send Util
 */

class HttpUtil extends AsyncTask<String, String, String> {

    private static final String SERVER_URL = "http://questcompany.io";
    private String requestUrl;
    private String body;
    private Method method;

    enum Method {POST, GET}

    HttpUtil(String requestUrl, Method method, String body) {
        this.requestUrl = requestUrl;
        this.method = method;
        this.body = body;
    }

    @Override
    protected String doInBackground(String... strings) {
        return send(requestUrl, method, body);
    }

    private String send(String requestUrl, Method method, String body) {
        String resultJsonString;
        try {
            URL url = new URL(SERVER_URL + requestUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("id", Singleton.getInstance().getId());
            connection.setRequestProperty("key", Singleton.getInstance().getKey());

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

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String outPutLine;
            while ((outPutLine = reader.readLine()) != null) {
                if (!outPutLine.trim().equals("")) {
                    builder.append(outPutLine);
                }
            }

            resultJsonString = builder.toString();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
            return "{'code':9999,'message':'서버와 통신에 실패했습니다.'}";
        }

        return resultJsonString;
    }
}
