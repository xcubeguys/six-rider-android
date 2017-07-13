package com.tommy.driver;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tommy.driver.adapter.Constants;
import com.tommy.driver.adapter.FontChangeCrawler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

@EActivity(R.layout.activity_rating)
public class RatingActivity extends AppCompatActivity {

    FlexibleRatingBar driverRatingBar;
    String driverId;
    ProgressDialog progressDialog;
    ImageView driverEmoji;

    @Click(R.id.back)
    void back() {

        Intent intent=new Intent(this,Map_Activity.class);
        startActivity(intent);
    }

    @ViewById(R.id.content_Txt)
    TextView contentTxt;

    @AfterViews
    void create() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        driverRatingBar=(FlexibleRatingBar)findViewById(R.id.flexibleRatingBar);
        driverEmoji=(ImageView) findViewById(R.id.driver_emoji);

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = prefs.getString("driverid", null);

        getOverallRatings();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {

        Intent intent=new Intent(RatingActivity.this,Map_Activity.class);
        startActivity(intent);
    }

    public void showDialog() {

        progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public void dismissDialog(){
        if(!RatingActivity.this.isFinishing()) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    public void getOverallRatings(){

        //Get datasnapshot at your "users" root node
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("trips_data");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue()!=null){
                            //Get map of users in datasnapshot
                            showDialog();
                            collectRatings((Map<String,Object>) dataSnapshot.getValue());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void collectRatings(Map<String,Object> users) {

        try {

            ArrayList<Float> Ratings = new ArrayList<>();

            //iterate through each user, ignoring their UID
            for (Map.Entry<String, Object> entry : users.entrySet()) {

                //Get user map
                Map singleUser = (Map) entry.getValue();

                //phoneNumbers.add((String) singleUser.get("driverid"));
                System.out.println("ratings driver id====>" + (String) singleUser.get("driverid"));

                System.out.println("ratings while getting====>" + driverId);

                String driver = String.valueOf(singleUser.get("driverid")).trim();

                if (!driver.equals("null")) {

                    if (driver.matches(driverId)) {

                        //Get phone field and append to list
                        String rate = String.valueOf(singleUser.get("rider_rating")).trim();
                        Float addrate = Float.parseFloat(rate);

                        if (addrate > 0) {

                            Ratings.add(addrate);
                        }
                    }
                }
            }

            System.out.println("ratings====>" + Ratings.toString());
            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.CEILING);

            Float totalrating = sumRatings(Ratings);
            System.out.println(df.format(totalrating));
            System.out.println("ratings====>" + String.valueOf(df.format(totalrating)));

            totalrating = Float.parseFloat(df.format(totalrating));
            driverRatingBar.setRating(totalrating);

            if (totalrating <= 3) {
                contentTxt.setText(R.string.low_rating);
            } else if (totalrating > 3) {
                contentTxt.setText(R.string.rating_content);
            } else if (totalrating == 0) {

                contentTxt.setText(R.string.no_rating_yet);
            }
            try {
                int ratingInt = Math.round(totalrating);

                switch(ratingInt) {

                    case 1:
                        driverEmoji.setBackgroundResource(R.drawable.one);
                        //Glide.with(this).load(R.drawable.testdra).into(imageViewTarget);

                        break;
                    case 2:
                        driverEmoji.setBackgroundResource(R.drawable.two);
                        //Glide.with(this).load(R.raw.emoji_rate2).into(imageViewTarget);
                        break;
                    case 3:
                        driverEmoji.setBackgroundResource(R.drawable.three);
                        //Glide.with(this).load(R.raw.emoji_rate3).into(imageViewTarget);
                        break;
                    case 4:

                        //Glide.with(this).load(R.raw.emoji_rate4).into(imageViewTarget);
                        driverEmoji.setBackgroundResource(R.drawable.four);
                        break;
                    case 5:
                        driverEmoji.setBackgroundResource(R.drawable.five);
                        //Glide.with(this).load(R.raw.emoji_rate5).into(imageViewTarget);
                        break;
                    case 0:
                        driverEmoji.setBackgroundResource(R.drawable.none);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            dismissDialog();
        }
        catch (Exception e)
        {
            System.out.println("Exception in Rating"+e);
            dismissDialog();
        }
    }

    public float sumRatings(ArrayList<Float> Ratings){

        int i;
        Float sum = 0.0f;

        for(i = 0; i < Ratings.size(); i++)
            sum += Ratings.get(i);

        return sum/Ratings.size();
    }
}