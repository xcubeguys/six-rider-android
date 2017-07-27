package com.tommy.rider;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.BuildConfig;
import com.crashlytics.android.core.CrashlyticsCore;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.tommy.rider.adapter.LruBitmapCache;
import com.tommy.rider.utils.LogUtils;

import io.fabric.sdk.android.Fabric;

public class AppController extends Application {

	public static final String TAG = AppController.class.getSimpleName();

	private static GoogleAnalytics sAnalytics;
	private static Tracker sTracker;
	private static AppController mInstance;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
			Fabric.with(this, new Crashlytics.Builder().core(core).build(), new Crashlytics(), new CrashlyticsNdk());

			sAnalytics = GoogleAnalytics.getInstance(this);

		} catch (Exception e) {
			LogUtils.e("Failed to initialize Crashlytics.");
		}
		mInstance = this;
	}

	synchronized public Tracker getDefaultTracker() {
		// To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
		if (sTracker == null) {
			sTracker = sAnalytics.newTracker(R.xml.global_tracker);
		}

		return sTracker;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(this.mRequestQueue,
					new LruBitmapCache());
		}
		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}