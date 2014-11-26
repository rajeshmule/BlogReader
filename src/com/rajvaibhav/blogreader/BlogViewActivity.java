package com.rajvaibhav.blogreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class BlogViewActivity extends ActionBarActivity {
	
	protected String mUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog);
		
		Intent intent = getIntent();
		Uri bloguri = intent.getData();
		mUrl = bloguri.toString();
		WebView webView = (WebView) findViewById(R.id.webView1);
		webView.loadUrl(mUrl);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.blog_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		
		if(itemId == R.id.action_share){  
			sharePost();
		}
		return super.onOptionsItemSelected(item);
	}

	private void sharePost() {
		// TODO Auto-generated method stub
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
		startActivity(Intent.createChooser(shareIntent, getString(R.string.how_do_you_want_to_share_)));
	}

	

}
