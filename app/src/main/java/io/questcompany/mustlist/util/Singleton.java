package io.questcompany.mustlist.util;

import android.app.ProgressDialog;
import android.content.Context;

import io.questcompany.mustlist.R;
import io.questcompany.mustlist.entity.User;

/**
 * Created by kimkkikki on 2016. 10. 21..
 * Singleton Pattern
 */

public class Singleton {
    private static Singleton uniqueInstance = new Singleton();

    private User user;
    private ProgressDialog progressDialog;

    private Singleton() {}

    public static Singleton getInstance() {
        return uniqueInstance;
    }

    public void setIdAndKey(String id, String key) {
        user = new User();
        user.id = id;
        user.key = key;
    }

    public String getId() {
        if (user == null) return null;
        return user.id;
    }

    public String getKey() {
        if (user == null) return null;
        return user.key;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void loading(Context context) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(context, "", context.getString(R.string.sign_in_progress), true);
        }
    }

    public void stopLoading() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
