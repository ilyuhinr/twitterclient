package com.example.twitterclient.utils;

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
import org.scribe.utils.OAuthEncoder;

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

	public static String getAuthHeader(String url, String mehtod) {
		String request_base_url = url;
		String oAuthConsumerKey = TwitterConstants.APIKEY;
		String stringNonce = genNonce();
		String oauth_signature_method = "HMAC-SHA1";
		Long timeStamp = System.currentTimeMillis() / 1000;
		String oauth_token = TwitterConstants.TOKEN.getToken();
		String oauth_version = "1.0";
		Map<String, String> mapKeyValue = new HashMap<String, String>();
		mapKeyValue.put(OAuthEncoder.encode("oauth_consumer_key"),
				OAuthEncoder.encode(oAuthConsumerKey));
		mapKeyValue.put(OAuthEncoder.encode("oauth_nonce"),
				OAuthEncoder.encode(stringNonce));
		mapKeyValue.put(OAuthEncoder.encode("oauth_signature_method"),
				OAuthEncoder.encode(oauth_signature_method));
		mapKeyValue.put(OAuthEncoder.encode("oauth_timestamp"),
				OAuthEncoder.encode("" + timeStamp));
		mapKeyValue.put(OAuthEncoder.encode("oauth_token"),
				OAuthEncoder.encode(oauth_token));
		mapKeyValue.put(OAuthEncoder.encode("oauth_version"),
				OAuthEncoder.encode(oauth_version));

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
		outputString = mehtod;
		outputString = outputString + "&";
		outputString = outputString + OAuthEncoder.encode(request_base_url);
		outputString = outputString + "&";
		outputString = outputString + OAuthEncoder.encode(stringRequestParams);
		final String stringSignatureBase = outputString;
		String signing_key = "";
		String consumer_secret = TwitterConstants.APISECRET;
		String access_token_secret = TwitterConstants.TOKEN.getSecret();
		signing_key = consumer_secret + "&" + access_token_secret;
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

	private static String generateAuthorizationHeader(
			HashMap<String, String> param) {
		String resultHeader = "";
		resultHeader = resultHeader + "OAuth ";
		resultHeader = resultHeader + OAuthEncoder.encode("oauth_signature");
		resultHeader = resultHeader + "=";
		resultHeader = resultHeader + "\"";
		resultHeader = resultHeader + OAuthEncoder.encode(param.get("oauth_signature"));
		resultHeader = resultHeader + "\"";
		resultHeader = resultHeader + ", ";

		resultHeader = resultHeader + OAuthEncoder.encode("oauth_version");
		resultHeader = resultHeader + "=";
		resultHeader = resultHeader + "\"";
		resultHeader = resultHeader + OAuthEncoder.encode(param.get("oauth_version"));
		resultHeader = resultHeader + "\"";
		resultHeader = resultHeader + ", ";

		resultHeader = resultHeader + OAuthEncoder.encode("oauth_nonce");
		resultHeader = resultHeader + "=";
		resultHeader = resultHeader + "\"";
		resultHeader = resultHeader + OAuthEncoder.encode(param.get("oauth_nonce"));
		resultHeader = resultHeader + "\"";
		resultHeader = resultHeader + ", ";

		resultHeader = resultHeader + OAuthEncoder.encode("oauth_signature_method");
		resultHeader = resultHeader + "=";
		resultHeader = resultHeader + "\"";
		resultHeader = resultHeader + OAuthEncoder.encode(param.get("oauth_signature_method"));
		resultHeader = resultHeader + "\"";
		resultHeader = resultHeader + ", ";

		resultHeader = resultHeader + OAuthEncoder.encode("oauth_consumer_key");
		resultHeader = resultHeader + "=";
		resultHeader = resultHeader + "\"";
		resultHeader = resultHeader + OAuthEncoder.encode(param.get("oauth_consumer_key"));
		resultHeader = resultHeader + "\"";
		resultHeader = resultHeader + ", ";

		resultHeader = resultHeader + OAuthEncoder.encode("oauth_timestamp");
		resultHeader = resultHeader + "=";
		resultHeader = resultHeader + "\"";
		resultHeader = resultHeader + "" + OAuthEncoder.encode(param.get("oauth_timestamp"));
		resultHeader = resultHeader + "\"";
		resultHeader = resultHeader + ", ";

		resultHeader = resultHeader + OAuthEncoder.encode("oauth_token");
		resultHeader = resultHeader + "=";
		resultHeader = resultHeader + "\"";
		resultHeader = resultHeader + OAuthEncoder.encode(param.get("oauth_token"));
		resultHeader = resultHeader + "\"";

		return resultHeader.replace("253D", "3D");
	}

	public static String genNonce() {
		Random gen = new Random(System.currentTimeMillis());
		StringBuilder nonceBuilder = new StringBuilder("");
		String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int baseLength = base.length();

		// Taking random word characters
		for (int i = 0; i < 32; ++i) {
			int position = gen.nextInt(baseLength);
			nonceBuilder.append(base.charAt(position));
		}

		String nonce = Base64.encodeToString(
				nonceBuilder.toString().getBytes(), Base64.NO_WRAP);
		return nonce;
	}

}
