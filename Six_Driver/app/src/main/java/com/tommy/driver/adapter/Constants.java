package com.tommy.driver.adapter;

/**
 * Created by Cogzidel.
 *
 * Constants used by this chatting application.
 * TODO: Replace PUBLISH_KEY and SUBSCRIBE_KEY with your personal keys.
 * TODO: Register app for GCM and replace GCM_SENDER_ID
 */
public class Constants {

    //Google API Key
    //public static final String Google_API_KEY   = "AIzaSyCScU4IEWKN2SPEbVMYvyNC_biwKFcYPBQ";
    public static final String Google_API_KEY   = "AIzaSyAD3Dz6tLDdFXLiiRqKu9ilkZOPVlbEOu4";

    //public static final String BASE_URL   =   "http://demo.cogzideltemplates.com/tommy/";
    //public static final String BASE_URL   =   "http://34.225.236.233/";
    //public static final String BASE_URL   =   "http://www.sixtnc.com/";
    //public static final String BASE_URL   =   "http://192.168.2.126/sixtnc/";     // PT
    public static final String BASE_URL   =   "http://52.64.2.146/";                // PT

    //Playstore App URL
    public static final String Play_Store_URL = "https://play.google.com/store/apps/developer?id=6%20SIX&hl=en";

    //Web page URL
    public static final String WEB_PAGE_URL   = BASE_URL;

    //demo urls
    public static final String LIVEURL   = BASE_URL+"driver/";
    public static final String LIVEURL_RIDER   = BASE_URL+"rider/";
    public static final String LIVEURL_REQUEST   = BASE_URL+"requests/";
    public static final String CATEGORY_LIVE_URL   =   BASE_URL;

    /* Google sign in web Credentials */
    public static final String WEB_CLIENT_ID   =   "1058818652378-0am3mag61tp91g546uni9jo64r7vt0uv.apps.googleusercontent.com";

    //FireBase Url
    public static final String FIREBASE_URL   = "https://tommy-68835.firebaseio.com/";

    //PREFS_KEY
    public static final String MY_PREFS_NAME ="Six_Driver";

    public static final String MY_STATE_KEY="saveTripState";

    // Splash screen timer
    public static int SPLASH_TIME_OUT = 2000;

    // Splash screen timer
    public static boolean MAP_ISSHOWING = false;

    // The minimum distance to change Updates in meters
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    public static final long MIN_TIME_BW_UPDATES = 1000 * 2; //2 Seconds

    public static final long updateLocationToFBHandlerTime = 1000 * 10; //10 Seconds

    //gps turn on
    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static final long SET_INTERVAL = 5000; //5 Seconds
    public static final long SET_FASTESTINTERVAL = 3000; //3 Seconds
    public static int GET_ZOOM_TIME = 4000;

    //Map Zooming Size
    public static final float MAP_ZOOM_SIZE = 14;

    public static final float MAP_ZOOM_SIZE_ONTRIP = 17;
}