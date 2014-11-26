package com.rajvaibhav.blogreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainListActivity extends ListActivity {
	
//	protected String[] mAndroidName;
	protected String[] mBlogPostTitles;
	public static final int NUMBER_OF_POSTS = 20;
	public static final String TAG  = MainListActivity.class.getSimpleName();
	protected JSONObject mBlogData;
	protected ProgressBar mProgressBar;
	
	
	private final String KEY_TITLE = "title"; 
	private final String KEY_AUTHOR = "author";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
	/*	Resources resources = getResources();
		mAndroidName = resources.getStringArray(R.array.android_name); 
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mAndroidName);
		setListAdapter(adapter);
		//String mas = getString(R.string.no_item);        
		//Toast.makeText(this,  mas, Toast.LENGTH_SHORT).show();*/
	
		if(isNetworkAvailable()){
			mProgressBar.setVisibility(View.VISIBLE);
			GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
			getBlogPostsTask.execute();
			
		}
		else {
			Toast.makeText(this, "no internet connection!", Toast.LENGTH_SHORT).show();
		}
		
 	}

	@Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
		
			super.onListItemClick(l, v, position, id);
			try {
	    		JSONArray jsonPosts = mBlogData.getJSONArray("posts");
	        	JSONObject jsonPost = jsonPosts.getJSONObject(position);
	        	
				String blogUrl = jsonPost.getString("url");
				
				Intent intent = new Intent(this, BlogViewActivity.class);
				intent.setData(Uri.parse(blogUrl));
				startActivity(intent);
				
			} catch (JSONException e) {
				logException(e);
			}
		}

	private void logException(Exception e) {
		Log.e(TAG,"Exception caught!",e);
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


	
	public void handleBlogResponse() {
		mProgressBar.setVisibility(View.INVISIBLE);
		if (mBlogData == null){
			updeteDisplayForError();
		}
		else{
			try {
				JSONArray jsonPosts = mBlogData.getJSONArray("posts");
				ArrayList<HashMap<String, String>> blogPosts = new ArrayList<HashMap<String, String>>();
	        	//mBlogPostTitles = new String[jsonPosts.length()];
	        	for(int i = 0; i < jsonPosts.length(); i++ ){
	        		JSONObject post = jsonPosts.getJSONObject(i);
	        		String title = post.getString(KEY_TITLE);
	        		title = Html.fromHtml(title).toString(); 
	        		//mBlogPostTitles[i]=title;
	        		String author = post.getString(KEY_AUTHOR);
	        		author = Html.fromHtml(author).toString();
	        	
	        		HashMap<String, String> blogPost = new HashMap<String, String>();
	        		blogPost.put(KEY_TITLE, title);
	        		blogPost.put(KEY_AUTHOR, author);
	        		
	        		blogPosts.add(blogPost);  
	        	}
	        	
				String[] keys = {KEY_TITLE,KEY_AUTHOR};
				int[] ids = {android.R.id.text1,android.R.id.text2};
				SimpleAdapter adapter = new SimpleAdapter(this,blogPosts,android.R.layout.simple_list_item_2,keys,ids);
	        	//ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mBlogPostTitles);
	        	setListAdapter(adapter);
 
	        	
			} catch (JSONException e) {
				Log.d(TAG, "Expetion caugch!", e);
			}
		}
		
	}

	private void updeteDisplayForError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.error_title));
		builder.setMessage(getString(R.string.error_massage));
		builder.setPositiveButton(android.R.string.ok, null);
		AlertDialog dialog = builder.create();
		dialog.show();
		
		TextView emptyTextView = (TextView) getListView().getEmptyView();
		emptyTextView.setText(getString(R.string.no_item));
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

	private class GetBlogPostsTask extends AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... arg0) {
			int responseCode = -1;
			JSONObject jsonResponse = null;
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
					
					jsonResponse = new JSONObject(responseData);
				/*	String status = jsonResponse.getString("status");
					Log.v(TAG, status);
					
					JSONArray jsonPosts = jsonResponse.getJSONArray("posts");
					for (int i = 0; i < jsonPosts.length(); i++) {
						JSONObject jsonPost = jsonPosts.getJSONObject(i);
						String title = jsonPost.getString("title");
						Log.v(TAG, "Post " + i+ ":" + title);
					}*/
					
				}
				else{
					Log.i(TAG, "Unsucefull HTTP responce coade: " + responseCode);
				}
				
			}
			catch(MalformedURLException e){ 
				logException(e);
			}
			catch(IOException e){
				logException(e);
			}
			catch(Exception e){
				logException(e);
			}
			return jsonResponse;
		}
		@Override
		protected void onPostExecute(JSONObject result){
			mBlogData = result;
			handleBlogResponse();
		}
	}

	

}
