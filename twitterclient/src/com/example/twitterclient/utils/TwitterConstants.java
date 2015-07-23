package com.example.twitterclient.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.scribe.model.Token;

import android.util.Base64;

public class TwitterConstants {

	public static String APIKEY = "POcOK3BhojAABQebrcaoN5gI0";
	public static String APISECRET = "CozQT3BPBf5W97KBJkf7lSNtSVO2vQwq0GeaHtnzIyTNblwRkA";
	public static String ACCESS_TOKEN = "";
	public static String ACCESS_TOKEN_VOLLEY = "";
	public static Token TOKEN = null;
	public static String DIRECT_MESSAGES_GET = "https://api.twitter.com/1.1/direct_messages.json";
	public static String FRIENDS_GET = "https://api.twitter.com/1.1/friends/list.json";
	public static String DIRECT_MESSAGES_SENT_GET = "https://api.twitter.com/1.1/direct_messages/sent.json";
	public static String FOLLOWERS_GET = "https://api.twitter.com/1.1/followers/list.json";
	public static final String CALLBACK_URL = "http://www.example.ru";
	public static String NEW_MESSAGE_POST = "https://api.twitter.com/1.1/direct_messages/new.json";
	public static String DELETE_MESSAGE_POST = "https://api.twitter.com/1.1/direct_messages/destroy.json";
	public static String VERIFY_TOKEN_GET = "https://api.twitter.com/1.1/account/verify_credentials.json";
	public static String VERIFER = "";
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	public static HashMap<String, String> HEADER_AUTH = null;

	public static String getAuthHeader(String url) {
		String request_base_url = url;
		String oAuthConsumerKey = TwitterConstants.APIKEY;
		String stringNonce = generateNonce()/*
											 * String.valueOf(randInt(10000,
											 * 99999)) +
											 * String.valueOf(randInt(10000,
											 * 99999))
											 */;
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
				stringRequestParams.length() - 1);
		String outputString = "";
		outputString = "GET";
		outputString = outputString + "&";
		outputString = outputString + OAuth.percentEncode(request_base_url);
		outputString = outputString + "&";
		outputString = outputString + OAuth.percentEncode(stringRequestParams);
		final String stringSignatureBase = outputString;
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
			mapKeyValue.put("oauth_signature", signature);
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		String result = "";
		Object[] keyses = mapKeyValue.keySet().toArray();
		Arrays.sort(keyses);
		for (Object key : keyses) {
			result = result + key + "=\"" + mapKeyValue.get(key) + "\", ";
		}
		result = result.substring(0, result.length() - 2);
		/*
		 * stringRequestParams = "OAuth " + stringRequestParams + " ," +
		 * "oauth_signature=\"" + signature + "\""; mapKeyValue.clear();
		 * mapKeyValue.put(OAuth.percentEncode("OAuth oauth_signature"),
		 * OAuth.percentEncode(signature));
		 * mapKeyValue.put(OAuth.percentEncode("oauth_consumer_key"),
		 * OAuth.percentEncode(oAuthConsumerKey));
		 * mapKeyValue.put(OAuth.percentEncode("oauth_timestamp"),
		 * OAuth.percentEncode("" + timeStamp));
		 * mapKeyValue.put(OAuth.percentEncode("oauth_signature_method"),
		 * OAuth.percentEncode(oauth_signature_method));
		 * mapKeyValue.put(OAuth.percentEncode("oauth_nonce"),
		 * OAuth.percentEncode(generateNonce()));
		 * mapKeyValue.put(OAuth.percentEncode("oauth_version"),
		 * OAuth.percentEncode(oauth_version));
		 */
		mapKeyValue.put(OAuth.percentEncode("oauth_signature"),
				OAuth.percentEncode(signature));
		return generateAuthorizationHeader((HashMap<String, String>) mapKeyValue);

	}

	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
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

	private static String generateNonce() {
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

	private static String generateAuthorizationHeader(
			HashMap<String, String> param) {
		String DST = "";
		DST = DST + "OAuth ";

		DST = DST + OAuth.percentEncode("oauth_consumer_key");
		DST = DST + "=";
		DST = DST + "\"";
		DST = DST + OAuth.percentEncode(param.get("oauth_consumer_key"));
		DST = DST + "\"";
		DST = DST + ", ";

		DST = DST + OAuth.percentEncode("oauth_nonce");
		DST = DST + "=";
		DST = DST + "\"";
		DST = DST + OAuth.percentEncode(param.get("oauth_nonce"));
		DST = DST + "\"";
		DST = DST + ", ";

		DST = DST + OAuth.percentEncode("oauth_signature");
		DST = DST + "=";
		DST = DST + "\"";
		DST = DST + OAuth.percentEncode(param.get("oauth_signature"));
		DST = DST + "\"";
		DST = DST + ", ";

		DST = DST + OAuth.percentEncode("oauth_signature_method");
		DST = DST + "=";
		DST = DST + "\"";
		DST = DST + OAuth.percentEncode(param.get("oauth_signature_method"));
		DST = DST + "\"";
		DST = DST + ", ";

		DST = DST + OAuth.percentEncode("oauth_timestamp");
		DST = DST + "=";
		DST = DST + "\"";
		DST = DST + OAuth.percentEncode("" + param.get("oauth_timestamp"));
		DST = DST + "\"";
		DST = DST + ", ";

		DST = DST + OAuth.percentEncode("oauth_token");
		DST = DST + "=";
		DST = DST + "\"";
		DST = DST + OAuth.percentEncode(param.get("oauth_token"));
		DST = DST + "\"";
		DST = DST + ", ";

		DST = DST + OAuth.percentEncode("oauth_version");
		DST = DST + "=";
		DST = DST + "\"";
		DST = DST + OAuth.percentEncode(param.get("oauth_version"));
		DST = DST + "\"";

		return DST.replace("253D", "3D");
	}
}
