package com.example.twitterclient.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.example.twitterclient.R;
import com.example.twitterclient.activity.TwitterMessageActivity;
import com.example.twitterclient.adapter.MessageAdapter;
import com.example.twitterclient.model.Message;
import com.example.twitterclient.request.MessageOAuthRequest;
import com.example.twitterclient.request.SendMessageOAuthRequest;
import com.example.twitterclient.utils.SortMessage;
import com.example.twitterclient.utils.TwitterApi;
import com.example.twitterclient.utils.TwitterConstants;
import com.google.gson.Gson;

public class MessageFragment extends Fragment implements
		TwitterMessageActivity.CallbackFragmentManager,
		OnItemLongClickListener, OnItemClickListener {
	private String KEY_CONTENT = getClass().getSimpleName();
	private ListView mListViewMessage;
	MessageAdapter mMessageAdapter;
	ActionBar actionBar;
	LoadMessages loadMessages;
	TextView mIsEmpty;
	private ActionMode actionMode;
	String currentFollowerId;
	SendMessageListener sendMessageListener = new SendMessageListener();
	ArrayList<Message> messages;

	public static MessageFragment newInstance(String content) {
		MessageFragment fragment = new MessageFragment();
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
		View v = inflater.inflate(R.layout.list_message, null);

		mListViewMessage = (ListView) v.findViewById(R.id.list_message);
		mListViewMessage.setOnItemLongClickListener(this);
		mListViewMessage.setOnItemClickListener(this);
		mListViewMessage.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		mIsEmpty = (TextView) v.findViewById(R.id.isempty);
		mIsEmpty.setText(getActivity().getResources().getString(
				R.string.empty_user));
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	@Override
	public void onAttach(Activity activity) {
		actionBar = ((SherlockFragmentActivity) activity).getSupportActionBar();
		super.onAttach(activity);
	}

	@Override
	public void enterFollower(String idFollower, String nameFollower) {
		currentFollowerId = idFollower;
		if (mMessageAdapter != null && mMessageAdapter.getCount() > 0)
			mMessageAdapter.removeAllMessage();
		getActivity().setProgressBarIndeterminateVisibility(true);
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		TwitterMessageActivity.mRequestQueue.add(new MessageOAuthRequest(
				Method.GET, TwitterConstants.DIRECT_MESSAGES_GET,
				new MessagesListener(TwitterConstants.DIRECT_MESSAGES_GET),
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						error.printStackTrace();
						Toast.makeText(
								getActivity(),
								"Произошла ошибка при получении данных! Попробуйте позже!",
								Toast.LENGTH_LONG).show();
					}
				}, param));

		mIsEmpty.setText("Загрузка сообщений от " + nameFollower + "...");
	}

	@Override
	public void newMessage(String message, String idFollower,
			String nameFollower) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("text", message);
		params.put("user_id", idFollower);
		TwitterMessageActivity.mRequestQueue.add(new SendMessageOAuthRequest(
				Method.POST, TwitterConstants.NEW_MESSAGE_POST,
				sendMessageListener, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getActivity(),
								"Произошла ошибка при доставке сообщения!",
								Toast.LENGTH_LONG).show();
						error.printStackTrace();
					}
				}, params));
	}

	private class LoadMessages extends
			AsyncTask<String, Void, ArrayList<Message>> {

		@Override
		protected void onPreExecute() {
			getActivity().setProgressBarIndeterminateVisibility(true);
			super.onPreExecute();
		}

		@Override
		protected ArrayList<Message> doInBackground(String... params) {
			ArrayList<Message> messages = new ArrayList<Message>();
			OAuthService service = new ServiceBuilder()
					.provider(TwitterApi.class).apiKey(TwitterConstants.APIKEY)
					.callback(TwitterConstants.CALLBACK_URL)
					.apiSecret(TwitterConstants.APISECRET).build();

			OAuthRequest req = new OAuthRequest(Verb.GET,
					TwitterConstants.DIRECT_MESSAGES_GET);
			service.signRequest(TwitterConstants.TOKEN, req);
			Response response = req.send();
			try {
				JSONArray jsons = new JSONArray(response.getBody());
				for (int i = 0; i < jsons.length(); i++) {
					JSONObject jsonObject = (JSONObject) jsons.get(i);
					Gson gson = new Gson();
					Message message = gson.fromJson(jsonObject.toString(),
							Message.class);
					if (!message.getSender_id().equals(params[0])) {
						continue;
					}
					message.setSent(false);
					messages.add(message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			req = new OAuthRequest(Verb.GET,
					TwitterConstants.DIRECT_MESSAGES_SENT_GET);
			service.signRequest(TwitterConstants.TOKEN, req);
			response = req.send();
			try {
				JSONArray jsons = new JSONArray(response.getBody());
				for (int i = 0; i < jsons.length(); i++) {
					JSONObject jsonObject = (JSONObject) jsons.get(i);
					Gson gson = new Gson();
					Message message = gson.fromJson(jsonObject.toString(),
							Message.class);
					if (!message.getRecipient_id().equals(params[0])) {
						continue;
					}
					message.setSent(true);
					messages.add(message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return messages;
		}

		@Override
		protected void onPostExecute(ArrayList<Message> messages) {
			Collections.sort(messages, new SortMessage());
			SparseBooleanArray array = new SparseBooleanArray();
			for (int i = 0; i < messages.size(); i++) {
				array.put(i, false);
			}
			mMessageAdapter = new MessageAdapter(getActivity(), 0, messages,
					array);
			mListViewMessage.setAdapter(mMessageAdapter);
			getActivity().setProgressBarIndeterminateVisibility(false);
			mIsEmpty.setText("");
			if (mMessageAdapter.getCount() == 0) {
				mIsEmpty.setText("Нет сообщений");
			}
			super.onPostExecute(messages);
		}

		@Override
		protected void onCancelled() {
			getActivity().setProgressBarIndeterminateVisibility(false);
			super.onCancelled();
		}

	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.main, menu);
			actionMode = mode;
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mMessageAdapter.removeSelection();
			actionMode = null;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.menu_delete:
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Удалить сообщения");
				builder.setMessage("Вы дуйствительно хотите удалить сообщения?");
				builder.setNegativeButton("Нет",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								actionMode.finish();
							}
						});
				builder.setPositiveButton("Да",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ArrayList<String> idsMessage = new ArrayList<String>();
								SparseBooleanArray selected = mMessageAdapter
										.getSelectedIds();
								for (int i = (selected.size() - 1); i >= 0; i--) {
									if (selected.valueAt(i)) {
										Message selectedItem = mMessageAdapter
												.getItem(selected.keyAt(i));
										idsMessage.add(selectedItem.getId_str());
									}
								}
								DeleteMessage deleteMessage = new DeleteMessage(
										selected);
								deleteMessage.execute((idsMessage.toArray()));
								actionMode.finish();
							}
						});
				builder.show();
				return true;
			default:
				return false;
			}

		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return true;
		}

	};

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		onListItemSelect(position);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (actionMode != null) {
			onListItemSelect(position);
		} else {

		}

	}

	@Override
	public void onPause() {
		super.onPause();
		if (this.actionMode != null) {
			this.actionMode.finish();
		}
	}

	private void onListItemSelect(int position) {
		mMessageAdapter.toggleSelection(position);
		boolean hasCheckedItems = mMessageAdapter.getSelectedCount() > 0;

		if (hasCheckedItems && actionMode == null)
			actionMode = ((SherlockFragmentActivity) getActivity())
					.startActionMode(mActionModeCallback);
		else if (!hasCheckedItems && actionMode != null) {
			actionMode.finish();
		}
		if (actionMode != null)
			actionMode.setTitle(String.valueOf(mMessageAdapter
					.getSelectedCount()) + " selected");
	}

	private class DeleteMessage extends AsyncTask<Object, Void, Boolean> {
		ProgressDialog progressDialog;
		SparseBooleanArray selectedPosition;

		public DeleteMessage(SparseBooleanArray deletePositions) {
			selectedPosition = deletePositions;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setTitle("Удаление сообщений");
			progressDialog.setMessage("Пожалуйста подождите...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			boolean deleteMessages = true;
			OAuthService service = new ServiceBuilder()
					.provider(TwitterApi.class).apiKey(TwitterConstants.APIKEY)
					.callback(TwitterConstants.CALLBACK_URL)
					.apiSecret(TwitterConstants.APISECRET).build();
			for (Object idMessage : params) {
				OAuthRequest request = new OAuthRequest(Verb.POST,
						TwitterConstants.DELETE_MESSAGE_POST);
				request.addBodyParameter("id", idMessage.toString());
				request.addHeader("Content-Type",
						"application/x-www-form-urlencoded");
				service.signRequest(TwitterConstants.TOKEN, request);
				Response response = request.send();
				try {
					JSONObject json = new JSONObject(response.getBody());
					Gson gson = new Gson();
					Message msg = gson.fromJson(json.toString(), Message.class);
					if (msg == null || !msg.getId_str().equals(idMessage)) {
						deleteMessages = false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					deleteMessages = false;
				}
			}
			return deleteMessages;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (result) {
				for (int i = (selectedPosition.size() - 1); i >= 0; i--) {
					if (selectedPosition.valueAt(i)) {
						Message selectedItem = mMessageAdapter
								.getItem(selectedPosition.keyAt(i));
						mMessageAdapter.remove(selectedItem);
					}
					Toast.makeText(
							getActivity(),
							getActivity().getResources().getString(
									R.string.delete_successfuly),
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(getActivity(),
						"Во время удаления произошла ошибка!",
						Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}
	}

	class SendMessageListener implements Listener<JSONObject> {

		@Override
		public void onResponse(JSONObject response) {
			Gson gson = new Gson();
			Message message = gson.fromJson(response.toString(), Message.class);
			if (message != null) {
				mMessageAdapter.addMessage(message);
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.success_send_message),
						Toast.LENGTH_LONG).show();
			}

		}
	}

	class MessagesListener implements Listener<JSONArray> {
		String url;

		public MessagesListener(String url) {
			this.url = url;
		}

		@Override
		public void onResponse(JSONArray response) {
			if (messages == null)
				messages = new ArrayList<Message>();
			Gson gson = new Gson();
			for (int i = 0; i < response.length(); i++) {
				JSONObject jsonObject = null;
				try {
					jsonObject = (JSONObject) response.get(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (jsonObject != null) {
					Message message = gson.fromJson(jsonObject.toString(),
							Message.class);
					if (url.equalsIgnoreCase(TwitterConstants.DIRECT_MESSAGES_GET)) {
						message.setSent(false);
						if (!message.getSender_id().equals(currentFollowerId)) {
							continue;
						}
					} else {
						message.setSent(true);
						if (!message.getRecipient_id()
								.equals(currentFollowerId)) {
							continue;
						}
					}

					messages.add(message);

				}

			}
			if (url.equalsIgnoreCase(TwitterConstants.DIRECT_MESSAGES_GET)) {
				ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
				TwitterMessageActivity.mRequestQueue
						.add(new MessageOAuthRequest(
								Method.GET,
								TwitterConstants.DIRECT_MESSAGES_SENT_GET,
								new MessagesListener(
										TwitterConstants.DIRECT_MESSAGES_SENT_GET),
								new ErrorListener() {
									@Override
									public void onErrorResponse(
											VolleyError error) {
										getActivity()
												.setProgressBarIndeterminateVisibility(
														false);
										error.printStackTrace();
										Toast.makeText(
												getActivity(),
												"Произошла ошибка при получении данных! Попробуйте позже!",
												Toast.LENGTH_LONG).show();
									}
								}, param));
			} else {
				Collections.sort(messages, new SortMessage());
				SparseBooleanArray array = new SparseBooleanArray();
				for (int i = 0; i < messages.size(); i++) {
					array.put(i, false);
				}
				mMessageAdapter = new MessageAdapter(getActivity(), 0,
						messages, array);
				mListViewMessage.setAdapter(mMessageAdapter);
				getActivity().setProgressBarIndeterminateVisibility(false);
				mIsEmpty.setText("");
				if (mMessageAdapter.getCount() == 0) {
					mIsEmpty.setText("Нет сообщений");
				}
				getActivity().setProgressBarIndeterminateVisibility(false);
			}
		}
	}

}
