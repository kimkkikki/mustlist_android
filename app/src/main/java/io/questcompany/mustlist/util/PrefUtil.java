package io.questcompany.mustlist.util;

import android.content.Context;
import android.content.SharedPreferences;

import io.questcompany.mustlist.entity.User;

/**
 * Created by kimkkikki on 2016. 10. 17..
 * Shared Preference Util
 */

public class PrefUtil {
    public static boolean isUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("mustlist", Context.MODE_PRIVATE);
        String id = preferences.getString("id", null);
        String key = preferences.getString("key", null);

        return id != null && key != null;
    }

    public static User getUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("mustlist", Context.MODE_PRIVATE);
        User user = new User();
        user.id = preferences.getString("id", null);
        user.key = preferences.getString("key", null);

        return user;
    }

    public static String getId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("mustlist", Context.MODE_PRIVATE);
        return preferences.getString("id", null);
    }

    public static void setUser(Context context, User user) {
        SharedPreferences preferences = context.getSharedPreferences("mustlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", user.id);
        editor.putString("key", user.key);
        editor.apply();
    }

    public static void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("mustlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

//    public static void setFirstTimeLaunch(Context context, boolean isFirstTime) {
//        SharedPreferences preferences = context.getSharedPreferences("mustlist", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean("firstTime", isFirstTime);
//        editor.apply();
//    }
//
//    public static boolean getFirstTimeLaunch(Context context) {
//        SharedPreferences preferences = context.getSharedPreferences("mustlist", Context.MODE_PRIVATE);
//        return preferences.getBoolean("firstTime", true);
//    }
}
