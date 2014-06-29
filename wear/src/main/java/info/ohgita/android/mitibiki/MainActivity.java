package info.ohgita.android.mitibiki;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.activity.*;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import info.ohgita.android.mitibiki.R;

public class MainActivity extends InsetActivity {
	final static String EXTRA_EVENT_ID = "extra_event_id";
	final static int NOTIFICATION_ID = 100;
	final static int SPEECH_REQUEST_CODE = 0;
	private static final int SPEECH_REQUEST = 0;

	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onReadyForContent() {
		setContentView(R.layout.activity_main);
		final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub) {
				mTextView = (TextView) stub.findViewById(R.id.text);
			}
		});
		displaySpeechRecognizer();
	}

	private void displaySpeechRecognizer() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.message_prompt_input_choices_by_voice));
		startActivityForResult(intent, SPEECH_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	                                Intent data) {
		if (requestCode == SPEECH_REQUEST) {
			if (resultCode == RESULT_OK) {
				List<String> results = data.getStringArrayListExtra(
						RecognizerIntent.EXTRA_RESULTS);
				String text = results.get(0);
				String[] choices = text.split(" ");
				if (choices.length <= 0) {
					return;
				}
				String res = doMitibiki(choices);
				launchResultNotification(res);
			}
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Filter a result with using the random number from choices
	 * @param choices Choice texts
	 */
	protected String doMitibiki(String[] choices) {
		Random r = new Random();
		int id = r.nextInt(choices.length);
		return choices[id];
	}

	/**
	 * Launch the result notification
	 * @param result Result item
	 */
	protected void launchResultNotification(String result) {
		// Cancel a old notification if necessary
		NotificationManagerCompat manager = NotificationManagerCompat.from(this);
		manager.cancel(NOTIFICATION_ID);

		// Make a intent for retrying
		Intent view_intent = new Intent(this, MainActivity.class);
		view_intent.putExtra(EXTRA_EVENT_ID, 0);
		PendingIntent pending_intent_retry = PendingIntent.getActivity(this, 0, view_intent, PendingIntent.FLAG_CANCEL_CURRENT);

		// Generate a notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle(getResources().getString(R.string.message_mitibiki_result_title))
			.setContentText(result)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setAutoCancel(true)
			.addAction(R.drawable.ic_action_refresh, getString(R.string.action_retry), pending_intent_retry)
		;

		// Launch a notification
		manager.notify(NOTIFICATION_ID, builder.build());
	}
}