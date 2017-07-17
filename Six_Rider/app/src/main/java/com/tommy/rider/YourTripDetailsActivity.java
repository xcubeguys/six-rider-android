package com.tommy.rider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tommy.rider.adapter.Constants;
import com.tommy.rider.adapter.FontChangeCrawler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@EActivity(R.layout.activity_your_trip_details)
public class YourTripDetailsActivity extends AppCompatActivity {

    String userID,tripID,strTripDate,strDriveName,strDriverImage,strCarName,strCarImage,strDistanceTraveled,
            strPickupTime,strDropTime,strPickupLocation,strDropLocation,strPaymentType,strTotalAmount,strCancelfee,frommap,
            strRating,strTax,strAirportSurge,strBookFee,google_api_key,waypointfee="0";
    ProgressDialog progressDialog;

    @ViewById(R.id.trip_date)
    TextView tripDate;

    @ViewById(R.id.driver_name)
    TextView driverName;

    @ViewById(R.id.car_type)
    TextView carName;

    @ViewById(R.id.distance_traveled)
    TextView distanceTraveled;

    @ViewById(R.id.pickup_time)
    TextView pickupTime;

    @ViewById(R.id.drop_time)
    TextView dropTime;

    @ViewById(R.id.pick_location)
    TextView pickupLocation;

    @ViewById(R.id.drop_location)
    TextView dropLocation;

    @ViewById(R.id.payment_mode)
    ImageView paymentMode;

    @ViewById(R.id.total_price)
    TextView totalAmount;

    @ViewById(R.id.toll_price)
    TextView tollPrice;

    @ViewById(R.id.txt_cancel_price)
    TextView txtcancel;

    @ViewById(R.id.cancel_price)
    TextView cancelprice;

    @ViewById(R.id.driver_image)
    ImageView driverImage;

    @ViewById(R.id.car_image)
    ImageView carImage;

    @ViewById(R.id.ratingBar)
    RatingBar ratingBar;

    @ViewById(R.id.backButton)
    ImageButton back;

    @ViewById(R.id.txt_book_price)
    TextView tvBookPrice;

    @ViewById(R.id.book_price)
    TextView bookPrice;


    @ViewById(R.id.txt_tax_price)
    TextView tvTaxPrice;

    @ViewById(R.id.tax_price)
    TextView taxPrice;


    @ViewById(R.id.txt_airport_price)
    TextView tvAirportPrice;

    @ViewById(R.id.airport_price)
    TextView airportPrice;

    @ViewById(R.id.cancellay)
    LinearLayout cancellay;

    @ViewById(R.id.waypinttxt)
    TextView wayPointTxt;

    @ViewById(R.id.waypointamnt)
    TextView wayPointAmount;


    private String TAG = null;


    @AfterViews void yourTripDetails(){
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        SharedPreferences prefs=getSharedPreferences(Constants.MY_PREFS_NAME,MODE_PRIVATE);
        userID=prefs.getString("userid",null);
        System.out.println("User ID in YourTripsDetails"+userID);

        Intent tripDetails = getIntent();
        tripID = tripDetails.getStringExtra("trip_id");
        frommap=tripDetails.getStringExtra("frommap");
        strTripDate = tripDetails.getStringExtra("created");
        System.out.println("Trip ID in YourTripsDetails"+tripID);

        tvBookPrice.setVisibility(View.GONE);
        bookPrice.setVisibility(View.GONE);
        tvTaxPrice.setVisibility(View.GONE);
        taxPrice.setVisibility(View.GONE);
        tvAirportPrice.setVisibility(View.GONE);
        airportPrice.setVisibility(View.GONE);

        wayPointTxt.setVisibility(View.GONE);
        wayPointAmount.setVisibility(View.GONE);

        getKeys();
        getTripDetails();
        getRating();
    }

    private void getTripDetails() {
        showDialog();
        final String url = Constants.REQUEST_URL + "getTrips/trip_id/"+tripID;
        System.out.println("Get Trips URL==>"+url);
        final JsonArrayRequest tripListReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() <= 0) {
                    dismissDialog();
                } else {
                    dismissDialog();
                    for (int i = 0; i < response.length(); i++) {

                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            //strTripDate=getDate(Long.parseLong(jsonObject.optString("created")));
                            strDriveName=jsonObject.optString("driver_name");
                            strDriverImage=jsonObject.optString("driver_profile");
//                            strCarImage=jsonObject.optString("");
                            strCarName=jsonObject.optString("car_category");
                            strDistanceTraveled=jsonObject.optString("total_distance");
//                            strPickupTime=jsonObject.optString("");
//                            strDropTime=jsonObject.optString("");
                            strPickupLocation=jsonObject.optString("pickup_address");
                            strDropLocation=jsonObject.optString("drop_address");
                            strPaymentType=jsonObject.optString("payment_mode");
                            strTotalAmount=jsonObject.optString("total_price");
                            strCancelfee=jsonObject.optString("cancelation_fee");

                            strTax=jsonObject.optString("tax_percentage");
                            strAirportSurge=jsonObject.optString("airport_surge");
                            strBookFee=jsonObject.optString("book_fee");
                            waypointfee=jsonObject.optString("waypoint_fee");
//                            strRating=jsonObject.optString("");

                            JSONObject waypointObject = jsonObject.optJSONObject("DestinationWaypoints");
                            if(waypointObject!=null){

                                int wayPointItemCount = waypointObject.length();
                                System.out.println("latitutesssss==>"+ wayPointItemCount);

                                wayPointTxt.setVisibility(View.VISIBLE);
                                wayPointAmount.setVisibility(View.VISIBLE);
                                wayPointAmount.setText("$ "+convertToDecimal(wayPointItemCount*Double.parseDouble(waypointfee)));
                                wayPointTxt.setText(String.valueOf(wayPointItemCount)+" Additional Stops");

                            }

                            JSONObject pickupObject = jsonObject.optJSONObject("pickup");
                            String strLat = pickupObject.optString("lat");
                            String strLong = pickupObject.optString("long");

                            System.out.println("latitute==>"+strLat+" longitute==>"+strLong);

                            String pickLatLng=null;

                            if(strLong!=null & strLong!=null)
                                pickLatLng=strLat+","+strLong;

                            if(strTax!=null)
                                if(!strTax.matches("0")&!strTax.matches("null")){
                                    taxPrice.setText(strTax+"%");
                                    tvTaxPrice.setVisibility(View.VISIBLE);
                                    taxPrice.setVisibility(View.VISIBLE);

                                }else {

                                    tvTaxPrice.setVisibility(View.GONE);
                                    taxPrice.setVisibility(View.GONE);

                                }
                            else{
                                tvTaxPrice.setVisibility(View.GONE);
                                taxPrice.setVisibility(View.GONE);
                            }

                            if(strAirportSurge!=null)
                                if(!strAirportSurge.matches("0")&!strAirportSurge.matches("null")){

                                    if(pickLatLng!=null)
                                        if(!pickLatLng.contains("null"))
                                            PlaceType(pickLatLng);

                                }else {
                                    tvAirportPrice.setVisibility(View.GONE);
                                    airportPrice.setVisibility(View.GONE);
                                }
                            else{
                                tvAirportPrice.setVisibility(View.GONE);
                                airportPrice.setVisibility(View.GONE);
                            }

                            if(strBookFee!=null)
                                if(!strBookFee.matches("0")&!strBookFee.matches("null")){

                                    double bookamt= Double.parseDouble(strBookFee);
                                    bookPrice.setText("$"+convertToDecimal(bookamt) );
                                    tvBookPrice.setVisibility(View.VISIBLE);
                                    bookPrice.setVisibility(View.VISIBLE);
                                }else {
                                    tvBookPrice.setVisibility(View.GONE);
                                    bookPrice.setVisibility(View.GONE);
                                }
                            else{
                                tvBookPrice.setVisibility(View.GONE);
                                bookPrice.setVisibility(View.GONE);
                            }

                            if(strTripDate!=null)
                                tripDate.setText(strTripDate);

                            if(strDriveName!=null) {
                                strDriveName=strDriveName.replaceAll("%20"," ");
                                //driverName.setText(Constants.capitalizeFirstLetter(strDriveName));
                                driverName.setText(strDriveName);
                            }

                            if(strCarName!=null) {
                                strCarName=strCarName.replaceAll("%20"," ");
                                carName.setText(strCarName);
                            }

                            if(strDistanceTraveled!=null) {
                                if(!strDistanceTraveled.equals("null")) {
                                    strDistanceTraveled = strDistanceTraveled.replaceAll("%20", " ");
                                    distanceTraveled.setText(strDistanceTraveled + " km");
                                }
                                else {
                                    distanceTraveled.setText("0 km");
                                }
                            }

                            if(strPickupTime!=null) {
                                strPickupTime=strPickupTime.replaceAll("%20"," ");
                                pickupTime.setText(strPickupTime);
                            }

                            if(strDropTime!=null) {
                                strDropTime=strDropTime.replaceAll("%20"," ");
                                dropTime.setText(strDropTime);
                            }

                            if(strPickupLocation!=null) {
                                strPickupLocation=strPickupLocation.replaceAll("%20"," ");
                                pickupLocation.setText(strPickupLocation);
                            }

                            if(strDropLocation!=null) {
                                strDropLocation=strDropLocation.replaceAll("%20"," ");
                                dropLocation.setText(strDropLocation);
                            }

                            if(strPaymentType!=null) {
                                strPaymentType=strPaymentType.replaceAll("%20"," ");
                                if (strPaymentType.matches("stripe")) {
                                    paymentMode.setBackground(getResources().getDrawable(R.mipmap.ub__payment_type_delegate));
                                } else if (strPaymentType.matches("cash")) {
                                    paymentMode.setBackground(getResources().getDrawable(R.drawable.ub__payment_type_cash));

                                }
                                else if (strPaymentType.matches("corpID")) {
                                    paymentMode.setBackground(getResources().getDrawable(R.mipmap.ic_cardss));
                                }
                            }

                            if(strTotalAmount!=null) {
                                strTotalAmount=strTotalAmount.replaceAll("%20"," ");
                                totalAmount.setText("$ "+strTotalAmount);
                            } else {
                                totalAmount.setText("$ 0");
                            }

                            if(strCancelfee!=null){
                                if(!strCancelfee.equals("0")){
                                    cancelprice.setVisibility(View.VISIBLE);
                                    txtcancel.setVisibility(View.VISIBLE);
                                    cancellay.setVisibility(View.VISIBLE);
                                    strTotalAmount=strTotalAmount.replaceAll("%20"," ");
                                    cancelprice.setText("$ "+strTotalAmount);
                                }
                                else{
                                    cancelprice.setVisibility(View.GONE);
                                    txtcancel.setVisibility(View.GONE);
                                    cancellay.setVisibility(View.GONE);
                                }
                            }


                            Glide.with(getApplicationContext()).load(strDriverImage).asBitmap().centerCrop().placeholder(R.drawable.account_circle_grey).skipMemoryCache(true).into(new BitmapImageViewTarget(driverImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    driverImage.setImageDrawable(circularBitmapDrawable);
                                }
                            });

                          /*  Glide.with(getApplicationContext()).load(strCarImage).asBitmap().centerCrop().placeholder(R.drawable.trip_car).skipMemoryCache(true).into(new BitmapImageViewTarget(carImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    carImage.setImageDrawable(circularBitmapDrawable);
                                }
                            });*/

//                            ratingBar.setRating(Float.parseFloat(strRating));

                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismissDialog();
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                } if (volleyError instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        tripListReq.setRetryPolicy(new DefaultRetryPolicy(5000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(tripListReq);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Click(R.id.backButton)
    void goBack(){
        if (frommap != null) {
            if(frommap.equals("yes"))
            {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
        else{
            finish();
        }

    }


    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        TimeZone tz = TimeZone.getDefault();
        cal.setTimeInMillis(time * 1000);
        cal.add(Calendar.MILLISECOND, tz.getOffset(cal.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date currenTimeZone = (Date) cal.getTime();
        System.out.println("Trip Date==>"+sdf.format(currenTimeZone));
        return sdf.format(currenTimeZone);
    }

    boolean isDouble(String str) {
        try {

            Double.parseDouble(str);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public String convertToDecimal(Double amount){

        if(amount>0){
            System.out.println("THE AMOUNT IS" + new DecimalFormat("0.00").format(amount));
            return new DecimalFormat("0.00").format(amount);
        }
        else {
            return String.valueOf(0);
        }
    }

    public void showDialog(){
        progressDialog= new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void dismissDialog(){
        if(progressDialog!=null && progressDialog.isShowing()){
            if(!isFinishing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    public void getRating(){
        if(tripID!=null) {

            //Get datasnapshot at your "users" root node
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
            ref.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get map of users in datasnapshot
                            if(dataSnapshot.getValue()!=null) {

                                System.out.println("response ===>"+dataSnapshot.toString());

                                Object tollAmount = dataSnapshot.child("tollfee").getValue();
                                Object RiderRatings = dataSnapshot.child("rider_rating").getValue();

                                System.out.println("response tollAmount===>"+tollAmount);
                                System.out.println("response RiderRatings===>"+RiderRatings);

                                if(tollAmount!=null){

                                    tollPrice.setText("$"+tollAmount);
                                }else {

                                    tollPrice.setText("$0");
                                }

                                if(RiderRatings!=null){

                                    ratingBar.setRating(Float.parseFloat(String.valueOf(RiderRatings)));
                                }else {

                                    ratingBar.setRating(0);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
        }
    }

    public void PlaceType(String latlng) {

        //final String url="https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeID+"&key="+google_api_key;
        //final String url="https://maps.googleapis.com/maps/api/geocode/json?latlng="+latlng+"&sensor=true&key="+google_api_key;
        final String url="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+latlng+"&radius=1000&types=airport&key="+google_api_key;

        System.out.println(" ONLINE URL is " + url);

        // Creating volley request obj
        JsonObjectRequest movieReq = new JsonObjectRequest(url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        // Parsing json
                        try {

                            String status = response.getString("status");
                            System.out.println("status responce====>" + status);


                            if (status.matches("OK")) {

                                JSONArray result = response.optJSONArray("results");

                                JSONObject zerothPostion = result.optJSONObject(0);

                                JSONArray types = zerothPostion.optJSONArray("types");

                                String placeType =types.optString(0);

                                System.out.println("Airport PlaceType==>" + placeType);

                                if(placeType.matches("airport")){

                                    double airportamt= Double.parseDouble(strAirportSurge);
                                    airportPrice.setText("$"+convertToDecimal(airportamt) );
                                    tvAirportPrice.setVisibility(View.VISIBLE);
                                    airportPrice.setVisibility(View.VISIBLE);

                                }


                            } else if (status.matches("INVALID_REQUEST")) {

                                String error_msg = response.getString("error_message");

                                System.out.println("The Error message in invalidreq"+error_msg);

                            } else if (status.matches("REQUEST_DENIED")) {

                                String error_msg = response.getString("error_message");

                                System.out.println("The Error message in reqdenied"+error_msg);

                            } else {

                                System.out.println("Execption on Getting Airport Type");
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {

                    Toast.makeText(YourTripDetailsActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }


    private void getKeys() {

        String url = Constants.CATEGORY_LIVE_URL + "settings/getdetails";
        System.out.println(" CATEGOR URL is " + url);

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject signIn_jsonobj = response.getJSONObject(i);
                                google_api_key = signIn_jsonobj.optString("google_api_key");

                            } catch (JSONException e) {
                                //stopAnim();
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {

                }
                VolleyLog.d("Error", "EarningActivity: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }
}