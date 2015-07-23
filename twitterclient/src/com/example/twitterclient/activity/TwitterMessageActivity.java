package com.example.twitterclient.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.twitterclient.R;
import com.example.twitterclient.fragments.FriendsFragment;
import com.example.twitterclient.fragments.MessageFragment;
import com.example.twitterclient.utils.TokenRequest;
import com.example.twitterclient.utils.TwitterConstants;
import com.example.twitterclient.utils.VolleyOAuthRequest;
import com.example.twitterclient.utils.VolleyUtils;
import com.viewpagerindicator.TabPageIndicator;

public class TwitterMessageActivity extends SherlockFragmentActivity implements
		FriendsFragment.CallerActivity {
	public static ImageLoader mImageLoader;
	ListView mMessageListView;
	private static final String[] CONTENT = new String[] { "Followers",
			"Messages" };
	FragmentPagerAdapter mTwitterPagerAdapter;
	CallbackFragmentManager mCallbackFragmentManager = null;
	ViewPager mViewPager;
	public static RequestQueue mRequestQueue;
	private TweetJsonListener mDownListener = new TweetJsonListener();
	private static final String TOKEN_URL = "https://api.twitter.com/oauth2/token";

	@Override
	protected void onCreate(Bundle arg0) {
		mImageLoader = (new VolleyUtils(this)).getImageLoader();
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.message_tabs);
		getSupportActionBar().setTitle("Followers");
		mTwitterPagerAdapter = new TwitterPagerAdapter(
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mTwitterPagerAdapter);
		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mViewPager);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 0)
					getSupportActionBar().setTitle("Followers");
				else {
					getSupportActionBar().setTitle("Messages");
				}

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		// params.add(new BasicNameValuePair("q", query));
		mRequestQueue = Volley.newRequestQueue(this);

		StringRequest request = new TokenRequest(Method.POST, TOKEN_URL,
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject object = new JSONObject(response);

							TwitterConstants.ACCESS_TOKEN_VOLLEY = object
									.optString("access_token");
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							/*
							 * params.add(new BasicNameValuePair("status",
							 * String .valueOf("Hello World!!!")));
							 */
							mRequestQueue
									.add(new VolleyOAuthRequest(
											Method.GET,
											"https://api.twitter.com/1.1/statuses/update.json",
											mDownListener, new ErrorListener() {
												@Override
												public void onErrorResponse(
														VolleyError error) {

													error.printStackTrace();
												}
											}, params));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						error.printStackTrace();
					}
				});
		mRequestQueue.add(request);

	}


	public class TweetJsonListener implements Listener<JSONArray> {// Listener<JSONObject>

		@Override
		public void onResponse(JSONArray response) {
			JSONObject jsonObject = null;
			int count = response != null ? response.length() : 0;
			for (int i = 0; i < count; i++) {
				try {
					System.out.println(response.get(i).toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (jsonObject != null) {

				}
			}
		}

	}

	@SuppressLint("DefaultLocale")
	class TwitterPagerAdapter extends FragmentPagerAdapter {
		public TwitterPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return FriendsFragment.newInstance(CONTENT[position
						% CONTENT.length]);
			case 1:
				Fragment fragment = MessageFragment
						.newInstance(CONTENT[position % CONTENT.length]);
				mCallbackFragmentManager = (CallbackFragmentManager) fragment;
				return fragment;

			}
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}

	public interface CallbackFragmentManager {
		public void enterFollower(String idFollower, String nameFollower);

		@SuppressLint("DefaultLocale")
		public void newMessage(String message, String idFollower,
				String nameFollower);

	}

	@Override
	public void callEnterFollower(String idFollower, String nameFollower) {
		if (mCallbackFragmentManager instanceof Fragment)
			mCallbackFragmentManager.enterFollower(idFollower, nameFollower);

	}

	@Override
	public void callNewMessage(String message, String idFollower,
			String nameFollower) {
		if (mCallbackFragmentManager instanceof Fragment)
			mCallbackFragmentManager.newMessage(message, idFollower,
					nameFollower);
		mViewPager.setCurrentItem(1);
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(getResources().getString(R.string.exit));
		alertDialog.setMessage(getResources().getString(R.string.isExit));
		alertDialog.setPositiveButton(getResources().getString(R.string.yes),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						TwitterConstants.TOKEN = null;
						TwitterMessageActivity.super.onBackPressed();
					}
				});

		alertDialog.setNegativeButton(getResources().getString(R.string.no),
				null);
		alertDialog.show();

	}

	}
