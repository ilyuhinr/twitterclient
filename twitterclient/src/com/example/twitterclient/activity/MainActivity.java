package com.example.twitterclient.activity;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.example.twitterclient.R;
import com.example.twitterclient.utils.NetworkUtils;
import com.example.twitterclient.utils.TwitterApi;
import com.example.twitterclient.utils.TwitterConstants;
import com.example.twitterclient.utils.VerifyOAuthToken;
import com.example.twitterclient.utils.VerifyTokenCallback;

public class MainActivity extends SherlockActivity implements
		VerifyTokenCallback {
	WebView mTwitterWebView;
	Button mButtonIsConnect;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		mTwitterWebView = (WebView) findViewById(R.id.twitterWebView);
		mButtonIsConnect = (Button) findViewById(R.id.isConnect);
		mTwitterWebView.setVisibility(View.GONE);
		if (NetworkUtils.isOnline(this)) {
			buildAuth();
		} else {
			getSherlock().setProgressBarIndeterminateVisibility(false);
			Toast.makeText(this, "Нет подключения к интернету!",
					Toast.LENGTH_LONG).show();
			mTwitterWebView.setVisibility(View.GONE);
			mButtonIsConnect.setVisibility(View.VISIBLE);
		}
		mButtonIsConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (NetworkUtils.isOnline(MainActivity.this)) {
					mButtonIsConnect.setVisibility(View.GONE);
					buildAuth();
				} else {
					Toast.makeText(MainActivity.this,
							"Нет подключения к интернету! Повторите попытку.",
							Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	public void buildAuth() {
		LoadWebView loadWebView = new LoadWebView();
		loadWebView.execute();
	}

	@Override
	protected void onResume() {
		if (NetworkUtils.isOnline(MainActivity.this)) {
			if (TwitterConstants.TOKEN != null) {
				VerifyOAuthToken verifyOAuthToken = new VerifyOAuthToken(this);
				verifyOAuthToken.setOnVerifyTokenCallback(this);
				verifyOAuthToken.execute();
			} else {
				mTwitterWebView.setVisibility(View.GONE);
				mButtonIsConnect.setVisibility(View.GONE);
				buildAuth();
			}

		} else {
			Toast.makeText(MainActivity.this,
					getResources().getString(R.string.no_connect),
					Toast.LENGTH_LONG).show();

		}
		super.onResume();
	}

	private class LoadWebView extends AsyncTask<Void, Void, String> {
		OAuthService s;
		Token requestToken;

		@Override
		protected void onPreExecute() {
			getSherlock().setProgressBarIndeterminateVisibility(true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			s = new ServiceBuilder().provider(TwitterApi.SSL.class)
					.apiKey(TwitterConstants.APIKEY)
					.apiSecret(TwitterConstants.APISECRET)
					.callback(TwitterConstants.CALLBACK_URL).build();

			requestToken = s.getRequestToken();
			final String authURL = s.getAuthorizationUrl(requestToken);
			if (authURL.startsWith(TwitterConstants.CALLBACK_URL)) {
				mTwitterWebView.setVisibility(View.GONE);
				return "";
			} else {
				return authURL;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.length() > 0) {
				mTwitterWebView.loadUrl(result);
				mTwitterWebView.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(final WebView view,
							final String url) {
						if (url.startsWith(TwitterConstants.CALLBACK_URL)) {
							mTwitterWebView.setVisibility(View.GONE);
							new Thread() {
								public void run() {
									Uri uri = Uri.parse(url);
									String verifier = uri
											.getQueryParameter("oauth_verifier");
									Verifier v = new Verifier(verifier);
									TwitterConstants.VERIFER = v.getValue();
									Token accessToken = s.getAccessToken(
											requestToken, v);
									TwitterConstants.ACCESS_TOKEN = accessToken
											.getToken();
									TwitterConstants.TOKEN = accessToken;

									startActivity(new Intent(MainActivity.this,
											TwitterMessageActivity.class));
									overridePendingTransition(
											R.anim.animation_layout,
											R.anim.animation_layout_back);
								}
							}.start();
							return true;
						}

						return super.shouldOverrideUrlLoading(view, url);
					}
				});
				mTwitterWebView.setVisibility(View.VISIBLE);
				getSherlock().setProgressBarIndeterminateVisibility(false);
			}
			super.onPostExecute(result);
		}
	}

	@Override
	public void isVerify(boolean verify) {
		if (verify) {
			startActivity(new Intent(MainActivity.this,
					TwitterMessageActivity.class));
		} else {
			mTwitterWebView.setVisibility(View.GONE);
			mButtonIsConnect.setVisibility(View.GONE);
			buildAuth();
		}

	}

	

}
