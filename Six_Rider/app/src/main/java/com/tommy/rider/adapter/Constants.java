package com.tommy.rider.adapter;

/**
 * Created by Cogzidel.
 *
 * Constants used by this chatting application.
 * TODO: Replace PUBLISH_KEY and SUBSCRIBE_KEY with your personal keys.
 */
public class Constants {

    //Google API Key
    //public static final String Google_API_KEY   = "AIzaSyCScU4IEWKN2SPEbVMYvyNC_biwKFcYPBQ";
    public static final String Google_API_KEY   = "AIzaSyAD3Dz6tLDdFXLiiRqKu9ilkZOPVlbEOu4";

    public static final String Google_DIRECTION_KEY   = "";

    //public static final String BASE_URL   =   "http://demo.cogzideltemplates.com/tommy/";
    public static final String BASE_URL   =   "http://34.225.236.233/";
    //public static final String BASE_URL   =   "http://www.sixtnc.com/";

    //Playstore App URL
    public static final String Play_Store_URL = "https://play.google.com/store/apps/developer?id=6%20SIX&hl=en";

    public static final String LIVE_URL   =   BASE_URL+"rider/";
    public static final String LIVE_URL_DRIVER   =   BASE_URL+"driver/";
    public static final String REQUEST_URL   =   BASE_URL+"requests/";
    public static final String CATEGORY_LIVE_URL   =   BASE_URL;

    //Google Distance matrix base URL
    public static final String DISTANCE_MATRIX   = "https://maps.googleapis.com/maps/api/distancematrix/json?";


    /* Google sign in web Credentials */
    public static final String WEB_CLIENT_ID    =    "1058818652378-93go5vle89im1t5i8c9vcetq3sc32i89.apps.googleusercontent.com";

    //Autocomplete
    public static final int ORIGIN_REQUEST_CODE_AUTOCOMPLETE = 1;
    public static final int DEST_REQUEST_CODE_AUTOCOMPLETE = 12;
    public static final int MULTI_DEST_REQUEST_CODE_AUTOCOMPLETE = 62524;
    public static final int UPDATE_REQUEST_CODE_AUTOCOMPLETE = 10;

    public static final String PAYMENT_TYPE_CASH   =   "cash";

    public static final String PAYMENT_TYPE_CORP_ID   =   "corpID";

    public static final String PAYMENT_TYPE_CARD   =   "stripe";

    //Web page URL
    public static final String WEB_PAGE_URL   = BASE_URL;

    //PREFS_KEY
    public static final String MY_PREFS_NAME ="Six_Rider";

    //State key
    public static final String MY_STATE_KEY="saveTripState";

    // Splash screen timer
    public static int SPLASH_TIME_OUT = 2000;

    //Scan Card
    public static final int REQUEST_SCAN = 100;
    public static final int REQUEST_AUTOTEST = 200;

    //Stripe
    public static final int REQUEST_STRIPE_PAYMENT = 2000;

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    // The minimum distance to change Updates in meters
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    public static final long MIN_TIME_BW_UPDATES = 1000 * 2; //2 Seconds
    //gps turn on
    public static final int REQUEST_CHECK_SETTINGS = 0x2;

    public static final long SET_INTERVAL = 5000; //5 Seconds
    public static final long SET_FASTESTINTERVAL = 3000; //3 Seconds
    public static int GET_ZOOM_TIME = 4000;

    //Map Zooming Size
    public static final float MAP_ZOOM_SIZE = 14;

    public static final float MAP_ZOOM_SIZE_ONTRIP = 17;
}
