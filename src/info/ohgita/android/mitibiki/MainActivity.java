package info.ohgita.android.mitibiki;

import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.example.info.ohgita.android.mitibiki.R;
import com.google.android.apps.dashclock.ui.SwipeDismissListViewTouchListener;
import com.google.android.apps.dashclock.ui.SwipeDismissListViewTouchListener.DismissCallbacks;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity implements DismissCallbacks,
		OnEditorActionListener, OnClickListener {
	private final static int MENU_ID_RUN = 0;
	private final static int MENU_ID_CLEAR = 1;
	private final static int MENU_ID_ABOUT = 2;
	private final static int INTENT_REQUEST_CODE = 1000;

	private ArrayList<String> choices;
	private ArrayAdapter<String> choicesListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize a Choices ListView
		choicesListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		ListView lv = (ListView) findViewById(R.id.listView_Choises);
		lv.setAdapter(choicesListAdapter);
		SwipeDismissListViewTouchListener listener = new SwipeDismissListViewTouchListener(
				lv, this);
		lv.setOnTouchListener(listener);
		lv.setOnScrollListener(listener.makeScrollListener());

		// Initialize a Choice input form
		EditText choice_input_et = (EditText) findViewById(R.id.editText_main_choiceInput);
		choice_input_et.setOnEditorActionListener(this);

		// Initialize buttons
		ImageButton choice_input_add_btn = (ImageButton) findViewById(R.id.imageButtonChoicesInputAdd);
		choice_input_add_btn.setOnClickListener(this);
		ImageButton choice_input_voice_btn = (ImageButton) findViewById(R.id.imageButtonChoicesInputVoice);
		choice_input_voice_btn.setOnClickListener(this);

		// Initialize choices
		choices = new ArrayList<String>();
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

	@Override
	public boolean canDismiss(int position) {
		return position <= choicesListAdapter.getCount() - 1;
	}

	/**
	 * On choice item has dismissed
	 */
	@Override
	public void onDismiss(ListView listView, int[] reverseSortedPositions) {
		for (int position : reverseSortedPositions) {
			choiceRemove(position);
			// String str = choicesListAdapter.getItem(position);
			// choicesListAdapter.remove(str);
		}
		choicesListAdapter.notifyDataSetChanged();
	}

	/**
	 * Add a choice item
	 * 
	 * @param Choice
	 *            text
	 */
	private boolean choiceAdd(String text) {
		if (0 < text.length()) {
			choices.add(text);
			choicesListAdapter.clear();
			choicesListAdapter.addAll(choices);
			EditText et = (EditText) findViewById(R.id.editText_main_choiceInput);
			et.requestFocus();
			return true;
		}
		return false;
	}

	/**
	 * Remove a choice item
	 */
	public void choiceRemove(int id) {
		choices.remove(id);
		choicesListAdapter.clear();
		choicesListAdapter.addAll(choices);
	}

	/**
	 * Clear all choice items
	 */
	public void choicesClear() {
		choicesListAdapter.clear();
	}

	/**
	 * Run mitibiki with choices
	 */
	public void mitibikiRun() {
		Intent intent = new Intent(this.getApplicationContext(),
				Activity_result.class);
		intent.putExtra("choices", choices);
		startActivity(intent);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			// Add to choices
			choiceAdd(v.getEditableText().toString());
			// Clear a input-form
			v.setText("");
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imageButtonChoicesInputAdd) {
			// Add to choices
			EditText et = (EditText) findViewById(R.id.editText_main_choiceInput);
			choiceAdd(et.getEditableText().toString());
			// Clear a input-form
			et.setText("");
		} else if (v.getId() == R.id.imageButtonChoicesInputVoice) {
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
			mitibikiRun();
			return true;
		case MENU_ID_CLEAR:
			choicesClear();
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
			for (int i = 0; i < results.size(); i++) {
				choiceAdd(results.get(i));
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
			choicesListAdapter.addAll(choices);
		}
	}
}
