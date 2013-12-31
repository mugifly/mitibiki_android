package info.ohgita.android.mitibiki;

import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.info.ohgita.android.mitibiki.R;

public class Activity_appInfo extends SherlockActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setTheme(R.style.Theme_Sherlock_Light);
		setContentView(R.layout.activity_appinfo);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		/* load license-page to webView */
		WebView wv = (WebView)findViewById(R.id.webView_license);
		wv.loadUrl("file:///android_asset/license.html");
	}
	
	@Override  
	public boolean onOptionsItemSelected(MenuItem item) {  
		switch(item.getItemId()) {  
			case android.R.id.home:  
				finish();  
				return true;  
		}
		return super.onOptionsItemSelected(item);  
	}  
	
}
