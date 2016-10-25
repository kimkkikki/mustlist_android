package com.questcompany.mustlist.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.questcompany.mustlist.entity.User;

/**
 * Created by kimkkikki on 2016. 10. 17..
 * Shared Preference Util
 */

public class PrefUtil {
    public static boolean isUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("mustlist", Context.MODE_PRIVATE);
        String id = preferences.getString("id", null);
        String key = preferences.getString("key", null);

        if (id != null && key != null) {
            return true;
        } else {
            return false;
        }
    }

    public static User getUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("mustlist", Context.MODE_PRIVATE);
        User user = new User();
        user.setId(preferences.getString("id", null));
        user.setKey(preferences.getString("key", null));

        return user;
    }

    public static String getId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("mustlist", Context.MODE_PRIVATE);
        return preferences.getString("id", null);
    }

    public static void setUser(Context context, User user) {
        SharedPreferences preferences = context.getSharedPreferences("mustlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", user.getId());
        editor.putString("key", user.getKey());
        editor.apply();
    }
}
