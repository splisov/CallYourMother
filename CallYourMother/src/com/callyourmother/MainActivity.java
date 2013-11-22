package com.callyourmother;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.callyourmother.data.*;

public class MainActivity extends Activity {

	private static final int NOTIFICATION_DRAWER = 0;
	private static final int CREATE_NEW_CIRCLE = 1;
	private CircleAdapter mAdapter;
	private ListView listView1;
	public static final String NOTIFIED = "None";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Circle circle_data[] = new Circle[]{new Circle("Test"),new Circle("Showers")};
			        
		mAdapter = new CircleAdapter(this, R.layout.circle_item, circle_data);
			         
		listView1 = (ListView)findViewById(R.id.listView1);		      
		View header = (View)getLayoutInflater().inflate(R.layout.header, null);
		listView1.addHeaderView(header);
		View notifFooterView = (View)getLayoutInflater().inflate(R.layout.notification_footer_view, null);
		View circleFooterView = (View)getLayoutInflater().inflate(R.layout.add_circle_footer_view, null);
		listView1.addFooterView(notifFooterView);
		listView1.addFooterView(circleFooterView);
		listView1.setAdapter(mAdapter);
		
	/*	notifFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(MainActivity.this,
						NotificationActivity.class), NOTIFICATION_DRAWER);
			}
		});
		
		circleFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(MainActivity.this,
						AddCircleActivity.class), NOTIFICATION_DRAWER);
			}
		});*/
		
	
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
