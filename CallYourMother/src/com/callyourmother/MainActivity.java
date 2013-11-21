package com.callyourmother;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.callyourmother.data.*;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		menu.findItem(R.id.action_data_android_contacts_test).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				
				List<Contact> contacts = AndroidUtility.getAndroidContacts(getApplicationContext());
				for(Contact c : contacts) {
					Toast.makeText(getApplicationContext(), c.getDisplayName(), Toast.LENGTH_SHORT).show();
				}
				
				return false;
			}
		});
		
		return true;
	}

}
