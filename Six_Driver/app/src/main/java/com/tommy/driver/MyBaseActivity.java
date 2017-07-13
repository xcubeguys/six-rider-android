package com.tommy.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.widget.Toast;

/**
 * Created by test on 15/5/17.
 **/

public class MyBaseActivity extends Activity {

    public static final long DISCONNECT_TIMEOUT = 15 * 60 * 1000; // 5 min = 5 * 60 * 1000 ms
    PowerManager.WakeLock wl;
    Intent intent=new Intent();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();
        // do your things, even when screen is off
        resetDisconnectTimer();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDisconnectTimer();
        wl.release();
    }

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect
            Toast.makeText(MyBaseActivity.this, "Timeout due to in activity", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(MyBaseActivity.this,LaunchActivity_.class);
            i.putExtra("timeout","timeout");
            startActivity(i);
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        //stopDisconnectTimer();
    }

    @Override
    public void onPause(){
        super.onPause();
        resetDisconnectTimer();
    }
}