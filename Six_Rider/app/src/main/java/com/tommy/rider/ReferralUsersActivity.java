package com.tommy.rider;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.tommy.rider.adapter.Constants;
import com.tommy.rider.adapter.FontChangeCrawler;
import com.tommy.rider.adapter.ReferralUsersListAdapter;
import com.tommy.rider.adapter.YourTrips;
import com.tommy.rider.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@EActivity(R.layout.activity_referral_users)
public class ReferralUsersActivity extends AppCompatActivity {

    private ArrayList<YourTrips> usersListItems = new ArrayList<YourTrips>();
    private ReferralUsersListAdapter usersListsAdapter;
    ProgressDialog progressDialog;
    String userID, date;
    Handler handler;

    @ViewById(R.id.tripRecyclerView)
    RecyclerView usersLists;

    @ViewById(R.id.empty_layout)
    TextView emptyLayout;

    @ViewById(R.id.backButton)
    ImageButton back;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @AfterViews
    void Create_ReferralUsersActivity() {
        //Change Font to Whole View
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        LogUtils.i("User ID in YourTrips" + userID);

        LinearLayoutManager verticalLayoutmanager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        usersLists.setLayoutManager(verticalLayoutmanager);
        usersListsAdapter = new ReferralUsersListAdapter(this, usersListItems, usersLists);
        usersLists.setAdapter(usersListsAdapter);
        handler = new Handler();

        displayYourTrips();

    }

    @Click(R.id.backButton)
    void goBack() {
        finish();
    }

    private void displayYourTrips() {
        showDialog();
        // http://54.172.2.238/requests/getReferralUserList/user_id/58dfb075192d2ee155234fde
        final String url = Constants.REQUEST_URL + "getReferralUserList/user_id/" + userID;
        LogUtils.i("User list URL==>" + url);
        final JsonArrayRequest tripListReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                dismissDialog();
                LogUtils.i("response length=" + response.length());
                //jsonArray=response;
                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject jsonObject = response.getJSONObject(i);
                        LogUtils.i("Status from user list" + jsonObject.optString("status"));

                        if (jsonObject.optString("status").equals("Success")) {
                            YourTrips trips = new YourTrips();

                            usersLists.setVisibility(View.VISIBLE);
                            emptyLayout.setVisibility(View.GONE);
                            String name = jsonObject.optString("first_name") + " " + jsonObject.optString("last_name");
                            name = name.replaceAll("%20", " ");
                            trips.setUserName(name);
                            trips.setUserImage(jsonObject.optString("profile_pic"));
                            String userType = jsonObject.optString("user_type");
                            if (userType.matches("Driver"))
                                trips.setUserType(userType + " " + jsonObject.optString("category"));
                            else
                                trips.setUserType(userType);

                            usersListItems.add(0, trips); // Reverse the list items in the Array

                        } else {
                            emptyLayout.setVisibility(View.VISIBLE);
                            usersLists.setVisibility(View.GONE);
                        }

                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                usersListsAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismissDialog();
                if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
                if (volleyError instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        tripListReq.setRetryPolicy(new DefaultRetryPolicy(5000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(tripListReq);
    }

    public void onBackPressed() {
        finish();
    }

    public void showDialog() {
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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