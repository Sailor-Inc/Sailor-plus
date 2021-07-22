package com.carlolj.sailor.controllers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.carlolj.sailor.R;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class CameraHelper extends AppCompatActivity {

    private static final String TAG = "CameraHelper";
    private static final int RESULT_PICK = 1;
    private static final int RESULT_TAKE = 2;
    private static final int ACCEPT_CAMERA = 20;

    ParseFile newProfileImage;
    ImageView ivPicture;
    Context context;
    Activity activity;

    private File photoFile;
    public String photoFileName = "photo.jpg";

    public CameraHelper(Context _context, Activity _activity, ImageView _ivPicture) {
        context = _context;
        activity = _activity;
        ivPicture = _ivPicture;
    }

    public void choosePicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.choose_camera_option)
                .setItems(R.array.camera_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                if (checkAndRequestPermissions()) {
                                    takePictureFromCamera();
                                    dialog.cancel();
                                }
                                break;
                            default:
                                takePictureFromGallery();
                                dialog.cancel();
                                break;
                        }
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity
                        ,new String[]{Manifest.permission.CAMERA}
                        ,ACCEPT_CAMERA);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCEPT_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePictureFromCamera();
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void takePictureFromCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(context.getPackageManager()) != null) {
            startActivityForResult(takePicture, RESULT_TAKE);
        }
    }

    private void takePictureFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, RESULT_PICK);
    }
}
