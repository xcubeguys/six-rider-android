package com.tommy.driver.adapter;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tommy.driver.Map_Activity;

public class Services extends Service {
    public static final String MY_SERVICE = ".adapter.Services";

    DatabaseReference requstStatus;

    String driverId;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = prefs.getString("driverid", null);
        System.out.println("Driver ID in service class===>" + driverId);

        listenListStatus();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requstStatus != null && handler != null) {
            requstStatus.removeEventListener(handler);
        }
    }

    private ValueEventListener handler;

    private void listenListStatus() {

        if (driverId != null) {

            requstStatus = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverId).child("request").child("status");

            handler = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        String Status = dataSnapshot.getValue().toString();

                        if (Status.matches("1")) {

                            System.out.println("IS MAPSHOWING?===>" + Constants.MAP_ISSHOWING);
                            System.out.println("Driver ID===>" + driverId);

                            if (!Constants.MAP_ISSHOWING) {

                                if (driverId != null) {

                                    System.out.println("<===== activity started =====> " + driverId);
                                    Intent intent = new Intent(Services.this, Map_Activity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            requstStatus.addValueEventListener(handler);
        }
    }
}