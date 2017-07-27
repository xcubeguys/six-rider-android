package com.tommy.rider;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tommy.rider.adapter.Constants;
import com.tommy.rider.adapter.CountryCodeDialog;
import com.tommy.rider.adapter.CountryCodePicker;
import com.tommy.rider.adapter.FontChangeCrawler;
import com.tommy.rider.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_sos)
public class SoSActivity extends AppCompatActivity implements CountryCodePicker.OnCountryChangeListener {


    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Dialog dialog;
    ProgressDialog progressDialog;
    String userID;
    String contact_no1, contact_no2;
    boolean coutrycode1 = false, coutrycode2 = false;
    MaterialEditText inputCountryCode1, inputCountryCode2;
    MaterialEditText inputMobileNumber1, inputMobileNumber2;
    String countrycode1, countrycode2, mobileno1, mobileno2;
    CountryCodePicker ccp;
    String status, message;
    String contact_no1_p, contact_no2_p, contact_no1_country, contact_no2_country;
    ImageButton back;
    TextView cancel, updateContact;
    RelativeLayout cancelLayout, updateLayout;
    List<String> mobileNumbers = new ArrayList<>();


    @AfterViews
    void Create_SoSActivity() {

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        LogUtils.i("UserID in Map" + userID);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        dialog = new Dialog(SoSActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_emg_contacts);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        back = (ImageButton) dialog.findViewById(R.id.backButton);
        cancel = (TextView) dialog.findViewById(R.id.txtCancelpayment);
        updateContact = (TextView) dialog.findViewById(R.id.txtupdate);

        inputCountryCode1 = (MaterialEditText) dialog.findViewById(R.id.countryCode1);
        inputCountryCode2 = (MaterialEditText) dialog.findViewById(R.id.countryCode2);
        ccp = (CountryCodePicker) dialog.findViewById(R.id.ccp);

        inputMobileNumber1 = (MaterialEditText) dialog.findViewById(R.id.mobileNumber1);
        inputMobileNumber2 = (MaterialEditText) dialog.findViewById(R.id.mobileNumber2);

        cancelLayout = (RelativeLayout) dialog.findViewById(R.id.cancel_layout);
        updateLayout = (RelativeLayout) dialog.findViewById(R.id.update_layout);

        ccp.setOnCountryChangeListener(this);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);

        if (Build.VERSION.SDK_INT < 23) {
            //your code here
        } else {
            requestContactPermission();
        }

        updateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateCountryCode(inputCountryCode1, inputMobileNumber1)) {

                }
                if (!validatePhone(inputCountryCode1, inputMobileNumber1)) {

                }
                if (!validateCountryCode(inputCountryCode2, inputMobileNumber2)) {

                }
                if (!validatePhone(inputCountryCode2, inputMobileNumber2)) {

                } else if (!validateUsing_libphonenumber(inputCountryCode1, inputMobileNumber1)) {
                    inputMobileNumber1.setError(getString(R.string.invalid_mobile_number));
                } else if (!validateUsing_libphonenumber(inputCountryCode2, inputMobileNumber2)) {
                    inputMobileNumber2.setError(getString(R.string.invalid_mobile_number));
                } else {
                    countrycode1 = inputCountryCode1.getText().toString().trim();
                    countrycode2 = inputCountryCode2.getText().toString().trim();
                    mobileno1 = inputMobileNumber1.getText().toString().trim();
                    mobileno2 = inputMobileNumber2.getText().toString().trim();

                    callSos();
                }
            }
        });

        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog();

            }
        });
        callSosShow();
    }


    @Click({R.id.settings_layout, R.id.sett_icon, R.id.sett_butt})
    void updateContacts() {

        showPaymentGatewaydialog();
    }

    @Click({R.id.sendsms_layout, R.id.sendbtn, R.id.sendsms_imageview})
    void sendsms() {

        LogUtils.i("new1 =" + contact_no1);
        LogUtils.i("new2 =" + contact_no2);


        if (contact_no1 != null) {
            mobileNumbers.add(contact_no1.trim());
        }

        if (contact_no2 != null) {
            mobileNumbers.add(contact_no2.trim());
        }


        if (mobileNumbers != null) {

            LogUtils.i("in the send loop==>" + mobileNumbers.size());

            if (mobileNumbers.size() > 0) {

                for (int i = 0; i < mobileNumbers.size(); i++) {
                    LogUtils.i("in the for loop");
                    String message = "Emergency Contact in Six";
                    String tempMobileNumber = mobileNumbers.get(i).toString();
                    if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_SMS") == PackageManager.PERMISSION_GRANTED) {
                        sendSMS(tempMobileNumber, message);
                    }
                }
                mobileNumbers.clear();
            } else {

                Toast.makeText(this, "Add Emergency Contact for Sending SMS", Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(this, "Add Emergency Contact for Sending SMS", Toast.LENGTH_SHORT).show();
        }

    }

    @Click(R.id.back)
    void goBack() {

        finish();
    }


    private void showPaymentGatewaydialog() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog();
            }
        });

        inputCountryCode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                coutrycode1 = true;
                coutrycode2 = false;
                CountryCodeDialog.openCountryCodeDialog(ccp);//Open country code dialog
            }
        });
        inputCountryCode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coutrycode1 = false;
                coutrycode2 = true;
                CountryCodeDialog.openCountryCodeDialog(ccp);//Open country code dialog
            }
        });


        dialog.show();

    }


    @Override
    public void onCountrySelected() {


        if (coutrycode1) {

            inputCountryCode1.setText(ccp.getSelectedCountryCodeWithPlus());
            inputMobileNumber1.setError(null);

        } else {

            inputCountryCode2.setText(ccp.getSelectedCountryCodeWithPlus());
            inputMobileNumber2.setError(null);
        }
    }

    private boolean validateCountryCode(MaterialEditText inputCountryCode, MaterialEditText inputMobileNumber) {

        if (inputCountryCode.getText().toString().trim().isEmpty()) {
            inputCountryCode.setError("");
            inputMobileNumber.setError(getString(R.string.enter_country_code));
            return false;
        } else if (inputCountryCode.getText().toString().equals("CC")) {
            inputMobileNumber.setError(getString(R.string.enter_country_code));
            inputCountryCode.setError("");
            return false;
        } else {
            inputCountryCode.setError(null);
        }
        return true;
    }

    private boolean validatePhone(MaterialEditText inputCountryCode, MaterialEditText inputMobileNumber) {
        if (inputMobileNumber.getText().toString().trim().isEmpty()) {
            inputMobileNumber.setError(getString(R.string.enter_mobile_number));
            return false;
        } else if (inputCountryCode.getText().toString().trim().isEmpty()) {
            return false;
        } else if (!inputMobileNumber.getText().toString().trim().isEmpty()) {
            if (inputMobileNumber.getText().toString().substring(0, 1).matches("0")) {
                inputMobileNumber.setError("Enter a valid number");
                return false;
            } else {
                int maxLengthofEditText = 15;
                inputMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengthofEditText)});
                inputMobileNumber.setError(null);
            }
            return true;
        }

        return true;
    }


    private boolean validateUsing_libphonenumber(MaterialEditText inputCountryCode, MaterialEditText inputMobileNumber) {

        String countrycode, mobileNumber;

        if (inputMobileNumber.getText().toString().length() <= 1) {
            return false;
        } else {
            countrycode = inputCountryCode.getText().toString();
            mobileNumber = inputMobileNumber.getText().toString();
            if (validatePhone(inputCountryCode, inputMobileNumber) && validateCountryCode(inputCountryCode, inputMobileNumber)) {
                LogUtils.i("CountryCode==>" + countrycode);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    countrycode = countrycode.replace("+", "");
                }
                LogUtils.i("SDK_VERSION==>" + Build.VERSION.SDK_INT);
                LogUtils.i("SDK_VERSION_RELEASE" + Build.VERSION.RELEASE);
                LogUtils.i("CountryCode1==>" + countrycode);
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countrycode));
                Phonenumber.PhoneNumber phoneNumber = null;

                try {
                    //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
                    phoneNumber = phoneNumberUtil.parse(mobileNumber, isoCode);
                } catch (NumberParseException e) {
                    System.err.println(e);
                }

                boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
                if (isValid) {
                    String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                    return true;
                } else {
                    inputMobileNumber.setError(getString(R.string.enter_a_valid_mobile_number));
                    return false;
                }
            }
            return true;
        }

    }

    private void callSos() {

        //http://demo.cogzideltemplates.com/tommy/rider/addEmergencyDetails/user_id/58b10dbfda71b40f1b8b4560/contact_number1/985741526/contact_number2/987654321/contact_number1_code/+91/contact_number2_code/+1/
        final String url = Constants.LIVE_URL + "addEmergencyDetails/user_id/" + userID + "/contact_number1/" + mobileno1 + "/contact_number2/" + mobileno2 + "/contact_number1_code/" + countrycode1 + "/contact_number2_code/" + countrycode2;
        LogUtils.i("SosUpURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                dialog.dismiss();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");

                        if (status.equals("Success")) {
                            message = jsonObject.optString("message");
                            Toast.makeText(SoSActivity.this, "Your Emergency Contacts uploaded Successfully", Toast.LENGTH_SHORT).show();
                            callSosShow();
                        } else {
                            //Toast.makeText(getApplicationContext(), "update failed",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    private void callSosShow() {

        final String url = Constants.LIVE_URL + "getEmergencyDetails/user_id/" + userID;
        LogUtils.i("SosUpURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");

                        if (status.equals("Success")) {

                            contact_no1 = jsonObject.optString("contact_number1");
                            contact_no2 = jsonObject.optString("contact_number2");
                            contact_no1_country = jsonObject.optString("contact_number1_code");
                            contact_no2_country = jsonObject.optString("contact_number2_code");


                            //contact_no1_country = contact_no1.substring(0, 3);
                            //contact_no2_country = contact_no1.substring(0, 3);
                            LogUtils.i("new1 =" + contact_no1);
                            LogUtils.i("new2 =" + contact_no2);
                            LogUtils.i("contact_no1_country =" + contact_no1_country);
                            LogUtils.i("contact_no1_country2 =" + contact_no2_country);

                           /* contact_no1_p =  contact_no1.substring(contact_no1.indexOf("no") + 4 , contact_no1.length());
                            contact_no2_p =  contact_no2.substring(contact_no2.indexOf("no") + 4 , contact_no2.length());

                            LogUtils.i("contact_no1_p ="+contact_no1_p);
                            LogUtils.i("contact_no1_p ="+contact_no2_p);*/

                            inputCountryCode1.setText(contact_no1_country);
                            inputCountryCode2.setText(contact_no2_country);
                            inputMobileNumber1.setText(contact_no1);
                            inputMobileNumber2.setText(contact_no2);
                            //dialog.dismiss();

                        } else {

//                            Toast.makeText(getApplicationContext(), "update failed",Toast.LENGTH_SHORT).show();
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


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    private void requestContactPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            LogUtils.d("SOSActivity " + "Permission is not granted, requesting");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, 123);
        } else {
            LogUtils.d("SOSActivity " + "Permission is granted");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LogUtils.d("SOSActivity: " + "Permission has been granted");
            } else {
                LogUtils.d("SOSActivity: " + "Permission has been denied or request cancelled");
            }
        }
    }

    private void cancelDialog() {
        if ((contact_no1_country != null) && !contact_no1_country.equals("null")) {
            inputCountryCode1.setText(contact_no1_country);
        }

        if ((contact_no2_country != null) && !contact_no2_country.equals("null")) {
            inputCountryCode2.setText(contact_no2_country);
        }

        if ((contact_no1 != null) && !contact_no1.equals("null")) {
            inputMobileNumber1.setText(contact_no1);
        }

        if ((contact_no2 != null) && !contact_no2.equals("null")) {
            inputMobileNumber2.setText(contact_no2);
        }

        inputMobileNumber2.onEditorAction(EditorInfo.IME_ACTION_DONE);
        inputMobileNumber1.onEditorAction(EditorInfo.IME_ACTION_DONE);

        dialog.cancel();


    }

    private void sendSMS(String phoneNumber, final String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        LogUtils.i("In the function");

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        // ---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {

                LogUtils.i("result code====>" + getResultCode());

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        ContentValues values = new ContentValues();
                        for (int i = 0; i < mobileNumbers.size() - 1; i++) {
                            values.put("address", mobileNumbers.get(i).toString());// txtPhoneNo.getText().toString());
                            values.put("body", message);
                        }
                        getContentResolver().insert(
                                Uri.parse("content://sms/sent"), values);
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {

                LogUtils.i("result code====>" + getResultCode());

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }


}
