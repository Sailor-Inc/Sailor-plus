package com.carlolj.sailor.controllers;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AlertDialogHelper {

    public static final int NORMAL_TYPE = 0;
    public static final int ERROR_TYPE = 1;
    public static final int SUCCESS_TYPE = 2;
    public static final int WARNING_TYPE = 3;
    public static final int CUSTOM_IMAGE_TYPE = 4;
    public static final int PROGRESS_TYPE = 5;

    public static void alertOnlyTitle(Context context, String title, int code) {
        new SweetAlertDialog(context, code)
                .setTitleText(title)
                .show();
    }

    public static void alertOnlyTitleDismiss(Context context, String title, int code, int time) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context,code);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sweetAlertDialog.dismissWithAnimation();
            }
        };
        handler.postDelayed(runnable, time);
    }

    public static void alertTitleAndDescription(Context context, String title, String description, int code) {
        new SweetAlertDialog(context, code)
                .setTitleText(title)
                .setContentText(description)
                .show();
    }

    public static SweetAlertDialog alertStartSpin(Context context) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }

    public static void alertStopSpin(SweetAlertDialog pDialog) {
        pDialog.cancel();
    }
}