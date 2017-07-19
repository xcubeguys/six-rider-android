package com.tommy.rider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tommy.rider.adapter.Constants;
import com.tommy.rider.adapter.FontChangeCrawler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

@EActivity(R.layout.activity_referral)
public class ReferralActivity extends MyBaseActivity implements Validator.ValidationListener {

    public String firstName, lastName, referral, referralStatus, nickName, email, passWord;
    Validator validator;
    ProgressDialog progressDialog;

    @NotEmpty(message = "Enter Referral Code")
    @ViewById(R.id.referral_et)
    MaterialEditText inputReferral;

    @ViewById(R.id.rounded_arrow)
    RelativeLayout next_button;

    @ViewById(R.id.skip)
    TextView Skip_button;

    @AfterViews
    void Create_ReferralActivity() {

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        validator = new Validator(this);
        validator.setValidationListener(this);

        Intent i = getIntent();
        firstName = i.getStringExtra("firstname");
        lastName = i.getStringExtra("lastname");
        nickName = i.getStringExtra("nickname");
        email = i.getStringExtra("email");
        passWord = i.getStringExtra("password");

        inputReferral.setFilters(new InputFilter[]{new ExcludeEmojiFilter()});
        inputReferral.addTextChangedListener(textWatcher);
    }

    private class ExcludeEmojiFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() > 0) {
                next_button.setVisibility(View.VISIBLE);
                Skip_button.setVisibility(View.GONE);
            } else {
                next_button.setVisibility(View.GONE);
                Skip_button.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (inputReferral != null) {
            inputReferral.removeTextChangedListener(textWatcher);
        }
    }

    @Click({R.id.imageButton3, R.id.imageButton2})
    void toSignUpName() {
        validator.validate();
    }

    @Click(R.id.backButton)
    public void goBack() {
        super.onBackPressed();
    }

    @Click(R.id.skip)
    public void goSkip() {

        Intent intent = new Intent(ReferralActivity.this, SignupMobile_.class);
        intent.putExtra("firstname", firstName);
        intent.putExtra("lastname", lastName);
        intent.putExtra("email", email);
        intent.putExtra("password", passWord);
        intent.putExtra("nickname", nickName);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onValidationSucceeded() {

        referral = inputReferral.getText().toString().trim();

        try {
            byte[] encoded = Base64.encode(referral.getBytes("UTF-8"), Base64.DEFAULT);
            referral = new String(encoded, "UTF-8");
            referral = referral.replaceAll("=", "").trim();
            System.out.println("Encoding UTF" + referral);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        showDialog();

        final String url = Constants.LIVE_URL + "refrel_code/code/" + referral;
        System.out.println("RefrelCode Check URL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                dismissDialog();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        referralStatus = jsonObject.optString("status");

                        if (referralStatus.equals("Success")) {
                            Intent intent = new Intent(ReferralActivity.this, SignupMobile_.class);
                            intent.putExtra("firstname", firstName);
                            intent.putExtra("lastname", lastName);
                            intent.putExtra("email", email);
                            intent.putExtra("password", passWord);
                            intent.putExtra("nickname", nickName);
                            intent.putExtra("referral", referral);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            inputReferral.setError(getResources().getString(R.string.referral_invalid));
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
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
            if (!isFinishing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }
}