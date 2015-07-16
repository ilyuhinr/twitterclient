package com.example.twitterclient.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.twitterclient.R;
import com.example.twitterclient.model.Message;
import com.example.twitterclient.utils.SortMessage;

public class MessageAdapter extends ArrayAdapter<Message> {
	Context mContext;
	ArrayList<Message> messages;
	LayoutInflater layoutInflater;
	private SparseBooleanArray mSelectedItemsIds;

	public MessageAdapter(Context context, int ids,
			ArrayList<Message> messages, SparseBooleanArray array) {
		super(context, ids, messages);
		mContext = context;
		this.messages = messages;
		layoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mSelectedItemsIds = new SparseBooleanArray();
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Message getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		long id = 0;
		try {
			id = Long.valueOf(messages.get(position).getId_str());
		} catch (NumberFormatException e) {
			Log.e(getClass().getName(), e.toString());
		}
		return id;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = layoutInflater.inflate(R.layout.message_item, null, true);
		Message msg = getItem(position);
		final TextView msgText = (TextView) v.findViewById(R.id.message_text);
		TextView time = (TextView) v.findViewById(R.id.message_time);
		msgText.setText(msg.getText());
		time.setText(getDate(msg.getCreated_at()));
		alignMsg(v, msg.isSent(), true);
		v.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4
				: Color.TRANSPARENT);
		return v;
	}

	private void alignMsg(View view, boolean isOutgoing, boolean isChecked) {
		View mainLayout = view.findViewById(R.id.message_type);
		View time = view.findViewById(R.id.message_time);
		TextView text = (TextView) view.findViewById(R.id.message_text);
		RelativeLayout.LayoutParams mainParams = (RelativeLayout.LayoutParams) mainLayout
				.getLayoutParams();
		RelativeLayout.LayoutParams timeParams = (RelativeLayout.LayoutParams) time
				.getLayoutParams();
		LinearLayout.LayoutParams textParams = (LinearLayout.LayoutParams) text
				.getLayoutParams();

		int ten = (int) mContext.getResources().getDimension(R.dimen.margin);
		int thirteen = (int) mContext.getResources().getDimension(
				R.dimen.thirteen);
		if (isOutgoing) {
			mainLayout.setBackgroundResource(R.drawable.message_left);
			mainLayout.setPadding(ten, ten, ten, ten);
			mainParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
			mainParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			timeParams.addRule(RelativeLayout.RIGHT_OF, 0);
			timeParams.addRule(RelativeLayout.LEFT_OF, R.id.message_type);
			mainLayout.setLayoutParams(mainParams);
			time.setLayoutParams(timeParams);
			textParams.leftMargin = 0;
			textParams.rightMargin = (int) mContext.getResources()
					.getDimension(R.dimen.margin);
			text.setTextColor(Color.WHITE);
			text.setLinkTextColor(mContext.getResources().getColor(
					R.color.default_title_indicator_selected_color));
			if (isChecked) {
				view.findViewById(R.id.message_mark)
						.setVisibility(View.VISIBLE);
			} else {
				view.findViewById(R.id.message_mark).setVisibility(View.GONE);
			}
		} else {
			mainLayout.setBackgroundResource(R.drawable.message_right);
			mainLayout.setPadding(ten, thirteen, ten, thirteen);
			mainParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
			mainParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			timeParams.addRule(RelativeLayout.LEFT_OF, 0);
			timeParams.addRule(RelativeLayout.RIGHT_OF, R.id.message_type);
			mainLayout.setLayoutParams(mainParams);
			time.setLayoutParams(timeParams);
			textParams.rightMargin = 0;
			textParams.leftMargin = (int) mContext.getResources().getDimension(
					R.dimen.margin);
			text.setTextColor(Color.BLACK);
			text.setLinkTextColor(mContext.getResources().getColor(
					R.color.default_title_indicator_selected_color));
		}
	}

	public void removeAllMessage() {
		messages.clear();
		notifyDataSetChanged();
	}

	public void addMessage(Message message) {
		messages.add(message);
		Collections.sort(messages, new SortMessage());
		notifyDataSetChanged();
	}

	public void toggleSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}

	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	public void selectView(int position, boolean value) {
		if (value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);

		notifyDataSetChanged();
	}

	public int getSelectedCount() {
		return mSelectedItemsIds.size();
	}

	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}

	public void setSparseBooleanArray(SparseBooleanArray booleanArray) {
		this.mSelectedItemsIds = booleanArray;
	}

	@SuppressLint("SimpleDateFormat")
	public String getDate(String datetime) {
		final String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(TWITTER, new Locale("en"));
		sf.setLenient(true);
		Date date = null;
		try {
			date = sf.parse(datetime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateToString = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		return date == null ? "" : dateToString
				.format(date);

	}
}
