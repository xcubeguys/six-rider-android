package com.tommy.driver;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Created by test on 12/27/16.
 */
public class TabActivity extends ActivityGroup {
    Intent intent;
    TabHost.TabSpec spec1,spec2,spec3,spec4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);

        //TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
        //tabHost.setup(this.getLocalActivityManager());

        /*TabHost.TabSpec spec1=tabHost.newTabSpec("Tab 1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Tab 1",getResources().getDrawable(R.drawable.home));

        TabHost.TabSpec spec2=tabHost.newTabSpec("Tab 2");
        spec1.setContent(R.id.tab2);
        spec2.setIndicator("Tab 2",getResources().getDrawable(R.drawable.home));

        TabHost.TabSpec spec3=tabHost.newTabSpec("Tab 3");
        spec3.setIndicator("Tab 3",getResources().getDrawable(R.drawable.home));
        spec3.setContent(R.id.tab3);

        TabHost.TabSpec spec4=tabHost.newTabSpec("Tab 4");
        spec3.setIndicator("Tab 3",getResources().getDrawable(R.drawable.home));
        spec3.setContent(R.id.tab3);*/

        intent = new Intent().setClass(getApplicationContext(),Map_Activity.class);
        //spec1 = tabHost.newTabSpec("tab_name").setIndicator("", getResources().getDrawable(R.drawable.home)).setContent(intent);
        //tabHost.addTab(spec1);

        System.out.println("dfdfdfdf");
        //intent = new Intent().setClass(getApplicationContext(),EarningActivity_.class);
        intent = new Intent().setClass(getApplicationContext(),EarningTabActivity.class);
       // spec2 = tabHost.newTabSpec("tab_name").setIndicator("", getResources().getDrawable(R.drawable.credit_card)).setContent(intent);
       // tabHost.addTab(spec2);

        intent = new Intent().setClass(getApplicationContext(),RatingActivity_.class);
       // spec3 = tabHost.newTabSpec("tab_name").setIndicator("", getResources().getDrawable(R.drawable.star)).setContent(intent);
        //tabHost.addTab(spec3);


        intent = new Intent().setClass(getApplicationContext(),SettingActivity_.class);
        //spec4 = tabHost.newTabSpec("tab_name").setIndicator("", getResources().getDrawable(R.drawable.account_box_outline)).setContent(intent);
       // tabHost.addTab(spec4);

    }
}
