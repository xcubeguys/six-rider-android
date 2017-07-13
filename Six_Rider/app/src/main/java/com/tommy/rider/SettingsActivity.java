package com.tommy.rider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.tommy.rider.SocialShare.SocialShare;
import com.tommy.rider.adapter.AppController;
import com.tommy.rider.adapter.Constants;
import com.tommy.rider.adapter.CountryCodePicker;
import com.tommy.rider.adapter.FontChangeCrawler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

@EActivity (R.layout.activity_settings)
public class SettingsActivity extends AppCompatActivity {

    public String userID, firstName, lastName, nickName,email, mobileNumber, referral_code,countryCode, profileImage, status, message;
    ProgressDialog progressDialog;
    Dialog socialShareDialog;

    @ViewById(R.id.profileImage)
    ImageView edtProfileImage;

    @ViewById(R.id.backButton)
    ImageButton backButton;

    @ViewById(R.id.editButton)
    ImageButton editButton;

    @ViewById(R.id.editCancelButton)
    ImageButton editCancel;

    @ViewById(R.id.save_button)
    Button saveButton;

    @ViewById(R.id.edtFirstName)
    EditText inputFirstName;

    @ViewById(R.id.edtLastName)
    EditText inputLastName;

    @ViewById(R.id.edtnickname)
    EditText inputNickName;

    @ViewById(R.id.edtCountryCode)
    EditText inputCountryCode;

    @ViewById(R.id.edtMobile)
    EditText inputMobileNumber;

    @ViewById(R.id.edtEmail)
    EditText inputEmail;

    @ViewById(R.id.edtreferral)
    TextView inputReferralcode;

    @ViewById(R.id.ccp)
    CountryCodePicker ccp;

    SocialShare socialShare;

    @Click (R.id.share)
    void share() {
        // Build view
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_SMS") == PackageManager.PERMISSION_GRANTED) {

            final View view = socialShare.getDefaultShareUI();

            // Do something with the view, for example show in Dialog

            socialShareDialog = new Dialog(SettingsActivity.this);
            socialShareDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            socialShareDialog.addContentView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            socialShareDialog.show();
     /*   Intent i=new Intent(SettingsActivity.this,ShareReferralActivity.class);
        i.putExtra("referralcode",referral_code);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);*/

        }
    }

    @AfterViews
    void settingsActivity() {


        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        System.out.println("UserID in settings" + userID);

        //Change Font to Whole View
        displayDetails();
    }

    @Click(R.id.editButton)
    void editProfile() {
    Intent i=new Intent(SettingsActivity.this,EditProfileActivity_.class);
        startActivity(i);
        finish();
    }

    @Click(R.id.backButton)
    void goBack(){
        finish();
    }

    public void displayDetails() {
        showDialog();
        final String url = Constants.LIVE_URL + "editProfile/user_id/"+userID;
        System.out.println("RiderProfileURL==>"+url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                dismissDialog();
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");
                        message = jsonObject.optString("message");

                        if(status.equals("Success")){
                            firstName=jsonObject.optString("firstname");
                            lastName=jsonObject.optString("lastname");
                            nickName=jsonObject.optString("nick_name"); ////////////////check NIck name

                            email=jsonObject.optString("email");
                            mobileNumber=jsonObject.optString("mobile");
                            profileImage=jsonObject.optString("profile_pic");
                            countryCode=jsonObject.optString("country_code");

                            referral_code=jsonObject.optString("refrel_code"); ////////////////check refrral code
//                            savepreferences();
                            //Share code via Social
                            socialShare = new SocialShare(SettingsActivity.this);
                            socialShare.setSubject("SIX Referral Code");
                            socialShare.setReferralCode(referral_code);
                            String message;
                            try {
                                message = "Sign Up Now  " + new URL(Constants.CATEGORY_LIVE_URL) + "\n\n"
                                        + "Download App Now  "
                                        + new URL(Constants.Play_Store_URL) + "\n\n" +
                                        getString(R.string.sms_text)
                                        +"\n\n"+"Your Referral Code is "+referral_code+"."+"\n\n"+
                                        getString(R.string.sms_text_last);
                            } catch (Exception e) {
                                e.printStackTrace();
                                message = getString(R.string.sms_text)+"\n\n"+"Your Referral Code is "+referral_code+"."+"\n\n"+getString(R.string.sms_text_last);
                            }
                            socialShare.setMessage(message);
                            try {
                                if(firstName == null ||(firstName.equals("null")))
                                    inputFirstName.setHint("First Name");
                                else {
                                    firstName = firstName.replaceAll("%20", " ");
                                    inputFirstName.setText(firstName);
                                }

                                if(lastName == null ||(lastName.equals("null")))
                                    inputLastName.setHint("Last Name");
                                else {
                                    lastName = lastName.replaceAll("%20", " ");
                                    inputLastName.setText(lastName);
                                }

                                if(nickName == null ||(nickName.equals("null")))
                                    inputNickName.setHint("Last Name");
                                else {
                                    nickName = nickName.replaceAll("%20", " ");
                                    inputNickName.setText(nickName);
                                }

                                if(email == null ||(email.equals("null")))
                                    inputEmail.setHint("Email");
                                else
                                    inputEmail.setText(email);

                                if(mobileNumber == null || mobileNumber.equals("null"))
                                    inputMobileNumber.setHint("Mobile number");
                                else
                                    inputMobileNumber.setText(mobileNumber);

                                if(referral_code == null || referral_code.equals("null"))
                                    inputMobileNumber.setHint("Referral Code");
                                else
                                    inputReferralcode.setText(referral_code);

                                if(countryCode == null ||countryCode.equals("null"))
                                    inputCountryCode.setHint("CC");
                                else
                                inputCountryCode.setText(countryCode);

                                Glide.with(getApplicationContext()).load(profileImage).asBitmap().error(R.drawable.account_circle_grey).centerCrop().skipMemoryCache(true).into(new BitmapImageViewTarget(edtProfileImage) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        edtProfileImage.setImageDrawable(circularBitmapDrawable);
                                    }
                                });

                            } catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        } else {
                            System.out.print("inside else");
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
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                }  if(volleyError instanceof TimeoutError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    public void showDialog(){
        progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public void dismissDialog(){
        if(progressDialog!=null && progressDialog.isShowing()){
            if(!isFinishing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(socialShareDialog!=null){

            if(socialShareDialog.isShowing())
            {
                Toast.makeText(this, "Please Wait...", Toast.LENGTH_SHORT).show();
                socialShareDialog.dismiss();
            }
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
  /*  private void showPaymentGatewaydialog() {



        dialog = new Dialog(SettingsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_dialog_share);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageButton back = (ImageButton) dialog.findViewById(R.id.backButton);
        TextView facebook = (TextView) dialog.findViewById(R.id.txtfacebook);
        TextView google = (TextView) dialog.findViewById(R.id.txtgoogle);
        TextView twitter = (TextView) dialog.findViewById(R.id.txttwitter);
        TextView contact = (TextView) dialog.findViewById(R.id.contacts);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();

            }
        });


        dialog.show();

    }*/
}