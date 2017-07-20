package com.tommy.rider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.tommy.rider.adapter.Constants;
import com.tommy.rider.adapter.creditcard.fields.CreditCardModule;
import com.tommy.rider.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@EActivity(R.layout.activity_card_payment)
public class CardPaymentActivity extends AppCompatActivity {
    CreditCardModule creditCardModule;
    TextView header;
    Button payButton;
    String userID, creditCardNumber, creditCardMonth, creditCardYear, creditCardCvv, stripeTokenID, status, message, page, Test_ApiKey, Live_ApiKey, is_live_stripe = "";
    ProgressDialog progressDialog;

    @AfterViews
    void cardPayment() {
        creditCardModule = (CreditCardModule) findViewById(R.id.credit_card_module);
        payButton = (Button) findViewById(R.id.pay_button);
        header = (TextView) findViewById(R.id.textView2);

        //UserID from Shared preferences
        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        LogUtils.i("UserID in Cardpayments" + userID);

        Intent i = getIntent();
        page = i.getStringExtra("page");

        //get Stripe live and test key
        getKeys();

        if (page.matches("wallet")) {

            header.setText("Add Amount to Wallet");
        }
    }

    @Click(R.id.pay_button)
    void addCard() {

        boolean complete = creditCardModule.getCreditCardController().isComplete();
        if (complete) {
            if (isNetworkAvailable()) {
                submitCard();
            } else
                Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sorry, Invalid Card", Toast.LENGTH_SHORT).show();
        }
    }

    public void submitCard() {
        showDialog();
        creditCardNumber = String.valueOf(creditCardModule.getCreditCardNumberEditField().getRawCreditCardNumber());
        creditCardCvv = String.valueOf(creditCardModule.getCreditCardController().getCVV());
        creditCardYear = String.valueOf(creditCardModule.getCreditCardController().getExpirationDate().toString());

        String dateStr = creditCardYear;
        @SuppressLint("SimpleDateFormat")
        DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        Date date = null;
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        creditCardMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
        creditCardYear = String.valueOf(cal.get(Calendar.YEAR) + 1);

        boolean DEBUG;

        if (is_live_stripe.matches("0"))
            DEBUG = Boolean.parseBoolean("true");
        else
            DEBUG = Boolean.parseBoolean("false");

        // TODO: replace with your own test key
        final String publishableApiKey = DEBUG ? Test_ApiKey : Live_ApiKey;

        Card card = new Card((creditCardNumber),
                Integer.valueOf(creditCardMonth),
                Integer.valueOf(creditCardYear),
                creditCardCvv);

        final Stripe stripe = new Stripe();

        stripe.createToken(card, publishableApiKey, new TokenCallback() {
            public void onSuccess(Token token) {
                // TODO: Send Token information to your backend to initiate a charge
                stripeTokenID = token.getId();
                LogUtils.i("TokenID==>" + token.getId());

                dismissDialog();

                if (page.matches("wallet")) {

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Token", stripeTokenID);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();

                } else {

                    updatePayment(stripeTokenID, creditCardNumber);
                }
            }

            public void onError(Exception error) {
                Log.d("Stripe error", error.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "Stripe error: " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    @Click(R.id.backButton)
    void goBack() {

        finish();
    }

    private void updatePayment(String stripeTokenID, String creditCardNumber) {
        showDialog();
        final String url = Constants.LIVE_URL + "updateStripeToken/userid/" + userID + "/token/" + stripeTokenID + "/card_number/" + creditCardNumber;
        LogUtils.i("SignUpURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                dismissDialog();
                LogUtils.i("SignUpURL==>" + response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");

                        switch (status) {
                            case "Success":
                                Intent intent = new Intent(getApplicationContext(), PaymentSelectActivity_.class);
                                intent.putExtra("paid", true);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), R.string.your_card_was_addedd_successfully, Toast.LENGTH_SHORT).show();
                                break;
                            case "Fail":
                                Toast.makeText(getApplicationContext(), R.string.error_while_adding_your_car, Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                String value = jsonObject.optString("message");
                                LogUtils.i("error==>" + value);
                                Toast.makeText(getApplicationContext(), R.string.error_while_adding_your_car, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } catch (JSONException | NullPointerException e) {
                        Toast.makeText(getApplicationContext(), R.string.error_while_adding_your_car, Toast.LENGTH_SHORT).show();
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
                } else
                    Toast.makeText(getApplicationContext(), R.string.error_while_adding_your_car, Toast.LENGTH_SHORT).show();
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), PaymentSelectActivity_.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        creditCardModule.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        creditCardModule.onRestoreSavedInstanceState(savedInstanceState);
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

                                Test_ApiKey = signIn_jsonobj.optString("Test_PublishKey");
                                Live_ApiKey = signIn_jsonobj.optString("Live_ApiKey");
                                is_live_stripe = signIn_jsonobj.optString("is_live_stripe");

                                LogUtils.i("strip live==>" + Live_ApiKey + "test==>" + Test_ApiKey);

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
                    LogUtils.i("NoConnectionError");
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

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}