package com.tommy.rider;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Token;
import com.tommy.rider.adapter.Constants;
import com.tommy.rider.adapter.FontChangeCrawler;
import com.tommy.rider.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.tommy.rider.R.id.Submit;

@EActivity(R.layout.activity_bank_details)
public class BankDetails extends AppCompatActivity implements Validator.ValidationListener, com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE = 100;
    public String accname, bankname, strfirstname, strlastname, routings, branch_code, accounts, pin_code, billaddress, riderUpdateURL, status, message, stripeToken, Test_ApiKey, Live_ApiKey, is_live_stripe, stripeKey;
    String userID = null;
    boolean isValue = false, isImage = false, isImageback = false;
    String side = "";
    Validator validator;
    ProgressDialog progressDialog;
    String picturePath, profImage, profileImageNew = "null", strdateofbirt, strSecurityNumber;
    String stripdoc = "null", stripdocback = "null";
    com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd;
    @NotEmpty(message = "Enter First Name")
    @ViewById(R.id.first_name)
    EditText firstName;
    @NotEmpty(message = "Enter Last Name")
    @ViewById(R.id.last_name)
    EditText lastName;
    @NotEmpty(message = "Enter Account Name")
    @ViewById(R.id.acc_name)
    EditText acc_name;
    @NotEmpty(message = "Enter Bank Name")
    @ViewById(R.id.Bank_name)
    EditText Bank_name;
    @Length(max = 4, message = "Enter 4 digit Bank Code")
    @NotEmpty(message = "Enter Bank Code")
    @ViewById(R.id.bankcode)
    EditText routing;
    @Length(max = 3, message = "Enter 3 digit Branch Code")
    @NotEmpty(message = "Enter Branch Code")
    @ViewById(R.id.branchcode)
    EditText branchcode;
    //@Length(max =6, message = "Enter 6 digit pin Code")
    @NotEmpty(message = "Enter Postal Code")
    @ViewById(R.id.pincode)
    EditText pincode;
    @NotEmpty(message = "Enter your account number")
    @Length(max = 15, message = "Enter 15 digit Account No")
    @ViewById(R.id.account)
    EditText account;
    @NotEmpty(message = "Enter Billing Address")
    @ViewById(R.id.billingaddress)
    EditText billingaddress;
    @ViewById(R.id.Submit)
    TextView submitButton;
    /*@NotEmpty(message = "Enter Address")
    @ViewById(R.id.cityaddress)
    EditText cityaddress;*/
    @NotEmpty(message = "Enter Date of Birth")
    @ViewById(R.id.date_birth)
    EditText editdate;
    //@Length(min =9, message = "Enter 9 digit SSN Social Security Number")
    @NotEmpty(message = "Enter your NRIC Number")
    @ViewById(R.id.social_number)
    EditText social_number;
    @ViewById(R.id.profileImage)
    ImageView edtProfileImage;
    @ViewById(R.id.profileImageback)
    ImageView edtProfileImageback;
    private Uri fileUri;

    @Click(R.id.back)
    void back() {
        finish();
    }

    @Click(Submit)
    void con() {
        validator.validate();
    }

    @Click(R.id.date_birth)
    void date() {

        Calendar minDate = Calendar.getInstance();
        //  DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(RideLater.this, getcalendar.get(Calendar.YEAR), getcalendar.get(Calendar.MONTH), getcalendar.get(Calendar.DAY_OF_MONTH));
        minDate.add(Calendar.DATE, 0);
        dpd.setMaxDate(minDate);
        dpd.show(getFragmentManager(), "datePicker");
        //dpd.show(getFragmentManager(),"datepicker");
    }

    @Click(R.id.profileImage)
    public void updateProfileImage() {
        side = "front";
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(BankDetails.this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage("Add Bank Document Front Image");

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (BankDetails.this.checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        BankDetails.this.requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 5);
                    }
                } else {
                    start_camera();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.gallery), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, MEDIA_TYPE_IMAGE);
            }
        });

        builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
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
                Toast.makeText(BankDetails.this, "Please provide permission to use camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void start_camera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image File name");
        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE);
    }

    @Click(R.id.profileImageback)
    public void profileback() {
        side = "back";
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(BankDetails.this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage("Add Bank Document Back Image");

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (BankDetails.this.checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        start_camera();
                    } else {
                        dialog.cancel();
                        BankDetails.this.requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 5);
                    }
                } else {
                    start_camera();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.gallery), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, MEDIA_TYPE_IMAGE);
            }
        });

        builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Click(R.id.delete)
    void delete() {
        if (submitButton.getText().toString().equals("Update") && (!isImage || !isImageback)) {
            Toast.makeText(this, "Loading... Please delete after loaded.", Toast.LENGTH_SHORT).show();
        } else {
            if (!isValue) {
                Toast.makeText(this, "You didn't submit any details", Toast.LENGTH_SHORT).show();
            } else {
                sweetDialog();
            }
        }
    }

    @AfterViews
    void create() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        validator = new Validator(this);
        validator.setValidationListener(this);
        //get Bank Details if Updated
        getBankDetails();
        getKeys();

        final Calendar now = Calendar.getInstance();
        dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
    }


    @Override
    public void onValidationSucceeded() {
        // String accname,bankname,routings,accounts,billaddress,address;

        accname = acc_name.getText().toString().trim();
        bankname = Bank_name.getText().toString().trim();
        strfirstname = firstName.getText().toString().trim();
        strlastname = lastName.getText().toString().trim();
        routings = routing.getText().toString().trim();
        branch_code = branchcode.getText().toString().trim();
        accounts = account.getText().toString().trim();
        pin_code = pincode.getText().toString().trim();
        billaddress = billingaddress.getText().toString().trim();
        strSecurityNumber = social_number.getText().toString().trim();
        strdateofbirt = editdate.getText().toString().trim();
        if (!Bank_name.getText().toString().matches("[a-zA-z]+([ '-][a-zA-Z]+)*")) {
            Bank_name.setError("Enter Valid Bank Name");

        } else if ((stripdoc == null || stripdoc.equals("null")) && (stripdocback == null || stripdocback.equals("null"))) {
            Toast.makeText(BankDetails.this, "Please upload your NRIC document", Toast.LENGTH_SHORT).show();
        } else if (stripdoc == null || stripdoc.equals("null")) {
            Toast.makeText(BankDetails.this, "Please upload your front side document", Toast.LENGTH_SHORT).show();
        } else if (stripdocback == null || stripdocback.equals("null")) {
            Toast.makeText(BankDetails.this, "Please upload your back side document", Toast.LENGTH_SHORT).show();
        } else {
            createBankToken();
        }
    }

    public void updateDetails() {

        accname = accname.replaceAll(" ", "%20");
        strfirstname = strfirstname.replaceAll(" ", "%20");
        strlastname = strlastname.replaceAll(" ", "%20");
        bankname = bankname.replaceAll(" ", "%20");
        accounts = accounts.replaceAll(" ", "%20");
        pin_code = pin_code.replaceAll(" ", "%20");
        try {
            byte[] encoded = Base64.encode(billaddress.getBytes("UTF-8"), Base64.DEFAULT);
            billaddress = new String(encoded, "UTF-8");
            billaddress = billaddress.replaceAll("=", "").trim();
            LogUtils.i("Encoding UTF" + billaddress);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //billaddress = billaddress.replaceAll(" ", "%20");

        //old format
        //riderUpdateURL = Constants.LIVE_URL + "addBankDetails/rider_id/" + userID + "/account_name/" + accname + "/bank_name/" + bankname + "/routing_number/" + routings + "/account_number/" + accounts + "/billing/" + billaddress + "/payment_mode/stripe/token/"+stripeToken+"/dob/"+strdateofbirt+"/ssn/"+strSecurityNumber+"/stripe_doc/"+profileImageNew;

        //New format changed
        //http://54.172.2.238/driver/addBankDetails?driver_id=58e351ff192d2eb64ca5bb36&account_name=Hira&bank_name=Baroda&routing_number=110000000&account_number=000123456789&billing=21/2-1,Anna%20nagar,Madurai.&payment_mode=stripe&token=btok_1AAxqdBQilqnldByKtnKz1rf&dob=21-04-2017&ssn=985857458&stripe_doc=9ef2dcfda1474df0bc1a7f4f6d47632b.jpg

        //riderUpdateURL = Constants.LIVE_URL + "addBankDetails?rider_id=" + userID + "&account_name=" + accname + "&bank_name=" + bankname + "&routing_number=" + routings + "&account_number=" + accounts + "&billing=" + billaddress + "&payment_mode=stripe&token="+stripeToken+"&dob="+strdateofbirt+"&ssn="+strSecurityNumber+"&stripe_doc="+profileImageNew;
        riderUpdateURL = Constants.LIVE_URL + "addBankDetails/rider_id/" + userID + "/first_name/" + strfirstname + "/last_name/" + strlastname + "/account_name/" + accname + "/bank_name/" + bankname + "/routing_number/" + routings + "/account_number/" + accounts + "/billing/" + billaddress + "/postal/" + pin_code + "/payment_mode/stripe/token/" + stripeToken + "/dob/" + strdateofbirt + "/ssn/" + strSecurityNumber + "/stripe_doc/" + stripdoc + "/stripe_doc_back/" + stripdocback;

        LogUtils.i("bankDetailsURL==>" + riderUpdateURL);
        showDialog();
        final JsonArrayRequest signUpReq = new JsonArrayRequest(riderUpdateURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                dismissDialog();

                LogUtils.i("bankDetailsURL response==>" + response);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");
                        message = jsonObject.optString("message");
                        if (status.equals("Success")) {
                            Toast.makeText(BankDetails.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BankDetails.this, MapActivity.class);
                            startActivity(intent);
                            finish();
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
                LogUtils.i("Exception" + volleyError);
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    public void getBankDetails() {

        showDialog();
        riderUpdateURL = Constants.LIVE_URL + "getBankDetails/rider_id/" + userID;
        LogUtils.i("bankDetailsURL==>" + riderUpdateURL);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(riderUpdateURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                dismissDialog();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");

                        if (status.matches("Success")) {

                            isValue = true;
                            String accName = jsonObject.optString("account_name");
                            String firstname = jsonObject.optString("first_name");
                            String lastname = jsonObject.optString("last_name");
                            String bank_name = jsonObject.optString("bank_name");
                            String routing_no = jsonObject.optString("routing_number");
                            String acc_no = jsonObject.optString("account_number");
                            String billing_address = jsonObject.optString("billing");
                            String pin_code = jsonObject.optString("postal");
                            strdateofbirt = jsonObject.optString("dob");
                            strSecurityNumber = jsonObject.optString("ssn");
                            stripdoc = jsonObject.optString("stripe_doc");
                            stripdocback = jsonObject.optString("stripe_doc_back");
                            submitButton.setText("Update");
                            //firstname
                            if (firstname != null) {
                                firstName.setText(firstname.replaceAll("%20", " "));
                                firstName.setSelection(firstName.length());
                            }

                            //lastname
                            if (lastname != null) {
                                lastName.setText(lastname.replaceAll("%20", " "));
                                lastName.setSelection(lastName.length());
                            }

                            //Account Name
                            if (accName != null) {
                                acc_name.setText(accName.replaceAll("%20", " "));
                                acc_name.setSelection(acc_name.length());
                            }
                            //Bank Name
                            if (bank_name != null) {
                                Bank_name.setText(bank_name.replaceAll("%20", " "));
                            }
                            //Routing No
                            if (routing_no != null) {
                                String[] bankArray = routing_no.split("-");
                                String bankcode = bankArray[0];
                                String branchcode1 = bankArray[1];

                                routing.setText(bankcode.replaceAll("%20", " "));
                                branchcode.setText(branchcode1.replaceAll("%20", " "));
                            }
                            //Account No
                            if (acc_no != null) {
                                account.setText(acc_no.replaceAll("%20", " "));
                            }

                            //pincode
                            if (pin_code != null) {
                                pincode.setText(pin_code.replaceAll("%20", " "));
                            }
                            //Address
                            if (billing_address != null) {
                                billingaddress.setText(billing_address.replaceAll("%20", " "));
                            }
                            if (strdateofbirt != null) {
                                editdate.setText(strdateofbirt);
                            }
                            if (strSecurityNumber != null && !strSecurityNumber.equals("null")) {
                                social_number.setText(strSecurityNumber);
                            }
                            //  Glide.with(getApplicationContext()).placeholder(R.drawable.file_document).load(profileImageNew).skipMemoryCache(true).into(edtProfileImage);
                            Glide.with(BankDetails.this)
                                    .load(stripdoc)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .priority(Priority.IMMEDIATE)
                                    .placeholder(R.drawable.file_document)
                                    .skipMemoryCache(true)
                                    .into(new GlideDrawableImageViewTarget(edtProfileImage) {
                                        @Override
                                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                            super.onResourceReady(resource, animation);
                                            //never called
                                            isImage = true;
                                            LogUtils.i("inside resource ready" + isImage);
                                        }

                                        @Override
                                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                            super.onLoadFailed(e, errorDrawable);
                                            //never called
                                            LogUtils.i("inside resource ready" + isImage + e);
                                        }
                                    });

                            Glide.with(BankDetails.this)
                                    .load(stripdocback)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .placeholder(R.drawable.file_document)
                                    .priority(Priority.IMMEDIATE)
                                    .skipMemoryCache(true)
                                    .into(new GlideDrawableImageViewTarget(edtProfileImageback) {
                                        @Override
                                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                            super.onResourceReady(resource, animation);
                                            //never called
                                            isImageback = true;
                                            LogUtils.i("inside resource ready failed" + isImageback);
                                        }

                                        @Override
                                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                            super.onLoadFailed(e, errorDrawable);
                                            //never called
                                            LogUtils.i("inside resource ready failed" + isImageback + e);
                                        }
                                    });

                            stripdoc = stripdoc.replaceAll(Constants.BASE_URL + "images/", "");
                            LogUtils.i("stipe documentation image front==>" + stripdoc);

                            stripdocback = stripdocback.replaceAll(Constants.BASE_URL + "images/", "");
                            LogUtils.i("stipe documentation image back==>" + stripdocback);
                        } else {
                            isValue = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismissDialog();
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
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
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
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
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void createBankToken() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (stripeKey == null) {
            Stripe.apiKey = "sk_test_PndPOXIAzvkHOqN28jaGO17U";
        } else {
            Stripe.apiKey = stripeKey;
        }

        LogUtils.i("Acc Name firstname lastname" + accname + strfirstname + strlastname);
        routings = routings + "-" + branch_code;

        LogUtils.i("Routing No" + routings);
        LogUtils.i("Acc No" + accounts);

        Map<String, Object> tokenParams = new HashMap<String, Object>();
        Map<String, Object> bank_accountParams = new HashMap<String, Object>();
        bank_accountParams.put("country", "SG");
        bank_accountParams.put("currency", "sgd");
        bank_accountParams.put("account_holder_name", accname);
        bank_accountParams.put("account_holder_type", "individual");
        bank_accountParams.put("routing_number", routings);
        bank_accountParams.put("account_number", accounts);
        tokenParams.put("bank_account", bank_accountParams);

        try {
            Token token = Token.create(tokenParams);
            stripeToken = token.getId();
            LogUtils.i("stripeToken" + stripeToken);
            updateDetails();
        } catch (AuthenticationException e) {
            Toast.makeText(this, "Check your Account/Branch/Bankcode No", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            Toast.makeText(this, "Check your Account/Branch/Bankcode No", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (CardException e) {
            Toast.makeText(this, "Check your Account/Branch/Bankcode No", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (APIException e) {
            Toast.makeText(this, "Check your Account/Branch/Bankcode No", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void getKeys() {

        //http://demo.cogzideltemplates.com/tommy/settings/getdetails
        String url = Constants.CATEGORY_LIVE_URL + "settings/getdetails";
        LogUtils.i(" CATEGOR URL is " + url);

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject signIn_jsonobj = response.getJSONObject(i);

                                Test_ApiKey = signIn_jsonobj.optString("Test_ApiKey");
                                Live_ApiKey = signIn_jsonobj.optString("Live_ApiKey");
                                is_live_stripe = signIn_jsonobj.optString("is_live_stripe");
                                if (is_live_stripe.matches("0")) {

                                    stripeKey = Test_ApiKey;

                                } else {

                                    stripeKey = Live_ApiKey;
                                }

                                LogUtils.i("stripe live==>" + Live_ApiKey + "test live==>" + Test_ApiKey);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE_IMAGE && resultCode == RESULT_OK) {

            picturePath = getRealPathFromURI(fileUri);

            edtProfileImage.setScaleType(ImageView.ScaleType.FIT_XY);
            edtProfileImageback.setScaleType(ImageView.ScaleType.FIT_XY);
            //                edtProfileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
/*

            Glide.with(getApplicationContext()).load(picturePath).asBitmap().error(R.drawable.account_circle_grey).centerCrop().skipMemoryCache(true).into(new BitmapImageViewTarget(edtProfileImage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    edtProfileImage.setImageDrawable(circularBitmapDrawable);
                }
            });
*/

            if (side.equals("front")) {
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(edtProfileImage);
            } else {
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(edtProfileImageback);
            }

            new BankDetails.ImageuploadTask(this).execute();

        } /*else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {*/ else if (requestCode == MEDIA_TYPE_IMAGE && resultCode == RESULT_OK && null != data) {

            //            String single_path = data.getStringExtra("single_path");
            Uri selectedImage = data.getData();
            if (Build.VERSION.SDK_INT >= 19) {
                if (selectedImage != null && !selectedImage.toString().equals("null")) {
                    LogUtils.i("greater 19:" + "kitkat");
                    picturePath = getImagePath(selectedImage);
                    LogUtils.i("mSelectedFissslssePath res" + picturePath);

                } else {
                    LogUtils.i("greater 19:" + "not kitkat");
                    picturePath = getPathOfImage(selectedImage);
                    LogUtils.i("mSelectedFissslePath res" + picturePath);
                }
            }

           /* if (selectedImage != null && !selectedImage.toString().equals("null")) {
                picturePath = getRealPathFromURI(selectedImage);
            } else {
                picturePath = "";
            }*/

            //            edtProfileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
/*

            Glide.with(getApplicationContext()).load(picturePath).asBitmap().error(R.drawable.file_document).centerCrop().skipMemoryCache(true).into(new BitmapImageViewTarget(edtProfileImage) {
                @Override
                protected void setResource(Bitmap resource) {
                   */
/* RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);*//*

                    edtProfileImage.setImageDrawable(circularBitmapDrawable);
                }
            });
*/
            if (side.equals("front")) {
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(edtProfileImage);
            } else {
                Glide.with(getApplicationContext()).load(picturePath).skipMemoryCache(true).into(edtProfileImageback);
            }
            new BankDetails.ImageuploadTask(BankDetails.this).execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (fileUri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Image File name");
            fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    // Save the activity state when it's going to stop.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("picUri", fileUri);
    }

    // Recover the saved state when the activity is recreated.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        fileUri = savedInstanceState.getParcelable("picUri");
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = this.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
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
        editdate.setText(date);
    }

    protected void Upload_Server() {
        try {

            HttpURLConnection connection;
            DataOutputStream outputStream;

            String pathToOurFile = picturePath;
            //	  String pathToOurFile1 = imagepathcam;

            LogUtils.i("Before Image Upload" + picturePath);

            String urlServer = Constants.LIVE_URL_DRIVER + "imageUpload/";
            LogUtils.i("URL SETVER" + urlServer);
            LogUtils.i("After Image Upload" + picturePath);
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;

            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));
            //  FileInputStream fileInputStream1 = new FileInputStream(new File(pathToOurFile1));

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();
            LogUtils.i("URL is " + url);
            LogUtils.i("connection is " + connection);
            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setChunkedStreamingMode(1024);
            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();


            LogUtils.i("image" + serverResponseMessage);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            DataInputStream inputStream1 = null;
            inputStream1 = new DataInputStream(connection.getInputStream());
            String str;
            String Str1_imageurl = "";

            while ((str = inputStream1.readLine()) != null) {
                LogUtils.e("Server Response " + str);

                Str1_imageurl = str;
                LogUtils.e("Server Response String imageurl" + str);
            }
            inputStream1.close();
            LogUtils.i("image url" + Str1_imageurl);

            //get the image url and store
            profImage = Str1_imageurl.trim();
            JSONArray array = new JSONArray(profImage);
            JSONObject jsonObj = array.getJSONObject(0);
            LogUtils.i("image name" + jsonObj.getString("image_name"));

            profileImageNew = jsonObj.optString("image_name");
            if (side.equals("front")) {
                stripdoc = profileImageNew;
                LogUtils.i("Profile Picture Path front" + stripdoc);
            } else if (side.equals("back")) {
                stripdocback = profileImageNew;
                LogUtils.i("Profile Picture Path back" + stripdocback);
            }

            LogUtils.i("Profile Picture Pathhh" + profImage);
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public void sweetDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this bank detail!")
                .setConfirmText("Yes,delete it!")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        deleteBankDetail(sDialog);
                    }
                })
                .show();
    }

    public void deleteBankDetail(final SweetAlertDialog sDialog) {

        String deletedetailurl = Constants.LIVE_URL + "delBankDetails/rider_id/" + userID;
        LogUtils.i("bankdeleteDetailsURL==>" + deletedetailurl);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(deletedetailurl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");
                        if (status.matches("Success")) {
                            isValue = false;
                            acc_name.setText("");
                            firstName.setText("");
                            lastName.setText("");
                            Bank_name.setText("");
                            routing.setText("");
                            branchcode.setText("");
                            account.setText("");
                            pincode.setText("");
                            billingaddress.setText("");
                            social_number.setText("");
                            editdate.setText("");
                            submitButton.setText("Submit");
                            edtProfileImage.setImageResource(0);
                            edtProfileImageback.setImageResource(0);
                            sDialog
                                    .setTitleText("Deleted!")
                                    .setContentText("Your bank detail has been deleted!")
                                    .setConfirmText("OK")
                                    .showCancelButton(false)
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                        }
                    } catch (JSONException | NullPointerException e) {

                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
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
            LogUtils.i("path111:" + path);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private String getPathOfImage(Uri uri) {
        String wholeID = DocumentsContract.getDocumentId(uri);

        LogUtils.i("WholeId:" + wholeID);

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

        LogUtils.i("File Path1:" + filePath);
        cursor.close();
        return filePath;
    }

    private class ImageuploadTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog dialog;
        private BankDetails activity;
        private Context context;

        ImageuploadTask(BankDetails activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

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

            if (dialog != null && dialog.isShowing()) {
                if (!activity.isFinishing() && !activity.isDestroyed()) {
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
            } catch (Exception e) {
                LogUtils.e("Schedule - UpdateSchedule failed " + e);
                return false;
            }
        }
    }
}