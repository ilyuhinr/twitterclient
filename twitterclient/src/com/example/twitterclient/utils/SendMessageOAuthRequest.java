package com.example.twitterclient.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

public class SendMessageOAuthRequest extends JsonObjectRequest {
	HashMap<String, String> params;
	OAuthRequest request;

	public SendMessageOAuthRequest(int method, String path,
			Listener<JSONObject> listener,
			Response.ErrorListener errorListener, HashMap<String, String> params) {
		super(method, TwitterConstants.NEW_MESSAGE_POST, listener,
				errorListener);
		this.params = params;
	}

	@Override
	protected Map<String, String> getParams() {
		return params;
	}

	@Override
	public String getUrl() {
		return super.getUrl();
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> par = new HashMap<String, String>();
		par.put("Content-Type", "application/x-www-form-urlencoded");
		par.put("Authorization",
				TwitterConstants.getAuthHeader(super.getUrl(), "POST"));
		OAuthService service = new ServiceBuilder().provider(TwitterApi.class)
				.apiKey(TwitterConstants.APIKEY)
				.callback(TwitterConstants.CALLBACK_URL)
				.apiSecret(TwitterConstants.APISECRET).build();
		OAuthRequest request = new OAuthRequest(Verb.POST,
				TwitterConstants.NEW_MESSAGE_POST);
		request.addBodyParameter("user_id", params.get("user_id"));
		request.addBodyParameter("text", params.get("text"));
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
		service.signRequest(TwitterConstants.TOKEN, request);
		return par;
	}

	/*
	 * @Override public String getBodyContentType() { return
	 * "application/x-www-form-urlencoded"; }
	 */

	@Override
	public byte[] getBody() {
		String body = "";
		for (String param : params.keySet()) {
			body = body + OAuth.percentEncode(param) + "="
					+ OAuth.percentEncode(params.get(param)) + "&";
		}
		body = body.substring(0, body.length() - 1);
		return body.getBytes();
	}

}