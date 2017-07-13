package com.tommy.driver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.tommy.driver.adapter.FontChangeCrawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cogzidel on 21/10/16.
 **/

public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    private final int SPLASH_DISPLAY_LENGTH = 4000;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 5;
    private VideoView videoView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);
            //getActionBar().hide();
            FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
            fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkMultiplePermissions();
            } else {
                showVideoOrImage();
            }
        }

    private void checkMultiplePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionsNeeded = new ArrayList<>();
            List<String> permissionsList = new ArrayList<>();
            if(!addPermission(permissionsList, android.Manifest.permission.CAMERA))
            {
                permissionsNeeded.add("Camera");
            }

            if (!addPermission(permissionsList, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                permissionsNeeded.add("Read Storage");
            }

            if(!addPermission(permissionsList, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                permissionsNeeded.add("Write Storage");
            }

            if(!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                permissionsNeeded.add("Access Fine Location");
            }
            if(!addPermission(permissionsList, android.Manifest.permission.READ_CONTACTS))
            {
                permissionsNeeded.add("Read Contacts");
            }
            if(!addPermission(permissionsList, android.Manifest.permission.SEND_SMS))
            {
                permissionsNeeded.add("Send Sms");
            }
            if(!addPermission(permissionsList, Manifest.permission.READ_SMS))
            {
                permissionsNeeded.add("Read Sms");
            }
            if(!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
            {
                permissionsNeeded.add("Call");
            }
            if (permissionsList.size() > 0)
            {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
            else{
                showVideoOrImage();
            }
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= 23)

            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);

                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION,PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED&& perms.get(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    startActivity(new Intent(this,SplashActivity.class));
                    finish();
                    return;
                } else {
                    // Permission Denied
                    if (Build.VERSION.SDK_INT >= 23) {
                        Toast.makeText(getApplicationContext(), "Please permit all the permissions", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void launchActivity(){

        Intent mainIntent = new Intent(getApplicationContext(), LaunchActivity_.class);
        //SplashActivity.this.startActivity(mainIntent);
        startActivity(mainIntent);
        finish();

    }

    public void showVideoOrImage(){

/*        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 3 seconds
                launchActivity();
            }
        }, 3000);*/

        videoView = (VideoView) findViewById(R.id.myvideoview);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.driver_splash);
        videoView.setVideoURI(video);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                launchActivity();
            }
        });

        videoView.start();
    }
}
