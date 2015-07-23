package com.example.twitterclient.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONObject;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

public class TweetsRequest extends JsonObjectRequest {
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	public TweetsRequest(int met, String url, Listener<JSONObject> listener,
			ErrorListener errorListener, List<NameValuePair> params) {
		super(met, url + "?" + URLEncodedUtils.format(params, "UTF-8"),
				(new String()), listener, errorListener);
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = new HashMap<String, String>();
		/*
		 * String auth = "Bearer " + TwitterConstants.ACCESS_TOKEN_VOLLEY;
		 * headers.put("Authorization", auth);
		 */
		/*
		 * headers.put("Authorization", "Basic " +
		 * TwitterConstants.TOKEN.getRawResponse());
		 */

		/* headers.put("Authorization: OAuth ", testHTTPRequest()); */
		String auth = "Bearer " + TwitterConstants.ACCESS_TOKEN;
		headers.put("Authorization", auth);
		return headers;
	}

	private String generateNonce() {
		try {
			byte[] nonceByteArray = new byte[32];
			String nonceString = Base64.encodeToString(nonceByteArray,
					Base64.NO_WRAP);
			return nonceString.replaceAll("[^\\p{L}\\p{Nd}]+", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String testHTTPRequest() {
		String request_base_url = "https://api.twitter.com/1.1/statuses/update.json";
		String oAuthConsumerKey = TwitterConstants.APIKEY;
		String stringNonce = generateNonce();
		String oauth_signature_method = "HMAC-SHA1";
		Long timeStamp = System.currentTimeMillis() / 1000;
		String oauth_token = TwitterConstants.TOKEN.getToken();
		String oauth_version = "1.0";
		Map<String, String> mapKeyValue = new HashMap<String, String>();
		mapKeyValue.put(OAuth.percentEncode("oauth_consumer_key"),
				OAuth.percentEncode(oAuthConsumerKey));
		mapKeyValue.put(OAuth.percentEncode("oauth_nonce"),
				OAuth.percentEncode(stringNonce));
		mapKeyValue.put(OAuth.percentEncode("oauth_signature_method"),
				OAuth.percentEncode(oauth_signature_method));
		mapKeyValue.put(OAuth.percentEncode("oauth_timestamp"),
				OAuth.percentEncode("" + timeStamp));
		mapKeyValue.put(OAuth.percentEncode("oauth_token"),
				OAuth.percentEncode(oauth_token));
		mapKeyValue.put(OAuth.percentEncode("oauth_version"),
				OAuth.percentEncode(oauth_version));

		String stringRequestParams = "";

		Object[] keys = mapKeyValue.keySet().toArray();
		Arrays.sort(keys);
		for (Object key : keys) {
			stringRequestParams = stringRequestParams + key + "="
					+ mapKeyValue.get(key) + "&";
		}
		stringRequestParams = stringRequestParams.substring(0,
				stringRequestParams.length() - 1); // to remove the last "&"

		// Create request signature:
		// https://dev.twitter.com/oauth/overview/creating-signatures
		String outputString = "";
		// Convert the HTTP Method to uppercase and set the output string equal
		// to this value.
		outputString = "GET";
		// Append the ‘&’ character to the output string.
		outputString = outputString + "&";
		// Percent encode the URL and append it to the output string.
		outputString = outputString + OAuth.percentEncode(request_base_url);
		// Append the ‘&’ character to the output string.
		outputString = outputString + "&";
		// Percent encode the parameter string and append it to the output
		// string.
		outputString = outputString + OAuth.percentEncode(stringRequestParams);
		final String stringSignatureBase = outputString;

		// Calculate signature:
		// https://dev.twitter.com/oauth/overview/creating-signatures
		String signing_key = "";
		String consumer_secret = TwitterConstants.APISECRET;
		String access_token_secret = TwitterConstants.TOKEN.getSecret();
		signing_key = OAuth.percentEncode(consumer_secret) + "&"
				+ OAuth.percentEncode(access_token_secret);
		String signature = "";
		try {
			signature = Base64.encodeToString(
					calculateRFC2104HMAC(stringSignatureBase, signing_key),
					Base64.NO_WRAP);
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		stringRequestParams = stringRequestParams + "&" + "oauth_signature="
				+ signature;
		mapKeyValue.clear();
		mapKeyValue.put("Content-Type: ", "application/x-www-form-urlencoded");
		mapKeyValue.put(OAuth.percentEncode("OAuth oauth_signature"),
				OAuth.percentEncode(signature));
		mapKeyValue.put(OAuth.percentEncode("oauth_consumer_key"),
				OAuth.percentEncode(oAuthConsumerKey));
		mapKeyValue.put(OAuth.percentEncode("oauth_timestamp"),
				OAuth.percentEncode("" + timeStamp));
		mapKeyValue.put(OAuth.percentEncode("oauth_signature_method"),
				OAuth.percentEncode(oauth_signature_method));
		mapKeyValue.put(OAuth.percentEncode("oauth_nonce"),
				OAuth.percentEncode(generateNonce()));
		mapKeyValue.put(OAuth.percentEncode("oauth_version"),
				OAuth.percentEncode(oauth_version));
		return stringRequestParams;
	}

	public static byte[] calculateRFC2104HMAC(String data, String key)
			throws SignatureException, NoSuchAlgorithmException,
			InvalidKeyException {
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),
				HMAC_SHA1_ALGORITHM);
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		return mac.doFinal(data.getBytes());
	}
}
