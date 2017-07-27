package com.tommy.rider;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tommy.rider.adapter.Constants;
import com.tommy.rider.adapter.FontChangeCrawler;
import com.tommy.rider.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_launch)
public class LaunchActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public Dialog dialog;
    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;
    Tracker mTracker;
    Bundle parameters;
    String fbEmail, fbFullName, fbFirstName, fbLastName, fbUserProfile, fbID, fbToken;
    String googleEmail, googleFirstName, googleLastName, googleUserProfile, googleID, googleIDToken;
    String signInStatus, signInMessage;
    boolean facebookLogin = false;
    ProgressDialog progressDialog;
    SharedPreferences.Editor editor;
    String userID, userFirstName, userLastName, userEmail;
    boolean doubleBackToExitPressedOnce = false;
    private int GOOGLE_SIGN_IN = 100;
    //Facebook Declaration
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    @AfterViews
    void launchActivity() {
        //Change Font to Whole View
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        LogUtils.i("UserID" + userID);
        Intent intent = getIntent();
        String istimeout = intent.getStringExtra("timeout");

        if (istimeout != null) {
            android.app.AlertDialog.Builder builder =
                    new AlertDialog.Builder(LaunchActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.timeout);
            builder.setMessage(R.string.time_out_msg);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }

            });

            builder.show();
        }

        if (userID != null) {
            Intent i = new Intent(getApplicationContext(), MapActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        // [END shared_tracker]


        editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.WEB_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Facebook Initialize
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        //getFbKeyHash(getString(R.string.Package_name));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                if (Profile.getCurrentProfile() == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            //LogUtils.v("facebook - profile2 - "+profile2.getFirstName());
                            displayMessage(profile2);
                            profileTracker.stopTracking();
                        }
                    };
                    profileTracker.startTracking();
                } else {
                    Profile profile = Profile.getCurrentProfile();
                    displayMessage(profile);
                    //LogUtils.v("facebook - profile "+profile.getFirstName());
                }

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                if (object != null) {
                                    LogUtils.i("jsonobject" + object);
                                    fbEmail = object.optString("email");
                                    fbFullName = object.optString("name");
                                    fbFullName = fbFullName.replaceAll(" ", "%20");
                                    fbID = object.optString("id");

                                    if (fbEmail == null) {
                                        fbEmail = "Nill";
                                    } else if (fbEmail.equals("")) {
                                        fbEmail = "Nill";
                                    }
                                    callFaceBookLogIn();
                                }
                            }
                        });

                parameters = new Bundle();
                parameters.putString("fields", "id,name,link,gender,birthday,email");
                request.setParameters(parameters);
                request.executeAsync();

                Bundle bundle = new Bundle();
                bundle.putString("fields", "token_for_business");
            }

            @Override
            public void onCancel() {
                LogUtils.i("Facebook Login failed!!");
                //Toast.makeText(EnrollActivity.this, "Login Cancelled by user!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(LaunchActivity.this, "An unknown network error has occured", Toast.LENGTH_LONG).show();
                LogUtils.i("Facebook Login failed!!");
            }
        });

        //Screen Tracker
        sendScreenImageName();
    }

    @Click(R.id.signin_button)
    void signIn() {
        Intent signin = new Intent(getApplicationContext(), SigninActivity_.class);
        startActivity(signin);
    }

    @Click(R.id.register_button)
    void toSignUpName() {
        Intent i = new Intent(LaunchActivity.this, SignupName_.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Click(R.id.google_button)
    void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        LogUtils.d("handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
//            Toast.makeText(this,"Successfully Logged in "+acct.getDisplayName(),Toast.LENGTH_SHORT).show();
            if (acct != null) {
                googleFirstName = acct.getGivenName();
                googleLastName = acct.getFamilyName();
                googleEmail = acct.getEmail();
                googleUserProfile = String.valueOf(acct.getPhotoUrl());
                if (googleUserProfile != null) {
                    googleUserProfile = googleUserProfile.replaceAll("/", "-__-");

                }
                googleID = acct.getId();
                googleIDToken = acct.getIdToken();
                googleSignOut();
                callGoogleLogIn();
            }
        } else {
            LogUtils.i("google signin result fail");
            // Signed out, show unauthenticated UI.
//            Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show();
        }
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
//                        Toast.makeText(getApplicationContext(),"Logged out Successfully",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Click(R.id.facebook_button)
    public void fblogin() {
        LoginManager.getInstance().logOut();//Logout Facebook
        LoginManager.getInstance().logInWithReadPermissions(LaunchActivity.this, Arrays.asList("public_profile", "email"));
    }

    private void callFaceBookLogIn() {
        LoginManager.getInstance().logOut();//Logout Facebook
        facebookLogin = true;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connecting to Facebook...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        if (fbFullName != null) {
            fbFullName = fbFullName.replaceAll(" ", "%20");
        }
        if (fbLastName != null) {
            fbLastName = fbLastName.replaceAll(" ", "%20");
        }
        final String url = Constants.LIVE_URL + "fbSignup/regid/" + "5765" + "/first_name/" + fbFullName + "/last_name/" + fbLastName + "/nick_name/" + fbFullName + "/mobile/" + "null" + "/country_code/" + "null" + "/city/" + "madurai" + "/email/" + fbEmail + "/fb_id/" + fbID + "/referral_code/null";
        LogUtils.i("FacebookSignUpURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (!isFinishing()) {
                    progressDialog.dismiss();
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        signInStatus = jsonObject.optString("status");
                        signInMessage = jsonObject.optString("message");

                        if (signInStatus.equals("Success")) {

                            if (jsonObject.optString("_id").isEmpty()) {

                                userID = jsonObject.optString("userid");

                            } else {

                                userID = jsonObject.optString("_id");
                            }

                            userFirstName = jsonObject.optString("first_name");
                            userLastName = jsonObject.optString("last_name");
                            userEmail = jsonObject.optString("email");
                            savepreferences();
                            Intent intent = new Intent(LaunchActivity.this, MapActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Toast.makeText(getApplicationContext(), R.string.logged_in_successfully, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), signInMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (!isFinishing()) {
                    progressDialog.dismiss();
                }
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    private void callGoogleLogIn() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connecting to Google...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (LaunchActivity.this.isFinishing())
            progressDialog.show();
        googleFirstName = googleFirstName.replaceAll(" ", "%20");
        googleLastName = googleLastName.replaceAll(" ", "%20");
        final String url = Constants.LIVE_URL + "googleSignup/regid/" + "5765" + "/first_name/" + googleFirstName + "/last_name/" + googleLastName + "/nick_name/" + googleFirstName + "/mobile/" + "null" + "/country_code/" + "null" + "/city/" + "madurai" + "/email/" + googleEmail + "/google_id/" + googleID + "/referral_code/null/profile_pic/" + googleUserProfile;
        LogUtils.i("GoogleSignUpURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (!isFinishing()) {
                    progressDialog.dismiss();
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        signInStatus = jsonObject.optString("status");
                        signInMessage = jsonObject.optString("message");

                        if (signInStatus.equals("Success")) {

                            if (jsonObject.optString("_id").isEmpty()) {

                                userID = jsonObject.optString("userid");

                            } else {

                                userID = jsonObject.optString("_id");
                            }

                            userFirstName = jsonObject.optString("first_name");
                            userLastName = jsonObject.optString("last_name");
                            userEmail = jsonObject.optString("email");
                            savepreferences();
                            Intent intent = new Intent(LaunchActivity.this, MapActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(), R.string.logged_in_successfully, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), signInMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.i("Error Response" + volleyError);
                if (!isFinishing()) {
                    progressDialog.dismiss();
                }
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }


    /*//Create FB KeyHash
    public void getFbKeyHash(String packageName) {

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                LogUtils.i("hash key value"+something);
                LogUtils.e("hash key "+something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            LogUtils.e("name not found"+e1.toString());
        } catch (NoSuchAlgorithmException e) {
            LogUtils.e("no such an algorithm "+e.toString());
        } catch (Exception e) {
            LogUtils.e("exception "+e.toString());
        }

    }*/

    //get Facebook Details
    private void displayMessage(Profile profile) {

        if (profile != null) {
            //Toast.makeText(getApplicationContext(),"displaymessage",Toast.LENGTH_LONG).show();
            fbFullName = profile.getName();
            fbFirstName = profile.getFirstName();
            fbLastName = profile.getLastName();
            fbID = profile.getId();
            fbUserProfile = profile.getProfilePictureUri(100, 100).toString();

            if (fbEmail != null) {
                if (!fbEmail.equals("")) {
                    LogUtils.i("fbEmail empty");
//                    callFaceBookLogIn();
                } else if (fbEmail.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter your E-mail address and Password to log into facebook", Toast.LENGTH_LONG).show();
                    LoginManager.getInstance().logOut();
                }
            }

            //String email1= this.getIntent().getExtras().getString("fields");
            //LogUtils.i("bundle email"+email1);
            LogUtils.i("Facebook Fullname" + fbFullName);
            LogUtils.i("Facebook Firstname" + fbFirstName);
            LogUtils.i("Facebook Lastname" + fbLastName);
            LogUtils.i("Facebook Profile" + fbUserProfile);
            LogUtils.i("Facebook ID" + fbID);
            LogUtils.i("Facebook Email" + fbEmail);
            LogUtils.i("Facebook Access Token" + fbToken);

        }
    }

    //Call Facebook StartActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN && data != null) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        } else if (data != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }


    public void savepreferences() {
        LogUtils.i("userid" + userID);
        LogUtils.i("username" + userFirstName);
        LogUtils.i("useremail" + userEmail);
        editor.putString("userid", userID);
        editor.putString("username", userFirstName);
        editor.putString("useremail", userEmail);
        editor.apply();

        LogUtils.i("Firebase URL" + FirebaseDatabase.getInstance().getReference());
        //Saving to Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("riders_location").child(userID);
        Map<String, Object> updates = new HashMap<>();
        updates.put("Paymenttype", "cash");

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

    /**
     * Record a screen view hit for the visible {@link } displayed
     * inside {@link }.
     */
    private void sendScreenImageName() {
        String name = "LaunchActivity";

        // [START screen_view_hit]
        LogUtils.i("Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}