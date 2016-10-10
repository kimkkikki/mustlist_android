package com.questcompany.mustlist.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.questcompany.mustlist.entity.Must;
import com.questcompany.mustlist.entity.Notice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimkkikki on 2016. 9. 26..
 * TODO:Network 통신 처리 추가 필요함
 */

public class NetworkManager {

    public static List<Must> getMustList(String id, Context context) {

        List<Must> mustList;
        String mustData = getSharedPreferences(context, "mustData");

        if (mustData == null) {
            mustList = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            mustList = gson.fromJson(mustData, new TypeToken<List<Must>>(){}.getType());
        }

        return mustList;
    }

    public static Must previewAddMust(String startDay, String period, String amount, String timeRange) {
        Must must = new Must();

        //TODO: 서버 API를 호출하도록 변경 필요
        {
            must.setStartDay(startDay);
            must.setPeriod(period);
            must.setAmount(amount);
            must.setTimeRange(timeRange);

            must.setDefaultPoint(100);
            must.setSuccessPoint(100);
        }

        return must;
    }

    public static void addMust(Context context, String name, String startDay, String period, String amount, String timeRange) {

        // SharedPreferences 를 쓰도록 임시로 구현
        Must must = new Must(name, startDay, period, amount, timeRange);

        Gson gson = new Gson();

        String arrayString = getSharedPreferences(context, "mustData");
        List<Must> list;
        if (arrayString == null) {
            // first
            list = new ArrayList<>();
            list.add(must);
        } else {
            list = gson.fromJson(arrayString, new TypeToken<List<Must>>(){}.getType());
            list.add(must);
        }
        String jsonString = gson.toJson(list);
        saveSharedPreferences(context, "mustData", jsonString);
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
