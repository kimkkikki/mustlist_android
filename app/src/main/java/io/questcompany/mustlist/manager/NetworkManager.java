package io.questcompany.mustlist.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import io.questcompany.mustlist.R;
import io.questcompany.mustlist.entity.Must;
import io.questcompany.mustlist.entity.Notice;
import io.questcompany.mustlist.entity.User;
import io.questcompany.mustlist.util.AlertUtil;
import io.questcompany.mustlist.util.HttpUtil;
import io.questcompany.mustlist.util.JsonResponse;
import io.questcompany.mustlist.util.PrefUtil;
import io.questcompany.mustlist.util.Purchase;
import io.questcompany.mustlist.util.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by kimkkikki on 2016. 9. 26..
 * Server 통신 처리
 */

public class NetworkManager {

    private static final String TAG = "NetworkManager";

    private static JsonResponse sendToServer(Context context, String endPoint, HttpUtil.Method method, String body) {
        Singleton.getInstance().loading(context);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            AlertUtil.alert(context, R.string.alert_not_connected_network);
            return null;
        }

        ProgressDialog progressDialog = ProgressDialog.show(context, "", context.getString(R.string.progress_loading), true);

        HttpUtil httpUtil = new HttpUtil(endPoint, method, body);

        try {
            JsonResponse response = new Gson().fromJson(httpUtil.execute().get(), JsonResponse.class);

            Log.d(TAG, "sendToServer: json - " + response);
            if (response != null) {
                progressDialog.dismiss();
                return response;
            } else {
                AlertUtil.alert(context, R.string.alert_not_connect_server);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "sendToServer Exception ");
        }

        progressDialog.dismiss();
        return null;
    }

    public static User postUser(Context context, User user) {
        JsonResponse response = sendToServer(context, "/user", HttpUtil.Method.POST, new Gson().toJson(user));
        if (response == null)
            return null;

        return new Gson().fromJson(response.getJson(), User.class);
    }

    public static User getUser(Context context) {
        JsonResponse response = sendToServer(context, "/user", HttpUtil.Method.GET, null);
        if (response == null)
            return null;

        if (response.getCode() == 401) {
            AlertUtil.alert(context, R.string.alert_not_exist_user);
            PrefUtil.clear(context);
            return null;
        }

        return new Gson().fromJson(response.getJson(), User.class);
    }

    public static List<Must> getMustList(Context context) {
        JsonResponse response = sendToServer(context, "/must", HttpUtil.Method.GET, null);
        if (response == null) {
            return new ArrayList<>();
        }
        return new Gson().fromJson(response.getJson(), new TypeToken<List<Must>>(){}.getType());
    }

    public static Must previewMust(Context context, Must must) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        JsonResponse response = sendToServer(context, "/must/preview", HttpUtil.Method.POST, gson.toJson(must));
        return gson.fromJson(response.getJson(), Must.class);
    }

    public static void addMust(Context context, Must must) {
        sendToServer(context, "/must", HttpUtil.Method.POST, new Gson().toJson(must));
    }

    public static void checkMust(Context context, Must must) {
        JsonResponse response = sendToServer(context, "/must/" + must.index, HttpUtil.Method.GET, null);
        if (response != null) {
            int code = response.getCode();
            switch (code) {
                case 200:
                    // Success
                    AlertUtil.alert(context, "수고하셨습니다");
                    break;
                case 204:
                    // Already Checked
                    break;
                default:
                    // Failure
                    AlertUtil.alert(context, "오늘은 설정한 기간이 아닙니다");
            }
        }
    }

    public static List<Notice> getNotice(Context context) {
        JsonResponse response = sendToServer(context, "/notice", HttpUtil.Method.GET, null);
        return new Gson().fromJson(response.getJson(), new TypeToken<List<Notice>>(){}.getType());
    }

    public static String getPayload(Context context) {
        JsonResponse response = sendToServer(context, "/billing/payload", HttpUtil.Method.GET, null);
        Purchase purchase = new Gson().fromJson(response.getJson(), Purchase.class);
        return purchase.getDeveloperPayload();
    }

    public static void purchaseSuccess(Context context, Purchase purchase) {
        sendToServer(context, "/billing", HttpUtil.Method.POST, new Gson().toJson(purchase));
    }
}
