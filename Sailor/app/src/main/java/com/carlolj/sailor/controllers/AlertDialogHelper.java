package com.carlolj.sailor.controllers;

import android.content.Context;

import com.carlolj.sailor.activities.LoginActivity;

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

    public static void alertTitleAndDescription(Context context, String title, String description, int code) {
        new SweetAlertDialog(context, code)
                .setTitleText(title)
                .setContentText(description)
                .show();
    }
}
