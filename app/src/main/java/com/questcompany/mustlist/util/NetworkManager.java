package com.questcompany.mustlist.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.questcompany.mustlist.entity.Must;
import com.questcompany.mustlist.entity.Notice;
import com.questcompany.mustlist.entity.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by kimkkikki on 2016. 9. 26..
 * TODO:Network 통신 처리 추가 필요함
 */

public class NetworkManager {

    private static final String TAG = "NetworkManager";

    public static User postUser() {
        HttpUtil httpUtil = new HttpUtil("/user", HttpUtil.Method.POST, null);
        User user = null;
        try {
            String json = httpUtil.execute().get();
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");

            if (code == 0) {
                String data = jsonObject.getString("data");
                Log.d(TAG, "data : " + data);
                Gson gson = new Gson();
                user = gson.fromJson(data, User.class);
            } else {
                Log.e(TAG, "postUser: ERROR");
            }

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static List<Must> getMustList() {
        List<Must> mustList = null;
        HttpUtil httpUtil = new HttpUtil("/must/list", HttpUtil.Method.GET, null);
        try {
            String json = httpUtil.execute().get();
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");

            if (code == 0) {
                Log.d(TAG, "success : " + json);
                String data = jsonObject.getString("data");
                mustList = new Gson().fromJson(data, new TypeToken<List<Must>>(){}.getType());
            } else {
                Log.e(TAG, "addMust: ERROR");
            }

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

//        List<Must> mustList;
//        String mustData = getSharedPreferences(context, "mustData");
//
//        if (mustData == null) {
//            mustList = new ArrayList<>();
//        } else {
//            mustList = new Gson().fromJson(mustData, new TypeToken<List<Must>>(){}.getType());
//        }

        return mustList;
    }

    public static Must previewAddMust(Must must) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        HttpUtil httpUtil = new HttpUtil("/must/preview", HttpUtil.Method.POST, gson.toJson(must));
        try {
            String json = httpUtil.execute().get();
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");

            if (code == 0) {
                String data = jsonObject.getString("data");
                Log.d(TAG, "data : " + data);
                must = gson.fromJson(data, Must.class);
            } else {
                Log.e(TAG, "previewAddMust: ERROR");
            }

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return must;
    }

    public static void addMust(Must must) {
        Gson gson = new Gson();
        HttpUtil httpUtil = new HttpUtil("/must", HttpUtil.Method.POST, gson.toJson(must));
        try {
            String json = httpUtil.execute().get();
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");

            if (code == 0) {
                Log.d(TAG, "success : " + json);
            } else {
                Log.e(TAG, "addMust: ERROR");
            }

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
//
//         SharedPreferences 를 쓰도록 임시로 구현
//        String arrayString = getSharedPreferences(context, "mustData");
//        List<Must> list;
//        if (arrayString == null) {
//            // first
//            list = new ArrayList<>();
//            list.add(must);
//        } else {
//            list = gson.fromJson(arrayString, new TypeToken<List<Must>>(){}.getType());
//            list.add(must);
//        }
//        String jsonString = gson.toJson(list);
//        saveSharedPreferences(context, "mustData", jsonString);
    }

    private static void saveSharedPreferences(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences("MustListData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String getSharedPreferences(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences("MustListData", Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    public static List<Notice> getNotice() {
        List<Notice> noticeList = new ArrayList<>();

        Notice temp = new Notice();
        temp.setTitle("[공지] 공지사항 임시 데이터");
        temp.setContents("[공지] 공지사항 임시 데이터입니다\n[공지] 공지사항 임시 데이터입니다[공지] 공지사항 임시 데이터입니다[공지] 공지사항 임시 데이터입니다[공지] 공지사항 임시 데이터입니다");

        noticeList.add(temp);
        noticeList.add(temp);

        return noticeList;
    }
}
