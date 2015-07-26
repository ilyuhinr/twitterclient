package com.example.twitterclient.request;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.scribe.utils.OAuthEncoder;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.twitterclient.utils.OAuthUtils;

public class SendMessageOAuthRequest extends JsonObjectRequest {
	HashMap<String, String> params;

	public SendMessageOAuthRequest(int method, String url,
			Listener<JSONObject> listener,
			Response.ErrorListener errorListener, HashMap<String, String> params) {
		super(method, url, listener, errorListener);
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
		OAuthUtils authUtils = new OAuthUtils();
		return authUtils.getHeaderSendMessage(params);
	}

	@Override
	public byte[] getBody() {
		String body = "";
		for (String param : params.keySet()) {
			body = body + OAuthEncoder.encode(param) + "="
					+ OAuthEncoder.encode(params.get(param)) + "&";
		}
		body = body.substring(0, body.length() - 1);
		return body.getBytes();
	}

}