package com.tommy.rider;


import android.animation.IntEvaluator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidadvance.topsnackbar.TSnackbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.tommy.rider.adapter.Constants;
import com.tommy.rider.adapter.Contributor;
import com.tommy.rider.adapter.CustomRatingBar;
import com.tommy.rider.adapter.EasyTimer;
import com.tommy.rider.adapter.FontChangeCrawler;
import com.tommy.rider.adapter.FragmentDrawer;
import com.tommy.rider.adapter.MapRipple;
import com.tommy.rider.adapter.RequestInfo;
import com.tommy.rider.adapter.RetrofitArrayAPI;
import com.tommy.rider.adapter.SlideUpPanelLayout;
import com.tommy.rider.adapter.SortPlaces;
import com.tommy.rider.adapter.UberProgressDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener,
        LocationSource, LocationListener, android.location.LocationListener,
        OnMyLocationButtonClickListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener,FragmentDrawer.FragmentDrawerListener,
        GeoQueryEventListener, DirectionCallback, View.OnClickListener {

    //Log
    private static final String TAG = "GoogleMap";

    boolean doubleBackToExitPressedOnce = false;

    LocationManager locationManager;

    //CategoryLayout
    int n = 5;//Total Category
    SlideUpPanelLayout slideUpPanelLayout;
    SeekBar categorySeekBar;
    TextView content;
    TextView txtHatchBack, txtSedan, txtSUV, txtfour, referral_count;
    int progresscount = 5;
    String seekBarValue = "5", requesttype;
    Bitmap suvBitmap, sedanBitmap, hatchBackBitmap, fourthBitmap;
    Drawable suvCar, sedanCar, hatchBackCar, fourthCar;
    TextView estimateTime, maxPeople, minFare, perMinutePrice, perKmPrice;
    RelativeLayout seekBarLayout;
    LinearLayout carCategoryTitleLayout;
    CardView fareLayout;
    String firstName,cancelprice,canceltripid;

    String[] strCategoryName = new String[n];
    String[] strMinFare = new String[n];
    String[] strMaxPeople = new String[n];
    String[] strMinutePrice = new String[n];
    String[] strKmPrice = new String[n];
    String[] categoryMarker = new String[n];
    String[] categoryLogo = new String[n];
    String[] bookfee = new String[n];
    String[] airportfee = new String[n];
    String[] taxpercentage = new String[n];
    String carCategory;
    String calcBaseFare, calcPriceKM, calcPriceMin,strSubjectCategory,strCarCategory,strBookfee,strTaxpercentage,strAirportfee;
    int categoryLength;
    SharedPreferences.Editor getState;
    String tripState, strCacnelStatus,status,surgePrice;
    private Geocoder geocoder;

    MapRipple mapRipple;

    String[] subjectcategory;

    private List<Address> addressList;
    DatabaseReference tollReference;
    ValueEventListener tollListener;
    //Google Maps
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private OnLocationChangedListener mMapLocationListener = null;
    LocationRequest mLocationRequest;
    Location filterLocation,centerLoc,markerLoc;
    LatLng startLatLng, destLatLng, exceptionLatLng, pickupLatLng, destinationLatLng, center, orginlat, destlat,multiLatLng;

    Double originLAT, originLNG, destLAT, destLNG, newOriginLat, newOriginLng, newDestLat, newDestLng, calctotalDistance,
            newMultiLat, newMultiLng;

    CardView txtDriverArrivalETALayout;
    //Views
    TextView txtDriverArrivalETA,setPickupLocation, originLocation, destinationLocation,diaogDestinationLocation,lastTrip, lastTripTime,
            tripDriverName, number_plate, tripModelMakeName, tripCarName, tripEndDriverName, tripEndAmount, txtUserName,
            txtETA,update_drop_location,txtPickArea,viewReferralUser;
    ImageView menuButton, driverImage, cashButton, userProfileImage, carCategoryImage;
    Button headerButton, requestButton, changePayment, ratingSubmit, getFareEstimate, tollFee,setTerminal,changeTerminal;
    DrawerLayout mDrawerLayout;
    FragmentDrawer drawerFragment;
    Dialog progressBar;
    ProgressBar requestBar;

    Location mCurrentLocation;

    String maxShare, rideType, cashStatus, totalDistance_meter="";

    float driverBearing = 0.0f, getBearing = 0.0f;

    double nearByDistanceRadius=5;

    //Layouts
    RelativeLayout locationLayout, tripHistoryLayout, requestLayout, dropLocationLayout, FAB,updatedrop_location_layout;
    LinearLayout ratingLayout, categoryLayout, trip_info_layout, driverLayout,terminal_layout;

    //Strings
    String userID, google_api_key, driverID, tripID, requestStatus, requestID, paymentType, driverProfileName, driverProfileImage,
            pickupCountryCode, dropCountryCode, completeAddress, category, driverDisplayName,licenseplate,
            driverFirstname,multiDestCountryCode,driverLastName, driverProfileImageAccepted, driverMobileNumber,
            tripDriverCategory, totalDuration,totalDistance, tripDriverVehicleMake,tripDriverVehicleModel, licenseplate_number,
            riderfirstname,tripTime, earnings, earnings_daily, earnings_weekly, earnings_monthly, earnings_yearly;

    //Firebase
    GeoQuery geoQuery;
    GeoFire geoFire;

    //Timer to run every 3 second
    EasyTimer easyTimer = new EasyTimer(3000);
    SharedPreferences state;
    //Int
    int count = 0;

    private Map<String, Marker> markers;

    //check tripStatus
    boolean tripStatus = false;

    //progressDialog
    ProgressDialog progressDialog;
    Dialog dialog, faredialog;

    CheckBox check;
    Spinner sharecount;

    BitmapDescriptor bitmapDescriptor;

    LatLng prevLatLng = new LatLng(0, 0);

    Marker pickUPrDropMarker, myMarker;
    Polyline mPolyline;

    MaterialSpinner terminalSpinner,subjectspinner;
    MyBaseActivity myBaseActivity=new MyBaseActivity();

    String[] terminalsArray = new String[]{"Select your pickup area","Terminal 1, Level 1 Arrivals", "Terminal 2, Level 2 Arrivals", "Terminal 3, B1 Arrivals", "Terminal 4"};
    String[] terminalOneArray = new String[]{"Select Area","Door 4", "Door 5", "Door 6", "Door 7"};
    String[] terminalTowArray = new String[]{"Select Area","Door 1", "Door 2", "Door 3"};
    String[] terminalThreeArray = new String[]{"Select Area","B1 Door 1", "B1 Door 2", "B1 Door 3"};
    String[] commonTerminal= new String[]{"Select your pickup area","Terminal 1", "Terminal 2", "Terminal 3", "Terminal 4"};

    Dialog dialogMultipleDestination;

    TextView[] mulitipleDestinationTextView;
    LatLng[] mulitiDestLatLng;
    String[] mulitiDestCountryCode,mulitiDestaddress;

    int numberOfDesView = 8-1;
    private Tracker mTracker;
    int multiTvID;

    //Variable for multi is latlng,countrycode,address

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Change Font to Whole View
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        //Get Google API key
        getKeys();

        //currentLocationBitmap = BitmapDescriptorFactory.fromBitmap(drawCircle());
        initializeDestinationDialog();

        myBaseActivity.stopDisconnectTimer();

        // setup markers
        this.markers = new HashMap<>();

        //Full screen theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        //create preference for state
        getState = getSharedPreferences(Constants.MY_STATE_KEY, MODE_PRIVATE).edit();
        state = getSharedPreferences(Constants.MY_STATE_KEY, MODE_PRIVATE);
        tripState = state.getString("tripstate", null);
        tripStatus = state.getBoolean("tripbool", false);
        if (tripState == null) {
            //set end click to get request first time
            tripState = "endclicked";
            tripStatus = false;
        }

        System.out.println("THE TRIPSTATE" + tripState);

        //UserID from Shared preferences
        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        riderfirstname = prefs.getString("username", null);
        System.out.println("UserID in Map" + userID);

        //getPaymentTypeofUser
        getPaymentReference();

        progressBar = new Dialog(MapActivity.this);
        progressBar.setContentView(R.layout.overlay_dialog);

        //rideLater
        listeningRideLater();

        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        // [END shared_tracker]

        //callriderlater1();

        setPickupLocation = (TextView) findViewById(R.id.set_pickup);
        //Change Color of 9-Patch Background
        Drawable setPickupLocationBackground = setPickupLocation.getBackground();
        /*Drawable drawable = tintDrawable(setPickupLocationBackground, ColorStateList.valueOf(Color.parseColor(getResources().getString(R.string.app_color))));*/
        Drawable drawable = tintDrawable(setPickupLocationBackground, ColorStateList.valueOf(Color.parseColor(getResources().getString(R.string.app_color))));
        setPickupLocation.setBackground(drawable);
        txtETA = (TextView) findViewById(R.id.nearby_eta);
        viewReferralUser = (TextView) findViewById(R.id.referral_user);
        update_drop_location= (TextView) findViewById(R.id.update_drop_location);
        menuButton = (ImageView) findViewById(R.id.menu_button);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        headerButton = (Button) findViewById(R.id.header_button);
        txtDriverArrivalETALayout = (CardView) findViewById(R.id.card_view_txtDriverArrival);
        txtDriverArrivalETA = (TextView) findViewById(R.id.txtDriverArrival);
        txtDriverArrivalETA.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtDriverArrivalETA.setSelected(true);
        tollFee = (Button) findViewById(R.id.showtollfee);
        requestBar = (ProgressBar) findViewById(R.id.requestBar);

        /*Navigation Drawer Layout*/
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setDrawerListener(this);
        userProfileImage = (ImageView) mDrawerLayout.findViewById(R.id.rider_profile_image);
        txtUserName = (TextView) mDrawerLayout.findViewById(R.id.userName);

        /*Location Layout*/
        dropLocationLayout = (RelativeLayout) findViewById(R.id.drop_location_layout);
        updatedrop_location_layout= (RelativeLayout) findViewById(R.id.updatedrop_location_layout);
        terminal_layout= (LinearLayout) findViewById(R.id.terminal_layout);
        locationLayout = (RelativeLayout) findViewById(R.id.location_layout);
        originLocation = (TextView) findViewById(R.id.pick_location);
        destinationLocation = (TextView) findViewById(R.id.drop_location);
        diaogDestinationLocation = (TextView) dialogMultipleDestination.findViewById(R.id.drop_locations);
        terminalSpinner = (MaterialSpinner) findViewById(R.id.terminalSpinner);
        setTerminal = (Button) findViewById(R.id.setterminal);
        changeTerminal = (Button) findViewById(R.id.changeterminal);
        txtPickArea = (TextView) findViewById(R.id.pick_area);

        /*Trip History Layout*/
        tripHistoryLayout = (RelativeLayout) findViewById(R.id.trip_history_layout);
        lastTrip = (TextView) findViewById(R.id.last_trip);
        lastTripTime = (TextView) findViewById(R.id.last_trip_time);

        /*Request Layout*/
        requestLayout = (RelativeLayout) findViewById(R.id.request_layout);
        requestButton = (Button) findViewById(R.id.request_button);
        cashButton = (ImageView) findViewById(R.id.cashButton);
        changePayment = (Button) findViewById(R.id.payment_change);
        categoryLayout = (LinearLayout) findViewById(R.id.category_layout);
        getFareEstimate = (Button) findViewById(R.id.fare_estimate);

        /*Driver Layout*/
        driverLayout = (LinearLayout) findViewById(R.id.driver_layout);
        //vehicle_nmuber = (LinearLayout) findViewById(R.id.vehicle_nmuber);
        driverImage = (ImageView) findViewById(R.id.driver_image);
        carCategoryImage = (ImageView) findViewById(R.id.car_category_image);
        tripDriverName = (TextView) findViewById(R.id.driver_name);
        tripCarName = (TextView) findViewById(R.id.car_name);
        tripModelMakeName = (TextView) findViewById(R.id.model_make);
        number_plate = (TextView) findViewById(R.id.vehicle_nmuber_plate);
        trip_info_layout = (LinearLayout) findViewById(R.id.trip_info_layout);

        /*Trip Rating Layout*/
        ratingLayout = (LinearLayout) findViewById(R.id.trip_rating_layout);
        tripEndDriverName = (TextView) findViewById(R.id.trip_end_driver);
        tripEndAmount = (TextView) findViewById(R.id.trip_end_amount);
        ratingSubmit = (Button) findViewById(R.id.submit_button);

        /*Category Layout */
        slideUpPanelLayout = (SlideUpPanelLayout) findViewById(R.id.slide_up_layout);
        categorySeekBar = (SeekBar) findViewById(R.id.category_seek_bar);
        fareLayout = (CardView) findViewById(R.id.fare_layout);
        estimateTime = (TextView) findViewById(R.id.estimated_time);
        maxPeople = (TextView) findViewById(R.id.number_of_people);
        minFare = (TextView) findViewById(R.id.minimum_fare);
        carCategoryTitleLayout = (LinearLayout) findViewById(R.id.car_category_title);
        fareLayout = (CardView) findViewById(R.id.fare_layout);
        perMinutePrice = (TextView) findViewById(R.id.per_minute_price);
        perKmPrice = (TextView) findViewById(R.id.per_km_price);
        txtHatchBack = (TextView) findViewById(R.id.txt_hatchback);
        txtSedan = (TextView) findViewById(R.id.txt_sedan);
        txtSUV = (TextView) findViewById(R.id.txt_suv);
        txtfour = (TextView) findViewById(R.id.txt_four);
        referral_count = (TextView) findViewById(R.id.referral_count);
        //Share ride
        check = (CheckBox) findViewById(R.id.shareride);
        sharecount = (Spinner) findViewById(R.id.sharecount);

        getEarning();

        terminalSpinner.setItems(commonTerminal);

        terminalSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if(item!=null){

                    switch(item){

                        case "Terminal 1, Level 1 Arrivals":

                            txtPickArea.setText(item);
                            terminalSpinner.setItems(terminalOneArray);
                            terminalSpinner.setSelectedIndex(0);

                            break;

                        case "Terminal 2, Level 2 Arrivals":

                            txtPickArea.setText(item);
                            terminalSpinner.setItems(terminalTowArray);
                            terminalSpinner.setSelectedIndex(0);

                            break;

                        case "Terminal 3, B1 Arrivals":

                            txtPickArea.setText(item);
                            terminalSpinner.setItems(terminalThreeArray);
                            terminalSpinner.setSelectedIndex(0);

                            break;

                        default:

                            if(!item.contains("Door") && !item.matches("Select your pickup area") &&
                                    !item.matches("Select Area")){
                                txtPickArea.setText(item);
                                terminalSpinner.setItems(terminalTowArray);
                                terminalSpinner.setSelectedIndex(0);
                            }

                            break;
                    }
                }
            }
        });

        viewReferralUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                slideUpPanelLayout.animateClose();
                Intent settings = new Intent(MapActivity.this, ReferralUsersActivity_.class);
                startActivity(settings);
            }
        });

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharecount.setVisibility(View.INVISIBLE);
                } else {
                    sharecount.setVisibility(View.INVISIBLE);
                }
            }
        });

        //Map Initialization
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Google API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled) {

            GPSTurnOnAlert();
        }


        setOfflineCategory();

        if (isOnline()) {
            getCategoryInfo();
        }
        getFareInfo(0);

        originLocation.setOnClickListener(new DebouncedOnClickListener(1000) {
            @Override
            public void onDebouncedClick(View v) {
                openAutocompleteActivity(Constants.ORIGIN_REQUEST_CODE_AUTOCOMPLETE);
            }

        });

        destinationLocation.setOnClickListener(new DebouncedOnClickListener(1000) {
            @Override
            public void onDebouncedClick(View v) {

                openAutocompleteActivity(Constants.DEST_REQUEST_CODE_AUTOCOMPLETE);

            }

        });

        update_drop_location.setOnClickListener(new DebouncedOnClickListener(1000) {
            @Override
            public void onDebouncedClick(View v) {

                showDestinationDialog();
                //openAutocompleteActivity(Constants.UPDATE_REQUEST_CODE_AUTOCOMPLETE);
            }
        });

        setTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("am in Seting==>");

                String selectedTerminal = terminalSpinner.getItems().get(terminalSpinner.getSelectedIndex()).toString();

                if(txtPickArea.getText().toString().trim().equals("")|selectedTerminal.matches("Select your pickup area"))
                    Toast.makeText(MapActivity.this, "Select your pickup area.", Toast.LENGTH_SHORT).show();
                else if(selectedTerminal.matches("Select Area"))
                    Toast.makeText(MapActivity.this, "Select comoplete area.", Toast.LENGTH_SHORT).show();
                else {

                    String pickupArea =txtPickArea.getText().toString()+", "+selectedTerminal;

                    getState.putBoolean("getPlaceType", false);
                    getState.putBoolean("getPlaceName", false);
                    getState.apply();
                    terminal_layout.setVisibility(View.GONE);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    if(userID!=null)
                        ref.child("riders_location").child(userID).child("pickup_terminal").setValue(pickupArea);
                }
            }
        });

        changeTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtPickArea.setText("");
                boolean isChangiAirport = state.getBoolean("getPlaceName", false);

                System.out.println("PlaceNamee in Changing==>" + isChangiAirport);

                if(isChangiAirport){
                    terminalSpinner.setItems(terminalsArray);
                    terminalSpinner.setSelectedIndex(0);

                }
                else{
                    terminalSpinner.setItems(commonTerminal);
                    terminalSpinner.setSelectedIndex(0);
                }
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check the Drawable state for Navigation Drawer
                Drawable drawable = menuButton.getBackground();
                if (drawable.getConstantState().equals(getResources().getDrawable(R.drawable.close).getConstantState())) {
                    cancelRequest();
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });

        requestButton.setOnClickListener(new DebouncedOnClickListener(600) {
            @Override
            public void onDebouncedClick(View v) {

                LatLng origin;

                if (orginlat == null && !(originLocation.getText().length() == 0)) {
                    origin = getLocationFromAddress(originLocation.getText().toString());
                } else
                    origin = orginlat;

                if (originLocation.getText().length() == 0) {
                    alertSnackBar(getResources().getString(R.string.enter_pickup_location));
                } else if (destinationLocation.getText().length() == 0) {
                    alertSnackBar(getResources().getString(R.string.enter_destination_location));
                } else {

                    Location start_point = convertLatLngToLocation(origin);
                    Location dest_point = convertLatLngToLocation(destlat);

                    float distance_meter = start_point.distanceTo(dest_point);
                    System.out.print("distance in meter in req " + distance_meter);

                    if (distance_meter <= 0.05) {
                        alertSnackBar(getResources().getString(R.string.minimum_distance_for_ride));
                    } else {
                        if (isNetworkAvailable()) {
                            Drawable drawable = cashButton.getBackground();
                            if (!drawable.getConstantState().equals(getResources().getDrawable(R.drawable.ub__payment_type_cash_no).getConstantState())) {
                                dropLocationLayout.setVisibility(View.VISIBLE);
                                sendRequest();
                            } else {
                                alertSnackBar(getResources().getString(R.string.cash_on_hand_not_available));
                            }
                        } else {
                            alertSnackBar(getResources().getString(R.string.check_network_connection));
                        }
                    }
                }
            }
        });

        FAB = (RelativeLayout) findViewById(R.id.myLocationButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLocation != null) {
                    LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                    System.out.println("INSIDE LOCATION CHANGE" + mCurrentLocation.getLatitude() + mCurrentLocation.getLongitude());

                    float zoomPosition;
                    if(tripStatus){
                        zoomPosition=Constants.MAP_ZOOM_SIZE_ONTRIP;
                    }else {
                        zoomPosition=Constants.MAP_ZOOM_SIZE;
                    }

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)                              // Sets the center of the map to current location
                            .zoom(zoomPosition)
                            .tilt(0)                                     // Sets the tilt of the camera to 0 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });

        getFareEstimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originLocation.getText().length() == 0) {
                    alertSnackBar(getResources().getString(R.string.enter_pickup_location));
                } else if (destinationLocation.getText().length() == 0) {
                    alertSnackBar(getResources().getString(R.string.enter_destination_location));
                } else {
                    if (isNetworkAvailable()) {

                        //Show Alert Dialog
                        if (seekBarValue != null) {
                            Integer a = Integer.parseInt(seekBarValue);
                            if (a == 5) {
                                getFareInfo(0);
                            } else if (a == 35) {
                                getFareInfo(1);
                            } else if (a == 65) {
                                getFareInfo(2);
                            } else if (a == 95) {
                                getFareInfo(3);
                            }

                            System.out.println("origin lat" + orginlat);
                            System.out.println("dest lat" + destlat);

                            if (center != null || destlat != null) {
                                calctotalDistance = CalculationByDistance(center, destlat);
                            }


                            System.out.println("Minimum Fare" + calcBaseFare);
                            System.out.println("Minimum KM" + calcPriceKM);
                            System.out.println("Minimum Price" + calcPriceMin);
                            System.out.println("Pickup Latlng" + pickupLatLng);

                            System.out.println("Pickup calctotalDistance" + calctotalDistance);
                            System.out.println("Pickup strBookfee" + strBookfee);
                            System.out.println("Pickup strAirportfee" + strAirportfee);
                            System.out.println("Pickup strTaxpercentage" + strTaxpercentage);

                            if (calctotalDistance != null && calctotalDistance != 0.0) {
                                String estimate = calculateEstimate(calctotalDistance, calcPriceKM, calcPriceMin, calcBaseFare,strBookfee,strAirportfee,strTaxpercentage);
                                System.out.println("Total Estimate" + estimate);
                                showFareEstimateDialog(originLocation.getText().toString(), destinationLocation.getText().toString(), getCategory(), estimate);
                            }
                        }

                    } else {
                        alertSnackBar(getResources().getString(R.string.check_network_connection));
                    }
                }
            }
        });

        setPickupLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show Request button only if has cars nearby
                if (setPickupLocation.getText().toString().matches("SET PICKUP LOCATION")) {
                    mDrawerLayout.isDrawerOpen(Gravity.LEFT);
                    menuButton.setBackground(getResources().getDrawable(R.drawable.close));
                    dropLocationLayout.setVisibility(View.VISIBLE);
                    requestLayout.setVisibility(View.VISIBLE);
                    categoryLayout.setVisibility(View.GONE);
                }
            }
        });

        ratingSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripHistoryLayout.setVisibility(View.GONE);
                locationLayout.setVisibility(View.VISIBLE);
                requestLayout.setVisibility(View.VISIBLE);
                ratingLayout.setVisibility(View.GONE);
            }
        });

        changePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent payment = new Intent(MapActivity.this, PaymentSelectActivity_.class);
                payment.putExtra("page", "page");
                startActivity(payment);
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callProfile();
            }
        });

        txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callProfile();
            }
        });

        trip_info_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDriverInfoDialog();
            }
        });


         /*Seek Bar*/
        slideUpPanelLayout.showHandle();


        seekBarLayout = (RelativeLayout) findViewById(R.id.seek_bar_layout);

        categorySeekBar.setProgressDrawable(new UberProgressDrawable(categorySeekBar.getProgressDrawable(), categorySeekBar, 4, getResources().getColor(R.color.colorPrimary)));

        suvBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_thumb_suv);

        sedanBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_thumb_luxury);

        hatchBackBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_thumb_standard);

        fourthBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_thumb_taxi);

        suvCar = new BitmapDrawable(getResources(), suvBitmap);
        sedanCar = new BitmapDrawable(getResources(), sedanBitmap);
        hatchBackCar = new BitmapDrawable(getResources(), hatchBackBitmap);
        fourthCar = new BitmapDrawable(getResources(), fourthBitmap);

        if (seekBarValue != null) {
            Integer a = Integer.parseInt(seekBarValue);
            categorySeekBar.incrementProgressBy(a);

            if (a == 5) {
                System.out.println("SeekbarValue==>" + a);
                getFareInfo(0);
                categorySeekBar.setThumb(hatchBackCar);
            }
            if (a == 35) {
                categorySeekBar.setThumb(sedanCar);
                System.out.println("SeekbarValue==>" + a);
                getFareInfo(1);
            }

            if (a == 65) {
                categorySeekBar.setThumb(suvCar);
                getFareInfo(2);
                System.out.println("SeekbarValue==>" + a);
            }
            if (a == 95) {
                categorySeekBar.setThumb(fourthCar);
                getFareInfo(3);
                System.out.println("SeekbarValue==>" + a);
            }

        } else {
            seekBarValue = "5";
            Integer a = Integer.parseInt(seekBarValue);
            categorySeekBar.incrementProgressBy(a);
            categorySeekBar.setThumb(suvCar);
            category = strCategoryName[0];
            setCategory(category);

            if (mMap != null)
                mMap.clear();

            setPickupLocation.setText("NO CARS AVAILABLE");
            setPickupLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            txtETA.setVisibility(View.GONE);
            //Geo Fire
            geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location").child(getCategory()));
            setGeofire(geoFire);
            getFareInfo(0);

            try {
                //Dynamic_Radious
                geoQuery = getGeofire().queryAtLocation(new GeoLocation(center.latitude, center.longitude), nearByDistanceRadius);
                geoQuery.addGeoQueryEventListener(MapActivity.this);
                System.out.println("geo status: geo queery started in oncreate");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        categorySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 20) {
                    progresscount = 5;
                    seekBar.setThumb(hatchBackCar);
                } else if (progress > 25 && progress <= 50) {
                    progresscount = 35;
                    seekBar.setThumb(sedanCar);
                } else if (progress > 50 && progress <= 80) {
                    progresscount = 65;
                    seekBar.setThumb(suvCar);
                } else if (progress > 80) {
                    progresscount = 95;
                    seekBar.setThumb(fourthCar);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (progresscount == 5) {

                    setProgresscount(seekBar, progresscount, 0, hatchBackCar);


                } else if (progresscount == 35) {

                    setProgresscount(seekBar, progresscount, 1, sedanCar);

                } else if (progresscount == 65) {

                    setProgresscount(seekBar, progresscount, 2, suvCar);

                } else if (progresscount == 95) {

                    setProgresscount(seekBar, progresscount, 3, fourthCar);
                }
            }
        });

        if (tripState.matches("requestAccept")) {
            getAcceptState();
        } else if (tripState.matches("arriving")) {
            tripStatus = true;
            getarrivingstate();
        } else if (tripState.matches("ontrip")) {
            tripStatus = true;
            getOnTripState();
        }
      /*  carCategoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fareLayout.getVisibility() == View.VISIBLE) {
                    fareLayout.setVisibility(View.GONE);
                } else {
                    fareLayout.setVisibility(View.VISIBLE);
                }
            }
        });*/

        //Screen Tracker
        sendScreenImageName();
    }

    private void setOfflineCategory() {

        System.out.println("In Offline");
        strCategoryName = new String[]{"Standard", "Luxury", "SUV", "Taxi"};
        strMinFare = new String[]{"10", "7", "7", "7"};
        strMaxPeople = new String[]{"3", "4", "2", "2"};
        strMinutePrice = new String[]{"5", "1", "2", "2"};
        strKmPrice = new String[]{"10", "3", "3", "3"};
        categoryLogo = new String[]{"null", "null", "null", "null"};
        categoryMarker = new String[]{"null", "null", "null", "null"};

        for (int i = 0; i < strCategoryName.length; i++) {
            System.out.println("cccatergoyname=" + strCategoryName[i]);
            if (strCategoryName != null && !strCategoryName.equals("null")) {
                if (i == 0)
                    txtHatchBack.setText(strCategoryName[i]);
                else if (i == 1)
                    txtSedan.setText(strCategoryName[i]);
                else if (i == 2)
                    txtSUV.setText(strCategoryName[i]);
                else if (i == 3)
                    txtfour.setText(strCategoryName[i]);

            }
        }
    }


    public LatLng getlatlng(Set startaddressSet) {
        int i = 0;
        String element = "0.0", element1 = "0.0";

        if (startaddressSet != null) {

            //access via new for-loop
            for (Object object : startaddressSet) {
                System.out.println("i===>" + i);
                if (i == 0)
                    element = (String) object;
                else
                    element1 = (String) object;

                i++;
            }
        }

        return new LatLng(Double.parseDouble(element), Double.parseDouble(element1));
    }

    public void getAcceptState() {
        tripStatus = true;
        trip_info_layout.setVisibility(View.VISIBLE);
        categoryLayout.setVisibility(View.GONE);
        headerButton.setVisibility(View.VISIBLE);
        txtDriverArrivalETALayout.setVisibility(View.VISIBLE);
        showTerminalSelectorLayout();
        headerButton.setText("ACCEPTED");
        setPickupLocation.setVisibility(View.GONE);
        txtETA.setVisibility(View.GONE);
        driverLayout.setVisibility(View.VISIBLE);
        //vehicle_nmuber.setVisibility(View.VISIBLE);
        dropLocationLayout.setVisibility(View.GONE);
        locationLayout.setVisibility(View.GONE);
        trip_info_layout.setVisibility(View.VISIBLE);

        if (geoQuery != null) {
            System.out.println("removing geo query listener");
            geoQuery.removeAllListeners();
            for (Marker marker : markers.values()) {
                marker.remove();
            }
            markers.clear();
        }

        //acceptViews();
        //checktripcancelstatus();
        String startaddress = state.getString("pickupposition", null);
        String endaddress = state.getString("destposition", null);
        String startaddresslat = state.getString("pickupposition_lat", null);
        String startaddresslng = state.getString("pickupposition_lng", null);
        String endaddresslat = state.getString("destposition_lat", null);
        String endaddresslng = state.getString("destposition_lng", null);

        if(startaddresslat!=null&startaddresslng!=null)
            if(!startaddresslat.matches("null")&!startaddresslng.matches("null"))
                pickupLatLng = new LatLng(Double.parseDouble(startaddresslat), Double.parseDouble(startaddresslng));

        if(endaddresslat!=null&endaddresslng!=null)
            if(!endaddresslat.matches("null")&!endaddresslng.matches("null"))
                destinationLatLng = new LatLng(Double.parseDouble(endaddresslat), Double.parseDouble(endaddresslng));


        driverID = state.getString("tripdriverid", null);
        tripID = state.getString("tripID", null);
        getstatusfromfirebase();
        if (driverID != null) {
            displayDetails(driverID);
        }
    }

    public void getOnTripState() {
        tripStatus = true;
        headerButton.setText("ON TRIP");
        txtDriverArrivalETALayout.setVisibility(View.GONE);
        updatedrop_location_layout.setVisibility(View.VISIBLE);
        terminal_layout.setVisibility(View.GONE);
        requestBar.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.GONE);
        headerButton.setVisibility(View.VISIBLE);
        //tollFee.setVisibility(View.VISIBLE);

        setPickupLocation.setVisibility(View.GONE);
        txtETA.setVisibility(View.GONE);
        driverLayout.setVisibility(View.VISIBLE);
        //vehicle_nmuber.setVisibility(View.VISIBLE);
        dropLocationLayout.setVisibility(View.GONE);
        locationLayout.setVisibility(View.GONE);
        driverID = state.getString("tripdriverid", null);
        tripID = state.getString("tripID", null);

        String startaddress = state.getString("pickupposition", null);
        String endaddress = state.getString("destposition", null);

        //LatLng startLatLng = getLocationFromAddress(startaddress);
        //LatLng endLatLng = getLocationFromAddress(endaddress);

        String startaddresslat = state.getString("pickupposition_lat", null);
        String startaddresslng = state.getString("pickupposition_lng", null);
        String endaddresslat = state.getString("destposition_lat", null);
        String endaddresslng = state.getString("destposition_lng", null);

        if(startaddresslat!=null&startaddresslng!=null)
            if(!startaddresslat.matches("null")&!startaddresslng.matches("null"))
                pickupLatLng = new LatLng(Double.parseDouble(startaddresslat), Double.parseDouble(startaddresslng));


        if(endaddresslat!=null&endaddresslng!=null)
            if(!endaddresslat.matches("null")&!endaddresslng.matches("null"))
                destinationLatLng = new LatLng(Double.parseDouble(endaddresslat), Double.parseDouble(endaddresslng));


        if(destinationLatLng!=null)
        {
            String dropAddress=getCompleteAddressString(destinationLatLng.latitude,destinationLatLng.longitude);
            update_drop_location.setText(dropAddress);
            diaogDestinationLocation.setText(dropAddress);
        }
        if (driverID != null) {
            displayDetails(driverID);
        }
        requestID = state.getString("tripreqid", null);
        getstatusfromfirebase();
    }

    public void getarrivingstate() {
        tripStatus = true;
        headerButton.setText("ARRIVING");
        txtDriverArrivalETALayout.setVisibility(View.VISIBLE);
        showTerminalSelectorLayout();
        requestBar.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.GONE);
        headerButton.setVisibility(View.VISIBLE);
        trip_info_layout.setVisibility(View.VISIBLE);
        setPickupLocation.setVisibility(View.GONE);
        txtETA.setVisibility(View.GONE);
        driverLayout.setVisibility(View.VISIBLE);
        //vehicle_nmuber.setVisibility(View.VISIBLE);
        dropLocationLayout.setVisibility(View.GONE);
        locationLayout.setVisibility(View.GONE);
        driverID = state.getString("tripdriverid", null);
        tripID = state.getString("tripID", null);
        String startaddress = state.getString("pickupposition", null);
        String endaddress = state.getString("destposition", null);
        //LatLng startLatLng = getLocationFromAddress(startaddress);
        //LatLng endLatLng = getLocationFromAddress(endaddress);

        String startaddresslat = state.getString("pickupposition_lat", null);
        String startaddresslng = state.getString("pickupposition_lng", null);
        String endaddresslat = state.getString("destposition_lat", null);
        String endaddresslng = state.getString("destposition_lng", null);

        if(startaddresslat!=null&startaddresslng!=null)
            if(!startaddresslat.matches("null")&!startaddresslng.matches("null"))
                pickupLatLng = new LatLng(Double.parseDouble(startaddresslat), Double.parseDouble(startaddresslng));

        if(endaddresslat!=null&endaddresslng!=null)
            if(!endaddresslat.matches("null")&!endaddresslng.matches("null"))
                destinationLatLng = new LatLng(Double.parseDouble(endaddresslat), Double.parseDouble(endaddresslng));

        getstatusfromfirebase();

        if (driverID != null) {
            displayDetails(driverID);
        }
    }


    private void getCategoryInfo() {
        final String url = Constants.CATEGORY_LIVE_URL + "Settings/getCategory";
        System.out.println("GetCategoryURL==>" + url);
        final JsonArrayRequest infoReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                categoryLength = response.length();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        System.out.println("Response from GetCategory==>" + jsonObject);
                        strCategoryName[i] = jsonObject.optString("categoryname");
                        strMinFare[i] = jsonObject.optString("price_fare");
                        strMaxPeople[i] = jsonObject.optString("max_size");
                        strMinutePrice[i] = jsonObject.optString("price_minute");
                        strKmPrice[i] = jsonObject.optString("price_km");
                        categoryLogo[i] = jsonObject.optString("Logo");
                        categoryMarker[i] = jsonObject.optString("Marker");
                        bookfee[i] = jsonObject.optString("book_fee");
                        airportfee[i]= jsonObject.optString("airport_surge");
                        taxpercentage[i]=jsonObject.optString("tax_percentage");

                        System.out.println("cccatergoyname=" + strCategoryName[i]);
                        if (strCategoryName != null && !strCategoryName.equals("null")) {
                            if (i == 0)
                                txtHatchBack.setText(strCategoryName[i]);
                            else if (i == 1)
                                txtSedan.setText(strCategoryName[i]);
                            else if (i == 2)
                                txtSUV.setText(strCategoryName[i]);
                            else if (i == 3)
                                txtfour.setText(strCategoryName[i]);

                        }
                    } catch (JSONException | NullPointerException | ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
                if (volleyError instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        infoReq.setRetryPolicy(new DefaultRetryPolicy(5000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(infoReq);
    }



    public void getFareInfo(int i) {

        if (strMinFare[i] != null && !strMinFare[i].equals("null"))
            calcBaseFare = strMinFare[i];

        if (strMinutePrice[i] != null && !strMinutePrice[i].equals("null"))
            calcPriceMin = strMinutePrice[i];

        if (strKmPrice[i] != null && !strKmPrice[i].equals("null"))
            calcPriceKM = strKmPrice[i];

        if (bookfee[i] != null && !bookfee[i].equals("null"))
            strBookfee= bookfee[i];

        if (airportfee[i] != null && !airportfee[i].equals("null"))
            strAirportfee= airportfee[i];

        if (taxpercentage[i] != null && !taxpercentage[i].equals("null"))
            strTaxpercentage = taxpercentage[i];
    }


    private void showDriverInfoDialog() {
        dialog = new Dialog(MapActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.call_driver_dialog);
        dialog.getWindow().setWindowAnimations(R.style.DialogTopAnimation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageButton back = (ImageButton) dialog.findViewById(R.id.backButton);
        LinearLayout call = (LinearLayout) dialog.findViewById(R.id.calllayout);
        LinearLayout msg = (LinearLayout) dialog.findViewById(R.id.msglayout);
        TextView txtDriverName = (TextView) dialog.findViewById(R.id.driver_text);
        TextView txtCancelTrip = (TextView) dialog.findViewById(R.id.txtCancelTrip);
        TextView txtDriverCategory = (TextView) dialog.findViewById(R.id.car_type);
        final ImageView driverimage = (ImageView) dialog.findViewById(R.id.driver_image);
        LinearLayout cancellay = (LinearLayout) dialog.findViewById(R.id.cancellay);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);

        if (driverFirstname != null && driverLastName != null) {
            txtDriverName.setText(driverFirstname + " " + driverLastName);
        }

        if (tripDriverCategory != null) {
            txtDriverCategory.setText(tripDriverCategory);
        }

        if(headerButton.getText().toString().equals("ON TRIP")){
            cancellay.setVisibility(View.GONE);
        }


        txtCancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v7.app.AlertDialog.Builder builder =
                        new android.support.v7.app.AlertDialog.Builder(MapActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getString(R.string.cancel_trip));
                builder.setMessage(getString(R.string.cancel_dis));
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        System.out.println("TripID in Cancel" + tripID);
                        System.out.println("driverID in Cancel" + tripID);
                        if (driverID != null && tripID != null) {
                            getState.putString("tripstate", "endclicked");
                            getState.apply();
                            strCacnelStatus = "ridercliked";
                            //DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID).child("accept");
                            DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
                            Map<String, Object> taskMap2 = new HashMap<String, Object>();
                            taskMap2.put("status", "5");
                            //databaseReference2.updateChildren(taskMap2);
                            databaseReference3.updateChildren(taskMap2);


                            if (driverID != null) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID);
                                Map<String, Object> taskMap = new HashMap<String, Object>();
                                taskMap.put("cancel_trip_id", tripID + ":" + riderfirstname);
                                databaseReference.updateChildren(taskMap);

                            }

                            updatecancelTrip();
                            dialog1.dismiss();

                            dialog.cancel();
                            driverID = null;//clear existing driver


                        }

                    }

                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                    //  alertdialog2.cancel();

                });

                builder.show();

            }
        });

        if (driverProfileImage != null) {
            Glide.with(getApplicationContext()).load(driverProfileImage).asBitmap().centerCrop().placeholder(R.drawable.account_circle_grey).skipMemoryCache(true).into(new BitmapImageViewTarget(driverimage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    driverimage.setImageDrawable(circularBitmapDrawable);
                }
            });

        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Driver mobile number" + driverMobileNumber);
                /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse(driverMobileNumber));
                try {
                    startActivity(sendIntent);
                } catch (ActivityNotFoundException e) {
                    // Display some sort of error message here.
                }*/
                if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_SMS") == PackageManager.PERMISSION_GRANTED) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", driverMobileNumber, null)));
                    } catch (ActivityNotFoundException e) {
                        // Display some sort of error message here.
                    }
                }




             /*   if (ridermobile != null && !ridermobile.isEmpty()) {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:9597848909"));
                    startActivity(sendIntent);
                } else {
                    Toast.makeText(Map_Activity.this, "Number not register", Toast.LENGTH_SHORT).show();
                }*/

            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (driverMobileNumber != null && !driverMobileNumber.isEmpty()) {


                    TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    int simState = telMgr.getSimState();
                    switch (simState) {
                        case TelephonyManager.SIM_STATE_ABSENT:
                            // do something
                            Toast.makeText(MapActivity.this, "No SIM Card Inserted", Toast.LENGTH_SHORT).show();

                            break;
                        case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                            // do something
                            break;
                        case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                            // do something
                            break;
                        case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                            // do something
                            break;
                        case TelephonyManager.SIM_STATE_READY:
                            // do something

                            if (ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }

                            try {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + driverMobileNumber));
                                startActivity(intent);
                            } catch (android.content.ActivityNotFoundException ex) {

                                Toast.makeText(getApplicationContext(), "Number is not founded", Toast.LENGTH_SHORT).show();
                            }


                            break;
                        case TelephonyManager.SIM_STATE_UNKNOWN:
                            // do something
                            break;
                    }


                } else {

                    Toast.makeText(MapActivity.this, "No mobile number", Toast.LENGTH_SHORT).show();
                }


            }
        });

        dialog.show();


    }

    private void cancelTrip() {
        tripStatus = false;

        if (mPolyline != null) {
            mPolyline.remove();
        }
        locationLayout.setVisibility(View.VISIBLE);
        categoryLayout.setVisibility(View.VISIBLE);
        requestLayout.setVisibility(View.GONE);
        setPickupLocation.setVisibility(View.VISIBLE);
        txtETA.setVisibility(View.VISIBLE);
        driverLayout.setVisibility(View.GONE);
        //vehicle_nmuber.setVisibility(View.GONE);

    }


    private void sendRequest() {
        setCancelStatus("0");
        if (pickupCountryCode == null && dropCountryCode == null) {
            pickupCountryCode = getResources().getConfiguration().locale.getCountry();
            dropCountryCode = getResources().getConfiguration().locale.getCountry();
        }

        System.out.println("Pickup country code==>" + pickupCountryCode);
        System.out.println("Drop country code==>" + dropCountryCode);

        if (pickupCountryCode != null && dropCountryCode != null) {
            if (pickupCountryCode.matches(dropCountryCode)) {
                menuButton.setBackground(getResources().getDrawable(R.drawable.menu));
                requestBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                requestBar.setVisibility(View.VISIBLE);

                progressBar.show();
                progressBar.setCancelable(false);
                progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progressBar.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                headerButton.setVisibility(View.VISIBLE);
                headerButton.setText("Requesting...");
                ImageButton cancelRequest = (ImageButton) progressBar.findViewById(R.id.cancel_request);
                requestLayout.setVisibility(View.GONE);
                categoryLayout.setVisibility(View.GONE);
                setPickupLocation.setVisibility(View.GONE);
                txtETA.setVisibility(View.GONE);
                cancelRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        easyTimer.stop();
                        if (requestID != null)
                            cancelRequest(requestID);

                    }
                });
                setRequestID(originLocation.getText().toString(), destinationLocation.getText().toString(), getCategory());
            } else {
                alertSnackBar(getResources().getString(R.string.service_not_available_for_the_destination));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                getRiderDetails();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        mMap.setLocationSource(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        //mMap.setMyLocationEnabled(true);
        //mMap.setOnMyLocationButtonClickListener(this);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setOnCameraChangeListener(this);


        mGoogleApiClient.connect();

        //Ripple hided
        mapRipple = new MapRipple(mMap, new LatLng(0.0,0.0), this);
        mapRipple.startRippleMapAnimation();





        /*//Adding style to the Map
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }*/

    }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
        System.out.println("location changed" + location);


        if (mMapLocationListener != null) {
            mMapLocationListener.onLocationChanged(location);
        }

        System.out.println(location.getProvider() + "," + location.getLatitude() + "," + location.getLongitude());
        mCurrentLocation = location;
        //Current lat and long of Rider
        destLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        //Ripple hided
        startRippleAnimation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        final Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null && count == 0) {
            System.out.println("The Last Known Location " + mLastLocation);
            filterLocation = mLastLocation;
            count = count + 1;

            float zoomPosition=Constants.MAP_ZOOM_SIZE_ONTRIP;

            if (!tripStatus) {

                zoomPosition=Constants.MAP_ZOOM_SIZE;

                getCountryName(this, mLastLocation.getLatitude(), mLastLocation.getLongitude(), "ORIGIN");
                getCompleteAddressString(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                setCategory(strCategoryName[0]);
                //Geo Fire

                geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location").child(getCategory()));
                setGeofire(geoFire);
                geoQuery = getGeofire().queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), nearByDistanceRadius);
                if (this.geoQuery != null) {
                    this.geoQuery.setCenter(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    // radius in km Dynamic_Radious
                    this.geoQuery.setRadius(nearByDistanceRadius);
                    this.geoQuery.addGeoQueryEventListener(MapActivity.this);
                    System.out.println("geo status: geo queery started in on connected");
                } else {
                    Toast.makeText(MapActivity.this, "Geoquery Null", Toast.LENGTH_SHORT).show();
                }
            }

            //place marker at current position for the first time
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()),
                            zoomPosition));

        }

        getFuesedLocation();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // Do nothing
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Do nothing
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mMapLocationListener = onLocationChangedListener;
    }


    @Override
    public void deactivate() {
        mMapLocationListener = null;
    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        //getlocation();
        try {
            List<Address> addressList = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addressList != null) {
                Address returnedAddress = addressList.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current address", "" + strReturnedAddress.toString());
                originLocation.setText(strReturnedAddress.toString());
                getCountryName(this, LATITUDE, LONGITUDE, "ORIGIN");
            } else {
                Log.w("My Current address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current address", "Cannnot get Address!");
        }
        return strAdd;
    }

    //get distance between two location
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        try {
            int Radius = 6371;// radius of earth in Km
            double lat1 = StartP.latitude;
            double lat2 = EndP.latitude;
            double lon1 = StartP.longitude;
            double lon2 = EndP.longitude;
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                    * Math.sin(dLon / 2);
            double c = 2 * Math.asin(Math.sqrt(a));
            double valueResult = Radius * c;
            double km = valueResult / 1;
            DecimalFormat newFormat = new DecimalFormat("####");
            int kmInDec = Integer.valueOf(newFormat.format(km));
            double meter = valueResult % 1000;
            int meterInDec = Integer.valueOf(newFormat.format(meter));
            Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec);

            return Radius * c;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void openAutocompleteActivity(int REQUEST_CODE_AUTOCOMPLETE) {
        try {

            //Get country alpha2 code
            String locale = this.getResources().getConfiguration().locale.getCountry();
            System.out.println("Current Country==>" + locale);

            //Location Filter based on the Country
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_LOCALITY)
                    .build();

            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.

            if (filterLocation != null) {
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .setFilter(autocompleteFilter)
                        .setBoundsBias(new LatLngBounds(new LatLng(filterLocation.getLatitude(), filterLocation.getLongitude()), new LatLng(filterLocation.getLatitude(), filterLocation.getLongitude())))
                        .build(this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            } else {
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .setFilter(autocompleteFilter)
                        .build(this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }


        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }


    public LatLng getLocationFromAddress1(String strAddress, String source) {
        System.out.println("Address" + strAddress);

        Geocoder coder = new Geocoder(MapActivity.this, Locale.ENGLISH);
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
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(originLAT,
                                    originLNG),
                            Constants.MAP_ZOOM_SIZE));
                }


            } else {

                destLAT = location.getLatitude();
                destLNG = location.getLongitude();
            }

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {
            if (source.matches("ORIGIN")) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(newOriginLat,
                                newOriginLng),
                        Constants.MAP_ZOOM_SIZE));
            }
            ex.printStackTrace();
        }

        return p1;
    }


    public void setAddress(Intent data, int resultCode, String source) {

        if (resultCode == RESULT_OK) {
            // Get the user's selected place from the Intent.
            Place place = PlaceAutocomplete.getPlace(this, data);
            Log.i(TAG, "Place Selected: " + place.getName());
            Log.i(TAG, "Latitude Selected: " + place.getLatLng());

            if (source.matches("ORIGIN")) {
                exceptionLatLng = place.getLatLng();
                newOriginLat = exceptionLatLng.latitude;
                newOriginLng = exceptionLatLng.longitude;
                getCountryName(this, newOriginLat, newOriginLng, source);
                orginlat = new LatLng(newOriginLat, newOriginLng);
                //new getPlaceType(String.valueOf(newOriginLat)+","+String.valueOf(newOriginLng)).execute();

            }else if(source.matches("UPDATE")){
                exceptionLatLng = place.getLatLng();
                newDestLat = exceptionLatLng.latitude;
                newDestLng = exceptionLatLng.longitude;
                getCountryName(this, newDestLat, newDestLng, source);
                destlat = new LatLng(newDestLat, newDestLng);
            }
            else if(source.matches("MULTI")) {
                exceptionLatLng = place.getLatLng();
                newMultiLat = exceptionLatLng.latitude;
                newMultiLng = exceptionLatLng.longitude;
                getCountryName(this, newMultiLat, newMultiLng, source);
                multiLatLng = new LatLng(newMultiLat, newMultiLng);
                mulitiDestLatLng[multiTvID]=multiLatLng;
            }
            else {
                exceptionLatLng = place.getLatLng();
                newDestLat = exceptionLatLng.latitude;
                newDestLng = exceptionLatLng.longitude;
                getCountryName(this, newDestLat, newDestLng, source);
                destlat = new LatLng(newDestLat, newDestLng);
            }

            if (source.matches("ORIGIN")) {
                // Format the place's details and display them in the TextView.
                originLocation.setText(formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri()));
                originLocation.setText(place.getAddress());



            }else if(source.matches("UPDATE")){
                update_drop_location.setText(formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri()));
                update_drop_location.setText(place.getAddress());

                diaogDestinationLocation.setText(formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri()));
                diaogDestinationLocation.setText(place.getAddress());
            }
            else if(source.matches("MULTI")){

                mulitipleDestinationTextView[multiTvID].setText(formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri()));
                mulitipleDestinationTextView[multiTvID].setText(place.getAddress());

            }
            else {
                // Format the place's details and display them in the TextView.

                Spanned fromPlaceDetails =formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri());
                    destinationLocation.setText(fromPlaceDetails);
                    destinationLocation.setText(place.getAddress());

                update_drop_location.setText(fromPlaceDetails);
                update_drop_location.setText(place.getAddress());

                diaogDestinationLocation.setText(fromPlaceDetails);
                diaogDestinationLocation.setText(place.getAddress());
            }

            System.out.println("address 1==>" + place.getName());
            System.out.println("address 2==>" + place.getAddress());
            System.out.println("address 3==>" + place.getViewport());
            System.out.println("address 4==>" + place.getPlaceTypes());
            System.out.println("address 5==>" + place.getId());
            System.out.println("address 6==>" + place.getAttributions());

            // Display attributions if required.
            CharSequence attributions = place.getAttributions();
            if (!TextUtils.isEmpty(attributions)) {

                if (source.matches("ORIGIN")) {
                    originLocation.setText(Html.fromHtml(attributions.toString()));

                }
                else if(source.matches("UPDATE")){
                    update_drop_location.setText(Html.fromHtml(attributions.toString()));
                    diaogDestinationLocation.setText(Html.fromHtml(attributions.toString()));
                }
                else if(source.matches("MULTI")){

                    mulitipleDestinationTextView[multiTvID].setText(Html.fromHtml(attributions.toString()));
                    mulitiDestaddress[multiTvID]=String.valueOf(Html.fromHtml(attributions.toString()));

                }
                else {

                     destinationLocation.setText(Html.fromHtml(attributions.toString()));
                     update_drop_location.setText(Html.fromHtml(attributions.toString()));
                     diaogDestinationLocation.setText(Html.fromHtml(attributions.toString()));

                }

            } else {

                if (source.matches("ORIGIN")) {
                    originLocation.setText(place.getAddress());

                }
                else if(source.equals("UPDATE")){
                    update_drop_location.setText(place.getAddress());
                    diaogDestinationLocation.setText(place.getAddress());
                }
                else if(source.matches("MULTI")){

                    mulitipleDestinationTextView[multiTvID].setText(place.getAddress());
                    mulitiDestaddress[multiTvID]=String.valueOf(place.getAddress());
                }
                else {

                     destinationLocation.setText(place.getAddress());
                     update_drop_location.setText(place.getAddress());
                     diaogDestinationLocation.setText(place.getAddress());
                }
            }

            if (pickupCountryCode == null | dropCountryCode == null) {
                pickupCountryCode = getResources().getConfiguration().locale.getCountry();
                dropCountryCode = getResources().getConfiguration().locale.getCountry();
            }

            String tempadd = String.valueOf(place.getAddress());
            System.out.println("address after parshe==>" + place.getAddress());

            //Get LAT and LNG
            getLocationFromAddress1(tempadd, source);

        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            Log.e(TAG, "Error: Status = " + status.toString());
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
            System.out.println("Canceled by user");
        }
    }

    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        switch (position) {
            case 0:

                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent trips = new Intent(this, TripsTabActivity.class);
                startActivity(trips);
                break;

            case 1:

                callProfile();
                break;

            case 2:

                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent wallet = new Intent(this, WalletActivity.class);
                startActivity(wallet);
                break;

            case 3:

                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent sos = new Intent(this, SoSActivity_.class);
                startActivity(sos);
                break;

            case 4:

                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent ridelater = new Intent(this, RideLater.class);
                startActivity(ridelater);
                break;

            case 5:

                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent bankDetails = new Intent(this, BankDetails_.class);
                startActivity(bankDetails);
                break;

            case 6:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                //to avoid bad token exception
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            showfeedbackDialog();
                        }
                    }
                });
                break;

            case 7:

                android.app.AlertDialog.Builder builder =
                        new AlertDialog.Builder(MapActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(R.string.log_out);
                builder.setMessage(R.string.log_out_msg);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearpreferences();
                        Intent logout = new Intent(MapActivity.this, LaunchActivity_.class);
                        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(logout);
                        finish();
                    }

                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });

                builder.show();
                break;

            default:
                break;

        }
    }

    //set Request Initialization
    private void setRequestID(String startAddress, String endAddress, String category) {

        try {

            category=category.replaceAll(" ","%20");
            //LatLng startLatLng = getLocationFromAddress(startAddress);
            LatLng startLatLng = new LatLng(center.latitude, center.longitude);
            LatLng endLatLng = getLocationFromAddress(endAddress);
            System.out.println("startlatlng" + startLatLng);
            System.out.println("endlatlng" + endLatLng);
            System.out.println("newendlatlng" + newDestLat);
            System.out.println("Category" + category);

            if (startLatLng == null || startLatLng.equals("null")) {
                startLatLng = destLatLng; //StartLatLng is null so replacing with current location
            }
            if (endLatLng == null || endLatLng.equals("null")) {
                endLatLng = new LatLng(newDestLat, newDestLng);
            }
            if (startLatLng == endLatLng) {
                Toast.makeText(MapActivity.this, "Pickup and Drop Locations cannot be same", Toast.LENGTH_SHORT).show();
            }
            if (startLatLng != null & endLatLng != null) {
                //Check the Drawable state before Payment
                Drawable drawable = cashButton.getBackground();
                if (drawable.getConstantState().equals(getResources().getDrawable(R.drawable.ub__payment_type_cash).getConstantState())) {
                    paymentType = Constants.PAYMENT_TYPE_CASH;
                } else if (drawable.getConstantState().equals(getResources().getDrawable(R.mipmap.ub__payment_type_delegate).getConstantState())) {
                    paymentType = Constants.PAYMENT_TYPE_CARD;
                } else {
                    paymentType = Constants.PAYMENT_TYPE_CORP_ID;
                }

                //Trip Start and End Latitude
                pickupLatLng = startLatLng;
                destinationLatLng = endLatLng;
                getState.putString("pickupposition", startAddress);
                getState.putString("destposition", endAddress);
                getState.putString("pickupposition_lat", String.valueOf(startLatLng.latitude));
                getState.putString("pickupposition_lng", String.valueOf(startLatLng.longitude));
                getState.putString("destposition_lat", String.valueOf(endLatLng.latitude));
                getState.putString("destposition_lng", String.valueOf(endLatLng.longitude));

                //Trip Start and End Address
                try {
                    startAddress = startAddress.replaceAll("/", "");
                    startAddress = URLEncoder.encode(startAddress, "UTF-8");
                    endAddress = endAddress.replaceAll("/", "");
                    endAddress = URLEncoder.encode(endAddress, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (check.isChecked()) {
                    maxShare = sharecount.getSelectedItem().toString();
                } else {
                    maxShare = "0";
                }

                if (maxShare.matches("0")) {
                    rideType = "none";
                    maxShare = "0";
                } else {
                    rideType = "shared";
                    maxShare = sharecount.getSelectedItem().toString();
                }
                new getPlaceType(String.valueOf(startLatLng.latitude)+","+String.valueOf(startLatLng.longitude)).execute();
                System.out.println("Ride Type" + rideType);
                System.out.println("Max Share" + maxShare);

                final String url = Constants.REQUEST_URL + "setRequest/userid/" + userID + "/start_lat/" + startLatLng.latitude + "/start_long/" + startLatLng.longitude + "/end_lat/" + endLatLng.latitude + "/end_long/" + endLatLng.longitude + "/payment_mode/" + paymentType + "/pickup_address/" + startAddress + "/drop_address/" + endAddress + "/category/" + category + "/ride_type/" + rideType + "/max_share/" + maxShare;
                System.out.println("get Request ID==>" + url);
                final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                System.out.println("response" + jsonObject);

                                requestStatus = jsonObject.optString("request_status");
                                requestID = jsonObject.optString("request_id");
                                System.out.println("requestStatus" + requestStatus);
                                System.out.println("requestID" + requestID);

                                //Save req id
                                getState.putString("tripreqid", requestID);
                                getState.apply();

                                processRequest(requestID);
                                //Set a runnable task every 3 seconds
                                AsynchTaskTimer(requestID, "normal_request");

                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Volley Error in Set request" + volleyError);
                        if (volleyError instanceof NoConnectionError) {

                            Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                signUpReq.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().addToRequestQueue(signUpReq);
            } else {
                //Issue with Geo Coder
                System.out.println("Geocoder");
                dismissViews();
            }
        } catch (NullPointerException e) {
            dismissViews();
            Toast.makeText(this, "Please make sure to set your Pickup and Drop Location more accurate", Toast.LENGTH_SHORT).show();
            System.out.println("Exception in Set Request" + e);
        }
    }

    //Sending requests to Available drivers
    private void processRequest(String requestID) {

        String estimate="0";
        if (orginlat != null || destlat != null){
            System.out.println("The pick up and drop "+destlat);
            calctotalDistance = CalculationByDistance(center, destlat);
        }

        System.out.println("Minimum Fare" + calcBaseFare);
        System.out.println("Minimum KM" + calcPriceKM);
        System.out.println("Minimum Price" + calcPriceMin);
        System.out.println("Pickup calctotalDistance" + calctotalDistance);
        System.out.println("Pickup strBookfee" + strBookfee);
        System.out.println("Pickup strAirportfee" + strAirportfee);
        System.out.println("Pickup strTaxpercentage" + strTaxpercentage);

        if (calctotalDistance != null && calctotalDistance != 0.0)
                 estimate = calculateEstimate(calctotalDistance, calcPriceKM, calcPriceMin, calcBaseFare,strBookfee,strAirportfee,strTaxpercentage);

        if(estimate.isEmpty() || estimate.equals(""))
            estimate="0";


        final String url = Constants.REQUEST_URL + "processRequest/request_id/" + requestID+"/est_fare/"+estimate;


        System.out.println("process Request ID==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        System.out.println("response from requestID in ProcessRequest" + jsonObject);

                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("Volley Error in Process request" + volleyError);
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    //get Request status
    private void getRequestID(final String requestID, final String request_type) {
        //Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REQUEST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitArrayAPI service = retrofit.create(RetrofitArrayAPI.class);
        Call<List<RequestInfo>> call = service.repoContributors(requestID);
        call.enqueue(new Callback<List<RequestInfo>>() {
            @Override
            public void onResponse(Call<List<RequestInfo>> call, retrofit2.Response<List<RequestInfo>> response) {
                try {
                    List<RequestInfo> RequestData = response.body();
                    System.out.println("Response Size" + RequestData.size());
                    for (int i = 0; i < RequestData.size(); i++) {
                        String responseStatus = RequestData.get(i).getRequest_status();
                        System.out.println("responseStatus" + responseStatus);
                        driverID = RequestData.get(i).getDriver_id();
                        tripID = RequestData.get(i).getTrip_id();
                        String pickupposition_lat = RequestData.get(i).getPickup().getLat();
                        String pickupposition_lng = RequestData.get(i).getPickup().getLong();
                        String destposition_lat = RequestData.get(i).getDestination().getLat();
                        String destposition_lng = RequestData.get(i).getDestination().getLong();

                        try {
                            if (responseStatus != null) {
                                if (responseStatus.matches("accept") && !tripID.isEmpty()) {

                                    tripStatus = true;
                                    //Notification to show Driver has accepted the trip
                                    generateNotification(getApplicationContext(), "Driver Accepted your Request");

                                    getState.putString("tripstate", "requestAccept");
                                    getState.putBoolean("tripbool", true);
                                    getState.apply();

                                    System.out.println("Driver ID" + driverID);
                                    System.out.println("Driver ID tripID" + tripID);

                                    getState.putString("tripdriverid", driverID);
                                    getState.putString("tripID", tripID);
                                    getState.apply();


                                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID).child("accept");
                                    Map<String, Object> taskMap1 = new HashMap<String, Object>();
                                    taskMap1.put("status", "0");
                                    taskMap1.put("trip_id", "0");
                                    taskMap1.put("trip_id_rider_name", "0");
                                    databaseReference1.updateChildren(taskMap1);

                                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID).child("request");
                                    Map<String, Object> taskMap2 = new HashMap<>();
                                    taskMap2.put("status", "0");
                                    databaseReference2.updateChildren(taskMap2);

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("riders_location").child(userID);
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("pickup_terminal","None");
                                    Map<String, Object> updateaccept= new HashMap<>();
                                    updateaccept.put("0","0");
                                    updateaccept.put("1","0");
                                    updates.put("Updatelocation",updateaccept);
                                    ref.updateChildren(updates);

                                    if (driverID != null) {
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID);
                                        Map<String, Object> taskMap = new HashMap<>();
                                        taskMap.put("cancel_trip_id", "0");
                                        databaseReference.updateChildren(taskMap);

                                    }

                                    getState.putString("tripdriverid", driverID);
                                    getState.apply();

                                    if (geoQuery != null) {
                                        System.out.println("removing geo query listener");
                                        geoQuery.removeAllListeners();
                                        for (Marker marker : markers.values()) {
                                            marker.remove();
                                        }
                                        markers.clear();
                                    }
                                    if (request_type != null) {
                                        System.out.println("ride later request accept");
                                        if (request_type.equals("ride_later_request")) {

                                            getState.putString("pickupposition_lat", pickupposition_lat);
                                            getState.putString("pickupposition_lng", pickupposition_lng);
                                            getState.putString("destposition_lat", destposition_lat);
                                            getState.putString("destposition_lng", destposition_lng);
                                            getState.apply();

                                            System.out.println("inside of ride later request" + userID + " " + requestID);
                                            changeFBRideLaterStatus(requestID);
                                            requesttype = request_type;

                                        }
                                    }
                                    acceptViews();
                                    //checktripcancelstatus();
                                    getstatusfromfirebase();
                                    if (!driverID.isEmpty()) {
                                        displayDetails(driverID);
                                    }
                                    trip_info_layout.setVisibility(View.VISIBLE);

                                } else if (responseStatus.matches("no_driver") || responseStatus.matches("no_driver_d")) {
                                    //No Drivers Available
                                    easyTimer.stop();
                                    dismissViews();
                                    noDriversAlert("No Driver", "It seems Drivers are not available to pick up. Try again later");
                                } else {
                                    System.out.println("Running");
                                }
                            }

                        } catch (Exception e) {
                            dismissViews();
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<RequestInfo>> call, Throwable t) {
                Log.d("onResponse", "There is an error ONFAILURE");
            }
        });
    }


    //get Request status
    private void getTripID(String requestID) {

        final String url = Constants.REQUEST_URL + "getRequest/request_id/" + requestID;
        System.out.println("getTripID==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        System.out.println("response for Trip ID" + jsonObject);
                        tripID = jsonObject.optString("trip_id");
                        System.out.println("tripID" + tripID);


                    } catch (JSONException | NullPointerException e) {
                        dismissViews();
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    //Get Trip status from Firebase
    private void getstatusfromfirebase() {

        System.out.println("driver id====>" + driverID);
        System.out.println("trip id====>" + tripID);

        if (tripID != null) {
            System.out.println("trip id====>" + tripID);
            //final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID).child("accept").child("status");
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID).child("status");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        System.out.println("Status from Firebase====>" + status);
                        if (status != null) {
                            if (status.matches("2")) {
                                headerButton.setText("ARRIVING");
                                updatedrop(userID,"0","0");
                                txtDriverArrivalETALayout.setVisibility(View.VISIBLE);
                                showTerminalSelectorLayout();
                                getState.putString("tripstate", "arriving");
                                getState.putString("tripdriverid", driverID);
                                getState.putString("tripID", tripID);
                                getState.apply();
                                trip_info_layout.setVisibility(View.VISIBLE);
                                //Notification to show Driver has arriving
                                generateNotification(getApplicationContext(), "Driver is Arriving Now");

                            } else if (status.matches("3")) {
                                headerButton.setText("ON TRIP");
                                txtDriverArrivalETALayout.setVisibility(View.GONE);
                                terminal_layout.setVisibility(View.GONE);
                                updatedrop_location_layout.setVisibility(View.VISIBLE);
                                //trip_info_layout.setVisibility(View.GONE);
                                tollFee.setVisibility(View.VISIBLE);
                                getState.putString("tripstate", "ontrip");
                                getState.putString("tripdriverid", driverID);
                                getState.putString("tripID", tripID);
                                getState.apply();
                                //check toll fee from fire base
                                checkToll();
                                //Notification to show the trip has started
                                generateNotification(getApplicationContext(), "Your Trip has started");
                             /*   if(destLatLng==null) {
                                    destLatLng= new LatLng(filterLocation.getLatitude(),filterLocation.getLongitude());
                                }*/
                                String endaddresslat = state.getString("destposition_lat", null);
                                String endaddresslng = state.getString("destposition_lng", null);
                                destLatLng= new LatLng(Double.parseDouble(endaddresslat), Double.parseDouble(endaddresslng));
                                destinationLatLng = new LatLng(Double.parseDouble(endaddresslat), Double.parseDouble(endaddresslng));

                                if (startLatLng != null && destLatLng != null) {
                                    try {
                                        GoogleDirection.withServerKey(google_api_key)
                                                .from(startLatLng)
                                                .to(destinationLatLng)
                                                .transportMode(TransportMode.DRIVING)
                                                .execute(MapActivity.this);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                if (requestID != null) {
                                    getTripID(requestID);
                                } else {
                                    String getreqid = state.getString("tripreqid", null);
                                    getTripID(getreqid);
                                }
                            } else if (status.matches("4")) {
                                getState.remove("multidestcount").apply();

                                getState.putBoolean("getPlaceType", false);
                                getState.putBoolean("getPlaceName", false);

                                updatedrop_location_layout.setVisibility(View.GONE);
                                databaseReference.removeEventListener(this);
                                updatedrop(userID,"0","0");

                                if (tollReference != null) {
                                    tollReference.removeEventListener(tollListener);
                                }
                                tripStatus = false;
                                tollFee.setVisibility(View.GONE);
                                getState.putString("tripstate", "endclicked");
                                getState.putBoolean("tripbool", false);
                                getState.apply();

                                headerButton.setText("TRIP END");
                                if (mPolyline != null) {
                                    mPolyline.remove();
                                }
                                mMap.clear();
                                driverID = null;//clear the existing Driver
                                //Notification to show the trip has ended
                                generateNotification(getApplicationContext(), "Your Trip has ended.Thanks for riding with us");
                                driverLayout.setVisibility(View.GONE);
                                //vehicle_nmuber.setVisibility(View.GONE);
                                setPickupLocation.setVisibility(View.VISIBLE);
                                txtETA.setVisibility(View.VISIBLE);
                                headerButton.setVisibility(View.GONE);
                                //Trip Summary Dialog
                                final Dialog dialogTripSummary = new Dialog(MapActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
                                dialogTripSummary.setContentView(R.layout.activity_trip_summary);
                                dialogTripSummary.setCancelable(false);
                                Button close = (Button) dialogTripSummary.findViewById(R.id.footer_button);
                                final ImageView driverImage = (ImageView) dialogTripSummary.findViewById(R.id.trip_end_profile);
                                final ImageView riderEmoji = (ImageView) dialogTripSummary.findViewById(R.id.rider_emoji);
                                TextView driverName = (TextView) dialogTripSummary.findViewById(R.id.trip_driver_name);
                                TextView tripDate = (TextView) dialogTripSummary.findViewById(R.id.trip_date);
                                final TextView driverTripAmount = (TextView) dialogTripSummary.findViewById(R.id.trip_amount);
                                final TextView driverTripDistance = (TextView) dialogTripSummary.findViewById(R.id.trip_distance);
                                final CustomRatingBar riderRating = (CustomRatingBar) dialogTripSummary.findViewById(R.id.rider_rating);

                                final CustomRatingBar driverRating = (CustomRatingBar) dialogTripSummary.findViewById(R.id.driver_rating);
                                driverRating.setEnabled(false);
                                driverRating.setFocusable(false);
                                driverRating.setClickable(false);

                                if (tripID != null) {
                                    //Listener to get Total Price from Firebase
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.getValue() != null) {

                                                Object tripPrice = dataSnapshot.child("Price").getValue();
                                                Object tripDistance = dataSnapshot.child("Distance").getValue();
                                                Object ratingFromDriver = dataSnapshot.child("driver_rating").getValue();

                                                System.out.println("Trip Summary: Price from firebase==>" + tripPrice);
                                                System.out.println("Trip Summary: Distance from firebase==>" + tripDistance);
                                                System.out.println("Trip Summary: Driver Rating from firebase==>" + ratingFromDriver);

                                                if (tripPrice!= null)
                                                    driverTripAmount.setText("$" + tripPrice);

                                                if(tripDistance!=null)
                                                    driverTripDistance.setText("Total Distance : " + tripDistance + " KM");

                                                if(ratingFromDriver!=null)
                                                    driverRating.setRating(Float.parseFloat(String.valueOf(ratingFromDriver)));


                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }

                                riderRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                    @Override
                                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                        System.out.println("Rating value" + rating);
                                        updateRating(String.valueOf(rating));
                                        try {
                                            int ratingInt = Math.round(rating);
                                            switch (ratingInt) {
                                                case 1:
                                                    riderEmoji.setBackgroundResource(R.drawable.one);
                                                    break;
                                                case 2:
                                                    riderEmoji.setBackgroundResource(R.drawable.two);
                                                    break;
                                                case 3:
                                                    riderEmoji.setBackgroundResource(R.drawable.three);
                                                    break;
                                                case 4:
                                                    riderEmoji.setBackgroundResource(R.drawable.four);
                                                    break;
                                                case 5:
                                                    riderEmoji.setBackgroundResource(R.drawable.five);
                                                    break;
                                                case 0:
                                                    riderEmoji.setBackgroundResource(R.drawable.none);
                                                    break;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                if (driverProfileName != null) {
                                    driverProfileName = driverProfileName.replaceAll("%20", " ");
                                    driverName.setText(driverProfileName);
                                }
                                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                tripDate.setText(currentDateTimeString);

                                System.out.println("afterview summary driverProfileImage" + driverProfileImage);

                                Glide.with(getApplicationContext()).load(driverProfileImage).asBitmap().centerCrop().placeholder(R.drawable.account_circle_grey).skipMemoryCache(true).into(new BitmapImageViewTarget(driverImage) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        driverImage.setImageDrawable(circularBitmapDrawable);
                                    }
                                });

                                close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (!isFinishing()) {
                                            dialogTripSummary.dismiss();
                                        }
                                        mMap.clear();
                                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }

                                });
                                //to avoid bad token exception
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isFinishing()) {
                                            dialogTripSummary.show();
                                        }
                                    }
                                });

                            } else if (status.matches("5")) {
                                updatedrop_location_layout.setVisibility(View.GONE);
                                terminal_layout.setVisibility(View.GONE);
                                txtDriverArrivalETALayout.setVisibility(View.GONE);
                                getState.putBoolean("tripbool", false);
                                if (strCacnelStatus != null) {
                                    if (strCacnelStatus.equals("drivercliked")) {
                                        strCacnelStatus = "mt";
                                        databaseReference.removeEventListener(this);
                                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    } else {
                                        generateNotification(getApplicationContext(), "Rider Cancelled the Trip");
                                        databaseReference.removeEventListener(this);
                                        getState.putString("tripstate", "endclicked");
                                        getState.apply();
                                        //cancel trip
                                        cancelTrip();
                                        driverID = null;//clear existing driver
                                        showDriverCancelDialog(getString(R.string.rider_cancel));
                                    }

                                } else {
                                    generateNotification(getApplicationContext(), "Driver Cancelled the Trip");
                                    databaseReference.removeEventListener(this);
                                    getState.putString("tripstate", "endclicked");
                                    getState.apply();
                                    //cancel trip
                                    cancelTrip();
                                    driverID = null;//clear existing driver
                                    showDriverCancelDialog(getString(R.string.driver_cancel));
                                }


                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            System.out.println("trip id is null====>");
        }
    }


    private void getKeys() {

        //http://demo.cogzideltemplates.com/tommy/settings/getdetails
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
                                String Distanceradius = signIn_jsonobj.optString("nearby_distance");

                                if (isDouble(Distanceradius)){

                                    nearByDistanceRadius = Double.valueOf(Distanceradius);

                                }else {
                                    nearByDistanceRadius = (double) Integer.parseInt(Distanceradius);
                                }


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
                    System.out.print("getKeys,no internet");
                    // stopAnim();
                    //
                    //    Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                VolleyLog.d("Error", "EarningActivity: " + error.getMessage());


            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    boolean isDouble(String str) {
        try {

            Double.parseDouble(str);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void checkToll() {
        if (driverID != null) {
            tollReference = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID).child("tollfee");
            tollListener = tollReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        if (status != null) {
                            tollFee.setText("Toll Fee: " + status);

                            //Notification to show toll was added
                            if (!status.equals("0"))
                                generateNotification(getApplicationContext(), "Toll amount was added in your trip");
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    private void showDriverCancelDialog(String message) {
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(MapActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.cancel_trip));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {
                try {
                    System.out.println("cancel trip and tripid"+canceltripid+" price"+cancelprice);
                    if(canceltripid!=null && !cancelprice.equals("0")){
                        Intent intent = new Intent(getApplicationContext(), YourTripDetailsActivity_.class);
                        intent.putExtra("frommap","yes");
                        intent.putExtra("trip_id",canceltripid);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        if (!isFinishing()) {
            builder.show();
        }
    }


    //Payment Type Listener
    private void getPaymentReference() {
        if (userID != null) {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("riders_location").child(userID).child("Paymenttype");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        if (status != null) {
                            if (status.matches("stripe")) {
                                cashButton.setBackground(getResources().getDrawable(R.mipmap.ub__payment_type_delegate));
                            } else if (status.matches("cash")) {

                                //getCashStatus
                                getCashOnOff();

                            } else if (status.matches("corpID")) {
                                cashButton.setBackground(getResources().getDrawable(R.mipmap.ic_cardss));

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

    //Payment Type Listener
    private void getCashOnOff() {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("cashoption").child("status");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        cashStatus = dataSnapshot.getValue().toString();
                        Drawable drawable = cashButton.getBackground();
                        if (!cashStatus.matches("on")) {

                            cashButton.setBackground(getResources().getDrawable(R.drawable.ub__payment_type_cash_no));


                        } else {

                            cashButton.setBackground(getResources().getDrawable(R.drawable.ub__payment_type_cash));

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }


    private void updatecancelTrip() {

        String url = Constants.REQUEST_URL + "updateTrips/trip_id/" + tripID + "/trip_status/cancel/accept_status/5/distance/0/user_id/" + userID;
        System.out.println(" ONLINE URL is " + url);

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            System.out.print("updatecancelTrip");
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                cancelprice=jsonObject.optString("total_price");
                                canceltripid=jsonObject.optString("trip_id");
                                System.out.println("price and tripid"+cancelprice+" "+canceltripid);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    System.out.print("updatecancelTrip, NoConnectionError");
                    // stopAnim();
                    // Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    //Get Latitude and Longitude from Address
    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(MapActivity.this, Locale.ENGLISH);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            System.out.println("location from address" + location);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return p1;
    }

    //fareestimate
    public void showFareEstimateDialog(String pickloc, String destloc, String category, String fareestimate) {
        faredialog = new Dialog(MapActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        faredialog.setContentView(R.layout.getfareestimate_layout);
        faredialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        faredialog.setCancelable(false);
        ImageView close = (ImageView) faredialog.findViewById(R.id.close);
        final TextView pickuplocation = (TextView) faredialog.findViewById(R.id.pick_location_fare);
        final TextView destinatocation = (TextView) faredialog.findViewById(R.id.drop_location_fare);
        final TextView categorytxt = (TextView) faredialog.findViewById(R.id.categorytxtfare);
        final TextView estimatedfare = (TextView) faredialog.findViewById(R.id.fareestimatetxt);

        pickuplocation.setText(pickloc);
        destinatocation.setText(destloc);
        categorytxt.setText(category);
        estimatedfare.setText("$ " + fareestimate);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faredialog.cancel();
            }
        });


        faredialog.show();
    }

    //Display profile details of Driver
    public void displayDetails(String driverID) {
        final String url = Constants.LIVE_URL_DRIVER + "editProfile/user_id/" + driverID;
        System.out.println("Driver Profile==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String status = jsonObject.optString("status");

                        if (status.equals("Success")) {
                            driverFirstname = jsonObject.optString("firstname");
                            driverLastName = jsonObject.optString("lastname");
                            driverDisplayName = jsonObject.optString("nick_name");
                            driverProfileImageAccepted = jsonObject.optString("profile_pic");
                            driverMobileNumber = jsonObject.optString("mobile");
                            tripDriverCategory = jsonObject.optString("category");
                            tripDriverVehicleMake = jsonObject.optString("vehicle_make");
                            tripDriverVehicleModel = jsonObject.optString("vehicle_model");
                            licenseplate = jsonObject.optString("license");
                            licenseplate_number = jsonObject.optString("number_plate");
                            System.out.println("THE TRIP DRIVER CATEGORY" + jsonObject.optString("category"));
                            try {
                                //Set Trip Name
                                if (driverDisplayName != null ) {
                                    driverProfileName = driverFirstname + " " + driverLastName;
                                    driverProfileName = driverDisplayName;
                                    driverProfileName = driverProfileName.replaceAll("%20", " ");
                                    tripDriverName.setText(Constants.capitalizeFirstLetter(driverProfileName));
                                    tripCarName.setText(tripDriverCategory);
                                    tripDriverVehicleMake = tripDriverVehicleMake.replaceAll("%20", " ");
                                    tripDriverVehicleModel = tripDriverVehicleModel.replaceAll("%20", " ");
                                    tripModelMakeName.setText(tripDriverVehicleMake + " " + tripDriverVehicleModel);
                                    System.out.println("licenseplate_number" + licenseplate_number);
                                    number_plate.setText("Plate no: " + licenseplate_number.replaceAll("%20", " "));
                                }


                                int carCategoryImageID;

                                System.out.println("Driver Category Driver Profile==>"+tripDriverCategory+"categories===>"+strCategoryName[0]+
                                        ","+strCategoryName[1]+","+strCategoryName[2]+","+strCategoryName[3]+",");

                                if (tripDriverCategory.equals(strCategoryName[1])) {
                                    carCategoryImageID = R.mipmap.ic_thumb_luxury;
                                } else if (tripDriverCategory.equals(strCategoryName[2])) {
                                    carCategoryImageID = R.mipmap.ic_thumb_suv;
                                } else if (tripDriverCategory.equals(strCategoryName[3])) {
                                    carCategoryImageID = R.mipmap.ic_thumb_taxi;
                                } else {
                                    carCategoryImageID = R.mipmap.ic_thumb_standard;
                                }

                                //  bitmap = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.standard);
                                // change car icon for ride later
/*
                                if(requesttype!=null && tripDriverCategory!=null){
                                    if(requesttype.equals("ride_later_request")){
                                        if(tripDriverCategory.equals(strCategoryName[1]))
                                        {

                                            bitmapDescriptor = getBitmapDescriptor(1);
                                            getState.putInt("tripcategory",1);
                                        }
                                        else if(tripDriverCategory.equals(strCategoryName[2]))
                                        {
                                            bitmapDescriptor = getBitmapDescriptor(2);
                                            getState.putInt("tripcategory",2);
                                        }
                                        else if(tripDriverCategory.equals(strCategoryName[3]))
                                        {
                                            bitmapDescriptor = getBitmapDescriptor(3);
                                            getState.putInt("tripcategory",3);
                                        }
                                        else
                                        {
                                            bitmapDescriptor = getBitmapDescriptor(0);
                                            getState.putInt("tripcategory",0);
                                        }
                                    }
                                }
*/

                                if (driverProfileImageAccepted != null) {
                                    driverProfileImage = driverProfileImageAccepted;
                                }


                                Glide.with(getApplicationContext()).load(driverProfileImageAccepted).asBitmap().centerCrop().error(R.drawable.account_circle).placeholder(R.drawable.account_circle).skipMemoryCache(true).into(new BitmapImageViewTarget(driverImage) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        driverImage.setImageDrawable(circularBitmapDrawable);
                                    }
                                });


                                Glide.with(getApplicationContext()).load(carCategoryImageID).asBitmap().centerCrop().error(R.mipmap.ic_thumb_standard).placeholder(R.mipmap.ic_thumb_standard).skipMemoryCache(true).into(new BitmapImageViewTarget(carCategoryImage) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        carCategoryImage.setImageDrawable(circularBitmapDrawable);
                                    }
                                });


                                //Glide.with(getApplicationContext()).load(licenseplate).asBitmap().error(R.drawable.account_circle).placeholder(R.drawable.account_circle).skipMemoryCache(true).into(new BitmapImageViewTarget(driver_licence));
                                //For update marker
                                updateMarker(tripDriverCategory);

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        } else {
                            System.out.print("else");
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }


    //Set cancel Request
    public void cancelRequest(String requestID) {
        final String url = Constants.REQUEST_URL + "cancelRequest/request_id/" + requestID;
        System.out.println("Cancel Request==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        System.out.println("The request is Cancelled" + jsonObject);
                        String requeststatus=jsonObject.optString("request_status");
                        if(requeststatus.equals("cancel")){
                            setCancelStatus("6");
                            requestLayout.setVisibility(View.GONE);
                            dropLocationLayout.setVisibility(View.GONE);
                            setPickupLocation.setVisibility(View.VISIBLE);
                            requestBar.setVisibility(View.GONE);
                            categoryLayout.setVisibility(View.VISIBLE);
                            headerButton.setVisibility(View.GONE);
                            txtETA.setVisibility(View.VISIBLE);
                            if (!MapActivity.this.isFinishing() && progressBar != null) {
                                progressBar.dismiss();
                            }
                        }
                        else{
                            Toast.makeText(MapActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException | NullPointerException e) {
                        Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    //acceptRequest
    public void acceptViews() {
        easyTimer.stop();
        if (!MapActivity.this.isFinishing() && progressBar != null) {
            progressBar.dismiss();
        }
        System.out.println("INside ACCEPT VIEWS");
        requestBar.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.GONE);
        headerButton.setVisibility(View.VISIBLE);
        txtDriverArrivalETALayout.setVisibility(View.VISIBLE);
        showTerminalSelectorLayout();
        headerButton.setText("ACCEPTED");
        setPickupLocation.setVisibility(View.GONE);
        driverLayout.setVisibility(View.VISIBLE);
        //vehicle_nmuber.setVisibility(View.VISIBLE);
        trip_info_layout.setVisibility(View.VISIBLE);
        dropLocationLayout.setVisibility(View.GONE);
        locationLayout.setVisibility(View.GONE);
        txtETA.setVisibility(View.GONE);
        //Stop Ripple Animation During Trip

        stopRippleAnimation();
    }

    //dismissRequest
    public void dismissViews() {
        easyTimer.stop();
        if (!MapActivity.this.isFinishing() && progressBar != null) {
            progressBar.dismiss();
        }
        requestBar.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.VISIBLE);
        requestLayout.setVisibility(View.GONE);
        headerButton.setVisibility(View.GONE);
        dropLocationLayout.setVisibility(View.GONE);
        setPickupLocation.setVisibility(View.VISIBLE);
        txtETA.setVisibility(View.VISIBLE);
        driverLayout.setVisibility(View.GONE);
        //vehicle_nmuber.setVisibility(View.GONE);
        trip_info_layout.setVisibility(View.GONE);
    }

    //clearAllPreferences
    public void clearpreferences() {
        final SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        //clear state
        final SharedPreferences.Editor stateeditor = getSharedPreferences(Constants.MY_STATE_KEY, MODE_PRIVATE).edit();
        stateeditor.clear();
        stateeditor.apply();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();

            this.finishAffinity();
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            System.exit(0);
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit the app", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    //A timer to get response every X seconds
    public void AsynchTaskTimer(final String requestID, final String request_type) {
        System.out.println("Request ID before starting Timer" + requestID);
        if (requestID != null) {
            easyTimer.start();
            easyTimer.setOnTaskRunListener(new EasyTimer.OnTaskRunListener() {
                @Override
                public void onTaskRun(long past_time, String rendered_time) {
                    // Change UI or do something with past_time and rendered_time.
                    // It will NOT block the UI thread.
                    getRequestID(requestID, request_type);
                }
            });
        }
    }

    //get Details of Driver
    private void getRiderDetails() {
        final String url = Constants.LIVE_URL + "editProfile/user_id/" + userID;
        System.out.println("Rider Profile in Map==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String status = jsonObject.optString("status");

                        if (status.equals("Success")) {
                            firstName = jsonObject.optString("firstname");
                            String lastName = jsonObject.optString("lastname");
                            String profileImage = jsonObject.optString("profile_pic");
                            if (firstName != null) {
                                firstName = firstName.replaceAll("%20", " ");
                                txtUserName.setText(firstName);
                            }
                            try {

                                Glide.with(getApplicationContext()).load(profileImage).asBitmap().error(R.drawable.account_circle_grey).centerCrop().placeholder(R.drawable.account_circle).skipMemoryCache(true).into(new BitmapImageViewTarget(userProfileImage) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        userProfileImage.setImageDrawable(circularBitmapDrawable);
                                    }
                                });

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        } else {
                            System.out.print("getRiderDetails else");
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    //No Drivers Alert
    public void noDriversAlert(String title, String message) {

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                changeFBRideLaterStatus(requestID);
            }

        });

        if (!MapActivity.this.isFinishing()) {
            //show dialog
            builder.show();
            easyTimer.stop();
        }
    }

    public void cancelRequest() {
        menuButton.setBackground(getResources().getDrawable(R.drawable.menu));
        dropLocationLayout.setVisibility(View.GONE);
        requestLayout.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.VISIBLE);
    }

    public void onDirectionSuccessPlaceMarker() {


        if (headerButton.getText().toString().matches("ON TRIP")) {

            if(startLatLng.latitude!=0 && startLatLng.longitude!=0){

                final CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(startLatLng)      // Sets the center of the map to Mountain View
                        .zoom(Constants.MAP_ZOOM_SIZE_ONTRIP)      // Sets the zoom
                        .bearing(getBearing)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }

        LatLng curPos = new LatLng(startLatLng.latitude, startLatLng.longitude);

        if(curPos.latitude!=0 && curPos.longitude!=0) {

            zoomCameraToPosition(curPos);
        }

        if (myMarker == null) {

            myMarker = mMap.addMarker(new MarkerOptions().position(startLatLng).icon(bitmapDescriptor).flat(true));
            myMarker.setAnchor(0.5f, 0.5f);
            myMarker.setRotation(getBearing);

        } else {

            if(prevLatLng!= new LatLng(0, 0)){

                if (!prevLatLng.equals(startLatLng)) {

                    double[] startValues = new double[]{prevLatLng.latitude, prevLatLng.longitude};
                    double[] endValues = new double[]{startLatLng.latitude, startLatLng.longitude};

                    System.out.println("Driver ID call listion direction===>" + driverID);
                    this.animateMarkerTo(myMarker, startValues, endValues, getBearing);

                }
                else {
                    myMarker.setRotation(getBearing);
                }
            }
            else {
                myMarker.setPosition(startLatLng);
                myMarker.setRotation(getBearing);
            }


            prevLatLng = new LatLng(startLatLng.latitude, startLatLng.longitude);

        }


        if (headerButton.getText().toString().equals("ACCEPTED") || headerButton.getText().toString().equals("ARRIVING")) {


            calculateETA(pickupLatLng,startLatLng);

            if (pickUPrDropMarker != null)
                pickUPrDropMarker.remove();

            System.out.println("pick location ===>" + pickupLatLng);
            pickUPrDropMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ub__ic_pin_pickup)));


        } else {



            if (pickUPrDropMarker != null)
                pickUPrDropMarker.remove();

            System.out.println("dest location ===>" + destinationLatLng);
            pickUPrDropMarker = mMap.addMarker(new MarkerOptions().position(destinationLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ub__ic_pin_dropoff)));


        }

    }

    /**
     * Direction listener to draw poly line
     */
    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

        System.out.println("Direction status" + direction.getStatus());
        System.out.println("Accepting Trip LatLng" + startLatLng + "||" + pickupLatLng);
        System.out.println("On Trip LatLng" + startLatLng + "||" + destinationLatLng);

        if (destLatLng == null) {
            if(filterLocation!=null)
                destLatLng = new LatLng(filterLocation.getLatitude(), filterLocation.getLongitude());

        }

        if (startLatLng != null || destLatLng != null) {
            if (direction.isOK()) {

                //mMap.clear();





                if (bitmapDescriptor == null) {
                    bitmapDescriptor = getBitmapDescriptor(state.getInt("tripcategory", 0));
                }
                onDirectionSuccessPlaceMarker();

                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();

                if (mPolyline == null) {

                    mPolyline = mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.BLUE));
                } else {

                    mPolyline.setPoints(directionPositionList);
                }

            }
        }
    }


    @Override
    public void onDirectionFailure(Throwable t) {

    }


    @Override
    public void onKeyEntered(String key, GeoLocation location) {

        System.out.println("location at geoquery" + location);
        System.out.println("Category at geoquery" + getCategory());
        System.out.println("Category at position 0" + strCategoryName[0]);
        System.out.println("Category at position 1" + strCategoryName[1]);
        System.out.println("Category at position 2" + strCategoryName[2]);
        System.out.println("Category at position 3" + strCategoryName[3]);

        if (location != null && !tripStatus) {

            //Car shown change it to SET PICKUP LOCATION
            setPickupLocation.setText("SET PICKUP LOCATION");
            setPickupLocation.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ub__button_request_circle_normal, 0, R.mipmap.ub__button_request_circle_arrow_normal, 0);
            setPickupLocation.setCompoundDrawablePadding(5);
            txtETA.setVisibility(View.VISIBLE);


            System.out.println("Driver Category on key Enter==>"+getCategory()+"  categories===>"+strCategoryName[0]+
                    ","+strCategoryName[1]+","+strCategoryName[2]+","+strCategoryName[3]+",");

            if (getCategory().equals(strCategoryName[1])) {
                bitmapDescriptor = getBitmapDescriptor(1);
                getState.putInt("tripcategory", 1);
            } else if (getCategory().equals(strCategoryName[2])) {
                bitmapDescriptor = getBitmapDescriptor(2);
                getState.putInt("tripcategory", 2);
            } else if (getCategory().equals(strCategoryName[3])) {
                bitmapDescriptor = getBitmapDescriptor(3);
                getState.putInt("tripcategory", 3);
            } else {
                bitmapDescriptor = getBitmapDescriptor(0);
                getState.putInt("tripcategory", 0);
            }

            getState.apply();

            // Add a new marker to the map
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude))
                    .icon(bitmapDescriptor).flat(true));
            marker.setFlat(true);
            marker.setAnchor(0.5f, 0.5f);
            //marker.setRotation(getDriverBearing(key));
            this.markers.put(key, marker);

        }

    }

    @Override
    public void onKeyExited(String key) {
        System.out.println("no matches found");
        System.out.println("Total Number of Markers" + markers.size());
        // Remove any old marker
        Marker marker = this.markers.get(key);
        if (marker != null) {
            if (markers.size() == 0) {
                //Car not shown change it to NO CARS AVAILABLE
                setPickupLocation.setText("NO CARS AVAILABLE");
                setPickupLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                txtETA.setVisibility(View.GONE);
            }
            marker.remove();
            this.markers.remove(key);
            System.out.println("Check marker is empty" + markers.isEmpty());
        }

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        System.out.println("Key moved");
        System.out.println("Key moved outside===>" + key);


        System.out.println("Key moved inside===>" + key);
        // Move the marker
        Marker marker = this.markers.get(key);

        if (marker != null) {

            //new moveKeyAsync(marker, location.latitude, location.longitude,keyMovedBear).execute();


            LatLng curPos, prevPos;

            prevPos = new LatLng(marker.getPosition().latitude, marker.getPosition().latitude);
            curPos = new LatLng(location.latitude, location.latitude);


            if (!headerButton.getText().toString().equals("ACCEPTED") && !headerButton.getText().toString().equals("ARRIVING")) {

                // zoomCameraToPosition(curPos);
            }


            if (!prevPos.equals(curPos)) {

                double[] startValues = new double[]{marker.getPosition().latitude, marker.getPosition().longitude};
                double[] endValues = new double[]{location.latitude, location.longitude};
                System.out.println("Driver ID call listion key moved===>" + key);
                this.animateMarkerTo(marker, startValues, endValues, getDriverBearing(key));

            }

        }
    }

    public void zoomCameraToPosition(LatLng curPos) {

        System.out.println("map location===>" + curPos.latitude + "  " + curPos.longitude);

        boolean contains = mMap.getProjection().getVisibleRegion().latLngBounds.contains(curPos);

        if (!contains) {
            // MOVE CAMERA
            // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(animatedValue[0],animatedValue[1]),17.0f));

            float zoomPosition;
            if(tripStatus){
                zoomPosition=Constants.MAP_ZOOM_SIZE_ONTRIP;
            }else {
                zoomPosition=Constants.MAP_ZOOM_SIZE;
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(curPos)                              // Sets the center of the map to current location
                    .zoom(zoomPosition)
                    .tilt(0)                                     // Sets the tilt of the camera to 0 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }


    }

    // Animation handler for old APIs without animation support
    private void animateMarkerTo(final Marker marker, double[] startValues, double[] endValues, float rotate) {

        ValueAnimator latLngAnimator = ValueAnimator.ofObject(new DoubleArrayEvaluator(), startValues, endValues);
        latLngAnimator.setDuration(1000);
        latLngAnimator.setInterpolator(new DecelerateInterpolator());
        latLngAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                double[] animatedValue = (double[]) animation.getAnimatedValue();
                marker.setPosition(new LatLng(animatedValue[0], animatedValue[1]));
            }
        });
        latLngAnimator.start();
        //float rotate = getBearing(new LatLng(startValues[0], startValues[1]), new LatLng(endValues[0], endValues[1]));
        System.out.println("Rotate===>" + rotate);
        //marker.setRotation(rotate + mMap.getCameraPosition().bearing);
        //marker.setRotation(getDriverBearing(driverID));
        marker.setRotation(rotate);
       /* if (headerButton.getText().toString().matches("ACCEPTED") || headerButton.getText().toString().matches("ARRIVING") || headerButton.getText().toString().matches("ON TRIP")) {

            float zoomPosition;
            if(tripStatus){
                zoomPosition=Constants.MAP_ZOOM_SIZE_ONTRIP;
            }else {
                zoomPosition=Constants.MAP_ZOOM_SIZE;
            }

            final CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(endValues[0], endValues[1]))      // Sets the center of the map to Mountain View
                    .zoom(zoomPosition)                   // Sets the zoom
                    .bearing(rotate)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }*/

    }


    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);

        return -1;
    }


    @Override
    public void onGeoQueryReady() {
        System.out.println("geoquery ready");
    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        System.out.println("geoquery error" + error);
    }

    @Override
    public void onCameraChange(final CameraPosition cameraPosition) {
        System.out.println("geo status: trip status===>" + tripStatus);
        if (!tripStatus) {
            //getCompleteAddressString(cameraPosition.target.latitude, cameraPosition.target.longitude);

            center = cameraPosition.target;
            centerLoc=new Location("center");
            centerLoc.setLatitude(center.latitude);
            centerLoc.setLongitude(center.longitude);

            markerLoc=new Location("markerloc");

            System.out.println("Camera Change: LatLng==> "+center.latitude+","+center.longitude);

            setPickupLocation.setText("NO CARS AVAILABLE");
            setPickupLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            txtETA.setVisibility(View.GONE);
            /*if(setPickupLocation.getText().toString().matches("NO CARS AVAILABLE")) {
                requestLayout.setVisibility(View.GONE);
                categoryLayout.setVisibility(View.VISIBLE);
            }*/

            if (mMap != null) {
                mMap.clear();

                //Ripple hided

                // Start Animation again only if it is not running
                if(mCurrentLocation!=null) {

                    stopAndStartRippleAnimation(mMap, new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), this);
                }
            }

            //Geo Fire
            geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location").child(getCategory()));
            setGeofire(geoFire);
            geoQuery = getGeofire().queryAtLocation(new GeoLocation(center.latitude, center.longitude), nearByDistanceRadius);
            if (this.geoQuery != null) {
                this.geoQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
                // radius in km //Dynamic_Radious
                this.geoQuery.setRadius(nearByDistanceRadius);
                this.geoQuery.addGeoQueryEventListener(MapActivity.this);
                System.out.println("geo status: geo queery started in camera change");

            } else {
                Toast.makeText(MapActivity.this, "Geoquery Null", Toast.LENGTH_SHORT).show();
            }

            try {
                new GetLocationAsync(center.latitude, center.longitude).execute();
                //new getPlaceType(String.valueOf(center.latitude)+","+String.valueOf(center.longitude)).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("The Markers size"+markers.size());
            System.out.println("The Markers==>"+markers);


            List<LatLng> locations = new ArrayList<LatLng>();

            for (Marker marker : markers.values()) {

                if (marker.getPosition().latitude!=0.0 | marker.getPosition().longitude!=0.0) {
                    locations.add(marker.getPosition());
                }
            }

            System.out.println("The Markers size"+locations.size());
            System.out.println("The Markers==>"+locations);

            if(locations.size()>=1){
                //sort the list, give the Comparator the current location
                if(locations.size()>1)
                    Collections.sort(locations, new SortPlaces(center));

                LatLng nearestKey= locations.get(0);

                calculateETA(center, new LatLng(nearestKey.latitude,nearestKey.longitude));
            }
        }
    }

    public void setCategory(String carcategory) {
        this.carCategory = carcategory;
    }

    public String getCategory() {
        if (category != null) {
            System.out.println("strCategory=" + carCategory);
            return carCategory;
        } else {
            System.out.println("strCategory=" + strCategoryName[0]);
            return strCategoryName[0];
        }
    }

    public void setGeofire(GeoFire geofire) {
        this.geoFire = geofire;
    }


    public GeoFire getGeofire() {
        return geoFire;
    }

    //get Instant location updates form Driver
    private void updateMarker(String category) {
        if (driverID != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_location").child(category).child(driverID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {

                        String drivBearing;

                        if(dataSnapshot.child("bearing").getValue()!=null)
                            drivBearing = dataSnapshot.child("bearing").getValue().toString(); //get bearing child
                        else
                            drivBearing = "0";

                        String status = dataSnapshot.child("l").getValue().toString(); //get location child
                        System.out.println("Status==>" + status);
                        System.out.println("Bearing" + status);
                        if (drivBearing != null) {
                            if (isFloat(drivBearing)) {
                                getBearing = Float.parseFloat(drivBearing);
                            } else {
                                getBearing = (float) Integer.parseInt(drivBearing);
                            }
                        }
                        String[] lat1ong = status.split(",");

                        System.out.println("length of the latlong==>" + lat1ong.length);

                        String latitude = lat1ong[0];
                        String longitude = lat1ong[1];


                        String latreplace = latitude.replaceAll("\\[", "");
                        String longreplace = longitude.replaceAll("\\]", "");


                        Double laat = Double.parseDouble(latreplace);
                        Double lngg = Double.parseDouble(longreplace);


                        LatLng driverLocation = new LatLng(laat, lngg);
                        startLatLng = driverLocation;

                        String startaddress = state.getString("pickupposition", null);
                        //LatLng startLatLng1 = getLocationFromAddress(startaddress);


                        String startaddresslat = state.getString("pickupposition_lat", null);
                        String startaddresslng = state.getString("pickupposition_lng", null);

                        if(startaddresslat!=null & startaddresslng!=null)
                            pickupLatLng = new LatLng(Double.parseDouble(startaddresslat), Double.parseDouble(startaddresslng));


                        System.out.println("THE LATLNG ++" + startLatLng + "Destination" + pickupLatLng);


                        if (headerButton.getText().toString().equals("ACCEPTED") || headerButton.getText().toString().equals("ARRIVING")) {
                            if (startLatLng != null && pickupLatLng != null) {

                                try {
                                    GoogleDirection.withServerKey(google_api_key)
                                            .from(startLatLng)
                                            .to(pickupLatLng)
                                            .transportMode(TransportMode.DRIVING)
                                            .execute(MapActivity.this);
                                    System.out.println("google_api_keyhhhhhhhh" + google_api_key);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        } else {

                            if (startLatLng != null && destinationLatLng != null) {

                                try {
                                    GoogleDirection.withServerKey(google_api_key)
                                            .from(startLatLng)
                                            .to(destinationLatLng)
                                            .transportMode(TransportMode.DRIVING)
                                            .execute(MapActivity.this);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

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

    boolean isFloat(String str) {
        try {

            Float.parseFloat(str);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        // remove all event listeners to stop updating in the background
        if (geoQuery != null) {
            this.geoQuery.removeAllListeners();
            for (Marker marker : this.markers.values()) {
                marker.remove();
            }
            this.markers.clear();
        }

        //Ripple hided
        stopRippleAnimation();


    }

    @Override
    protected void onStart() {
        super.onStart();

        // add an event listener to start updating locations again
        if (!tripStatus) {
            if (geoQuery != null) {
                try {
                    this.geoQuery.addGeoQueryEventListener(this);
                    System.out.println("geo status: geo queery started in on start");
                } catch (NullPointerException e) {
                    System.out.print("Geo query event listener Null Point exception" + e);
                } catch (IllegalArgumentException e) {
                    System.out.print("Geo query event listener Illegal Argument exception" + e);
                }
            }
        }
    }


    //generateNotifications
    private static void generateNotification(Context context, String message) {

        //Some Vars
        final int NOTIFICATION_ID = 1; //this can be any int
        String title = "SIX";
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Building the Notification
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIcon);
        builder.setContentText(message);
        builder.setContentTitle(title);

        builder.setLights(Color.RED, 3000, 3000);
        builder.setLights(Color.RED, 3000, 3000);
        builder.setSound(uri);
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.getNotification().flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;


        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                notificationManager.cancel(NOTIFICATION_ID);
                timer.cancel();
            }
        }, 10000, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void callProfile() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        Intent settings = new Intent(this, SettingsActivity_.class);
        startActivity(settings);
    }

    @Override
    public void onClick(View v) {

        placeAddress(v.getId());
    }

    private class GetLocationAsync extends AsyncTask<String, Void, String> {

        double x, y;
        StringBuilder str;

        public GetLocationAsync(double latitude, double longitude) {
            // TODO Auto-generated constructor stub
            x = latitude;
            y = longitude;

        }

        @Override
        protected void onPreExecute() {
        }

        @SuppressLint("NewApi")
        @Override
        protected String doInBackground(String... params) {

            try {
                geocoder = new Geocoder(MapActivity.this);
                addressList = geocoder.getFromLocation(x, y, 1);

                str = new StringBuilder();

                if (Geocoder.isPresent()) {

                    if (addressList.size() > 0) {
                        Address returnAddress = addressList.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder("");

                        for (int i = 0; i < returnAddress.getMaxAddressLineIndex(); i++) {
                            strReturnedAddress.append(returnAddress.getAddressLine(i)).append("\n");
                        }
                        completeAddress = strReturnedAddress.toString();
                        pickupCountryCode = addressList.get(0).getCountryCode();

                    }
                }
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                System.out.println("Address is " + completeAddress);
                if (completeAddress != null) {
                    originLocation.setText(completeAddress);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    private Location convertLatLngToLocation(LatLng latLng) {
        Location location = new Location("someLoc");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    public void getCountryName(Context context, double latitude, double longitude, String locationType) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                if (locationType.matches("ORIGIN")) {
                    pickupCountryCode = addresses.get(0).getCountryCode();
                }
                if (locationType.matches("MULTI")) {
                    multiDestCountryCode = addresses.get(0).getCountryCode();
                    mulitiDestCountryCode[multiTvID]=multiDestCountryCode;
                }
                else {
                    dropCountryCode = addresses.get(0).getCountryCode();
                }
                System.out.println("Pickup==>" + pickupCountryCode);
                System.out.println("multi==>" + multiDestCountryCode);
                System.out.println("Drop==>" + dropCountryCode);

            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public void updateRating(String rating) {
        if (tripID != null) {
            System.out.println("tripIDtripIDtripID" + tripID);
            if (rating != null) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
                Map<String, Object> taskMap = new HashMap<String, Object>();
                taskMap.put("rider_rating", rating);
                databaseReference.updateChildren(taskMap);
            }
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private String calculateEstimate(double strDistance, String priceKM, String priceMin, String baseFare,String bookfee,String airportfee,String taxpercentage) {

        System.out.println("Inside calculation" + priceKM +"    "+ priceMin +"    "+ baseFare);
        try {
            double totalEstimate;
            double totalDistance,totalPriceMin,totalBaseFare,totbookfee,totairportfee;

            if(isDouble(priceKM))
                totalDistance = strDistance * Double.valueOf(priceKM);
            else
                totalDistance = strDistance * (double) Integer.parseInt(priceKM);

            if(isDouble(priceMin))
                totalPriceMin = Double.valueOf(priceMin);
            else
                totalPriceMin = (double) Integer.parseInt(priceMin);

            if(isDouble(baseFare))
                totalBaseFare = Double.valueOf(baseFare);
            else
                totalBaseFare = (double) Integer.parseInt(baseFare);

            if(isDouble(bookfee))
                totbookfee = Double.valueOf(bookfee);
            else
                totbookfee = (double) Integer.parseInt(bookfee);

            if(isDouble(airportfee))
                totairportfee = Double.valueOf(airportfee);
            else
                totairportfee = (double) Integer.parseInt(airportfee);


            //set total amount
            double taxpercent,taxaddamount,totalAmount=totalDistance + totalPriceMin + totalBaseFare;

            try {
                taxpercent = (totalAmount*(Double.parseDouble(taxpercentage)/100.0f));
                taxaddamount=totalAmount+taxpercent ;
                System.out.println("After adding tax percentage of"+taxpercent+"to"+taxaddamount);
                totalAmount=taxaddamount;
            } catch (Exception e) {
                taxpercent=0.0;
                taxaddamount=totalAmount+taxpercent;
                totalAmount=taxaddamount;
                e.printStackTrace();
            }


            boolean isAirport = state.getBoolean("getPlaceType", false);
            boolean isChangiAirport = state.getBoolean("getPlaceName", false);

            System.out.println("PlaceType in showing==>" + isAirport);
            System.out.println("PlaceNamee in showing==>" + isChangiAirport);
            if(isAirport && isChangiAirport){
                 totalEstimate = totbookfee+totalAmount+totairportfee;
            }
            else{
                 totalEstimate = totbookfee+totalAmount;
            }

            return convertToDecimal(totalEstimate);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    //Function to Calculate ETA between two LatLng
    private void calculateETA(LatLng originAddress, LatLng destinationAddress) {
        if(google_api_key==null || google_api_key.equals(""))
            getKeys();

        String url = Constants.DISTANCE_MATRIX + "origins=" + originAddress.latitude + "," + originAddress.longitude + "&destinations=" + destinationAddress.latitude + "," + destinationAddress.longitude + "&sensor=false&key=" + google_api_key + "&mode=driving";
        System.out.print("Calculate ETA" + url);

        // Creating volley request obj
        JsonObjectRequest movieReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        // Parsing json
                        try {
                            JSONArray rows = response.optJSONArray("rows");
                            JSONObject json = rows.optJSONObject(0);
                            JSONArray elements = json.optJSONArray("elements");
                            JSONObject elemenJSONObject = elements.optJSONObject(0);
                            String duration = elemenJSONObject.optString("duration");
                            String distance = elemenJSONObject.optString("distance");

                            if (!duration.isEmpty()) {
                                JSONObject durationJSONObject = new JSONObject(duration);
                                totalDuration = durationJSONObject.optString("text");
                                totalDuration = totalDuration.replaceAll("[^0-9]", "");
                                System.out.println("Total Duration time- " + totalDuration);
                                txtETA.setText(totalDuration + "\n" + "MIN");
                                txtDriverArrivalETA.setText("Your Driver will Arrive in "+totalDuration+" MIN");

                                if (!setPickupLocation.getText().toString().matches("NO CARS AVAILABLE")) {
                                    // estimateTime.setText(totalDuration + " MIN");
                                    System.out.print("Calculate ETA no cars");
                                } else {
                                    // estimateTime.setText("--");
                                    System.out.print("Calculate ETA no else");
                                }
                            }

                            if (!distance.isEmpty()) {

                                JSONObject distanceJSONObject = new JSONObject(distance);
                                totalDistance = distanceJSONObject.optString("text");
                                totalDistance_meter = distanceJSONObject.optString("value");
                                System.out.println("Total Distance- " + totalDistance);
                                //totalDistance = totalDistance.replaceAll("[^0-9]", "");
                                System.out.println("Total Distance m- " + totalDistance_meter);

                                //txtDriverArrivalETA.setText("Your Driver will Arrive in "+totalDuration+" MIN and "+
                                  //      totalDistance+ "away from you.");

                                txtDriverArrivalETA.setText("Your Driver is "+totalDistance+ " away from you, will arrive in "+totalDuration+" MIN");
                            }

                        } catch (Exception e) {

                            if(dialog!=null)
                                if(dialog.isShowing())
                                    dialog.dismiss();

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    Toast.makeText(MapActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    public void listeningRideLater() {
        System.out.println("userid for ridelater" + userID);
        System.out.println("firebase url=" + FirebaseDatabase.getInstance().getReference());
        if (userID != null) {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ride_later").child(userID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("userid for ridelater in statusd" + dataSnapshot.getValue());

                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        System.out.println("ride later Status" + status);

                        listeningRideLaterStatus();


                    } else {
                        //Toast.makeText(MapActivity.this, "ALready! in trip", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //  dataSnapshot.getValue().toString()
                }
            });
        }
    }

    public void listeningRideLaterStatus() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ride_later").child(userID);
        final Query userQuery = databaseReference.orderByChild(userID);

        userQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //map.clear();
                //Get the node from the datasnapshot
                System.out.println("new pppp");
                String requestIDRidelater = dataSnapshot.getKey();

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    String key = child.getKey();

                    String value = child.getValue().toString();
                    System.out.println("parentid=" + requestIDRidelater);
                    System.out.println("key=" + key);
                    System.out.println("value=" + value);

                    if (value != null) {

                        if (value.equals("ready_for_ride")) {

                            generateNotification(getApplicationContext(), "Rider Ready For your Ride");
                            if (requestIDRidelater != null) {
                                readyForRideDialog(requestIDRidelater);
                            }
                        } else if (value.equals("request")) {
                            if (requestIDRidelater != null) {
                                rideLater(requestIDRidelater);
                            }

                        } else {
                            System.out.println("empty time");

                        }

                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getRideLaterCarCategory(String rideLaterRequestID) {

        if (userID != null) {

            //Get datasnapshot at your "users" root node
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ride_later").child(userID).child(rideLaterRequestID);
            ref.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get map of users in datasnapshot
                            if (dataSnapshot.getValue() != null) {

                                System.out.println("response ===>" + dataSnapshot.toString());

                                Object rideLaterCarCategory = dataSnapshot.child("car_category").getValue();
                                //getpickup drop latlng
                                Object orginlati = dataSnapshot.child("orgin_lat").getValue();
                                Object orginlngi= dataSnapshot.child("orgin_lng").getValue();
                                Object destlati = dataSnapshot.child("dest_lat").getValue();
                                Object destlngi = dataSnapshot.child("dest_lng").getValue();

                                System.out.println("response rideLaterCarCategory===>" + rideLaterCarCategory);

                                if (rideLaterCarCategory != null) {

                                    changeMapCarCategoryIcon(String.valueOf(rideLaterCarCategory));
                                    orginlat=new LatLng(Double.parseDouble(String.valueOf(orginlati)),Double.parseDouble(String.valueOf(orginlngi)));
                                    destlat=new LatLng(Double.parseDouble(String.valueOf(destlati)),Double.parseDouble(String.valueOf(destlngi)));
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

    public void rideLater(final String rideLaterRequestID) {

        android.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(MapActivity.this);
        tripState = state.getString("tripstate", null);

        System.out.println("Trip state in ride later" + tripState);
        if (tripState != null) {

            System.out.println("Trip state in ride later" + tripState);

            if (tripState.matches("endclicked")) {

                rideLaterRequestDialog(builder, rideLaterRequestID);


            } else {

                generateNotification(getApplicationContext(), "Schedule trip was cancelled");

                builder.setTitle("Ride Later Request");
                builder.setMessage("You Are Already in Trip. Your Schedule trip was automatically cancelled  on this time");
                builder.setCancelable(false);
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        changeFBRideLaterStatus(rideLaterRequestID);

                    }


                });


                if (!MapActivity.this.isFinishing() && builder != null) {
                    System.out.println("check status no : Already in trip ");
                    builder.show();
                }
            }

        } else {

            rideLaterRequestDialog(builder, rideLaterRequestID);

        }
    }

    public void rideLaterRequestDialog(android.app.AlertDialog.Builder builder, final String rideLaterRequestID) {

        System.out.println("Trip state in ride later" + tripState);


        getRideLaterCarCategory(rideLaterRequestID);

        dismissViews();
        generateNotification(getApplicationContext(), "Do you want start Rider Later Request");
        builder.setTitle("Ride Later Request");
        builder.setMessage("Do you want request for trip");
        builder.setCancelable(false);
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                rideLaterprogress(rideLaterRequestID);
            }

        });
        builder.setNeutralButton("close", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                changeFBRideLaterStatus(rideLaterRequestID);
                dialog.dismiss();

            }
            //  alertdialog2.cancel();

        });
        if (!isFinishing())
            builder.show();
    }

    public void readyForRideDialog(final String rideLaterRequestID) {
        android.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("Ride Later Request");
        builder.setMessage("Get Ready for Ride");
        builder.setCancelable(false);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                changeFBRideLaterStatus(rideLaterRequestID);
            }


        });


        if (!MapActivity.this.isFinishing()) {
            System.out.println("check status no :  ");
            builder.show();
        }
    }

    public void rideLaterprogress(final String parentnode) {
        setCancelStatus("0");
        menuButton.setBackground(getResources().getDrawable(R.drawable.menu));
        requestBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        requestBar.setVisibility(View.VISIBLE);
        progressBar.show();
        progressBar.setCancelable(false);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressBar.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        headerButton.setVisibility(View.VISIBLE);
        headerButton.setText("Requesting...");
        ImageButton cancelRequest = (ImageButton) progressBar.findViewById(R.id.cancel_request);
        requestLayout.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.GONE);
        setPickupLocation.setVisibility(View.GONE);
        txtETA.setVisibility(View.GONE);

        System.out.println("data ddsaved==" + parentnode);
        requestID = parentnode;
        processRequest(requestID);
        //Set a runnable task every 3 seconds
        AsynchTaskTimer(requestID, "ride_later_request");

        cancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                easyTimer.stop();

                if (requestID != null)
                    cancelRequest(requestID);

                    changeFBRideLaterStatus(requestID);
            }
        });
    }

    private void setCancelStatus(String status) {
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("riders_location").child(userID);
        Map<String, Object> taskMap2 = new HashMap<String, Object>();
        taskMap2.put("status", status);
        databaseReference2.updateChildren(taskMap2);

    }

    public boolean isOnline() {
        boolean connection = false;
        ConnectivityManager cm = (ConnectivityManager) MapActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if ((activeNetwork != null) && activeNetwork.isConnected()) {
            connection = true;
        }
        System.out.println("Network status " + connection);
        return connection;
    }

    public BitmapDescriptor getBitmapDescriptor(int position) {
        switch (position) {

            case 0:
                //bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ub__marker_vehicle_fallback);
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_standard);

                break;

            case 1:
                //bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.map_lux);
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_suv_white);

                break;

            case 2:
                //bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.map_suv);
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_suv);

                break;

            case 3:
                //bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.map_taxi);
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_taxi);

                break;

            default:
                //bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ub__marker_vehicle_fallback);
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_standard);

                break;

        }

        return bitmapDescriptor;
    }

    public Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }


    public float getDriverBearing(String key) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_location").child(getCategory()).child(key).child("bearing");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String status = dataSnapshot.getValue().toString();
                    System.out.println("Status" + status);

                    if (isFloat(status)) {
                        driverBearing = Float.parseFloat(status);
                    } else {
                        driverBearing = (float) Integer.parseInt(status);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return driverBearing;
        //return 0;
    }

    public void getEarning() {

        String url = Constants.LIVE_URL + "refrel_detail/user_id/" + userID;
        System.out.println(" ONLINE URL is " + url);

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject signIn_jsonobj = response.getJSONObject(i);
                                earnings = signIn_jsonobj.getString("referd_amount");
                                tripTime = signIn_jsonobj.getString("referd_users");
                                earnings_daily = signIn_jsonobj.optString("referd_amount_date");
                                earnings_weekly = signIn_jsonobj.optString("referd_amount_week");
                                earnings_monthly = signIn_jsonobj.optString("referd_amount_month");
                                earnings_yearly = signIn_jsonobj.optString("referd_amount_year");

                                if (earnings != null && !earnings.isEmpty()) {
                                    Double amount = Double.parseDouble(earnings);
                                    estimateTime.setText("$" + convertToDecimal(amount));

                                } else {
                                    estimateTime.setText("$0");
                                }

                                if (earnings_daily != null && !earnings_daily.isEmpty()) {
                                    Double amount = Double.parseDouble(earnings_daily);
                                    maxPeople.setText("$" + convertToDecimal(amount));

                                } else {
                                    maxPeople.setText("$0");
                                }
                                if (earnings_weekly != null && !earnings_weekly.isEmpty()) {
                                    Double amount = Double.parseDouble(earnings_weekly);
                                    minFare.setText("$" + convertToDecimal(amount));

                                } else {
                                    minFare.setText("$0");
                                }
                                if (earnings_monthly != null && !earnings_monthly.isEmpty()) {
                                    Double amount = Double.parseDouble(earnings_monthly);
                                    perMinutePrice.setText("$" + convertToDecimal(amount));

                                } else {
                                    perMinutePrice.setText("$0");
                                }
                                if (earnings_yearly != null && !earnings_yearly.isEmpty()) {
                                    Double amount = Double.parseDouble(earnings_yearly);
                                    perKmPrice.setText("$" + convertToDecimal(amount));

                                } else {
                                    perKmPrice.setText("$0");
                                }

                                if (tripTime != null && !tripTime.isEmpty()) {
                                    referral_count.setText(tripTime + " Users used your code");
                                } else {
                                    referral_count.setText("0" + " Users used your code");
                                }
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
                    System.out.println("getEarning NoConnectionError");
                    // stopAnim();
                }
                VolleyLog.d("Error", "EarningActivity: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
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

    public class DoubleArrayEvaluator implements TypeEvaluator<double[]> {

        private double[] mArray;

        /**
         * Create a DoubleArrayEvaluator that does not reuse the animated value. Care must be taken
         * when using this option because on every evaluation a new <code>double[]</code> will be
         * allocated.
         *
         * @see #DoubleArrayEvaluator(double[])
         */
        public DoubleArrayEvaluator() {
        }

        /**
         * Create a DoubleArrayEvaluator that reuses <code>reuseArray</code> for every evaluate() call.
         * Caution must be taken to ensure that the value returned from
         * {@link android.animation.ValueAnimator#getAnimatedValue()} is not cached, modified, or
         * used across threads. The value will be modified on each <code>evaluate()</code> call.
         *
         * @param reuseArray The array to modify and return from <code>evaluate</code>.
         */
        public DoubleArrayEvaluator(double[] reuseArray) {
            mArray = reuseArray;
        }

        /**
         * Interpolates the value at each index by the fraction. If
         * {@link #DoubleArrayEvaluator(double[])} was used to construct this object,
         * <code>reuseArray</code> will be returned, otherwise a new <code>double[]</code>
         * will be returned.
         *
         * @param fraction   The fraction from the starting to the ending values
         * @param startValue The start value.
         * @param endValue   The end value.
         * @return A <code>double[]</code> where each element is an interpolation between
         * the same index in startValue and endValue.
         */
        @Override
        public double[] evaluate(float fraction, double[] startValue, double[] endValue) {
            double[] array = mArray;
            if (array == null) {
                array = new double[startValue.length];
            }

            for (int i = 0; i < array.length; i++) {
                double start = startValue[i];
                double end = endValue[i];
                array[i] = start + (fraction * (end - start));
            }
            return array;
        }
    }

    public void setProgresscount(SeekBar seekBar, int seekBarCount, int categoryCount, Drawable thumbIcon) {

        seekBarValue = String.valueOf(seekBarCount);
        seekBar.setProgress(seekBarCount);
        seekBar.setThumb(thumbIcon);
        category = strCategoryName[categoryCount];

        System.out.println("category and count===>" + category + "  " + categoryCount);
        setCategory(category);

        if (mMap != null)
            mMap.clear();

        for (Marker marker : this.markers.values()) {
            marker.remove();
        }

        markers.clear();

        setPickupLocation.setText("NO CARS AVAILABLE");
        setPickupLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        txtETA.setVisibility(View.GONE);
        //Geo Fire
        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location").child(getCategory()));
        setGeofire(geoFire);
        getFareInfo(categoryCount);

        try {
            //Dynamic_Radious
            geoQuery = getGeofire().queryAtLocation(new GeoLocation(center.latitude, center.longitude), nearByDistanceRadius);
            geoQuery.addGeoQueryEventListener(MapActivity.this);
            System.out.println("geo status: geo queery started in progress count");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeMapCarCategoryIcon(String carCategory) {

        count = 0;

        for (int i = 0; i <= 3; i++) {

            if (carCategory.matches(strCategoryName[i])) {

                count = i;
            }
        }


        switch (count) {

            case 0:

                setProgresscount(categorySeekBar, 5, count, hatchBackCar);

                break;

            case 1:

                setProgresscount(categorySeekBar, 35, count, sedanCar);

                break;

            case 2:

                setProgresscount(categorySeekBar, 65, count, suvCar);

                break;

            case 3:

                setProgresscount(categorySeekBar, 95, count, fourthCar);

                break;
        }
    }

    public void changeFBRideLaterStatus(String rideLaterRequestID) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("ride_later").child(userID).child(rideLaterRequestID).child("status").setValue("0");
    }

    public void OverLay(final GroundOverlay groundOverlay) {
        ValueAnimator vAnimator = ValueAnimator.ofInt(0, 2000);
        int r = 99999;
        vAnimator.setRepeatCount(r);
        //vAnimator.setIntValues(0, 500);
        vAnimator.setDuration(12000);
        vAnimator.setEvaluator(new IntEvaluator());
        vAnimator.setInterpolator(new LinearInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                Integer i = (Integer) valueAnimator.getAnimatedValue();
                groundOverlay.setDimensions(i);
            }
        });
        vAnimator.start();
    }

    public Location getFuesedLocation() {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        System.out.println("Location Provoider:" + " Fused Location");

        if (mCurrentLocation == null) {

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            System.out.println("Location Provoider:" + " Fused Location Fail: GPS Location");

            if (locationManager != null) {

                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        Constants.MIN_TIME_BW_UPDATES,
                        Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (mCurrentLocation == null) {

                    System.out.println("Location Provoider:" + " GPS Location Fail: Network Location");

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            Constants.MIN_TIME_BW_UPDATES,
                            Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        }

        return mCurrentLocation;
    }

    public void GPSTurnOnAlert() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(Constants.SET_INTERVAL); //5 seconds
        locationRequest.setFastestInterval(Constants.SET_FASTESTINTERVAL); //3 seconds
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MapActivity.this, Constants.REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }

            }
        });
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onActivityResult()", Integer.toString(resultCode));

        switch (requestCode) {
            case Constants.REQUEST_CHECK_SETTINGS:

                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        Toast.makeText(MapActivity.this, "Enabling Location please wait...", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                            @Override
                            public void run() {
                                // This method will be executed once the timer is over

                                if (ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }

                                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                                if (mCurrentLocation != null) {


                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))                              // Sets the center of the map to current location
                                            .zoom(Constants.MAP_ZOOM_SIZE)
                                            .tilt(0)                                     // Sets the tilt of the camera to 0 degrees
                                            .build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                } else {
                                    System.out.print("inside else");
                                    // handler.postDelayed(this, Constants.updateLocationToFBHandlerTime);
                                }


                            }
                        }, Constants.GET_ZOOM_TIME);

                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(MapActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;

            case Constants.ORIGIN_REQUEST_CODE_AUTOCOMPLETE:

                setAddress(data, resultCode, "ORIGIN");

                break;

            case Constants.DEST_REQUEST_CODE_AUTOCOMPLETE:

                setAddress(data, resultCode, "DEST");

                break;

            case Constants.UPDATE_REQUEST_CODE_AUTOCOMPLETE:

                setAddress(data, resultCode, "UPDATE");

                break;

            case Constants.MULTI_DEST_REQUEST_CODE_AUTOCOMPLETE:

                setAddress(data, resultCode, "MULTI");

                break;
        }
    }

    public float getBearing(double lat1, double lng1, double lat2, double lng2) {

        double dLon = (lng2 - lng1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double brng = Math.toDegrees((Math.atan2(y, x)));
        brng = (360 - ((brng + 360) % 360));

        return Float.parseFloat(String.valueOf(brng));
    }

    public void updatedrop(String userID,String lat,String lng) {
        if (userID != null) {
            int previouscount, count = 0;
            System.out.println("INside the update location" + driverID);
            System.out.println("INside the DestinationWaypoints" + userID);
            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("riders_location").child(userID).child("Updatelocation");
            Map<String, Object> taskMap2 = new HashMap<String, Object>();
            taskMap2.put("0", lat);
            taskMap2.put("1", lng);
            databaseReference2.updateChildren(taskMap2);


            DatabaseReference wayPointRef = FirebaseDatabase.getInstance().getReference().child("riders_location").child(userID).child("DestinationWaypoints");
            DatabaseReference wayPointcountRef = FirebaseDatabase.getInstance().getReference().child("riders_location").child(userID);
            wayPointRef.removeValue();

            Map<String, Object> wayPointsCount = new HashMap<>();



            if (!lat.equals("0") && !lng.equals("0")) {
                if (headerButton.getText().toString().equals("ACCEPTED") || headerButton.getText().toString().equals("ARRIVING")) {
                    getState.putString("pickupposition_lat", lat);
                    getState.putString("pickupposition_lng", lng);
                    getState.apply();
                } else {
                    getState.putString("destposition_lat", lat);
                    getState.putString("destposition_lng", lng);
                    getState.apply();
                    destinationLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                }


                int wayPointCount = 0;

                String cordinatesValue = "",contryCodeValue = "",addressValue = "";

                for (int i = 0; i <= numberOfDesView - 1; i++) {

                    if (mulitiDestLatLng[i] != null) {

                        Map<String, Object> wayPoints = new HashMap<>();
                        Map<String, Object> CoordinatesPoints = new HashMap<>();
                        Map<String, Object> wayPointsProperties = new HashMap<>();


                        cordinatesValue =  cordinatesValue+","+String.valueOf(mulitiDestLatLng[i].latitude)+
                                "|"+String.valueOf(mulitiDestLatLng[i].latitude);
                        contryCodeValue = contryCodeValue+","+String.valueOf(mulitiDestCountryCode[i]);
                        addressValue = addressValue+"|"+String.valueOf(mulitiDestaddress[i]);

                        CoordinatesPoints.put("0", mulitiDestLatLng[i].latitude);
                        CoordinatesPoints.put("1", mulitiDestLatLng[i].longitude);
                        wayPointsProperties.put("Address", mulitiDestaddress[i]);
                        wayPointsProperties.put("CountryCode", mulitiDestCountryCode[i]);
                        wayPointsProperties.put("Coordinates", CoordinatesPoints);
                        wayPoints.put("WayPoint " + (wayPointCount + 1), wayPointsProperties);
                        wayPointCount++;
                        wayPointRef.updateChildren(wayPoints);

                    }


                }
                cordinatesValue = cordinatesValue.startsWith(",") ? cordinatesValue.substring(1) : cordinatesValue;
                contryCodeValue = contryCodeValue.startsWith(",") ? contryCodeValue.substring(1) : contryCodeValue;
                addressValue = addressValue.startsWith("|") ? addressValue.substring(1) : addressValue;

                System.out.println("string array cordinatesValue are===>"+cordinatesValue);
                System.out.println("string array contryCodeValue are===>"+contryCodeValue);
                System.out.println("string array addressValue are===>"+addressValue);

                if(!cordinatesValue.matches("") & !addressValue.matches("") & !contryCodeValue.matches("")){


                    try {

                        cordinatesValue = cordinatesValue.replaceAll("/", "");
                        cordinatesValue = URLEncoder.encode(cordinatesValue, "UTF-8");

                        addressValue = addressValue.replaceAll("/", "");
                        addressValue = URLEncoder.encode(addressValue, "UTF-8");

                        contryCodeValue = contryCodeValue.replaceAll("/", "");
                        contryCodeValue = URLEncoder.encode(contryCodeValue, "UTF-8");

                        if(tripID!=null)
                            updateDestinationWaypoints(cordinatesValue,addressValue,contryCodeValue);


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                if (!lat.equals("0") && !lng.equals("0")) {
                    if (headerButton.getText().toString().equals("ACCEPTED") || headerButton.getText().toString().equals("ARRIVING")) {
                        getState.putString("pickupposition_lat", lat);
                        getState.putString("pickupposition_lng", lng);
                        getState.apply();
                    } else {
                        getState.putString("destposition_lat", lat);
                        getState.putString("destposition_lng", lng);
                        previouscount = state.getInt("multidestcount", 0);

                        count = previouscount + wayPointCount;

                        System.out.println("the counts are +" + previouscount + " " + count + " " + wayPointCount);
                        //Toast.makeText(this, "Count is" + count, Toast.LENGTH_SHORT).show();

                        wayPointsCount.put("WayPointCount", count);
                        wayPointcountRef.updateChildren(wayPointsCount);

                        getState.putInt("multidestcount", count);
                        getState.apply();
                        destinationLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    }

                    displayDetails(driverID);
                }
            }
        }
    }

    private void getSurgePricingPercentage(String starttime, final String requesttype) {
        //Retrofit

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.CATEGORY_LIVE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitArrayAPI service = retrofit.create(RetrofitArrayAPI.class);
        Call<List<Contributor>> call = service.repoContributors(starttime,starttime);
        call.enqueue(new Callback<List<Contributor>>() {
            @Override
            public void onResponse(Call<List<Contributor>> call, retrofit2.Response<List<Contributor>> response) {
                try {
                    List<Contributor> RequestData= response.body();
                    System.out.println("Response Size"+RequestData.size());
                    for (int i = 0; i < RequestData.size(); i++) {
                        status=RequestData.get(i).getStatus();
                        if(status.equals("Success"))
                        {
                            surgePrice= RequestData.get(i).getPercentage();

                            System.out.println("The Price was"+surgePrice);
                        }
                        else if(requesttype.equals("normal")){
                            sendRequest();
                        }
                        else{
                            rideLaterprogress(requesttype);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Contributor>> call, Throwable t) {

            }
        });
    }

    public String getCurrentTime() {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm");//24 Hour Format
        date.setTimeZone(TimeZone.getDefault());

        String localTime = date.format(currentLocalTime);

        return localTime.replaceAll(" ","%20");
    }

    private void showfeedbackDialog() {

            getSubjectCategory();
            dialog = new Dialog(MapActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_feedback);
            dialog.setCancelable(false);

            //layouts
            ImageButton back=(ImageButton)dialog.findViewById(R.id.back);
            final EditText feedback =(EditText) dialog.findViewById(R.id.feedback);
            feedback.setVisibility(View.VISIBLE);
            feedback.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.app_font)));
            TextView titletxt=(TextView)dialog.findViewById(R.id.titletxt);
            titletxt.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.app_font)));
            TextView titletxt1=(TextView)dialog.findViewById(R.id.titletxt1);
            titletxt1.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.app_font)));
            TextView continuetxt=(TextView)dialog.findViewById(R.id.Submit);
            subjectspinner=(MaterialSpinner) dialog.findViewById(R.id.subject_category);
            continuetxt.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.app_font)));

            //setOnclicks

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            continuetxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(feedback.getText().toString().trim().length()==0){
                        Toast.makeText(MapActivity.this, "Enter your feedback!", Toast.LENGTH_SHORT).show();
                    }
                    else if(strSubjectCategory == null || strSubjectCategory.equals("Select a subject")) {
                        Toast.makeText(MapActivity.this, "Enter a subject!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        sendfeedback(strSubjectCategory,feedback.getText().toString());
                        subjectspinner.setSelectedIndex(0);
                        feedback.setText("");
                    }
                }
            });
        subjectspinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                strSubjectCategory = subjectspinner.getItems().get(subjectspinner.getSelectedIndex()).toString();
            }

            });


            dialog.show();

    }


    private void sendfeedback(String subject,String feedback) {

        try {
            feedback=feedback.replaceAll("#","%23");
            feedback= URLEncoder.encode(feedback, "UTF-8");
            subject=subject.replaceAll("#","%23");
            subject= URLEncoder.encode(subject, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String url = Constants.LIVE_URL+ "feedback?user_id="+ userID+"&feedback="+feedback+"&subject="+subject ;
        System.out.println("Driver Profile==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String status = jsonObject.optString("status");

                        if (status.equals("Success")) {
                            new SweetAlertDialog(MapActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                    .setTitleText("Thank You!")
                                    .setContentText("Your feedback was submitted successfully.")
                                    .setCustomImage(R.mipmap.ic_launcher)
                                    .show();
                        } else {
                            Toast.makeText(MapActivity.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }


    public class getPlaceType extends AsyncTask<String, Void, String> {

        String latlng;

        private getPlaceType(String latlng)
        {
            this.latlng=latlng;
        }


        protected void onPreExecute(){

        }

        protected String doInBackground(String... arg0) {
            try {

                if(google_api_key!=null)
                    PlaceType(latlng);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
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

                                String placeName = zerothPostion.optString("name");

                                JSONArray types = zerothPostion.optJSONArray("types");

                                String placeType =types.optString(0);

                                boolean savePlaceType,savePlaceName;

                                System.out.println("Airport PlaceType==>" + placeType);
                                System.out.println("Airport PlaceName==>" + placeName);

                                if(placeType.matches("airport"))
                                    savePlaceType=true;
                                else
                                    savePlaceType=false;

                                if(placeName.contains("Changi"))
                                    savePlaceName=true;
                                else
                                    savePlaceName=false;

                                getState.putBoolean("getPlaceType", savePlaceType);
                                getState.putBoolean("getPlaceName", savePlaceName);
                                getState.apply();

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

                    Toast.makeText(MapActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    public void showTerminalSelectorLayout(){

        boolean isAirport = state.getBoolean("getPlaceType", false);
        boolean isChangiAirport = state.getBoolean("getPlaceName", false);

        System.out.println("PlaceType in showing==>" + isAirport);
        System.out.println("PlaceNamee in showing==>" + isChangiAirport);

        if(isAirport)
            terminal_layout.setVisibility(View.VISIBLE);


        if(isChangiAirport)
            terminalSpinner.setItems(terminalsArray);


    }



    public Bitmap drawCircle(){

        // Initialize a new Bitmap object
        Bitmap bitmap = Bitmap.createBitmap(
                50, // Width
                50, // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(bitmap);

        // Draw a solid color to the canvas background
        canvas.drawColor(Color.TRANSPARENT);

        // Initialize a new Paint instance to draw the Circle
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);

        // Calculate the available radius of canvas
        int radius = Math.min(canvas.getWidth(),canvas.getHeight()/2);

        System.out.println("radious of the circle===>"+radius);

        // Set a pixels value to padding around the circle
        int padding = 5;

                /*
                    public void drawCircle (float cx, float cy, float radius, Paint paint)
                        Draw the specified circle using the specified paint. If radius is <= 0, then
                        nothing will be drawn. The circle will be filled or framed based on the
                        Style in the paint.

                    Parameters
                        cx : The x-coordinate of the center of the circle to be drawn
                        cy : The y-coordinate of the center of the circle to be drawn
                        radius : The radius of the cirle to be drawn
                        paint : The paint used to draw the circle
                */
        // Finally, draw the circle on the canvas
        canvas.drawCircle(
                canvas.getWidth() / 2, // cx
                canvas.getHeight() / 2, // cy
                radius - padding, // Radius
                paint // Paint
        );

        return bitmap;
    }

    public void startRippleAnimation(){
       /* mapRipple.stopRippleMapAnimation();
        mapRipple = new MapRipple(mMap, new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), this);
        mapRipple.startRippleMapAnimation();*/

      /*  myMarker = mMap.addMarker(new MarkerOptions()
                .icon(currentLocationBitmap)
                .position( new LatLng(mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude()))
                .flat(true));
        myMarker.setAnchor(0.5f, 0.5f);*/

    }

    public void stopRippleAnimation(){

        /*if (mapRipple!=null) {

            if (mapRipple.isAnimationRunning()) {
                mapRipple.stopRippleMapAnimation();
            }
        }*/

        /*if(myMarker==null)
            myMarker.remove();*/

    }
    public void stopAndStartRippleAnimation(GoogleMap mMap, LatLng loc,Context con){

        /*mapRipple.stopRippleMapAnimation();
        mapRipple = new MapRipple(mMap, loc, con);
        mapRipple.startRippleMapAnimation();*/
      /*  if(myMarker!=null){
            myMarker.setPosition(loc);
        }else {
            myMarker = mMap.addMarker(new MarkerOptions()
                    .icon(currentLocationBitmap)
                    .position(loc)
                    .flat(true));
            myMarker.setAnchor(0.5f, 0.5f);
        }*/

    }

    protected void initializeDestinationDialog(){

        dialogMultipleDestination= new Dialog(MapActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialogMultipleDestination.setContentView(R.layout.dialog_multiple_destination);
        dialogMultipleDestination.setCancelable(false);


        final LinearLayout dynamicDestinationView = (LinearLayout)dialogMultipleDestination.findViewById(R.id.dynamic_destination_view);



        mulitipleDestinationTextView = new TextView[numberOfDesView];
        mulitiDestLatLng = new LatLng[numberOfDesView];
        mulitiDestCountryCode=new String[numberOfDesView];
        mulitiDestaddress=new String[numberOfDesView];

        LinearLayout.LayoutParams paramsButton = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < numberOfDesView; i++) {

            mulitipleDestinationTextView[i] = new TextView(MapActivity.this);
            mulitipleDestinationTextView[i].setHint("Location " + (i + 1));
            mulitipleDestinationTextView[i].setPadding(10, 5, 5, 5);
            paramsButton.setMargins(5, 15, 5, 0);
            mulitipleDestinationTextView[i].setTextColor(ContextCompat.getColor(MapActivity.this, R.color.colorPrimary));
            mulitipleDestinationTextView[i].setTextSize(18);
            mulitipleDestinationTextView[i].setGravity(Gravity.START | Gravity.CENTER);
            mulitipleDestinationTextView[i].setMinimumHeight(130);
            mulitipleDestinationTextView[i].setBackgroundColor(Color.WHITE);
            mulitipleDestinationTextView[i].setMaxLines(1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mulitipleDestinationTextView[i].setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Small);
            }else {
                mulitipleDestinationTextView[i].setTextAppearance(this,android.R.style.TextAppearance_DeviceDefault_Small);
            }
            mulitipleDestinationTextView[i].setEllipsize(TextUtils.TruncateAt.END);
            mulitipleDestinationTextView[i].setId(i);
            mulitipleDestinationTextView[i].setTag("stop"+i);
            mulitipleDestinationTextView[i].setOnClickListener(this);
            mulitipleDestinationTextView[i].setLayoutParams(paramsButton);

            dynamicDestinationView.addView(mulitipleDestinationTextView[i]); // dynamic is the container of the buttons

        }

    }

    protected void showDestinationDialog(){

        dialogMultipleDestination.show();


        mulitDesArrayValueProperties("VIEW");

        Button done = (Button) dialogMultipleDestination.findViewById(R.id.footer_button_done);
        Button cancel = (Button) dialogMultipleDestination.findViewById(R.id.footer_button_cancel);

        final LinearLayout expand = (LinearLayout) dialogMultipleDestination.findViewById(R.id.expand_loc_lay);
        final RelativeLayout add_loc_lay = (RelativeLayout) dialogMultipleDestination.findViewById(R.id.add_loc_lay);
        final ImageView expandimageview = (ImageView) dialogMultipleDestination.findViewById(R.id.expandimageview);
        final TextView tvexpand = (TextView) dialogMultipleDestination.findViewById(R.id.tvexpand);


        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvexpand.getText().toString().matches(getResources().getString(R.string.add_additional_location))){
                    expandimageview.setImageResource(R.drawable.ic_arrow_drop_up);
                    tvexpand.setText(getResources().getString(R.string.hide_location));
                    add_loc_lay.setVisibility(View.VISIBLE);
                }
                else if(tvexpand.getText().toString().matches(getResources().getString(R.string.hide_location))){
                    expandimageview.setImageResource(R.drawable.ic_arrow_drop_down);
                    tvexpand.setText(getResources().getString(R.string.add_additional_location));
                    add_loc_lay.setVisibility(View.GONE);
                    mulitDesArrayValueProperties("REMOVE");
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogMultipleDestination.dismiss();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("Array of latlng==>"+Arrays.toString(mulitiDestLatLng));
                System.out.println("Array of CC==>"+ Arrays.toString(mulitiDestCountryCode));
                System.out.println("Array of Address==>"+Arrays.toString(mulitiDestaddress));
                System.out.println("drop coutrycode==>"+dropCountryCode);
                System.out.println("Pickup country code==>" + pickupCountryCode );

                boolean isSameCC=true;
                String alertMsg;

                for (String cc : mulitiDestCountryCode) {
                    if(cc!=null){
                        if(!cc.matches(pickupCountryCode)){
                            isSameCC=false;
                            break;
                        }
                    }
                }


                if(diaogDestinationLocation.getText()==null)
                    showAlertDialog(getResources().getString(R.string.enter_destination_location));
                else if(!diaogDestinationLocation.getText().toString().trim().matches("")){

                    if(isSameCC & pickupCountryCode.matches(dropCountryCode)){


                        dialogMultipleDestination.dismiss();

                        if(newDestLat!=null & newDestLng!=null)
                            updatedrop(userID,String.valueOf(newDestLat),String.valueOf(newDestLng));

                    }
                    else {

                        if(!isSameCC & !pickupCountryCode.matches(dropCountryCode)){
                            update_drop_location.setText("");
                            diaogDestinationLocation.setText("");
                            alertMsg = getResources().getString(R.string.service_not_available_for_the_stopings_dest);
                        }
                        else if( !pickupCountryCode.matches(dropCountryCode)){
                            update_drop_location.setText("");
                            diaogDestinationLocation.setText("");
                            alertMsg = getResources().getString(R.string.service_not_available_for_the_destination);
                        }
                        else
                            alertMsg = getResources().getString(R.string.service_not_available_for_the_stopings);


                        showAlertDialog(alertMsg);

                    }
                }
                else
                    showAlertDialog(getResources().getString(R.string.enter_destination_location));


            }
        });

        diaogDestinationLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openAutocompleteActivity(Constants.UPDATE_REQUEST_CODE_AUTOCOMPLETE);
            }
        });


    }

    public void mulitDesArrayValueProperties(String option){

        try {

            if(option.matches("REMOVE")){

                for (int i=0;i<=numberOfDesView-1;i++) {

                    mulitipleDestinationTextView[i].setText("");
                    mulitiDestLatLng[i] = null;
                    mulitiDestCountryCode[i] = null;
                    mulitiDestaddress[i] = null;
                }


            }else {

                for (int i=0;i<=numberOfDesView-1;i++) {

                    if (mulitiDestaddress[i] != null) {

                        mulitipleDestinationTextView[i].setText(mulitiDestaddress[i]);

                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void showAlertDialog(String alertMsg){

        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(MapActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Alert!");
        builder.setMessage(alertMsg);
        builder.setCancelable(false);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
        builder.show();
    }

    public void placeAddress(int ID) {


        multiTvID = ID;

        String existingText = mulitipleDestinationTextView[multiTvID].getText().toString().trim();

        if (!existingText.matches("")) {


            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(MapActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Alert!");
            builder.setMessage("Do you want to clear Address or Edit Address?");
            // builder.setCancelable(false);
            builder.setPositiveButton("Edit Address",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            openAutocompleteActivity(Constants.MULTI_DEST_REQUEST_CODE_AUTOCOMPLETE);
                        }
                    });

            builder.setNegativeButton("Clear Address",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mulitipleDestinationTextView[multiTvID].setText("");

                            mulitiDestLatLng[multiTvID] = null;
                            mulitiDestCountryCode[multiTvID] = null;
                            mulitiDestaddress[multiTvID] = null;

                            dialog.dismiss();
                        }
                    });

            builder.setNeutralButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
            builder.show();

        } else {

            openAutocompleteActivity(Constants.MULTI_DEST_REQUEST_CODE_AUTOCOMPLETE);
            // mulitipleDestinationTextView[multiTvID].setTextColor(ContextCompat.getColor(MapActivity.this, R.color.colorPrimary));
        }
    }
    private void getSubjectCategory()
    {

        final String url=Constants.CATEGORY_LIVE_URL + "home/getsubject";
        System.out.println("URL is"+url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        subjectcategory=new String[response.length()+1];
                        for (int i = 0; i < response.length(); i++) {
                            try
                            {
                                JSONObject  strJsonCategory = response.getJSONObject(i);
                                strCarCategory= strJsonCategory.optString("subject");
                                Log.d("OUTPUT IS",strCarCategory);
                                subjectcategory[0]="Select a subject";
                                subjectcategory[i+1]=strCarCategory;
                                System.out.println("CATEGORY"+subjectcategory[i]);
                               //adapteradapter  = new ArrayAdapter<String>(MapActivity.this, R.layout.spinner_item, subjectcategory);
                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                        subjectspinner.setItems(subjectcategory);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    Toast.makeText(getApplicationContext(),"No Intenet Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof NetworkError) {

                    Toast.makeText(getApplicationContext(),"No Net", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void updateDestinationWaypoints(String coordinatesValue,String addressValue,String ccValue)
    {

        final String url=Constants.REQUEST_URL+"updateDestinationWaypoints/trip_id/"+tripID+"/coordinates/"+coordinatesValue+"/countrycodes/"+
                ccValue+"/address/"+addressValue;
        System.out.println("URL is"+url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String status = jsonObject.optString("status");
                                if (status.equals("Success")) {

                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    Toast.makeText(getApplicationContext(),"No Intenet Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(),"No Intenet Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    /**
     * Record a screen view hit for the visible {@link } displayed
     * inside {@link }.
     */
    private void sendScreenImageName() {
        String name = "MapActivity";

        // [START screen_view_hit]
        Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }

}