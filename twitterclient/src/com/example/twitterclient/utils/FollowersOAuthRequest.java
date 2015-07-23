package com.example.twitterclient.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

public class FollowersOAuthRequest extends JsonObjectRequest {

	private HashMap<String, String> params;
	private HashMap<String, String> header_params;

	public FollowersOAuthRequest(int method, String path,
			Listener<JSONObject> listener,
			Response.ErrorListener errorListener, List<NameValuePair> par) {
		super(method, path, listener, errorListener);
		params = new HashMap<String, String>();
		header_params = new HashMap<String, String>();
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
		return super.getUrl();
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> par = new HashMap<String, String>();
		par.put("Authorization",
				TwitterConstants.getAuthHeader(super.getUrl(), "GET"));
		return par;
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