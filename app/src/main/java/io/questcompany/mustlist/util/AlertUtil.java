package io.questcompany.mustlist.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import io.questcompany.mustlist.R;

/**
 * Created by kimkkikki on 2017. 1. 12..
 * Alert Util
 */

public class AlertUtil {

    public static void alert(Context context, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setMessage(message);
        alert.show();
    }

    public static void alert(Context context, int message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setMessage(message);
        alert.show();
    }

    public static void alert(Context context, int message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setPositiveButton(R.string.alert_ok, onClickListener);
        alert.setMessage(message);
        alert.show();
    }
}
