package com.tommy.driver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.text.InputFilter;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tommy.driver.adapter.AppController;
import com.tommy.driver.adapter.Constants;
import com.tommy.driver.adapter.FontChangeCrawler;
import com.tommy.driver.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;


@EActivity(R.layout.activity_signup)
public class SignUp_Activity extends MyBaseActivity implements Validator.ValidationListener {

    Boolean statusEmail = false;
    Boolean statusMobile = false;
    Boolean Statusreferral = false;
    ProgressDialog progressDialog;

    @NotEmpty(message = "")
    @Email(message = "Enter a valid email")
    @ViewById(R.id.input_email)
    EditText edtEmail;

    @NotEmpty(message = "Enter first name")
    @ViewById(R.id.input_name_first)
    EditText edtFirstName;

    @NotEmpty(message = "Enter last name")
    @ViewById(R.id.input_name_last)
    EditText edtLastName;

    @NotEmpty(message = "Enter nick name")
    @ViewById(R.id.input_nickname)
    EditText edtNickName;

    @NotEmpty(message = "Enter Mobile Number")
    @ViewById(R.id.input_mobile)
    EditText edtMobile;

    @NotEmpty(message = "")
    @Length(min = 8, message = "Enter a minimum of 8 characters")
    @ViewById(R.id.input_password)
    @Password(message = "")
    EditText edtpassword;

    @NotEmpty(message = "")
    // @Length(min=8, message="Enter a minimum of 8 characters")
    @ConfirmPassword(message = "Password does not match")
    @ViewById(R.id.input_conform_password)
    EditText edtConPassword;

    @NotEmpty(message = "")
    @Length(min = 1, message = "Enter City")
    @ViewById(R.id.input_city)
    EditText edtCity;



    /*@NotEmpty(message = "")
     @Length(min=1, message="Enter number of passengers")
    @ViewById (R.id.numberofpass)
    EditText numberofpass;*/

    @NotEmpty(message = "")
    @ViewById(R.id.input_country_code)
    MaterialEditText userCountryCode;


    Validator validator;
    String strEmail, strFirstName, strLastName, referralStatus, strPassword, strConfirmPassword, strCity, strMobile, strCountyCode, strnickname, strNumOfPassenger;
    CountryCodePicker ccp;


    @Click(R.id.back)
    void back() {
        finish();
    }

    @Click(R.id.signIn)
    void signin() {
        Intent signin = new Intent(this, SigninActivity_.class);
        startActivity(signin);
    }

    @Click(R.id.continueReg)
    void con() {
        validator.validate();
    }


    @Click(R.id.input_country_code)
    void countryCode() {
        CountryCodeDialog.openCountryCodeDialog(ccp);//Open country code dialog
    }


    @AfterViews
    void create() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        ccp = (CountryCodePicker) findViewById(R.id.ccp);

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                userCountryCode.setText(ccp.getSelectedCountryCodeWithPlus());
            }
        });

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public void onValidationSucceeded() {

        strEmail = edtEmail.getText().toString().trim();
        strFirstName = edtFirstName.getText().toString().trim();
        strLastName = edtLastName.getText().toString().trim();
        strPassword = edtpassword.getText().toString().trim();
        strConfirmPassword = edtConPassword.getText().toString().trim();
        strCity = edtCity.getText().toString().trim();
        strMobile = edtMobile.getText().toString();
        strnickname = edtNickName.getText().toString();

        strFirstName = strFirstName.replaceAll(" ", "%20");
        strLastName = strLastName.replaceAll(" ", "%20");
        strCity = strCity.replaceAll(" ", "%20");
        strMobile = strMobile.replaceAll(" ", "%20");
        // strreferralcode=strreferralcode.replaceAll(" ","%20");
        strnickname = strnickname.replaceAll(" ", "%20");
//        strCountyCode=strCountyCode.replaceAll("\\+","%2B");
        strCountyCode = userCountryCode.getText().toString();
        try {
            byte[] encoded = Base64.encode(strPassword.getBytes("UTF-8"), Base64.DEFAULT);
            strPassword = new String(encoded, "UTF-8");
            strPassword = strPassword.replaceAll("=", "").trim();
            LogUtils.i("Encoding UTF" + strPassword);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

       /* if(strMobile.length() < 10 || strMobile.charAt(0) == '0' && strMobile.charAt(1) == '0' && strMobile.charAt(2) == '0')
        {
            edtMobile.setError("Enter a valid number");
        }*/
/*
        if(strVehicleyear==)
*/

        if (!validatePhone()) {

        }
        if (!validateCountryCode()) {

        } else if (!validateUsing_libphonenumber()) {
            edtMobile.setError(getString(R.string.invalid_mobile_number));
        } else {
            //  loginEmail();
            loginPhone();
        }


    }

    private void loginPhone() {

        showDialog();
        final String url = Constants.LIVEURL + "checkEmailPhone/" + "email/" + strEmail + "/mobile/" + strMobile;
        LogUtils.i("Driver SignUp URL==>" + url);

        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        dismissDialog();
                        // Parsing json
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {

                                try {

                                    JSONObject register_jsonobj = response.getJSONObject(i);

                                    if (register_jsonobj.optString("status").matches("Success")) {

                                        Intent editIntent = new Intent(SignUp_Activity.this, ImageUpload_.class);
                                        editIntent.putExtra("FirstName", strFirstName);
                                        editIntent.putExtra("LastName", strLastName);
                                        editIntent.putExtra("Email", strEmail);
                                        editIntent.putExtra("Password", strPassword);
                                        editIntent.putExtra("Mobile", strMobile);
                                        editIntent.putExtra("City", strCity);
                                        editIntent.putExtra("CountryCode", strCountyCode);
                                        editIntent.putExtra("nick_name", strnickname);

//                                        editIntent.putExtra("num_of_passenger",strNumOfPassenger);
                                        startActivity(editIntent);
                                    } else if (register_jsonobj.optString("message").matches("Mobile exist")) {
                                        LogUtils.i("Mobile number Already exists");
                                        edtMobile.setError("Mobile Number already exists");
                                    } else if (register_jsonobj.optString("message").matches("Email exist")) {
                                        LogUtils.i("Email Already exists");
                                        edtEmail.setError("Email already exists");
                                    } else if (register_jsonobj.optString("message").matches("Both Exits")) {
                                        LogUtils.i("Both Already exists");
                                        edtMobile.setError("Mobile Number already exists");
                                        edtEmail.setError("Email already exists");
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissDialog();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    private void loginEmail() {
        //  showDialog();
        final String url = Constants.LIVEURL + "emailExist/" + "email/" + strEmail;
        LogUtils.i("Driver SignUp URL==>" + url);

        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {

                                try {

                                    JSONObject register_jsonobj = response.getJSONObject(i);

                                    if (register_jsonobj.optString("status").matches("Success")) {
                                        // dismissDialog();
                                        statusEmail = true;
                                    } else if (register_jsonobj.optString("status").matches("Fail")) {
                                        // dismissDialog();
                                        edtEmail.setError("Email already exists");
                                        LogUtils.i("Email Already exists");
                                    }

                                } catch (JSONException e) {
                                    // dismissDialog();
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                            // dismissDialog();
                        }
                    }
                });
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(movieReq);
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

    private boolean validateCountryCode() {

        if (userCountryCode.getText().toString().trim().isEmpty()) {
            userCountryCode.setError("");
            edtMobile.setError(getString(R.string.enter_country_code));
            return false;
        } else if (userCountryCode.getText().toString().equals("CC")) {
            edtMobile.setError(getString(R.string.enter_country_code));
            userCountryCode.setError("");
            return false;
        } else {
            userCountryCode.setError(null);
        }
        return true;
    }

    private boolean validatePhone() {

        if (edtMobile.getText().toString().trim().isEmpty()) {
            edtMobile.setError(getString(R.string.enter_mobile_number));
            return false;
        } else if (userCountryCode.getText().toString().trim().isEmpty()) {
            return false;
        } else if (!edtMobile.getText().toString().trim().isEmpty()) {
            if (edtMobile.getText().toString().substring(0, 1).matches("0")) {
                edtMobile.setError("Enter a valid number");
                return false;
            } else {
                int maxLengthofEditText = 15;
                edtMobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengthofEditText)});
                edtMobile.setError(null);
            }
            return true;
        }
        return true;
    }

    private void validatereferral(String referralcode) {

        showDialog();

        final String url = Constants.LIVEURL + "refrel_code/code/" + referralcode;
        LogUtils.i("EmailExistURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                dismissDialog();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        referralStatus = jsonObject.optString("status");

                        if (referralStatus.equals("Success")) {
                            loginPhone();
                        } else {
                            // edtReferralcode.setError(getResources().getString(R.string.referral_invalid));
                            LogUtils.i("inside else");
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
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    private boolean validateUsing_libphonenumber() {
        if (edtMobile.getText().toString().length() <= 1) {
            return false;
        } else if (edtMobile.getText().toString().substring(0, 1).matches("0")) {
            edtMobile.setError("Enter a valid number");
            return false;
        } else {
            strCountyCode = userCountryCode.getText().toString();
            strMobile = edtMobile.getText().toString();
            if (validatePhone() && validateCountryCode()) {
                LogUtils.i("CountryCode==>" + strCountyCode);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    strCountyCode = strCountyCode.replace("+", "");
                }
                LogUtils.i("SDK_VERSION==>" + Build.VERSION.SDK_INT);
                LogUtils.i("SDK_VERSION_RELEASE" + Build.VERSION.RELEASE);
                LogUtils.i("CountryCode1==>" + strCountyCode);
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(strCountyCode));
                Phonenumber.PhoneNumber phoneNumber = null;

                try {
                    //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
                    phoneNumber = phoneNumberUtil.parse(strMobile, isoCode);
                } catch (NumberParseException e) {
                    System.err.println(e);
                }

                boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
                if (isValid) {
                    String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                    return true;
                } else {
                    edtMobile.setError(getString(R.string.enter_a_valid_mobile_number));
                    return false;
                }
            }
            return true;
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
}
