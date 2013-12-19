package com.reptoh.flickrjson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private static final String URL = "http://www.flickr.com/services/feeds/photos_public.gne?format=json";

	private static final String TAG = "flickr test";

	private ArrayList<Post> postsList;
	private CustomAdapter mAdapter;
	private ListView mListView;
	private PostsTask mPostsTask;
	private String response;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_main);

		postsList = new ArrayList<Post>();

		mListView = (ListView) findViewById(R.id.lv);

		mAdapter = new CustomAdapter(getApplicationContext());
		
		//mPostsTask = new PostsTask();
		
		connect();

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {

				Post selectedItem = postsList.get(index);

				Uri uri = Uri.parse(selectedItem.getLink());

				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				
			}
			
		});

	}
	
	public void connect(){
		
		ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		if(networkInfo != null && networkInfo.isConnected()){
			
			mPostsTask = new PostsTask();
			mPostsTask.execute();
			
		} else {
			
			Toast.makeText(getApplicationContext(), "Ошибка! Проверьте настройки подключения.", Toast.LENGTH_LONG).show();
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:

			if (mPostsTask != null && (mPostsTask.getStatus() == AsyncTask.Status.RUNNING
					|| mPostsTask.getStatus() == AsyncTask.Status.PENDING)) {

				mPostsTask.cancel(true);

			}

			postsList.clear();

			connect();

			break;
		default:
			break;
		}
		return true;
	}

	class PostsTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Void doInBackground(Void... param) {
			try {
				postsList = getPosts();
				mAdapter.setData(postsList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			mListView.setAdapter(mAdapter);
			setSupportProgressBarIndeterminateVisibility(false);
		}

		public ArrayList<Post> getPosts() throws Exception {

			try {
				URL url = new URL(URL);

				Log.d(TAG, "Открытие URL " + url.toString());

				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();

				urlConnection.setRequestMethod("GET");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);

				urlConnection.connect();

				response = streamToString(urlConnection.getInputStream());

				Log.v(TAG, "JSON " + response);

				int end = response.length() - 1;

				response = response.substring(15, end);

				JSONObject jsonObj = (JSONObject) new JSONTokener(response)
						.nextValue();

				JSONArray groups = (JSONArray) jsonObj.getJSONArray("items");

				Log.v(TAG, "Размер " + groups.length());

				int length = groups.length();

				if (length > 0) {
					for (int i = 0; i < length; i++) {

						JSONObject item = (JSONObject) groups.get(i);

						Post post = new Post();

						post.setTitle(item.getString("title"));
						post.setLink(item.getString("link"));
						JSONObject med = item.getJSONObject("media");
						post.setMedia(med.getString("m"));
						post.setDate(item.getString("published"));
						post.setDescription(item.getString("tags"));

						postsList.add(post);
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// Log.v(TAG, "Размер из метода " + postsList.size());
			return postsList;
		}

		private String streamToString(InputStream is) throws IOException {
			String str = "";

			if (is != null) {
				StringBuilder sb = new StringBuilder();
				String line;

				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is));

					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}

					reader.close();
				} finally {
					is.close();
				}

				str = sb.toString();
			}

			return str;
		}

	}

}
