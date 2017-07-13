package com.tommy.driver;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.tommy.driver.adapter.Constants;

public class EarningTabActivity extends TabActivity {

    ImageButton back;
    ImageView bank_info;
    View tabview;
    String userID;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earningtab);

        back = (ImageButton)findViewById(R.id.backButton);
        bank_info = (ImageView) findViewById(R.id.bank_info);

        TabHost tabHost = getTabHost();

        System.out.println("tabactvivylsdifj");
        // Tab for Photos
        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("driverid", null);
        System.out.println("UserID in settings tab" + userID);

        TabSpec photospec = tabHost.newTabSpec("Driver Earnings");
        tabview = createTabView(tabHost.getContext(), "Driver Earnings");
       // photospec.setIndicator("Photos", getResources().getDrawable(R.drawable.icon_photos_tab));
        photospec.setIndicator("Drive Earnings");
        Intent photosIntent = new Intent(this, EarningActivity_.class);
        photosIntent.putExtra("userid",userID);
        photospec.setContent(photosIntent);
        tabHost.addTab(photospec);
        // Tab for Songs
        TabSpec songspec = tabHost.newTabSpec("Referral Earning");
        // setting Title and Icon for the Tab
       tabview = createTabView(tabHost.getContext(), "Referral Earning");
        songspec.setIndicator("Referral Earning");

        Intent songsIntent = new Intent(this, ReferralEarningsActivity_.class);
        songsIntent.putExtra("userid",userID);
        songspec.setContent(songsIntent);
        
        // Adding all TabSpec to TabHost
        // Adding photos tab
        tabHost.addTab(songspec); // Adding songs tab

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EarningTabActivity.this,Map_Activity.class);
                startActivity(intent);
            }
        });
        bank_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(EarningTabActivity.this,Bank_Details_.class);
                startActivity(i);
                finish();
            }
        });
    }

    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setGravity(Gravity.CENTER);
        tv.setText(text);
        return view;
    }
}