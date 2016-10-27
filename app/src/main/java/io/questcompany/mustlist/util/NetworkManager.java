package io.questcompany.mustlist.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.questcompany.mustlist.entity.Must;
import io.questcompany.mustlist.entity.Notice;
import io.questcompany.mustlist.entity.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by kimkkikki on 2016. 9. 26..
 * Server 통신 처리
 */

public class NetworkManager {

    private static final String TAG = "NetworkManager";

    private static String sendToServer(Context context, String endPoint, HttpUtil.Method method, String body) {
        HttpUtil httpUtil = new HttpUtil(endPoint, method, body);
        String data = null;
        try {
            String json = httpUtil.execute().get();
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");

            if (code == 0) {
                if (jsonObject.has("data")) {
                    data = jsonObject.getString("data");
                    Log.d(TAG, "data : " + data);
                } else {
                    Log.d(TAG, "sendToServer: No Have Data");
                }
            } else {
                Log.e(TAG, "sendToServer ERROR : " + json);

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                if (jsonObject.has("message")) {
                    alert.setMessage(jsonObject.getString("message"));
                } else {
                    alert.setMessage("서버에서 오류가 발생했습니다.\n잠시 후 다시 시도해 주십시오");
                }
                alert.show();
            }

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "sendToServer Exception ");
        }

        return data;
    }

    public static User postUser(Context context) {
        String data = sendToServer(context, "/user", HttpUtil.Method.POST, null);
        return new Gson().fromJson(data, User.class);
    }

    public static List<Must> getMustList(Context context) {
        String data = sendToServer(context, "/must/list", HttpUtil.Method.GET, null);
        return new Gson().fromJson(data, new TypeToken<List<Must>>(){}.getType());
    }

    public static Must previewAddMust(Context context, Must must) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        String data = sendToServer(context, "/must/preview", HttpUtil.Method.POST, gson.toJson(must));
        return gson.fromJson(data, Must.class);
    }

    public static void addMust(Context context, Must must) {
        sendToServer(context, "/must", HttpUtil.Method.POST, new Gson().toJson(must));
    }

    public static List<Notice> getNotice(Context context) {
        String data = sendToServer(context, "/notice", HttpUtil.Method.GET, null);
        return new Gson().fromJson(data, new TypeToken<List<Notice>>(){}.getType());
    }
}
