package com.example.twitterclient.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import com.example.twitterclient.model.Message;

public class SortMessage implements Comparator<Message> {
	public int compare(Message left, Message right) {
		final String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(TWITTER, new Locale("en"));
		sf.setLenient(true);
		Date dateLeft = null;
		try {
			dateLeft = sf.parse(left.getCreated_at());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date dateRight = null;
		try {
			dateRight = sf.parse(right.getCreated_at());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (dateLeft != null && dateRight != null) {
			return dateRight.compareTo(dateLeft);
		} else {
			return -1;
		}
	}
}