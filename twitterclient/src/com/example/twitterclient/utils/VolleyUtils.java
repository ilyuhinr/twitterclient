package com.example.twitterclient.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleyUtils {
	public static final String TAG = "VolleySingleton";
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	public VolleyUtils(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
		mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
	}

	public ImageLoader getImageLoader() {
		return this.mImageLoader;
	}

}
