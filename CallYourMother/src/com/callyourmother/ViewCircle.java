package com.callyourmother;

import java.util.List;

import com.callyourmother.data.Contact;
import com.callyourmother.data.DatabaseClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class ViewCircle extends Activity {
	ListView listView3;
	NotificationContactAdapter mAdapter; 
	DatabaseClient db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_drawer);
		
		db = new DatabaseClient(getApplicationContext());
		mAdapter = new NotificationContactAdapter(this, R.id.listView3);

		// Grab necessary extras
		Bundle b = getIntent().getExtras();
		long cId = b.getLong("circle_id");
		String title = b.getString("circle_description");
		
		// Set up View
		listView3 = (ListView)findViewById(R.id.listView3);		      
		View header = (View)getLayoutInflater().inflate(R.layout.view_circle_header, null);
		listView3.addHeaderView(header);
		View footer = (View)getLayoutInflater().inflate(R.layout.view_footer, null);
		listView3.addFooterView(footer);
		listView3.setAdapter(mAdapter);
		
		// Add contacts to view
		TextView circleTitleView = (TextView)findViewById(R.id.circle_title_view);
		circleTitleView.setText(title + " Circle");
		
		List<Contact> contacts = db.getCircleContacts(cId, getApplicationContext());
		Log.i("DEBUG", cId + "");
		
		if (contacts != null){
			for (int i = 0; i < contacts.size(); i++){	//NOT WORKING
				contacts.get(i);
				mAdapter.add(contacts.get(i));
			}
		}
	
		Button deleteCircleButton = (Button)findViewById(R.id.delete_circle_button);
		deleteCircleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//db.deleteCircle(circle)
			}
		});
		
		Button editCircleButton = (Button)findViewById(R.id.edit_circle_button);
		editCircleButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//send information to edit circle class
				Intent editExistingCircleIntent = new Intent(getApplicationContext(), EditCircleActivity.class);
				startActivityForResult(editExistingCircleIntent, 0); // HARDCODED; CHANGE LATER
			}
		});
		
		
	}
	
}