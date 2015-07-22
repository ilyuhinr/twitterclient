package com.example.twitterclient.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

public class VolleyOAuthRequest<T> extends Request<T> {

	private HashMap<String, String> params;
	private OAuthRequest oAuthRequest;
	Response.Listener<T> mListener;

	public VolleyOAuthRequest(int method, String path,
			Response.Listener<T> listener, Response.ErrorListener errorListener) {
		super(method, path, errorListener);
		params = new HashMap<String, String>();
		mListener = listener;
	}

	public void addParameter(String key, String value) {
		params.put(key, value);
	}

	@Override
	protected Map<String, String> getParams() {
		return params;
	}

	@Override
	public String getUrl() {
		if (oAuthRequest == null) {
			buildOAuthRequest();

			for (Map.Entry<String, String> entry : oAuthRequest
					.getOauthParameters().entrySet()) {
				addParameter(entry.getKey(), entry.getValue());
			}
		}
		return super.getUrl() + getParameterString();
	}

	private void buildOAuthRequest() {
		oAuthRequest = new OAuthRequest(getVerb(), super.getUrl());
		for (Map.Entry<String, String> entry : getParams().entrySet()) {
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

	private String getParameterString() {
		StringBuilder sb = new StringBuilder("?");
		Iterator<String> keys = params.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			sb.append(String.format("&%s=%s", key, params.get(key)));
		}
		return sb.toString();
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);

	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}