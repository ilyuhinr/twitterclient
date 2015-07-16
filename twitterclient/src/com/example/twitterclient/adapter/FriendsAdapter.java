package com.example.twitterclient.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.twitterclient.R;
import com.example.twitterclient.activity.TwitterMessageActivity;
import com.example.twitterclient.model.Friend;

public class FriendsAdapter extends BaseAdapter {
	Context mContext;
	ArrayList<Friend> friends;
	LayoutInflater layoutInflater;
	Friend currentFriend;

	public FriendsAdapter(Context context, ArrayList<Friend> friends) {
		mContext = context;
		this.friends = friends;
		layoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (friends != null)
			return friends.size();
		else {
			return 0;
		}
	}

	@Override
	public Friend getItem(int position) {
		return friends.get(position);
	}

	@Override
	public long getItemId(int position) {
		long id = 0;
		try {
			id = Long.valueOf(friends.get(position).getId());
		} catch (NumberFormatException e) {
			Log.e(getClass().getName(), e.toString());
		}
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = layoutInflater.inflate(R.layout.friend_item, null, true);
		Friend friend = getItem(position);
		TextView nameFriend = (TextView) v.findViewById(R.id.name_friend);
		TextView emailFriend = (TextView) v.findViewById(R.id.location_friend);
		ImageView cur_indicator = (ImageView) v.findViewById(R.id.indicator);
		NetworkImageView profile_icon = (NetworkImageView) v
				.findViewById(R.id.image_profile);
		nameFriend.setText(friend.getName());
		emailFriend.setText("Location: " + friend.getLocation());
		profile_icon.setImageUrl(friend.getProfile_image_url(),
				TwitterMessageActivity.mImageLoader);
		if (friend.isCurrent()) {
			cur_indicator.setVisibility(View.VISIBLE);
		} else {
			cur_indicator.setVisibility(View.INVISIBLE);
		}
		return v;
	}

	public void setCurrentFriend(int position) {
		if (currentFriend == getItem(position)) {
			return;
		}
		if (currentFriend != null)
			currentFriend.setCurrent(false);
		getItem(position).setCurrent(true);
		currentFriend = getItem(position);
		notifyDataSetChanged();
	}

	public Friend getCurrentFriend() {
		return currentFriend;
	}

}
