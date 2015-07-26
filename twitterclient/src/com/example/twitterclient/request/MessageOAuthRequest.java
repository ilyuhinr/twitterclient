package com.example.twitterclient.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONArray;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.twitterclient.utils.TwitterConstants;

public class MessageOAuthRequest extends JsonArrayRequest {

	public MessageOAuthRequest(int method, String url,
			Listener<JSONArray> listener, Response.ErrorListener errorListener,
			List<NameValuePair> params) {
		super(method, url,
				listener, errorListener);
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
	
	 
}