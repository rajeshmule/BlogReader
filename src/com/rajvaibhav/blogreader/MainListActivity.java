package com.rajvaibhav.blogreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainListActivity extends ListActivity {
	
//	protected String[] mAndroidName;
	protected String[] mBlogPostTitles;
	public static final int NUMBER_OF_POSTS = 20;
	public static final String TAG = MainListActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		
		
	/*	Resources resources = getResources();
		mAndroidName = resources.getStringArray(R.array.android_name); 
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mAndroidName);
		setListAdapter(adapter);
		//String mas = getString(R.string.no_item);        
		//Toast.makeText(this,  mas, Toast.LENGTH_SHORT).show();*/
	
		if(isNetworkAvailable()){
			GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
			getBlogPostsTask.execute();
			
		}
		else {
			Toast.makeText(this, "no internet connection!", Toast.LENGTH_SHORT).show();
		}
		
 	}

	private boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		boolean isAvailable = false;
		if(networkInfo != null && networkInfo.isConnected()){
			isAvailable = true;
		}
		return isAvailable;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class GetBlogPostsTask extends AsyncTask<Object, Void, String> {

		@Override
		protected String doInBackground(Object... arg0) {
			int responseCode = -1;
			try{
				URL blogFeedUrl = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count=" + NUMBER_OF_POSTS);
				HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection(); 
				
				responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK){
					InputStream inputStream = connection.getInputStream();
					Reader reader = new InputStreamReader(inputStream);
					int contentLength = connection.getContentLength();
					char [] charArray = new char[contentLength];
					reader.read(charArray);
					String responseData = new String( charArray );
					//Log.v(TAG,responseData);
					
					JSONObject jsonResponse = new JSONObject(responseData);
					String status = jsonResponse.getString("status");
					Log.v(TAG, status);
					
					JSONArray jsonPosts = jsonResponse.getJSONArray("posts");
					for (int i = 0; i < jsonPosts.length(); i++) {
						JSONObject jsonPost = jsonPosts.getJSONObject(i);
						String title = jsonPost.getString("title");
						Log.v(TAG, "Post " + i+ ":" + title);
					}
					
				}
				else{
					Log.i(TAG, "Unsucefull responce coade: " + responseCode);
				}
				
			}
			catch(MalformedURLException e){ 
				Log.e(TAG, "Exception caught: ", e);
			}
			catch(IOException e){
				Log.e(TAG, "Exception caught: ", e);
			}
			catch(Exception e){
				Log.e(TAG, "Exception caught: ", e);
			}
			return "Code: " + responseCode;
		}
		
	}

}