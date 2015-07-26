package com.example.twitterclient.utils;

import java.util.HashMap;
import java.util.Map;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthRequest;
import org.scribe.model.SignatureType;
import org.scribe.model.Verb;

public class OAuthUtils {
	OAuthRequest request;
	MyServiceImpl serv;

	public OAuthUtils() {
		OAuthConfig config = new OAuthConfig(TwitterConstants.APIKEY,
				TwitterConstants.APISECRET, null, SignatureType.Header, null,
				System.out);

		DefaultApi10a api = new TwitterApi();
		serv = new MyServiceImpl(api, config);
	}

	public Map<String, String> getHeaderSendMessage(
			HashMap<String, String> params) {
		request = new OAuthRequest(Verb.POST, TwitterConstants.NEW_MESSAGE_POST);
		request.addBodyParameter("user_id", params.get("user_id"));
		request.addBodyParameter("text", params.get("text"));
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
		serv.signRequest(TwitterConstants.TOKEN, request);
		return request.getHeaders();
	}

}
