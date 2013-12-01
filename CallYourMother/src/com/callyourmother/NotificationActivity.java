package com.callyourmother;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;



public class NotificationActivity extends Activity {
	ListView listView3;
	NotificationContactAdapter mAdapter; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_drawer);
		
		mAdapter = new NotificationContactAdapter(this, R.id.listView3);
		
		// Set up View
		listView3 = (ListView)findViewById(R.id.listView3);		      
		View header = (View)getLayoutInflater().inflate(R.layout.notification_header, null);
		listView3.addHeaderView(header);
		listView3.setAdapter(mAdapter);
	
		
	}
	
}