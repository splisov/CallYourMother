package com.callyourmother;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.Window;
import android.widget.Toast;

import com.callyourmother.data.*;

public class MainActivity extends Activity {

	public static final String NOTIFIED = "None";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		menu.findItem(R.id.action_data_android_contacts_test)
				.setOnMenuItemClickListener(
						new MenuItem.OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item) {
								// TODO Auto-generated method stub

								List<Contact> contacts = AndroidUtility
										.getAndroidContacts(getApplicationContext());
								for (Contact c : contacts) {
									Toast.makeText(getApplicationContext(),
											c.getDisplayName(),
											Toast.LENGTH_SHORT).show();
								}

								return false;
							}
						});

		Button notiTest = (Button) findViewById(R.id.notification_test);

		notiTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent notiIntent = new Intent(MainActivity.this, NotifyUser.class);
				startService(notiIntent);
				
			}
		});

		return true;
	}

}
