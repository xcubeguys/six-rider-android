package com.tommy.rider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.tommy.rider.adapter.Constants;
import com.tommy.rider.adapter.FontChangeCrawler;
import com.tommy.rider.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class WalletActivity extends AppCompatActivity {

    private static final String TAG = "WalletActivity";

    JSONObject wallet_jsonobj, update_jsonobj;
    String walletamount, wallet_status, wallet_userfname, wallet_userlname, wallet_userimage, wallup_status, Amount, User_id;
    ImageView propic;
    TextView bal_amt, header;
    Button addamount;
    EditText get_amout;
    ImageButton back;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        propic = (ImageView) findViewById(R.id.propic);
        bal_amt = (TextView) findViewById(R.id.bal_amt);
        addamount = (Button) findViewById(R.id.addamount);
        get_amout = (EditText) findViewById(R.id.get_amout);
        back = (ImageButton) findViewById(R.id.back);
        header = (TextView) findViewById(R.id.header);

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        User_id = prefs.getString("userid", null);
        LogUtils.i("UserID in settings" + User_id);
        //Change Font to Whole View

        get_wallet_detail();

        back.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                // Check if no view has focus:
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                finish();
            }
        });

        addamount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (get_amout.getText().toString().length() == 0) {
                    //getcurrency.setError("Required");
                    Toast.makeText(WalletActivity.this, "Enter Amount", Toast.LENGTH_SHORT).show();

                } else if (get_amout.getText().toString().startsWith("0")) {
                    //getcurrency.setError("Enter Valid Amount");
                    Toast.makeText(WalletActivity.this, "Enter Valid Amount", Toast.LENGTH_SHORT).show();
                } else {

                    Amount = get_amout.getText().toString();

                    //payamount = Double.valueOf(Amount);
                    callStripe();
                }
            }
        });
    }

    public void show_progress() {
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
                try {
                    progressDialog.dismiss();
                    progressDialog = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void callStripe() {

        Intent i = new Intent(WalletActivity.this, CardPaymentActivity_.class);
        i.putExtra("page", "wallet");
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(i, Constants.REQUEST_STRIPE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Stripe Payment
        if (requestCode == Constants.REQUEST_STRIPE_PAYMENT) {

            LogUtils.i("result code==>" + Activity.RESULT_OK);

            if (resultCode == Activity.RESULT_OK) {

                String result = data.getStringExtra("Token");

                LogUtils.i("Stripe token wallet==>" + result);

                update_walletamount(result);  //change for problem

                //Toast.makeText(WalletActivity.this, result, Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(WalletActivity.this, "Payment canceled by user", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void get_wallet_detail() {

        show_progress();
        LogUtils.i("am insid the function==>");

        final String url = Constants.LIVE_URL + "editProfile/user_id/" + User_id;
        LogUtils.i("URL is" + url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        dismissDialog();
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                wallet_jsonobj = response.getJSONObject(i);
                                wallet_status = wallet_jsonobj.getString("status");
                                Log.d("OUTPUT IS", wallet_status);

                                if (wallet_status.matches("Success")) {

                                    wallet_userfname = wallet_jsonobj.optString("firstname");
                                    wallet_userlname = wallet_jsonobj.optString("lastname");
                                    wallet_userimage = wallet_jsonobj.optString("profile_pic");
                                    walletamount = wallet_jsonobj.optString("wallet_amount");

                                    String name = "         " + wallet_userfname + " " + wallet_userlname;
                                    name = name.replaceAll("%20", " ");

                                    header.setText(name);
                                    if (!walletamount.isEmpty()) {
                                        if (walletamount == null || walletamount.equals("null") || walletamount.equals("")) {
                                            bal_amt.setText("0.00");
                                            LogUtils.i("am in not setting the amount==>" + walletamount);
                                        } else {
                                            LogUtils.i("am in setting the amount==>" + walletamount);

                                            Double amount;

                                            if (isDouble(walletamount)) {

                                                amount = Double.parseDouble(walletamount);

                                            } else {
                                                amount = (double) Integer.parseInt(walletamount);
                                            }

                                            bal_amt.setText(convertToDecimal(amount));
                                        }
                                    }

                                    LogUtils.i("Insite the Profile Activity Profile image" + wallet_userimage);

                                    Glide.with(getApplication()).load(wallet_userimage).asBitmap().placeholder(R.drawable.account_circle_grey).error(R.drawable.account_circle_grey).into(new BitmapImageViewTarget(propic) {
                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            RoundedBitmapDrawable circularBitmapDrawable =
                                                    RoundedBitmapDrawableFactory.create(view.getContext().getResources(), resource);
                                            circularBitmapDrawable.setCircular(true);
                                            propic.setImageDrawable(circularBitmapDrawable);
                                        }
                                    });
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //findViewById(R.id.progressBar1).setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dismissDialog();
                if (error instanceof NoConnectionError) {
                    LogUtils.i("No internet connection");
                    Toast toast = Toast.makeText(getApplicationContext(), "There is no network please connect.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 70);
                    toast.show();
                }
                VolleyLog.d(TAG, "Error: " + error);
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy());
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    boolean isDouble(String str) {
        try {

            Double.parseDouble(str);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String convertToDecimal(Double amount) {

        if (amount > 0) {
            LogUtils.i("THE AMOUNT IS" + new DecimalFormat("0.00").format(amount));
            return new DecimalFormat("0.00").format(amount);
        } else {
            return String.valueOf(0);
        }
    }

    public void update_walletamount(String stripeToken) {

        show_progress();
        //http://demo.cogzideltemplates.com/tommy/rider/updateWalletAamount/payid/tok_19rl2AGO6Fk1Vdt6O8CKqzZd/user_id/58a32805da71b437308b4567/payment_amount/100
        final String url = Constants.LIVE_URL + "updateWalletAmount/user_id/" + User_id + "/payid/" + stripeToken + "/payment_amount/" + Amount;
        LogUtils.i("URL is" + url);
        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        dismissDialog();
                        LogUtils.i("responses==>" + response.toString());
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                update_jsonobj = response.getJSONObject(i);
                                wallup_status = update_jsonobj.getString("status");
                                Log.d("OUTPUT IS", wallup_status);

                                if (wallup_status.matches("Success")) {

                                    Toast.makeText(WalletActivity.this, "successfully Added to Wallet!!", Toast.LENGTH_SHORT).show();
                                    walletamount = update_jsonobj.optString("message");

                                    if (!walletamount.isEmpty()) {
                                        if (walletamount == null || walletamount.equals("null") || walletamount.equals("")) {
                                            bal_amt.setText("0.00");

                                        } else {

                                            bal_amt.setText(walletamount);
                                            get_amout.setText("");
                                        }
                                    }

                                } else {
                                    Toast.makeText(WalletActivity.this, "Failed to Add Amount in Wallet", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //findViewById(R.id.progressBar1).setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dismissDialog();
                if (error instanceof NoConnectionError) {
                    LogUtils.i("No internet connection");
                    Toast toast = Toast.makeText(getApplicationContext(), "There is no network please connect.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 70);
                    toast.show();
                }
                VolleyLog.d(TAG, "Error: " + error);
            }
        });

        // Adding request to request queue
        movieReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(movieReq);
    }
}