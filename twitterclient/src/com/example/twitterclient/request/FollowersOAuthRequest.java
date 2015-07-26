package com.example.twitterclient.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.twitterclient.utils.TwitterConstants;

public class FollowersOAuthRequest extends JsonObjectRequest {

	private HashMap<String, String> params;

	public FollowersOAuthRequest(int method, String url,
			Listener<JSONObject> listener,
			Response.ErrorListener errorListener, List<NameValuePair> par) {
		super(method, url, listener, errorListener);
		params = new HashMap<String, String>();
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
	public byte[] getBody() {
		return super.getBody();
	}

}