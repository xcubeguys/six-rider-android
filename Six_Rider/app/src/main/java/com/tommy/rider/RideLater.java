package com.tommy.rider;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tommy.rider.adapter.Constants;
import com.tommy.rider.utils.LogUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class RideLater extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    TextView pickupdate, pickuptime, pickuploc, droploc;
    RelativeLayout pickup_layout, time_layout, pickup_address_layout, drop_address_layout;
    DatePickerDialog dpd;
    TimePickerDialog tpd;

    JSONObject strJsonCategory;
    String[] carcategory;

    LatLng startLatLng;
    LatLng destLatLng;
    LatLng exceptionLatLng;
    LatLng pickupLatLng;
    LatLng destinationLatLng;
    LatLng center;
    LatLng orginlat;
    LatLng destlat;
    String pickupCountryCode, User_id, pickupDateTime;
    String dropCountryCode, strCarCategory, strSelectedCategory, pickupAddress, destinationAddress;
    Double originLAT, originLNG, destLAT, destLNG, newOriginLat, newOriginLng, newDestLat, newDestLng, calctotalDistance;
    ImageButton back;
    Button done;
    Spinner carategory;
    ProgressDialog progressDialog;
    String rideLaterStatus, rideLaterMessage, request_id, carCategory;
    String strpickuploca, strdroploca, cashStatus, paymenttype;

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        LogUtils.e(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ridelater);


        pickup_layout = (RelativeLayout) findViewById(R.id.pickup_layout);
        time_layout = (RelativeLayout) findViewById(R.id.time_layout);
        pickup_address_layout = (RelativeLayout) findViewById(R.id.pickup_address_layout);
        drop_address_layout = (RelativeLayout) findViewById(R.id.drop_address_layout);

        pickupdate = (TextView) findViewById(R.id.edtpickupdate);
        pickuptime = (TextView) findViewById(R.id.edtpickuptime);
        pickuploc = (TextView) findViewById(R.id.edtpickupaddress);
        droploc = (TextView) findViewById(R.id.edtdropaddress);

        back = (ImageButton) findViewById(R.id.backButton);
        done = (Button) findViewById(R.id.done_button);


        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        User_id = prefs.getString("userid", null);
        LogUtils.i("UserID in settings" + User_id);
        //Change Font to Whole View


        getCategoryDetails();

        carategory = (Spinner) findViewById(R.id.car_category);


        carategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                strSelectedCategory = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RideLater.this, MapActivity.class);
                startActivity(i);
                finish();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPaymentReference();
            }
        });
        //date and time picker dialog
        Calendar now = Calendar.getInstance();
        dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)


        );

        pickup_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar minDate = Calendar.getInstance();
                //  DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(RideLater.this, getcalendar.get(Calendar.YEAR), getcalendar.get(Calendar.MONTH), getcalendar.get(Calendar.DAY_OF_MONTH));
                minDate.add(Calendar.DATE, 0);
                dpd.setMinDate(minDate);
                dpd.show(getFragmentManager(), "datePicker");
                //dpd.show(getFragmentManager(),"datepicker");

            }
        });

        time_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance(TimeZone.getDefault());
                LogUtils.i("Current time => " + c.getTime());

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = df.format(c.getTime());
                LogUtils.i("formattedDate date==" + formattedDate);
                tpd = TimePickerDialog.newInstance(
                        RideLater.this,
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        false
                );
                if (pickupdate.getText().toString().trim().length() == 0) {
                    alertSnackBar(getResources().getString(R.string.enter_pickup_date));
                } else {
                    if (pickupdate.getText().toString().trim().equals(formattedDate)) {
                        tpd.setMinTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE) + 3,
                                c.get(Calendar.SECOND));
                        tpd.show(getFragmentManager(), "Timepickerdialog");
                    } else {
                        tpd.setMinTime(0, 0,
                                0);
                        tpd.show(getFragmentManager(), "Timepickerdialog");
                    }
                }

            }
        });

       /* pickup_address_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickup_address_layout.setEnabled(false);
                openAutocompleteActivity(Constants.ORIGIN_REQUEST_CODE_AUTOCOMPLETE);
            }
        });*/
        pickup_address_layout.setOnClickListener(new DebouncedOnClickListener(500) {
            @Override
            public void onDebouncedClick(View v) {
                pickup_address_layout.setEnabled(false);
                openAutocompleteActivity(Constants.ORIGIN_REQUEST_CODE_AUTOCOMPLETE);
            }
        });
       /* drop_address_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drop_address_layout.setEnabled(false);
                openAutocompleteActivity(Constants.DEST_REQUEST_CODE_AUTOCOMPLETE);
            }
        });*/
        drop_address_layout.setOnClickListener(new DebouncedOnClickListener(500) {
            @Override
            public void onDebouncedClick(View v) {
                drop_address_layout.setEnabled(false);
                openAutocompleteActivity(Constants.DEST_REQUEST_CODE_AUTOCOMPLETE);
            }
        });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        LogUtils.i("monthofyear=" + monthOfYear);
        String month = "", day = "";
        if (dayOfMonth < 10) {
            day = "0" + dayOfMonth;
            LogUtils.i("day=" + day);
        } else {
            day = String.valueOf(dayOfMonth);
        }
        if (monthOfYear < 9) {
            month = "0" + (++monthOfYear);
            LogUtils.i("month=" + month);
        } else {
            month = String.valueOf(++monthOfYear);
        }
        //    String date = dayOfMonth+"-"+(++monthOfYear)+"-"+year;
        String date = day + "-" + month + "-" + year;
        LogUtils.i("new date of settext==" + date);
        pickupdate.setText(date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        int hour = hourOfDay;
        int minutes = minute;
        int hours = hourOfDay;
        String timeSet = "";
        if (hours > 12) {
            hour -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String min = "";
        if (minutes < 10)
            min = "0" + minutes;
        else
            min = String.valueOf(minutes);
        String hr = "";
        if (hour < 10)
            hr = "0" + hour;
        else
            hr = String.valueOf(hour);


        LogUtils.i("hour====" + hr);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hr).append(':')
                .append(min).append(" ").append(timeSet).toString();
        //et1.setText(aTime);

        LogUtils.i("Current Country==>" + aTime);
        pickuptime.setText(aTime);
    }

    private void openAutocompleteActivity(int REQUEST_CODE_AUTOCOMPLETE) {
        try {

            //Get country alpha2 code
            String locale = this.getResources().getConfiguration().locale.getCountry();
            LogUtils.i("Current Country==>" + locale);

            //Location Filter based on the Country
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_LOCALITY)
                    .build();

            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.


            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(autocompleteFilter)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            pickup_address_layout.setEnabled(true);
            drop_address_layout.setEnabled(true);

        } catch (GooglePlayServicesRepairableException e) {
            pickup_address_layout.setEnabled(true);
            drop_address_layout.setEnabled(true);
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            pickup_address_layout.setEnabled(true);
            drop_address_layout.setEnabled(true);
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            LogUtils.e(message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == Constants.ORIGIN_REQUEST_CODE_AUTOCOMPLETE && data != null) {

            setAddress(data, resultCode, "ORIGIN");

        } else if (requestCode == Constants.DEST_REQUEST_CODE_AUTOCOMPLETE && data != null) {

            setAddress(data, resultCode, "DEST");
        }
    }

    public void setAddress(Intent data, int resultCode, String source) {

        if (resultCode == RESULT_OK) {
            // Get the user's selected place from the Intent.
            Place place = PlaceAutocomplete.getPlace(this, data);
            LogUtils.i("Place Selected: " + place.getName());
            LogUtils.i("Latitude Selected: " + place.getLatLng());

            if (source.matches("ORIGIN")) {
                exceptionLatLng = place.getLatLng();
                newOriginLat = exceptionLatLng.latitude;
                newOriginLng = exceptionLatLng.longitude;
                getCountryName(this, newOriginLat, newOriginLng, source);
                orginlat = new LatLng(newOriginLat, newOriginLng);
            } else {
                exceptionLatLng = place.getLatLng();
                newDestLat = exceptionLatLng.latitude;
                newDestLng = exceptionLatLng.longitude;
                getCountryName(this, newDestLat, newDestLng, source);
                destlat = new LatLng(newDestLat, newDestLng);
            }

            if (source.matches("ORIGIN")) {
                // Format the place's details and display them in the TextView.
                pickuploc.setText(formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri()));
                pickuploc.setText(place.getAddress());

            } else {
                // Format the place's details and display them in the TextView.
                droploc.setText(formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri()));
                droploc.setText(place.getAddress());
            }

            LogUtils.i("address 1===>" + place.getName());
            LogUtils.i("address 2==>" + place.getAddress());

            // Display attributions if required.
            CharSequence attributions = place.getAttributions();
            if (!TextUtils.isEmpty(attributions)) {

                if (source.matches("ORIGIN")) {
                    pickuploc.setText(Html.fromHtml(attributions.toString()));

                } else {
                    droploc.setText(Html.fromHtml(attributions.toString()));
                }

            } else {

                if (source.matches("ORIGIN")) {
                    pickuploc.setText(place.getAddress());

                } else {
                    droploc.setText(place.getAddress());

                }
            }
            String tempadd = String.valueOf(place.getAddress());

            LogUtils.i("address after parshe==>" + place.getAddress());

            //Get LAT and LNG
            getLocationFromAddress1(tempadd, source);

        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            LogUtils.e("Error: Status = " + status.toString());
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
            LogUtils.i("Canceled by user");
        }
    }

    public void getCountryName(Context context, double latitude, double longitude, String locationType) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                if (locationType.matches("ORIGIN")) {
                    pickupCountryCode = addresses.get(0).getCountryCode();
                } else {
                    dropCountryCode = addresses.get(0).getCountryCode();
                }
                LogUtils.i("Pickup==>" + pickupCountryCode);
                LogUtils.i("Drop==>" + dropCountryCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LatLng getLocationFromAddress1(String strAddress, String source) {
        LogUtils.i("Address" + strAddress);

        Geocoder coder = new Geocoder(RideLater.this, Locale.ENGLISH);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            if (source.matches("ORIGIN")) {

                originLAT = location.getLatitude();
                originLNG = location.getLongitude();

                if (originLAT != null & originLNG != null) {

                }

            } else {

                destLAT = location.getLatitude();
                destLNG = location.getLongitude();
            }


            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return p1;
    }

    private void getCategoryDetails() {
        final String url = Constants.CATEGORY_LIVE_URL + "Settings/getCategory";
        LogUtils.i("URL is" + url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        carcategory = new String[response.length() + 1];
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                strJsonCategory = response.getJSONObject(i);
                                strCarCategory = strJsonCategory.getString("categoryname");
                                LogUtils.d("OUTPUT IS " + strCarCategory);
                                carcategory[0] = "Select car category";
                                carcategory[i + 1] = strCarCategory;
                                LogUtils.i("CATEGORY" + carcategory[i]);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(RideLater.this, R.layout.spinner_item, carcategory);
                                carategory.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "No net", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "No Net", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void callRideLater() {

        showDialog();

        //http://demo.cogzideltemplates.com/tommy/requests/rideLater/userid/5854c286da71b4d6308b4567/start_lat/32.2/
        // start_long/33.3/end_lat/43.3/end_long/44.4/pickup_address/madurai tamil nadu/drop_address/chennai tamil nadu/
        // category/xx/date_time/10-03-2017 11:00 AM/payment_mode/stripe
        final String url = Constants.REQUEST_URL + "ridelater/userid/" + User_id + "/start_lat/" + newOriginLat + "/start_long/" + newOriginLng +
                "/end_lat/" + newDestLat + "/end_long/" + newDestLng + "/pickup_address/" + strpickuploca + "/drop_address/" + strdroploca +
                "/category/" + strSelectedCategory + "/date_time/" + pickupDateTime + "/payment_mode/" + paymenttype;
        LogUtils.i("SignUpURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                dismissDialog();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        rideLaterStatus = jsonObject.optString("status");

                        if (rideLaterStatus.equals("Success")) {

                            request_id = jsonObject.optString("request_id");
                            carCategory = jsonObject.optString("car_category");
                            saveInFirebase(request_id, carCategory, newOriginLat, newOriginLng, newDestLat, newDestLng);
                            Toast.makeText(RideLater.this, "we'll request a car on your behalf to be there when you’re ready to go.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RideLater.this, MapActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        } else if (rideLaterStatus.equals("Fail")) {
                            Toast.makeText(getApplicationContext(), "Already added the ride on this time", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.invalid_username_or_password, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismissDialog();
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();

                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    public void showDialog() {
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            if (!isFinishing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    private void alertSnackBar(String alertMessage) {
        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), alertMessage, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.RED);
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER);
        textView.setMaxHeight(30);
        textView.setMaxLines(3);
        textView.setPaddingRelative(0, 20, 0, 0);
        textView.setPadding(0, 20, 0, 0);
        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT));
        params.setMargins(0, 30, 0, 0);
        textView.setLayoutParams(params);
        snackbar.show();
    }

    public void saveInFirebase(String request_id, String carCategory, Double orginlat, Double orginlng, Double destlat, Double destlng) {

        if (User_id != null && !User_id.isEmpty()) {
            LogUtils.i("Firebase URL" + FirebaseDatabase.getInstance().getReference());

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ride_later").child(User_id).child(request_id);
            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("status", "0");
            updates.put("car_category", carCategory);
            updates.put("orgin_lat", orginlat);
            updates.put("orgin_lng", orginlng);
            updates.put("dest_lat", destlat);
            updates.put("dest_lng", destlng);
            //updates.put("request_id",request_id);
            ref.updateChildren(updates);
            LogUtils.i("status updated in firebase");
            /*Map<String, Object> updateaccept= new HashMap<String, Object>();
            updateaccept.put("status","0");
            updateaccept.put("request_id","0");
            updates.put(request_id,updateaccept);*/

            ref.setValue(updates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    LogUtils.i("DATA SAVED SUCCESSFULLY");
                    if (databaseError != null) {
                        LogUtils.i("DATA SAVED SUCCESSFULLY");
                    }
                }
            });
        }
    }

    public String getCurrentTime() {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("hh:mm aa");//24 Hour Format
        date.setTimeZone(TimeZone.getDefault());

        return date.format(currentLocalTime);
    }

    private boolean checktimings(String time, String endtime) {

        String pattern = "hh:mm aa";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1.before(date2)) {

                return true;
            } else if (date1.equals(date2)) {

                return true;
            } else {

                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Payment Type Listener
    private void getPaymentReference() {
        if (User_id != null) {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("riders_location").child(User_id).child("Paymenttype");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        if (status != null) {
                            if (status.matches("stripe")) {
                                paymenttype = "stripe";
                                callridelater();
                            } else if (status.matches("cash")) {
                                getCashOnOff();
                            } else if (status.matches("corpID")) {
                                paymenttype = "corpID";
                                callridelater();
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void callridelater() {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        LogUtils.i("Current time => " + c.getTime());

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        LogUtils.i("formattedDate date==" + formattedDate);
        if (strSelectedCategory == null || strSelectedCategory.equals("Select car category")) {
            alertSnackBar(getString(R.string.selectcategory));
        } else if (pickupdate.getText().toString().trim().length() == 0) {
            alertSnackBar(getResources().getString(R.string.enter_pickup_date));
        } else if (pickuptime.getText().toString().trim().length() == 0) {
            alertSnackBar(getResources().getString(R.string.enter_pickup_time));
        } else if (pickuploc.getText().toString().trim().length() == 0) {
            alertSnackBar(getResources().getString(R.string.enter_pickup_location));
        } else if (droploc.getText().toString().trim().length() == 0) {
            alertSnackBar(getResources().getString(R.string.enter_destination_location));
        } else {

            LogUtils.i("====>" + pickupdate.getText().toString() + pickuptime.getText().toString());

            pickupDateTime = pickupdate.getText().toString() + " " + pickuptime.getText().toString();
            LogUtils.i("pickuptimeee in irest=" + pickupDateTime);


            strpickuploca = pickuploc.getText().toString();
            strdroploca = droploc.getText().toString();

            try {
                pickupDateTime = pickupDateTime.replaceAll("/", "");
                pickupDateTime = URLEncoder.encode(pickupDateTime, "UTF-8");
                //   pickupDateTime = pickupDateTime.replaceAll(" ", "%20");
                LogUtils.i("pickuptimeee=" + pickupDateTime);
                strpickuploca = strpickuploca.replaceAll("/", "");
                strpickuploca = URLEncoder.encode(strpickuploca, "UTF-8");
                strdroploca = strdroploca.replaceAll("/", "");
                strdroploca = URLEncoder.encode(strdroploca, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (pickupdate.getText().toString().trim().equals(formattedDate)) {
                if (checktimings(pickuptime.getText().toString(), getCurrentTime())) {
                    alertSnackBar(getString(R.string.valid_time));
                } else {
                    callRideLater();
                }
            } else {
                callRideLater();
            }

        }

    }

    //Payment Type Listener
    private void getCashOnOff() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("cashoption").child("status");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    cashStatus = dataSnapshot.getValue().toString();

                    if (!cashStatus.matches("on")) {
                        alertSnackBar(getString(R.string.cashnotavailable));
                    } else {
                        paymenttype = "cash";
                        callridelater();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}