package com.example.twitterclient.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;

public class VolleyOAuthRequest extends JsonArrayRequest {

	private HashMap<String, String> params;
	private HashMap<String, String> header_params;
	private OAuthRequest oAuthRequest;

	public VolleyOAuthRequest(int method, String path,
			Listener<JSONArray> listener, Response.ErrorListener errorListener,
			List<NameValuePair> par) {
		super(method, TwitterConstants.DIRECT_MESSAGES_SENT_GET, listener,
				errorListener);
		params = new HashMap<String, String>();
		// params.put(par.get(0).getName(), par.get(0).getValue());
		header_params = new HashMap<String, String>();
		// mListener = listener;
	}

	public void addParameter(String key, String value) {
		// params.put(key, value);
		header_params.put(key, value);
	}

	@Override
	protected Map<String, String> getParams() {

		return params;
	}

	@Override
	public String getUrl() {
		if (oAuthRequest == null) {
			buildOAuthRequest();
			header_params = (HashMap<String, String>) oAuthRequest.getHeaders();
			/*
			 * for (Map.Entry<String, String> entry : oAuthRequest
			 * .getOauthParameters().entrySet()) { addParameter(entry.getKey(),
			 * entry.getValue()); }
			 */
		}
		return super.getUrl();// + getParameterString();
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> par = new HashMap<String, String>();
		par.put("Authorization", TwitterConstants.getAuthHeader(super.getUrl()));
		return par;
	}

	private void buildOAuthRequest() {
		oAuthRequest = new OAuthRequest(getVerb(), super.getUrl());
		for (Map.Entry<String, String> entry : getParams().entrySet()) {
			// oAuthRequest.addBodyParameter(entry.getKey(), entry.getValue());
			oAuthRequest.addQuerystringParameter(entry.getKey(),
					entry.getValue());
		}
		Token token = TwitterConstants.TOKEN;
		OAuthService s = new ServiceBuilder().provider(TwitterApi.SSL.class)
				.apiKey(TwitterConstants.APIKEY)
				.apiSecret(TwitterConstants.APISECRET)
				.callback(TwitterConstants.CALLBACK_URL).build();
		s.signRequest(token, oAuthRequest);
	}

	private Verb getVerb() {
		switch (getMethod()) {
		case Method.GET:
			return Verb.GET;
		case Method.DELETE:
			return Verb.DELETE;
		case Method.POST:
			return Verb.POST;
		case Method.PUT:
			return Verb.PUT;
		default:
			return Verb.GET;
		}
	}

	@Override
	public String getBodyContentType() {
		return "application/x-www-form-urlencoded";
	}

	@Override
	public byte[] getBody() {
		return super.getBody();
	}

}