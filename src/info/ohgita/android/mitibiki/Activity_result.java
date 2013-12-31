package info.ohgita.android.mitibiki;

import java.util.ArrayList;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.info.ohgita.android.mitibiki.R;

public class Activity_result extends SherlockActivity {
	private final static int MENU_ID_SHARE = 0;
	private TextView tvDescription;
	private TextView tvResultText;
	private TextView tvNotice;
	private String resultText;
	private final Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setTheme(R.style.Theme_Sherlock_Light);
		setContentView(R.layout.activity_result);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		tvDescription = (TextView) findViewById(R.id.textView_result_description);
		tvResultText = (TextView) findViewById(R.id.textView_result_text);
		tvNotice = (TextView) findViewById(R.id.textView_result_notice);
		
		// Read intent
		Intent intent = getIntent();
		if (intent.getExtras().get("choices") == null) {
			return;
		}
		
		// Run mitibiki
		ArrayList<String> choices = (ArrayList<String>) intent.getExtras().get("choices");
		if (mitibiki(choices) == null) {
			finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem item_run = menu.add(Menu.NONE, MENU_ID_SHARE, 0, R.string.result_menu_share);
		item_run.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item_run.setIcon(R.drawable.ic_action_share);
		
		return true;
	}
	
	@Override  
	public boolean onOptionsItemSelected(MenuItem item) {  
		switch(item.getItemId()) {  
			case android.R.id.home:  
				finish();  
				return true;
			case MENU_ID_SHARE:
				resultShare();
				return true;
		}
		return super.onOptionsItemSelected(item);  
	}  
	
	/**
	 * Run mitibiki
	 * @param choices
	 */
	public String mitibiki(ArrayList<String> choices) {
		if (choices.size() == 0) {
			return null;
		}
		
		int n = (int) (Math.random() * choices.size());
		resultText = choices.get(n);
		
		// Show processing text :)
		tvResultText.setText(R.string.result_processing);
		
		// Show a result
		Runnable task = new Runnable() {
			@Override
			public void run() {
				tvDescription.setVisibility(View.VISIBLE);
				tvNotice.setVisibility(View.VISIBLE);
				tvResultText.setText(">> " + resultText + " <<");
			}
	    };
		handler.postDelayed(task, 1000);
		
		return resultText;
	}
	
	/**
	 * Share the result text
	 */
	public void resultShare() {
		String text = getResources().getText(R.string.result_share_text_prefix) + ">> " + resultText + " <<";
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(Intent.createChooser(intent, getString(R.string.result_share_title)));
	}
}
