package com.tommy.rider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.text.InputFilter;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tommy.rider.adapter.AppController;
import com.tommy.rider.adapter.BulletTextUtil;
import com.tommy.rider.adapter.Constants;
import com.tommy.rider.adapter.CountryCodeDialog;
import com.tommy.rider.adapter.CountryCodePicker;
import com.tommy.rider.adapter.FontChangeCrawler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@EActivity (R.layout.activity_signup_mobile)
public class SignupMobile extends MyBaseActivity implements CountryCodePicker.OnCountryChangeListener {

    public String firstName,lastName,email,passWord,mobileNumber,countrycode,registerID,signUpStatus,signUpMessage,nickName, content;
    ProgressDialog progressDialog;
    Dialog dialog;

    @ViewById(R.id.imageButton2)
    ImageButton submitArrow;

    @ViewById(R.id.imageButton3)
    ImageButton submitCircle;

    @ViewById(R.id.termsandconditions)
    CheckBox tandc;

    @ViewById(R.id.ccp)
    CountryCodePicker ccp;

    @NotEmpty (message = "")
    @ViewById(R.id.countryCode)
    MaterialEditText inputCountryCode;

    @NotEmpty (message = "Enter Mobile Number")
    @ViewById(R.id.mobileNumber)
    MaterialEditText inputMobileNumber;

    @ViewById(R.id.backButton)
    ImageButton backButton;

    SharedPreferences.Editor editor;

    String userID,userFirstName,userLastName,userEmail,userMobile,userNickName,referral;

    int count=0;

    @AfterViews
    void signUpMobile() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        submitArrow.setEnabled(true);
        submitCircle.setEnabled(true);

        ccp.setOnCountryChangeListener(this);

        getTermsCondition();

        editor = getSharedPreferences(Constants.MY_PREFS_NAME,getApplicationContext().MODE_PRIVATE).edit();

        Intent i = getIntent();
        firstName = i.getStringExtra("firstname");
        lastName = i.getStringExtra("lastname");
        nickName = i.getStringExtra("nickname");
        email = i.getStringExtra("email");
        passWord = i.getStringExtra("password");
        referral = i.getStringExtra("referral");

    }

    @Click({R.id.imageButton3,R.id.imageButton2})
    void toSignUpMobile() {

        if (!validateCountryCode()) {

        }
        if (!validatePhone()) {

        } else if (!validateUsing_libphonenumber()) {
            inputMobileNumber.setError(getString(R.string.invalid_mobile_number));
        }
        else if(!tandc.isChecked()){
            Toast.makeText(SignupMobile.this, "Agree terms and conditions", Toast.LENGTH_SHORT).show();
        }
        else {
            submitArrow.setEnabled(false);
            submitCircle.setEnabled(false);
                callSignUp();
        }
    }

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

    private void showTermsDialog() {
        dialog = new Dialog(SignupMobile.this);
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

    @Click(R.id.countryCode)
    public void countryCode(View view) {
        CountryCodeDialog.openCountryCodeDialog(ccp);//Open country code dialog
    }

    @Click (R.id.backButton)
    public void goBack() {
        super.onBackPressed();
    }

    public void callSignUp(){

        showDialog();
        firstName=firstName.replaceAll(" ","%20");
        lastName=lastName.replaceAll(" ","%20");
        nickName=nickName.replaceAll(" ","%20");
        if(count==0) {//Encrypt Password only for first time
            try {
                byte[] encoded = Base64.encode(passWord.getBytes("UTF-8"), Base64.DEFAULT);
                passWord = new String(encoded, "UTF-8");
                passWord = passWord.replaceAll("=", "").trim();
                System.out.println("Encoding UTF" + passWord);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        String url = Constants.LIVE_URL + "signUp/regid/"+registerID+"/first_name/"+firstName+"/last_name/"+lastName+"/nick_name/"+nickName+"/mobile/"+mobileNumber+"/country_code/"+countrycode+"/password/"+passWord+"/city/"+"null"+"/email/"+email;

        if(referral != null){
            url=url+"/referral_code/"+referral;
        }
        System.out.println("SignUpURL==>"+url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                dismissDialog();
                for (int i=0;i<response.length();i++){
                    try {
                        count=1;
                        JSONObject jsonObject = response.getJSONObject(i);
                        signUpStatus = jsonObject.optString("status");
                        signUpMessage = jsonObject.optString("message");

                        if(signUpStatus.equals("Success")){
                            userID=jsonObject.optString("userid");
                            userFirstName=jsonObject.optString("first_name");
                            userLastName=jsonObject.optString("last_name");
                            userEmail=jsonObject.optString("email");
                            userMobile=jsonObject.optString("mobile");
                            userNickName=jsonObject.optString("nick_name");
                            savepreferences();
                            Toast.makeText(getApplicationContext(), R.string.successfully_registered,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupMobile.this,MapActivity.class);
                            intent.putExtra("userid",userID);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else if(signUpStatus.equals("Fail")){
                           inputMobileNumber.setError(getResources().getString(R.string.mobile_number_already_exisits));
                            submitArrow.setEnabled(true);
                            submitCircle.setEnabled(true);
                        } else {
                            submitArrow.setEnabled(true);
                            submitCircle.setEnabled(true);
                        }
                    } catch (JSONException | NullPointerException e) {
                        count=1;
                        submitArrow.setEnabled(true);
                        submitCircle.setEnabled(true);
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismissDialog();
                count=1;
                if (volleyError instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                    submitArrow.setEnabled(true);
                    submitCircle.setEnabled(true);
                } else if(volleyError instanceof NetworkError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                    submitArrow.setEnabled(true);
                    submitCircle.setEnabled(true);
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    private boolean validateCountryCode() {

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

    private boolean validatePhone() {
        if(inputMobileNumber.getText().toString().trim().isEmpty()) {
            inputMobileNumber.setError(getString(R.string.enter_mobile_number));
            return false;
        }
        else if (inputCountryCode.getText().toString().trim().isEmpty())
        {
            return false;
        }
        else  if (!inputMobileNumber.getText().toString().trim().isEmpty())
        {
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

    private boolean validateUsing_libphonenumber() {
        if(inputMobileNumber.getText().toString().length()<=1){
            return false;
        }
        else{
            countrycode = inputCountryCode.getText().toString();
            mobileNumber = inputMobileNumber.getText().toString();
            if (validatePhone() && validateCountryCode()) {
                System.out.println("CountryCode==>" + countrycode);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    countrycode = countrycode.replace("+", "");
                }
                System.out.println("SDK_VERSION==>" + Build.VERSION.SDK_INT);
                System.out.println("SDK_VERSION_RELEASE" + Build.VERSION.RELEASE);
                System.out.println("CountryCode1==>" + countrycode);
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countrycode));
                Phonenumber.PhoneNumber phoneNumber = null;

                try {
                    //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
                    phoneNumber = phoneNumberUtil.parse(mobileNumber, isoCode);
                } catch (NumberParseException e) {
                    e.printStackTrace();
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

    @Override
    public void onCountrySelected() {
        inputCountryCode.setText(ccp.getSelectedCountryCodeWithPlus());
        inputMobileNumber.setError(null);
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
            if(!isFinishing())
            {
            progressDialog.dismiss();
            progressDialog=null;
            }
        }
    }

    public void savepreferences()
    {

        stopDisconnectTimer();
        editor.putString("userid", userID);
        editor.putString("username", userFirstName);
        editor.putString("userphonenum", userMobile);
        editor.apply();

        //Saving to Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("riders_location").child(userID);
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("Paymenttype","cash");

        ref.setValue(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                System.out.println("DATA SAVED SUCCESSFULLY");
                if(databaseError!=null){
                    System.out.println("DATA SAVED SUCCESSFULLY");
                }
            }
        });
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
                Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
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
}