package info.ohgita.android.mitibiki;

import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nhaarman.listviewanimations.itemmanipulation.*;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

public class MainActivity extends SherlockActivity implements OnEditorActionListener, OnClickListener, OnDismissCallback {
	private final static int MENU_ID_RUN = 0;
	private final static int MENU_ID_CLEAR = 1;
	private final static int MENU_ID_ABOUT = 2;
	private final static int INTENT_REQUEST_CODE = 1000;
	
	private ArrayList<String> choices;
	private ArrayAdapter<String> choicesListAdapter;
	private SwipeDismissAdapter choicesListSwipeDismissAdapter;
	private Button buttonDoMitibiki;
	
	private int showHintCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Initialize a Choices ListView
		showHintCount = 0;
		choicesListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		choicesListSwipeDismissAdapter = new SwipeDismissAdapter(choicesListAdapter, this);
		ListView lv = (ListView) findViewById(R.id.listView_Choises);
		lv.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (showHintCount <= 1) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.main_choices_item_longclick_hint), Toast.LENGTH_SHORT).show();
				}
				showHintCount++;
				return false;
			}
			
		});
		choicesListSwipeDismissAdapter.setAbsListView(lv);
		lv.setAdapter(choicesListSwipeDismissAdapter);
		
		// Initialize a Choice input form
		EditText choice_input_et = (EditText) findViewById(R.id.editText_main_choiceInput);
		choice_input_et.setOnEditorActionListener(this);

		// Initialize buttons
		ImageButton choice_input_add_btn = (ImageButton) findViewById(R.id.imageButtonChoicesInputAdd);
		choice_input_add_btn.setOnClickListener(this);
		Button choice_input_voice_btn = (Button) findViewById(R.id.buttonChoicesInputVoice);
		choice_input_voice_btn.setOnClickListener(this);
		buttonDoMitibiki = (Button) findViewById(R.id.buttonDoMitibiki);
		buttonDoMitibiki.setOnClickListener(this);
		
		// Initialize choices
		choices = new ArrayList<String>();
		buttonDoMitibiki.setEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem item_run = menu.add(Menu.NONE, MENU_ID_RUN, 0,
				R.string.general_menu_run);
		item_run.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item_run.setIcon(R.drawable.ic_action_send);

		MenuItem item_clear = menu.add(Menu.NONE, MENU_ID_CLEAR, 0,
				R.string.general_menu_clear);
		item_clear.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		item_clear.setIcon(R.drawable.ic_content_remove);

		MenuItem item_about = menu.add(Menu.NONE, MENU_ID_ABOUT, 0,
				R.string.general_menu_about);
		item_about.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		return true;
	}

	/**
	 * Add a choice item
	 * 
	 * @param text choice
	 */
	private boolean addChoice(String text) {
		if (0 < text.length()) {
			choices.add(text);
			choicesListAdapter.clear();
			for (int i = 0; i < choices.size(); i++) {
				choicesListAdapter.add(choices.get(i));
			}
			choicesListSwipeDismissAdapter.notifyDataSetChanged();
			EditText et = (EditText) findViewById(R.id.editText_main_choiceInput);
			et.requestFocus();
			
			buttonDoMitibiki.setEnabled(true);
			return true;
		}
		return false;
	}

	/**
	 * Remove a choice item
	 */
	private void removeChoice(int id) {
		if (id < choices.size()) {
			choices.remove(id);
			choicesListAdapter.clear();
			for (int i = 0; i < choices.size(); i++) {
				choicesListAdapter.add(choices.get(i));
			}
		}
		
		if (choices.isEmpty()) {
			buttonDoMitibiki.setEnabled(false);
		}
		choicesListSwipeDismissAdapter.notifyDataSetChanged();
	}

	/**
	 * Clear all choice items
	 */
	private void clearChoices() {
		choices.clear();
		choicesListAdapter.clear();
		choicesListSwipeDismissAdapter.notifyDataSetChanged();
		buttonDoMitibiki.setEnabled(false);
	}
	
	/**
	 * Run mitibiki with choices
	 */
	private void doMitibiki() {
		Intent intent = new Intent(this.getApplicationContext(),
				Activity_result.class);
		intent.putExtra("choices", choices);
		startActivity(intent);
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_NONE || event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			// Add to choices
			addChoice(v.getEditableText().toString());
			// Clear a input-form
			v.setText("");
			v.findFocus();
		} else {
			Toast.makeText(this, "Press:" + actionId, Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonDoMitibiki) {
			doMitibiki();
		} else if (v.getId() == R.id.imageButtonChoicesInputAdd) {
			// Add to choices
			EditText et = (EditText) findViewById(R.id.editText_main_choiceInput);
			addChoice(et.getEditableText().toString());
			// Clear a input-form
			et.setText("");
		} else if (v.getId() == R.id.buttonChoicesInputVoice) {
			// Input a choice with using Voice-recognition
			try {
				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
						RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources()
						.getText(R.string.main_choices_input_voice_prompt));
				startActivityForResult(intent, INTENT_REQUEST_CODE);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(
						this,
						getResources()
								.getText(
										R.string.general_error_speech_recog_activity_notfound),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ID_RUN:
			doMitibiki();
			return true;
		case MENU_ID_CLEAR:
			clearChoices();
			return true;
		case MENU_ID_ABOUT:
			Intent intent_0 = new Intent(this.getApplicationContext(),
					Activity_appInfo.class);
			startActivity(intent_0);
			return true;
		}
		;
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == INTENT_REQUEST_CODE && resultCode == RESULT_OK) {
			ArrayList<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (0 < results.size()) {
				String[] items = results.get(0).split(" ");
				for (int i = 0; i < items.length; i++) {
					addChoice(items[i]);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArrayList("choices", choices);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		ArrayList<String> list = savedInstanceState
				.getStringArrayList("choices");
		if (list != null) {
			choices = list;
			for (int i = 0; i < choices.size(); i++) {
				choicesListAdapter.add(choices.get(i));
			}
			buttonDoMitibiki.setEnabled(true);
		}
	}

	@Override
	public void onDismiss(AbsListView listView, int[] reverseSortedPositions) {
		for (int position : reverseSortedPositions) {
			removeChoice(position);
	    }
	}
}
