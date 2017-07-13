package com.tommy.rider;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.androidadvance.topsnackbar.TSnackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.tommy.rider.adapter.AppController;
import com.tommy.rider.adapter.Constants;
import com.tommy.rider.adapter.FontChangeCrawler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.card.payment.CardIOActivity;
import io.card.payment.CardType;
import io.card.payment.CreditCard;

@EActivity(R.layout.activity_select_payment)
public class PaymentSelectActivity extends AppCompatActivity {

    @ViewById(R.id.text_cash)
    TextView inputCash;

    @ViewById(R.id.text_card)
    TextView inputCard;

    @ViewById(R.id.text_corpid)
    TextView inputCorpID;

    @ViewById(R.id.cash_image)
    ImageView cashImage;

    Drawable tickDrawable;

    @ViewById(R.id.corpid_layout)
    LinearLayout corporateView;

    @ViewById(R.id.cash_layout)
    LinearLayout cashView;

    String userID;

    Dialog dialog;
    ProgressDialog progressDialog;

    boolean paymentMade = false, paymentCorporate = false;
    protected static final String TAG = "PaymentSelectActivity";
    String corpid,Test_ApiKey,Live_ApiKey,is_live_stripe="";
    String status, c_id;

    @AfterViews
    void selectPayment() {

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        System.out.println("UserID in Map" + userID);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        tickDrawable = getApplicationContext().getResources().getDrawable(R.drawable.check);

        //getPaymentType
        getPaymentReference();

        getRiderDetails();
        getKeys();
    }

    @Click(R.id.text_cash)
    void cashPayment() {
        updatePayment(Constants.PAYMENT_TYPE_CASH);
    }

    @Click(R.id.text_corpid)
    void corpPayment() {
        updatePayment(Constants.PAYMENT_TYPE_CORP_ID);
    }

    @Click(R.id.text_card)
    void cardPayment() {
        if (paymentMade) {
            updatePayment(Constants.PAYMENT_TYPE_CARD);
        } else {
            TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), "Click Add payment to select Credit or Debit Card", TSnackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.WHITE);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.RED);
            TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    @Click(R.id.add_payment)
    void addPayment() {

        showPaymentGatewaydialog();
    }

    @Click(R.id.backButton)
    void goBack() {
        finish();
    }

    public void updatePayment(String paymentType) {
        if (paymentType != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("riders_location").child(userID);
            Map<String, Object> taskMap = new HashMap<>();
            taskMap.put("Paymenttype", paymentType);
            databaseReference.updateChildren(taskMap);
        }
    }

    private void getPaymentReference() {

            DatabaseReference databasecashReference = FirebaseDatabase.getInstance().getReference().child("cashoption").child("status");
            databasecashReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        if (status != null) {

                            if (status.matches("off")) {

                                cashView.setVisibility(View.GONE);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    cashImage.setImageDrawable(getResources().getDrawable(R.drawable.ub__payment_type_cash_no, getApplicationContext().getTheme()));
                                } else {
                                    cashImage.setImageDrawable(getResources().getDrawable(R.drawable.ub__payment_type_cash_no));
                                }

                            } else if (status.matches("on")) {

                                cashView.setVisibility(View.VISIBLE);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    cashImage.setImageDrawable(getResources().getDrawable(R.drawable.ub__payment_type_cash, getApplicationContext().getTheme()));
                                } else {
                                    cashImage.setImageDrawable(getResources().getDrawable(R.drawable.ub__payment_type_cash));
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        if (userID != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("riders_location").child(userID).child("Paymenttype");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        if (status != null) {
                            if (status.matches("stripe")) {

                                inputCard.setCompoundDrawablesWithIntrinsicBounds(null, null, tickDrawable, null);
                                inputCash.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                inputCorpID.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                            } else if (status.matches("cash")) {

                                inputCash.setCompoundDrawablesWithIntrinsicBounds(null, null, tickDrawable, null);
                                inputCard.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                inputCorpID.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                                //Notification to show the trip has started
                            } else if (status.matches("corpID")) {

                                inputCorpID.setCompoundDrawablesWithIntrinsicBounds(null, null, tickDrawable, null);
                                inputCash.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                inputCard.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                                //Notification to show the trip has started
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    //get Details of Rider
    private void getRiderDetails() {
        final String url = Constants.LIVE_URL + "editProfile/user_id/" + userID;
        System.out.println("Rider Profile in Map==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String status = jsonObject.optString("card_status");
                        String statuscorporate = jsonObject.optString("corporate_status");

                        if (statuscorporate.equals("0")) {
                            paymentCorporate = false;
                            corporateView.setVisibility(View.GONE);
                        } else {
                            paymentCorporate = true;
                            corporateView.setVisibility(View.VISIBLE);
                        }

                        paymentMade = !status.equals("0");

                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError) {
                    System.out.print("No Internet");
                }
            }
        });

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");

        String outStr = "";
        //Bitmap cardTypeImage = null;

        if ((requestCode == Constants.REQUEST_SCAN || requestCode == Constants.REQUEST_AUTOTEST) && data != null
                && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            CreditCard result = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
            if (result != null) {


                Log.i(TAG, "Set result response: " + result);

                outStr += "Card number: " + result.getRedactedCardNumber() + "\n";

                //myService.setCardNumber( result.cardNumber );

                CardType cardType = result.getCardType();
                //cardTypeImage = cardType.imageBitmap(this);
                outStr += "Card type: " + cardType.name() + " cardType.getDisplayName(null)="
                        + cardType.getDisplayName(null) + "\n";

                outStr += "Expiry: " + result.expiryMonth + "/" + result.expiryYear + "\n";


                outStr += "CVV: " + result.cvv + "\n";


                outStr += "Postal Code: " + result.postalCode + "\n";


                outStr += "Cardholder Name: " + result.cardholderName + "\n";


                // Do something with the raw number, e.g.:

                boolean DEBUG;

                if(is_live_stripe.matches("0"))
                    DEBUG = Boolean.parseBoolean("true");
                else
                    DEBUG = Boolean.parseBoolean("false");

                // TODO: replace with your own test key
                final String publishableApiKey = DEBUG ? Test_ApiKey :Live_ApiKey;

                Card card = new Card((result.cardNumber),
                        result.expiryMonth,
                        result.expiryYear,
                        result.cvv
                );

                final Stripe stripe = new Stripe();
                final String finalCardNumber = result.cardNumber;
                stripe.createToken(card, publishableApiKey, new TokenCallback() {
                    public void onSuccess(Token token) {
                        // TODO: Send Token information to your backend to initiate a charge
                        String stripeTokenID = token.getId();

                        //printRes(token.getId()+"Numeber==> "+ finalCardNumber);

                        updatePayment(stripeTokenID, finalCardNumber);
                    }

                    public void onError(Exception error) {
                        Log.d("Stripe error", error.getLocalizedMessage());
                        Toast.makeText(getApplicationContext(), "Stripe error: " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Canceled by user", Toast.LENGTH_SHORT).show();
        }

        Bitmap card = CardIOActivity.getCapturedCardImage(data);
        //mResultImage.setImageBitmap(card);
        //mResultCardTypeImage.setImageBitmap(cardTypeImage);
        Log.i(TAG, "Set result: " + outStr);
    }

    public void printRes(String token) {

        Toast.makeText(this, "Passed" + token, Toast.LENGTH_SHORT).show();
    }

    private void updatePayment(String stripeTokenID, String creditCardNumber) {
        showDialog();

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        final String url = Constants.LIVE_URL + "updateStripeToken/userid/" + userID + "/token/" + stripeTokenID + "/card_number/" + creditCardNumber;
        System.out.println("SignUpURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                dismissDialog();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");

                        if (status.equals("Success")) {
                            Intent intent = new Intent(getApplicationContext(), PaymentSelectActivity_.class);
                            intent.putExtra("paid", true);
                            startActivity(intent);
                            finish();

                            Toast.makeText(getApplicationContext(), R.string.your_card_was_addedd_successfully, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_while_adding_your_car, Toast.LENGTH_SHORT).show();
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

        signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);
    }

    private void showPaymentGatewaydialog() {

        dialog = new Dialog(PaymentSelectActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_paymentgateway);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageButton back = (ImageButton) dialog.findViewById(R.id.backButton);
        Button updateCorpId = (Button) dialog.findViewById(R.id.update);
        //final MaterialEditText corporate_id = (MaterialEditText) dialog.findViewById(R.id.corporate_id);

        TextView cancel = (TextView) dialog.findViewById(R.id.txtCancelpayment);
        LinearLayout stripelayout = (LinearLayout) dialog.findViewById(R.id.stripelayout);
        LinearLayout scancard = (LinearLayout) dialog.findViewById(R.id.brainlayout);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);

        scancard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();

                if (paymentMade) {
                    TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), "You have added your card already!", TSnackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                    TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                } else {
                    startScanCard();
                }
            }
        });

/*        updateCorpId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(paymentCorporate) {
                    TSnackbar snackbar =TSnackbar.make(findViewById(android.R.id.content),"You have added a Corporate id already!",TSnackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                    TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();

                }else{
                    corpid =  corporate_id.getText().toString();
                    checkCorporateID();
                }

                //startScanCard();
            }
        });*/

        stripelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();

                if (paymentMade) {
                    TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), "You have added your card already!", TSnackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                    TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                } else {

                    Intent card = new Intent(PaymentSelectActivity.this, CardPaymentActivity_.class);
                    card.putExtra("page", "card");
                    card.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(card);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void startScanCard() {

        Intent intent = new Intent(this, CardIOActivity.class)
                .putExtra(CardIOActivity.EXTRA_NO_CAMERA, false)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true)
                .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)
                .putExtra(CardIOActivity.EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY, false)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true)
                .putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false)
                .putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, true)
                .putExtra(CardIOActivity.EXTRA_LANGUAGE_OR_LOCALE, "en")
                .putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, true)
                .putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, true)
                .putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, Color.GREEN)
                .putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, false)
                .putExtra(CardIOActivity.EXTRA_SUPPRESS_SCAN, false)
                .putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true);

        try {
            int unblurDigits = Integer.parseInt("16");
            intent.putExtra(CardIOActivity.EXTRA_UNBLUR_DIGITS, unblurDigits);
        } catch (NumberFormatException ignored) {
        }

        dialog.cancel();
        startActivityForResult(intent, Constants.REQUEST_SCAN);
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
        System.out.println(" CATEGOR URL is " + url);

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

                                System.out.println("strip live==>"+Live_ApiKey+"test==>"+Test_ApiKey);

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
                    System.out.print("No Internet");
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

    private void checkCorporateID() {

        final String url = Constants.LIVE_URL + "corporateid/userid/" + userID + "/cid/" + corpid;
        System.out.println("SosUpURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");

                        if (status.equals("Success")) {
                            Toast.makeText(getApplicationContext(), "Thank you for your update", Toast.LENGTH_SHORT).show();
                            //message = jsonObject.optString("message");
                            // dialog.dismiss();
                            // callCorporateID();


                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Corporate Id", Toast.LENGTH_SHORT).show();
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

    private void callCorporateID() {

        final String url = Constants.LIVE_URL + "get_corporateid/user_id/" + userID;
        System.out.println("SosUpURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");

                        if (status.equals("Success")) {
                            c_id = jsonObject.optString("c_id");
                            if (c_id.equals(corpid)) {
                                Toast.makeText(getApplicationContext(), "Thankyou for your update", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Corporate Id", Toast.LENGTH_SHORT).show();
                            }
                            // Toast.makeText(getApplicationContext(), "Thankyou for your update",Toast.LENGTH_SHORT).show();
                            //message = jsonObject.optString("message");
                            // dialog.dismiss();

                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Corporate Id", Toast.LENGTH_SHORT).show();
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
}