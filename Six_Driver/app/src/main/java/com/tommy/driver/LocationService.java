package com.tommy.driver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tommy.driver.utils.LogUtils;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by vipul on 12/13/2015.
 **/
public class LocationService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation, lStart, lEnd;
    static double distance = 0;
    double speed;
    private Context context;
    //SavePref savePref;
    String strDistance;
    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        return mBinder;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onConnected(Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        distance = 0;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //MainActivity.locate.dismiss();
        mCurrentLocation = location;
        if (lStart == null) {
            lStart = mCurrentLocation;
            lEnd = mCurrentLocation;
        } else
            lEnd = mCurrentLocation;

        //Calling the method below updates the  live values of distance and speed to the TextViews.
        //updateUI();
        //calculating the speed with getSpeed method it returns speed in m/s so we are converting it into kmph
        speed = location.getSpeed() * 18 / 5;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class LocalBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }
    }

    //The live feed of Distance and Speed are being set in the method below .
    private void updateUI() {
        if (Map_Activity.p == 0) {
            distance = distance + (lStart.distanceTo(lEnd) / 1000.00);
            Map_Activity.endTime = System.currentTimeMillis();
            long diff = Map_Activity.endTime - Map_Activity.startTime;
            diff = TimeUnit.MILLISECONDS.toMinutes(diff);

            // Map_Activity.time.setText("Total Time: " + diff + " minutes");
            if (speed > 0.0)

                LogUtils.i("Speedd===>" + new DecimalFormat("0.00").format(speed) + " km/hr");
                //speed.setText("Current speed: " + new DecimalFormat("#.##").format(speed) + " km/hr");
            else {
                Toast.makeText(LocationService.this, "DISTANCE==>" + new DecimalFormat("0.0##").format(distance) + " Km's.", Toast.LENGTH_SHORT).show();
            }

            lStart = lEnd;
            strDistance = new DecimalFormat("0.0##").format(distance) + "Km";
            LogUtils.i("TOTAL DISTANCE+++>" + strDistance);
            //SavePref.saveInt(context,"TotalDistance", strDistance);

            LogUtils.i("Distance in shared preference==>" + strDistance);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        lStart = null;
        lEnd = null;
        distance = 0;
        return super.onUnbind(intent);
    }
}