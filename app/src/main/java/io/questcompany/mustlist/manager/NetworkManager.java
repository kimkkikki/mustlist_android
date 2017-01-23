package io.questcompany.mustlist.manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import io.questcompany.mustlist.entity.Must;
import io.questcompany.mustlist.entity.Pay;
import io.questcompany.mustlist.entity.Score;
import io.questcompany.mustlist.entity.User;
import io.questcompany.mustlist.util.HttpUtil;
import io.questcompany.mustlist.util.JsonResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by kimkkikki on 2016. 9. 26..
 * Server 통신 처리
 */

public class NetworkManager {

    private static final String TAG = "NetworkManager";

    public static boolean checkNetworkStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            Log.d(TAG, "checkNetworkStatus: network info : " + cm.getActiveNetworkInfo());
            return true;
        }
    }

    private static JsonResponse sendToServer(Context context, String endPoint, HttpUtil.Method method, String body) {
        if (!checkNetworkStatus(context)) {
            return null;
        }

        int version;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version = 0;
        }

        HttpUtil httpUtil = new HttpUtil(endPoint, method, body, context, "" + version);

        try {
            JsonResponse response = new Gson().fromJson(httpUtil.execute().get(), JsonResponse.class);

            Log.d(TAG, "sendToServer: json - " + response);
            if (response != null) {
                return response;
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "sendToServer Exception ");
        }

        return null;
    }

    public static User postUser(Context context, User user) {
        Log.d(TAG, "postUser: user : " + new Gson().toJson(user));
        JsonResponse response = sendToServer(context, "/user", HttpUtil.Method.POST, new Gson().toJson(user));
        if (response == null)
            return null;

        return new Gson().fromJson(response.getJson(), User.class);
    }

    public static User deleteUser(Context context) {
        Log.d(TAG, "deleteUser: user : ");
        JsonResponse response = sendToServer(context, "/user", HttpUtil.Method.DELETE, null);
        if (response == null)
            return null;

        return new Gson().fromJson(response.getJson(), User.class);
    }

    public static User getUser(Context context) {
        JsonResponse response = sendToServer(context, "/user", HttpUtil.Method.GET, null);
        if (response == null)
            return null;

        return new Gson().fromJson(response.getJson(), User.class);
    }

    public static List<Must> getMustList(Context context) {
        return getMustList(context, 0);
    }

    public static List<Must> getMustList(Context context, int page) {
        JsonResponse response = sendToServer(context, "/must?page=" + page, HttpUtil.Method.GET, null);
        if (response == null) {
            return null;
        }
        return new Gson().fromJson(response.getJson(), new TypeToken<List<Must>>(){}.getType());
    }

    public static Must previewMust(Context context, Must must) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        JsonResponse response = sendToServer(context, "/must/preview", HttpUtil.Method.POST, gson.toJson(must));
        if (response != null) {
            return gson.fromJson(response.getJson(), Must.class);
        } else {
            return null;
        }
    }

    public static void addMust(Context context, Must must) {
        sendToServer(context, "/must", HttpUtil.Method.POST, new Gson().toJson(must));
    }

    public static int checkMust(Context context, Must must) {
        JsonResponse response = sendToServer(context, "/must/" + must.index, HttpUtil.Method.GET, null);
        if (response != null) {
            return response.getCode();
        }

        return 400;
    }

//    public static List<Notice> getNotice(Context context) {
//        JsonResponse response = sendToServer(context, "/notice", HttpUtil.Method.GET, null);
//        if (response != null) {
//            return new Gson().fromJson(response.getJson(), new TypeToken<List<Notice>>(){}.getType());
//        } else {
//            return null;
//        }
//    }

    public static int pay(Context context, Pay pay, Must must) {
        Map<String, Object> dict =  new HashMap<>();
        dict.put("pay", pay);
        dict.put("must", must);
        JsonResponse response = sendToServer(context, "/pay", HttpUtil.Method.POST, new Gson().toJson(dict));
        if (response != null) {
            return response.getCode();
        } else {
            return -1;
        }
    }

    public static List<Score> getScoreList(Context context) {
        JsonResponse response = sendToServer(context, "/score", HttpUtil.Method.GET, null);
        if (response == null) {
            return null;
        }
        return new Gson().fromJson(response.getJson(), new TypeToken<List<Score>>(){}.getType());
    }

    public static int checkVersion(Context context) {
        JsonResponse response = sendToServer(context, "/version", HttpUtil.Method.GET, null);
        if (response == null) {
            return -1;
        } else {
            return response.getCode();
        }
    }
}
