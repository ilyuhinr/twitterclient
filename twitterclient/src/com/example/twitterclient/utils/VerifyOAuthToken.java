package com.example.twitterclient.utils;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class VerifyOAuthToken extends AsyncTask<Void, Void, Boolean> {
	Token token = TwitterConstants.TOKEN;
	OAuthService mOAuthService;
	Context mContext;
	ProgressDialog progressDialog;
	VerifyTokenCallback mVerifyTokenCallback;

	public VerifyOAuthToken(Context context) {
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setTitle("Проверка");
		progressDialog.setMessage("Проверяем токен...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (token == null) {
			return false;
		}
		mOAuthService = new ServiceBuilder().provider(TwitterApi.class)
				.apiKey(TwitterConstants.APIKEY)
				.callback(TwitterConstants.CALLBACK_URL)
				.apiSecret(TwitterConstants.APISECRET).build();
		OAuthRequest req = new OAuthRequest(Verb.GET,
				TwitterConstants.VERIFY_TOKEN_GET);
		mOAuthService.signRequest(TwitterConstants.TOKEN, req);
		Response response = req.send();
		if (response.getCode() == 200) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		progressDialog.dismiss();
		if (mVerifyTokenCallback != null) {
			mVerifyTokenCallback.isVerify(result);
		}
		super.onPostExecute(result);
	}

	public void setOnVerifyTokenCallback(VerifyTokenCallback verifyTokenCallback) {
		mVerifyTokenCallback = verifyTokenCallback;
	}
}
