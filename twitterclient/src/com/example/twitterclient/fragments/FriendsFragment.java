package com.example.twitterclient.fragments;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.twitterclient.R;
import com.example.twitterclient.adapter.FriendsAdapter;
import com.example.twitterclient.model.FrendsResult;
import com.example.twitterclient.model.Friend;
import com.example.twitterclient.utils.NetworkUtils;
import com.example.twitterclient.utils.TwitterApi;
import com.example.twitterclient.utils.TwitterConstants;
import com.google.gson.Gson;

public class FriendsFragment extends Fragment implements OnItemClickListener,
		OnClickListener {
	private String KEY_CONTENT = getClass().getSimpleName();
	private ListView mContactListView;
	FriendsAdapter mFriendsAdapter;
	EditText mEditMessage;
	ImageView mSendMessage;
	ProgressBar mProgressBar;
	CallerActivity mCallerActivity = null;

	public static FriendsFragment newInstance(String content) {
		FriendsFragment fragment = new FriendsFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.new_message_fragment, null);

		mContactListView = (ListView) v.findViewById(R.id.list_frends);
		mContactListView.setOnItemClickListener(this);

		mEditMessage = (EditText) v.findViewById(R.id.edit_message);
		mEditMessage.setEnabled(false);

		mSendMessage = (ImageView) v.findViewById(R.id.btn_sent_message);
		mSendMessage.setEnabled(false);
		mSendMessage.setOnClickListener(this);

		mProgressBar = (ProgressBar) v.findViewById(R.id.progress_friends);
		LoadFriends loadFriends = new LoadFriends();
		loadFriends.execute();

		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	private class LoadFriends extends AsyncTask<Void, Void, ArrayList<Friend>> {
		ArrayList<Friend> friends = new ArrayList<Friend>();

		@Override
		protected void onPreExecute() {
			mProgressBar.setVisibility(View.VISIBLE);
			mContactListView.setVisibility(View.GONE);
			super.onPreExecute();
		}

		@Override
		protected ArrayList<Friend> doInBackground(Void... params) {

			OAuthService service = new ServiceBuilder()
					.provider(TwitterApi.class).apiKey(TwitterConstants.APIKEY)
					.callback(TwitterConstants.CALLBACK_URL)
					.apiSecret(TwitterConstants.APISECRET).build();

			OAuthRequest req = new OAuthRequest(Verb.GET,
					TwitterConstants.FOLLOWERS_GET);
			service.signRequest(TwitterConstants.TOKEN, req);
			Response response = req.send();
			try {
				JSONObject json = new JSONObject(response.getBody());
				Gson gson = new Gson();
				FrendsResult frendsResult = gson.fromJson(json.toString(),
						FrendsResult.class);
				friends = frendsResult.getUsers();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return friends;
		}

		@Override
		protected void onPostExecute(ArrayList<Friend> result) {
			mProgressBar.setVisibility(View.GONE);
			mContactListView.setVisibility(View.VISIBLE);
			mFriendsAdapter = new FriendsAdapter(getActivity(), result);
			mContactListView.setAdapter(mFriendsAdapter);
			super.onPostExecute(result);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (!NetworkUtils.isOnline(getActivity())) {
			Toast.makeText(getActivity(), "Нет подключения!", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mEditMessage.setHint("Введите сообщение");
		mEditMessage.setEnabled(true);
		mSendMessage.setEnabled(true);
		mFriendsAdapter.setCurrentFriend(position);
		mCallerActivity.callEnterFollower(mFriendsAdapter.getItem(position)
				.getId(), mFriendsAdapter.getItem(position).getName());
	}

	@Override
	public void onClick(View v) {
		if (!NetworkUtils.isOnline(getActivity())) {
			Toast.makeText(getActivity(), "Нет подключения!", Toast.LENGTH_LONG)
					.show();
			return;
		}
		switch (v.getId()) {
		case R.id.btn_sent_message:
			if (mEditMessage.getText().toString().replaceAll(" ", "")
					.equals("")) {
				Toast.makeText(getActivity(), "Введите сообщение!",
						Toast.LENGTH_LONG).show();
				return;
			}
			if (mEditMessage.getText().length() > 140) {
				Toast.makeText(getActivity(),
						"Сообщение превышает допустимую длину символов.",
						Toast.LENGTH_LONG).show();
				return;
			}
			mCallerActivity.callNewMessage(mEditMessage.getText().toString(),
					mFriendsAdapter.getCurrentFriend().getId(), mFriendsAdapter
							.getCurrentFriend().getName());
			mEditMessage.setText("");
			break;

		default:
			break;
		}

	}

	public interface CallerActivity {
		public void callEnterFollower(String idFollower, String nameFollower);

		public void callNewMessage(String message, String idFollower,
				String nameFollower);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof SherlockFragmentActivity) {
			mCallerActivity = (CallerActivity) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet listener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallerActivity = null;
	}
}
