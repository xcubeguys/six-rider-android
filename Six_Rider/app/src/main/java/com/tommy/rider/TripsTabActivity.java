package com.tommy.rider;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class TripsTabActivity extends TabActivity {
    ImageButton back;
    View tabview;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripstab);

        back = (ImageButton)findViewById(R.id.backButton);

        TabHost tabHost = getTabHost();

        System.out.println("TripsTabActivity");
        // Tab for Photos

        TabSpec photospec = tabHost.newTabSpec("Your Trips");
        tabview = createTabView(tabHost.getContext(), "Your Trips");
       // photospec.setIndicator("Photos", getResources().getDrawable(R.drawable.icon_photos_tab));
        photospec.setIndicator("Your Trips");
        Intent photosIntent = new Intent(this, YourTripsActivity_.class);
        photospec.setContent(photosIntent);
        tabHost.addTab(photospec);
        // Tab for Songs
        TabSpec songspec = tabHost.newTabSpec("Upcoming Trips");
        // setting Title and Icon for the Tab
       tabview = createTabView(tabHost.getContext(), "Upcoming Trips");
        songspec.setIndicator("Upcoming Trips");
        Intent songsIntent = new Intent(this, YourUpcomingTripsActivity_.class);
        songspec.setContent(songsIntent);
        
        // Adding all TabSpec to TabHost
        // Adding photos tab
        tabHost.addTab(songspec); // Adding songs tab

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent=new Intent(TripsTabActivity.this,MapActivity.class);
                startActivity(intent);;*/
                finish();
            }
        });


    }
    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;
    }
}