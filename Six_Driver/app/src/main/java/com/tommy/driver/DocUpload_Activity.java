package com.tommy.driver;


import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.tommy.driver.adapter.AppController;
import com.tommy.driver.adapter.BulletTextUtil;
import com.tommy.driver.adapter.Constants;
import com.tommy.driver.adapter.FontChangeCrawler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by test on 12/16/16.
 **/

@EActivity(R.layout.activity_upload_doc)
public class DocUpload_Activity extends MyBaseActivity implements Validator.ValidationListener {

    Uri mCapturedImageURI;
    private static final int CAMERA_REQUEST=1;
    int count;
    String picturePath,licsImg,idImg,arcaImg,vehicledocimg,commercialImgStr,strreferral,strVehiclemake,strVehiclemodel,strVehicleyear,strVehiclemileage,strreferralcode,strnumberplate,content;
    ProgressDialog progressDialog;
    SharedPreferences.Editor editor;
    String[] carcategory;
    Dialog dialog;

    String driverID,driverFirstName,driverLastName,driverNickName,driverEmail,driverMobile,strCarCategory,strNumOfPassenger,referralStatus;
    JSONObject strJsonCategory;

    @Click (R.id.back)
    void getback()
    {
        finish();
    }
    @ViewById(R.id.idproofImg)
    ImageView idproof;

    @ViewById(R.id.licenceImg)
    ImageView licence;


    @ViewById(R.id.termsandconditions)
    CheckBox tandc;


    @ViewById(R.id.arcaprofile)
    ImageView arcaprofileimg;

    @ViewById(R.id.licenceplateImg1)
    ImageView vehicledoc;

    @ViewById(R.id.commercial)
    ImageView commercialImg;

   /* @ViewById(R.id.licenceplateImg)
    ImageView licenceplate;
*/
    @ViewById(R.id.txtidproof)
    TextView txtidProof;

    @ViewById(R.id.txtidlicence)
    TextView txtidlicence;

    @ViewById(R.id.txtarcaprofile)
    TextView txtarcaprofile;

    @ViewById(R.id.txtlicenceplate1)
    TextView txtvehicledoc;

    @ViewById(R.id.txtcommercial)
    TextView txtcommercial;

    @NotEmpty(message = "")
    @Length(min=1, message="Enter number of passengers")
    @ViewById (R.id.numberofpass)
    EditText numberofpass;

    @CheckedChange(R.id.termsandconditions)
    public void terms(boolean isChecked){
        if(isChecked){
            getTermsCondition();
            showTermsDialog();
        }
        else{
            if(dialog!=null)
                dialog.dismiss();
        }
    }

    @NotEmpty(message = "Enter Vehicle License Plate Number")
    @ViewById (R.id.vehicle_numberplate)
    EditText editVehiclenumberplate;

    @NotEmpty(message = "Enter Vehicle Make")
    @ViewById (R.id.vehicle_make)
    EditText edtVehicleMake;

    @NotEmpty(message = "Enter Vehicle Model")
    @ViewById (R.id.vehicle_model)
    EditText edtVehicleModel;

    @NotEmpty(message = "Enter Vehicle Year")
    @ViewById (R.id.vehicle_year)
    EditText edtVehicleYear;

    @NotEmpty(message = "Enter Vehicle Mileage")
    @ViewById (R.id.vehicle_mileage)
    EditText edtVehicleMileage;


    @ViewById (R.id.input_Referral_code)
    EditText edtReferralcode;


    /* @ViewById(R.id.txtlicenceplate)
    TextView txtlicenceplate;
*/
    String strEmail,strFirstName,strLastName,strNickName,strPassword,strCity,strMobile,strCountyCode,strProfileImage,strLicense,strArcaProfile,strVehicleReg,strInsurnce,strComingfrom,strFBID,strGoogleID,strSelectedCategory,strCommercial;

    Validator validator;

    Spinner spinnerCategory;

    @AfterViews
    void signUpImage()
    {

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        Intent i = getIntent();
        strFirstName = i.getStringExtra("FirstName");
        strLastName = i.getStringExtra("LastName");
        strPassword = i.getStringExtra("Password");
        strCity = i.getStringExtra("City");
        strMobile = i.getStringExtra("Mobile");
        strEmail= i.getStringExtra("Email");
        strCountyCode = i.getStringExtra("CountryCode");
        strProfileImage= i.getStringExtra("ProfilePicture");
        strComingfrom= i.getStringExtra("Comingfrom");
        strFBID= i.getStringExtra("FbID");
        strGoogleID= i.getStringExtra("GoogleID");
        strNickName = i.getStringExtra("nick_name");
        strreferral = i.getStringExtra("referral");

        System.out.println("strreferral===="+strreferral);
        System.out.println("strVehiclemake===="+strVehiclemake);
        System.out.println("strVehiclemodel===="+strVehiclemodel);
        System.out.println("strVehicleyear===="+strVehicleyear);
        System.out.println("strVehiclemileage===="+strVehiclemileage);

        System.out.println("DOCUMENT PAGEE " + "FNAME==" + strFirstName + " " + "LNAME==>" + strLastName + " " + "MOBILE==" + strMobile + " " + "PASSWORD==" + strPassword + " " + "CITY==" + strCity + " " + "PROFILE PICTUREE" + strProfileImage);
        getCategoryDetails();

        editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();

       getTermsCondition();

        validator = new Validator(this);
        validator.setValidationListener(this);

        spinnerCategory=(Spinner)findViewById(R.id.car_category);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                strSelectedCategory= parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showTermsDialog() {
        dialog = new Dialog(DocUpload_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_terms_and_condition);
        dialog.setCancelable(false);

        //layouts
        Button ok=(Button)dialog.findViewById(R.id.iagree);
        TextView titletxt=(TextView)dialog.findViewById(R.id.termscontent);
        titletxt.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.app_font)));

        CharSequence bulletedList = BulletTextUtil.makeBulletListFromStringArrayResource(10,getApplicationContext(),getResources().getIdentifier("termscontent", "array", getPackageName()));
        if(content!=null){
            content=content.replaceAll("%20"," ").replace("&rsquo;", "'").replaceAll("&amp;","&");
            content=content.replaceAll("%40","\n");
            titletxt.setText(content);
        }

        //setOnclicks

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getCategoryDetails() {

        final String url=Constants.CATEGORY_LIVE_URL + "Settings/getCategory";
        System.out.println("URL is"+url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        carcategory=new String[response.length()+1];
                        for (int i = 0; i < response.length(); i++) {
                            try
                            {
                                strJsonCategory = response.getJSONObject(i);
                                strCarCategory= strJsonCategory.getString("categoryname");
                                Log.d("OUTPUT IS",strCarCategory);
                                carcategory[0]="Select car category";
                                carcategory[i+1]=strCarCategory;
                                System.out.println("CATEGORY"+carcategory[i]);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(DocUpload_Activity.this, R.layout.spinner_item, carcategory);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerCategory.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(),"No Internet. Please Connect.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(),"No Internet. Please Connect.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    @Click(R.id.continuebut)
    void con()
    {
        validator.validate();
    }

    private void googleSignUP() {
        showDialog();
        strFirstName=strFirstName.replaceAll(" ","%20");
        strLastName=strLastName.replaceAll(" ","%20");
        strSelectedCategory=strSelectedCategory.trim().replaceAll(" ","%20");
        //  http://demo.cogzidel.com/arcane_lite/driver/googleSignup/regid/5765/first_name/cogzidel/last_name/c/mobile/73376543212/email/cogzidel_new33@gmrail.com/license/yy.png/insurance/zz.png/google_id/23244
        String url= Constants.LIVEURL+"googleSignup/"+"first_name/"+strFirstName+"/last_name/"+strLastName+"/nick_name/"+strFirstName+"/email/"+strEmail+"/regid/344444444444444"+"/google_id/"+strGoogleID+"/license/"+strLicense+"/insurance/"+strInsurnce+"/ARCA/"+strArcaProfile+"/vehicle_reg/"+strVehicleReg+"/commercial_pic/"+strCommercial+"/category/"+strSelectedCategory+"/profile_pic/"+strProfileImage+"/vehicle_make/"+strVehiclemake+"/vehicle_model/"+strVehiclemodel+"/vehicle_year/"+strVehicleyear+"/vehicle_mileage/"+strVehiclemileage+"/number_plate/"+strnumberplate;

        if(strreferralcode!= null){
            if(!strreferralcode.equals("")) {
                url=url+"/referral_code/"+strreferralcode;
            }
        }
        System.out.println("Driver SignUp URL==>"+url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        dismissDialog();
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject register_status= response.getJSONObject(i);

                                if(register_status.optString("status").matches("Success")) {

                                    if(register_status.optString("_id").isEmpty()){
                                        driverID=register_status.optString("userid");
                                    }else {
                                        driverID=register_status.optString("_id");
                                        saveInFirebase();
                                    }

                                    System.out.println("THR DRIER IS"+driverID);
                                    driverFirstName=register_status.optString("first_name");
                                    driverLastName=register_status.optString("last_name");
                                    driverEmail=register_status.optString("email");
                                    driverMobile=register_status.optString("mobile");
                                    savepreferences();

                                    Intent map=new Intent(getApplicationContext(),Map_Activity.class);
                                    map.putExtra("loginfrom","socialnetwork");
                                    map.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(map);
                                    Toast.makeText(getApplicationContext(),"Registered Successfully",Toast.LENGTH_SHORT).show();
                                }
                                else if(register_status.optString("status_extra").matches("Exist"))
                                {
                                    driverID=register_status.optString("userid");
                                    driverFirstName=register_status.optString("first_name");
                                    driverLastName=register_status.optString("last_name");
                                    driverEmail=register_status.optString("email");
                                    driverMobile=register_status.optString("mobile");
                                    savepreferences();
                                    Intent map=new Intent(getApplicationContext(),Map_Activity.class);
                                    map.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(map);
                                    finish();
                                    Toast.makeText(DocUpload_Activity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                                }
                                else if (register_status.optString("status").equals("Email Already Exists."))
                                {
                                    //stopAnim();
                                    Toast.makeText(DocUpload_Activity.this, "Sorry! Email already exist.", Toast.LENGTH_SHORT).show();
                                }
                                else if (register_status.optString("status").equals("Mobile Number Already Exists.")) {
                                    //stopAnim();
                                    Toast.makeText(DocUpload_Activity.this, "Sorry! Mobile Number already exist.", Toast.LENGTH_SHORT).show();
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
                dismissDialog();
                //protected static final String TAG = null;
                if(error instanceof NoConnectionError) {
                    // stopAnim();
                    Toast.makeText(DocUpload_Activity.this, "No Internet. Please Connect.", Toast.LENGTH_SHORT).show();
                }
                VolleyLog.d("DOCUMENT ACTIVITY", "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void faceBookLogin() {
        showDialog();
        if(strFirstName!=null) {
            strFirstName = strFirstName.replaceAll(" ", "%20");
        } else
            strFirstName = "";
        if(strLastName!=null) {
            strLastName = strLastName.replaceAll(" ", "%20");
        } else
            strLastName = "";
        strSelectedCategory=strSelectedCategory.trim().replaceAll(" ","%20");
        //final String url= Constants.LIVEURL+"fbSignup/"+"first_name/"+strFirstName+"/last_name/"+strLastName+"/email/"+strEmail+"/regid/344444444444444"+"/profile_pic/"+strProfileImage+"/fb_id/"+strFBID+"/license/"+strLicense+"/insurance/"+strInsurnce;
        String url= Constants.LIVEURL+"fbSignup/"+"first_name/"+strFirstName+"/last_name/"+strLastName+"/nick_name/"+strFirstName+"/email/"+strEmail+"/regid/344444444444444"+"/fb_id/"+strFBID+"/license/"+strLicense+"/insurance/"+strInsurnce+"/ARCA/"+strArcaProfile+"/vehicle_reg/"+strVehicleReg+"/commercial_pic/"+strCommercial+"/category/"+strSelectedCategory+"/vehicle_make/"+strVehiclemake+"/vehicle_model/"+strVehiclemodel+"/vehicle_year/"+strVehicleyear+"/vehicle_mileage/"+strVehiclemileage+"/number_plate/"+strnumberplate;
        if(strreferralcode != null){
            if(!strreferralcode.equals(""))
            {
                url=url+"/referral_code/"+strreferralcode;
            }
        }
        System.out.println("Driver SignUp URL==>"+url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        dismissDialog();
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject register_status= response.getJSONObject(i);

                                if(register_status.optString("status").matches("Success"))
                                {

                                    if(register_status.optString("_id").isEmpty()){

                                        driverID=register_status.optString("userid");
                                    }else {
                                        driverID=register_status.optString("_id");
                                        saveInFirebase();
                                    }

                                    System.out.println("Uswer ID"+driverID);
                                    driverFirstName=register_status.optString("first_name");
                                    System.out.println("driverFirstName ID"+driverFirstName);

                                    driverLastName=register_status.optString("last_name");
                                    driverEmail=register_status.optString("email");
                                    driverMobile=register_status.optString("mobile");
                                    savepreferences();

                                    Intent map=new Intent(getApplicationContext(),Map_Activity.class);
                                    map.putExtra("loginfrom","socialnetwork");
                                    map.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(map);
                                    finish();
                                    Toast.makeText(getApplicationContext(),"Registered Successfully",Toast.LENGTH_SHORT).show();
                                }
                                else if (register_status.optString("status").equals("Email Already Exists."))
                                {
                                    //stopAnim();
                                    Toast.makeText(DocUpload_Activity.this, "Sorry! Email already exist.", Toast.LENGTH_SHORT).show();
                                }
                                else if (register_status.optString("status").equals("Mobile Number Already Exists.")) {
                                    //stopAnim();
                                    Toast.makeText(DocUpload_Activity.this, "Sorry! Mobile Number already exist.", Toast.LENGTH_SHORT).show();
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
                dismissDialog();
                if(error instanceof NoConnectionError) {
                    // stopAnim();
                    Toast.makeText(DocUpload_Activity.this, "No Internet. Please Connect.", Toast.LENGTH_SHORT).show();
                }
                VolleyLog.d("DOCUMENT ACTIVITY", "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void loginSuccess() {
        showDialog();
        System.out.println("Number of passenger in DocUpload"+strNumOfPassenger);
        strFirstName=strFirstName.replaceAll(" ","%20");
        strLastName=strLastName.replaceAll(" ","%20");
        strSelectedCategory=strSelectedCategory.trim().replaceAll(" ","%20");

        String url= Constants.LIVEURL+"signUp/"+"first_name/"+strFirstName+"/last_name/"+strLastName+"/nick_name/"+strNickName+"/mobile/"+strMobile+"/country_code/"+strCountyCode+"/password/"+strPassword+"/city/"+strCity+"/email/"+strEmail+"/regid/344444444444444"+"/profile_pic/"+strProfileImage+"/license/"+strLicense+"/insurance/"+strInsurnce+"/ARCA/"+strArcaProfile+"/vehicle_reg/"+strVehicleReg+"/commercial_pic/"+strCommercial+"/category/"+strSelectedCategory+"/passenger_count/"+strNumOfPassenger+"/vehicle_make/"+strVehiclemake+"/vehicle_model/"+strVehiclemodel+"/vehicle_year/"+strVehicleyear+"/vehicle_mileage/"+strVehiclemileage+"/number_plate/"+strnumberplate;

        if(strreferralcode != null){
            if(!strreferralcode.equals("")) {
                url = url + "/referral_code/" + strreferralcode;
            }
        }
        System.out.println("Driver SignUp URL==>"+url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        dismissDialog();
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject register_status= response.getJSONObject(i);

                                String status = register_status.optString("status");

                                if(status.matches("Success"))
                                {
                                    driverID=register_status.optString("userid");
                                    driverFirstName=register_status.optString("first_name");
                                    driverLastName=register_status.optString("last_name");
                                    driverNickName=register_status.optString("nick_name");
                                    driverEmail=register_status.optString("email");
                                    driverMobile=register_status.optString("mobile");
                                    savepreferences();
                                    saveInFirebase();
                                    Intent map=new Intent(getApplicationContext(),Map_Activity.class);
                                    map.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(map);
                                    finish();

                                    Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else if(status.matches("Fail")){

                                    String statusMessage = register_status.optString("message");

                                    if (statusMessage.matches("Email already exists"))
                                    {
                                        //stopAnim();
                                        Toast.makeText(DocUpload_Activity.this, "Sorry! Email already exist.", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (statusMessage.matches("Mobile number already exists")) {
                                        //stopAnim();
                                        Toast.makeText(DocUpload_Activity.this, "Sorry! Mobile Number already exist.", Toast.LENGTH_SHORT).show();
                                    }else {
                                        //stopAnim();
                                        Toast.makeText(DocUpload_Activity.this, "Sorry! Email and Mobile Number already exist.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //protected static final String TAG = null;
                dismissDialog();
                if(error instanceof NoConnectionError) {
                    // stopAnim();
                    Toast.makeText(DocUpload_Activity.this, "No Internet. Please Connect.", Toast.LENGTH_SHORT).show();
                }
                VolleyLog.d("DOCUMENT ACTIVITY", "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    @Click({R.id.idproofImg})
    void idproofUpload(){

        count=0;
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
         builder.setMessage(getString(R.string.select_id));
        //builder.setMessage(getString(R.string.carlicenseplate));

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {



            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (DocUpload_Activity.this.checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        DocUpload_Activity.this.requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 5);
                    }
                } else {
                    start_camera();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.gallery),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });

        builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }

    private void start_camera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image File name");
        mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Click({R.id.txtidproof})
    void txtidproofUpload(){

        count=0;
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
         builder.setMessage(getString(R.string.select_id));
        //builder.setMessage(getString(R.string.carlicenseplate));

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {



            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (DocUpload_Activity.this.checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        DocUpload_Activity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
                    }
                } else {
                    start_camera();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.gallery),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });

        builder.setPositiveButton(getString(R.string.close),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }

    @Click({R.id.txtidlicence})
    void txtlicenceUpload(){

        count=1;
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(getString(R.string.licence));

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {



            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (DocUpload_Activity.this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        DocUpload_Activity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
                    }
                } else {
                    start_camera();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.gallery),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });

        builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }

    @Click({R.id.arcaprofile})
    void licenceUpload(){

        count=2;
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(getString(R.string.arcaprofile));

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {



            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (DocUpload_Activity.this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        DocUpload_Activity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
                    }
                } else {
                    start_camera();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.gallery),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });

        builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }

    @Click({R.id.txtarcaprofile})
    void licenceplateUpload(){

        count=2;
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
        //  builder.setMessage(getString(R.string.licence));
        builder.setMessage(getString(R.string.arcaprofile));

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {



            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (DocUpload_Activity.this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        DocUpload_Activity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
                    }
                } else {
                    start_camera();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.gallery),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });

        builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }

    @Click({R.id.licenceImg})
    void licence(){

        count=1;
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
        // builder.setMessage(getString(R.string.select_id));
        builder.setMessage(getString(R.string.carlicenseplate));

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {



            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (DocUpload_Activity.this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        DocUpload_Activity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
                    }
                } else {
                    start_camera();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.gallery),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });

        builder.setPositiveButton(getString(R.string.close),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }

    @Click({R.id.commercial})
    void commercialreg(){

        count=4;
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
        //  builder.setMessage(getString(R.string.licence));
        builder.setMessage(getString(R.string.commercialreg));

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {



            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (DocUpload_Activity.this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        DocUpload_Activity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
                    }
                } else {
                    start_camera();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.gallery),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });

        builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }

    @Click({R.id.txtcommercial})
    void txtcommercialreg(){
        count=4;
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
        // builder.setMessage(getString(R.string.select_id));
        builder.setMessage(getString(R.string.commercialreg));

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {



            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (DocUpload_Activity.this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        DocUpload_Activity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
                    }
                } else {
                    start_camera();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.gallery),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });

        builder.setPositiveButton(getString(R.string.close),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });


        builder.show();
    }

    @Click({R.id.licenceplateImg1})
    void vehiclereg(){

        count=3;
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
        //  builder.setMessage(getString(R.string.licence));
        builder.setMessage(getString(R.string.vehiclereg));

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {



            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (DocUpload_Activity.this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        DocUpload_Activity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
                    }
                } else {
                    start_camera();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.gallery),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });

        builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The Permission is granted to you... Continue your left job...
                start_camera();
            } else {
                Toast.makeText(DocUpload_Activity.this, "Please provide permission to use camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Click({R.id.txtlicenceplate1})
    void txtVehicledoc(){

        count=3;
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
        // builder.setMessage(getString(R.string.select_id));
        builder.setMessage(getString(R.string.vehiclereg));

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {



            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (DocUpload_Activity.this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        DocUpload_Activity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
                    }
                } else {
                    start_camera();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.gallery),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });


        builder.setPositiveButton(getString(R.string.close),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            picturePath= getRealPathFromURI(mCapturedImageURI);

            if (count == 0) {
                idproof.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(idproof);

            }else   if (count == 1){
                licence.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(licence);
            }
            else if (count == 2){

                arcaprofileimg.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(arcaprofileimg);

            }
            else if (count == 3){
                vehicledoc.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(vehicledoc);
            }
            else {
                commercialImg.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(commercialImg);
            }
            new ImageuploadTask(this).execute();
        }

        if (requestCode == 100 && resultCode == Activity.RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            if (Build.VERSION.SDK_INT >= 19) {
                if (selectedImage != null && !selectedImage.toString().equals("null")) {
                    System.out.println("greater 19:" + "kitkat");
                    picturePath = getImagePath(selectedImage);
                    System.out.println("mSelectedFissslssePath res" + picturePath);

                } else {
                    System.out.println("greater 19:" + "not kitkat");
                    picturePath = getPathOfImage(selectedImage);
                    System.out.println("mSelectedFissslePath res" + picturePath);
                }
            }


           /* if (selectedImage != null && !selectedImage.toString().equals("null")) {
                picturePath = getRealPathFromURI(selectedImage);
            } else {
                picturePath = "";
            }
*/
            System.out.println("Request Code+requestCode" + "Result Code" + resultCode + "data" + data);
            if (count == 0) {
                System.out.println("The count is Zero " + count);

                // Set the Image in ImageView after decoding the String
                idproof.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(idproof);
                new ImageuploadTask(this).execute();

            } else if (count == 1){

                // Set the Image in ImageView after decoding the String
                licence.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(licence);

                new ImageuploadTask(this).execute();
            }
            else if (count == 2){

                // Set the Image in ImageView after decoding the String
                arcaprofileimg.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(arcaprofileimg);

                new ImageuploadTask(this).execute();
            }
            else if(count==3){

                // Set the Image in ImageView after decoding the String
                vehicledoc.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(vehicledoc);

                new ImageuploadTask(this).execute();
            }
             else{

                // Set the Image in ImageView after decoding the String
                commercialImg.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(commercialImg);

                new ImageuploadTask(this).execute();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCapturedImageURI == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Image File name");
            mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    // Save the activity state when it's going to stop.
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelable("picUri", mCapturedImageURI);
        super.onSaveInstanceState(outState);
    }

    // Recover the saved state when the activity is recreated.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mCapturedImageURI = savedInstanceState.getParcelable("picUri");
    }

    public String getRealPathFromURI(Uri contentUri) {

        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        catch (Exception e)
        {
            return contentUri.getPath();
        }
    }

    @Override
    public void onValidationSucceeded() {

        if (licsImg == null)
        {
            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(getString(R.string.attach_license));

            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });
            builder.show();

        } else if (idImg == null) {

            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(getString(R.string.attach_id_proof));

            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });

            builder.show();

        } else if (arcaImg == null) {

            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(getString(R.string.attach_arca_proof));

            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });

            builder.show();
        }
        else if (vehicledocimg == null) {

            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(getString(R.string.vehicle_reg_proof));

            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });

            builder.show();
        }
        else if (commercialImgStr == null) {

            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(getString(R.string.commercial_id_proof));

            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });
            builder.show();
        }

        else if(strSelectedCategory == null) {

            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(DocUpload_Activity.this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(getString(R.string.select_category));

            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });
            builder.show();

        } else if(numberofpass.getText().toString().trim().length() == 0) {
            numberofpass.setError("Enter number of passengers");
        }
        else if(!tandc.isChecked()){
            Toast.makeText(DocUpload_Activity.this, "Agree terms and conditions", Toast.LENGTH_SHORT).show();
        }
        else {
            numberofpass.setError(null);
            strNumOfPassenger = numberofpass.getText().toString().trim();

            strVehiclemake = edtVehicleMake.getText().toString();
            strVehiclemodel = edtVehicleModel.getText().toString();
            strVehicleyear = edtVehicleYear.getText().toString();
            strVehiclemileage = edtVehicleMileage.getText().toString();

            strreferralcode = edtReferralcode.getText().toString().trim();
            strnumberplate = editVehiclenumberplate.getText().toString().trim();
            strnumberplate = strnumberplate .replaceAll(" ","%20");

            strVehiclemake = strVehiclemake.replaceAll(" ", "%20");
            strVehiclemodel = strVehiclemodel.replaceAll(" ", "%20");

            if (!strreferralcode.equals("")) {

                try {
                    byte[] encoded = Base64.encode(strreferralcode.getBytes("UTF-8"), Base64.DEFAULT);
                    strreferralcode = new String(encoded, "UTF-8");
                    strreferralcode = strreferralcode.replaceAll("=","").trim();
                    System.out.println("Encoding UTF" + strreferralcode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                validatereferral(strreferralcode);
                System.out.println("enter the condition");

            } else if (strComingfrom != null) {

                System.out.println("enter the condition two");

                if (strComingfrom.matches("facebook")) {
                    faceBookLogin();
                } else if (strComingfrom.matches("google")) {
                    googleSignUP();
                }
            } else {
                loginSuccess();
            }
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ImageuploadTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;
        private DocUpload_Activity activity;

        ImageuploadTask(DocUpload_Activity activity) {

            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

        private Context context;

        protected void onPreExecute() {

            dialog = new ProgressDialog(context);
            dialog.setMessage("Uploading...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (dialog != null && dialog.isShowing())
            {
                if(!activity.isFinishing() && !activity.isDestroyed()){
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            try {
                // ... processing ...
                Upload_Server();
                return true;
            } catch (Exception e){
                Log.e("Schedule", "UpdateSchedule failed", e);
                return false;
            }
        }
    }

    protected void Upload_Server() {

        System.out.println("After call progress");
        try{

            Log.e("Image Upload", "Inside Upload");

            HttpURLConnection connection;
            DataOutputStream outputStream;

            String pathToOurFile = picturePath;
            //	  String pathToOurFile1 = imagepathcam;

            System.out.println("Before Image Upload"+picturePath);

            String urlServer=Constants.LIVEURL+"imageUpload";
            System.out.println("URL SETVER"+urlServer);
            System.out.println("After Image Upload"+picturePath);
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;

            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));
            //  FileInputStream fileInputStream1 = new FileInputStream(new File(pathToOurFile1));

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();
            System.out.println("URL is "+url);
            System.out.println("connection is "+connection);
            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setChunkedStreamingMode(1024);
            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            outputStream = new DataOutputStream( connection.getOutputStream() );
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            //int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();


            System.out.println("image"+serverResponseMessage);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            DataInputStream inputStream1;
            inputStream1 = new DataInputStream (connection.getInputStream());
            String str;
            String Str1_imageurl="";

            while ((  str = inputStream1.readLine()) != null)
            {
                Log.e("Debug","Server Response "+str);

                Str1_imageurl = str;
                Log.e("Debug","Server Response String imageurl"+str);
            }
            inputStream1.close();
            System.out.println("image url"+Str1_imageurl);

            //get the image url and store
            if(count==0)
            {
                licsImg=Str1_imageurl.trim();
                System.out.println("Licence==="+licsImg);
                JSONArray array = new JSONArray(licsImg);
                JSONObject jsonObj  = array.getJSONObject(0);
                System.out.println("image name"+jsonObj.getString("image_name"));
                strLicense=jsonObj.optString("image_name");
                System.out.println("License===>"+strLicense);
            }
            else if(count==1){
                idImg=Str1_imageurl.trim();
                System.out.println("Insurance===="+idImg);
                JSONArray array = new JSONArray(idImg);
                JSONObject jsonObj  = array.getJSONObject(0);
                System.out.println("image name"+jsonObj.getString("image_name"));
                strInsurnce=jsonObj.optString("image_name");
                System.out.println("Insurenace===>"+strInsurnce);
            }
            else if(count==2){
                arcaImg=Str1_imageurl.trim();
                System.out.println("Insurance===="+arcaImg);
                JSONArray array = new JSONArray(arcaImg);
                JSONObject jsonObj  = array.getJSONObject(0);
                System.out.println("image name"+jsonObj.getString("image_name"));
                strArcaProfile=jsonObj.optString("image_name");
                System.out.println("ARCAPROFILE===>"+strArcaProfile);
            }
            else if(count==3){
                vehicledocimg=Str1_imageurl.trim();
                System.out.println("Insurance===="+vehicledocimg);
                JSONArray array = new JSONArray(vehicledocimg);
                JSONObject jsonObj  = array.getJSONObject(0);
                System.out.println("image name"+jsonObj.getString("image_name"));
                strVehicleReg=jsonObj.optString("image_name");
                System.out.println("VEHICLEREG===>"+strVehicleReg);
            }
            else{
                commercialImgStr=Str1_imageurl.trim();
                System.out.println("commercialImgStr===="+commercialImgStr);
                JSONArray array = new JSONArray(commercialImgStr);
                JSONObject jsonObj  = array.getJSONObject(0);
                System.out.println("image name"+jsonObj.getString("image_name"));
                strCommercial=jsonObj.optString("image_name");
                System.out.println("COMMERCIALPICURL===>"+strCommercial);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void savepreferences() {

        editor.putString("driverid", driverID);
        editor.putString("drivername", driverFirstName);
        editor.putString("driverphonenum", driverMobile);
        editor.putString("carcategory", strSelectedCategory);
        editor.apply();
    }

    public void saveInFirebase() {

        if(driverID!=null && !driverID.isEmpty()) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID);
            Map<String, Object> updates = new HashMap<>();
            updates.put("name",driverFirstName);
            updates.put("proof_status","Pending");    //proofstatus
            updates.put("online_status", "0");    //onlinestatus

            Map<String, Object> updateaccept= new HashMap<>();
            updateaccept.put("status","");
            updateaccept.put("trip_id","");
            updates.put("accept",updateaccept);

            Map<String, Object> updaterequest= new HashMap<>();
            updaterequest.put("req_id","");
            updaterequest.put("status","");
            updates.put("request",updaterequest);

            ref.setValue(updates, new DatabaseReference.CompletionListener()
            {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    System.out.println("DATA SAVED SUCCESSFULLY");
                    if(databaseError!=null){
                        System.out.println("DATA SAVED SUCCESSFULLY");
                    }
                }
            });
        }
    }

    public void showDialog() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public void dismissDialog() {

        if(progressDialog!=null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
            progressDialog=null;
        }
    }

    private void validatereferral(String referralcode){

        showDialog();

        final String url = Constants.LIVEURL + "refrel_code/code/"+referralcode;
        System.out.println("EmailExistURL==>"+url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                dismissDialog();
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        referralStatus = jsonObject.optString("status");

                        if(referralStatus.equals("Success")){
                            if(strComingfrom!=null) {

                                if(strComingfrom.matches("facebook"))
                                {
                                    faceBookLogin();
                                }
                                else if(strComingfrom.matches("google"))
                                {
                                    googleSignUP();
                                }
                            }
                            else
                            {
                                loginSuccess();
                            }
                        } else {
                            edtReferralcode.setError(getResources().getString(R.string.referral_invalid));
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
                if (volleyError instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(), "No Internet, Please Connect.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    private void getTermsCondition() {
        final String url = Constants.CATEGORY_LIVE_URL + "Settings/termsconditions";
        System.out.println("GetTermsURL==>" + url);
        final JsonArrayRequest infoReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        System.out.println("Response from GetTerms==>" + jsonObject);
                        content = jsonObject.optString("value");

                    } catch (JSONException | NullPointerException | ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                System.out.println("The ERror in url"+volleyError);
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
                if (volleyError instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });

        infoReq.setRetryPolicy(new DefaultRetryPolicy(5000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(infoReq);
    }

    public String getImagePath(Uri uri) {
        String path = "";
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            System.out.println("path111:" + path);
            cursor.close();
        } catch (Exception e) {

        }
        return path;
    }

    private String getPathOfImage(Uri uri) {
        String wholeID = DocumentsContract.getDocumentId(uri);

        System.out.println("WholeId:" + wholeID);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        System.out.println("File Path1:" + filePath);
        cursor.close();
        return filePath;
    }

}