package com.example.twitterclient.utils;

import org.scribe.model.Token;

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
}
