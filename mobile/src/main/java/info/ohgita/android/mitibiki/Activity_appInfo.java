package info.ohgita.android.mitibiki;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class Activity_appInfo extends SherlockActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setTheme(R.style.Theme_Sherlock_Light);
		setContentView(R.layout.activity_appinfo);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Read the app version 
		try {
			PackageManager package_manager = getPackageManager();
			PackageInfo package_info = package_manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
			TextView tv_version = (TextView) findViewById(R.id.textView_version);
			tv_version.setText(package_info.versionName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Read the license page into a webView
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
