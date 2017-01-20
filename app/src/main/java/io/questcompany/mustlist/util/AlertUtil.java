package io.questcompany.mustlist.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import io.questcompany.mustlist.R;

/**
 * Created by kimkkikki on 2017. 1. 12..
 * Alert Util
 */

public class AlertUtil {

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

    public static void shareSNSAlert(final Activity context, final String message, final boolean isFinish) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isFinish) {
                    context.finish();
                }
            }
        });
        alert.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);

                Intent chooser = Intent.createChooser(share, context.getString(R.string.sns_share_title));

                context.startActivity(chooser);
                dialog.dismiss();
                if (isFinish) {
                    context.finish();
                }
            }
        });
        alert.setMessage(R.string.alert_sns_share);
        alert.show();
    }

    public static void alertWithCancel(Context context, int message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setPositiveButton(R.string.alert_ok, onClickListener);
        alert.setMessage(message);
        alert.show();
    }

    public static ProgressDialog showProgress(Context context, int message) {
        return ProgressDialog.show(context, "", context.getString(message), true);
    }

    public static ProgressDialog getLoadingDialog(Context context) {
        ProgressDialog loadingDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        loadingDialog.setMessage(context.getString(R.string.progress_loading));
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(false);

        return loadingDialog;
    }
}
