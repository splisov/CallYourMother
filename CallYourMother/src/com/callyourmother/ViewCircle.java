package com.callyourmother;

import java.util.List;

import com.callyourmother.data.Contact;
import com.callyourmother.data.DatabaseClient;
import com.callyourmother.data.Contact.ContactNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class ViewCircle extends Activity {
	ListView listView3;
	ContactAdapter mAdapter; 
	DatabaseClient db;
	String titlee;
	private static final int EDIT = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_drawer);
		
		db = new DatabaseClient(getApplicationContext());
		mAdapter = new ContactAdapter(this, R.layout.view_item);

		// Grab necessary extras
		Bundle b = getIntent().getExtras();
		final long cId = b.getLong("circle_id");
		final String title = b.getString("circle_description");
		titlee = title;
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
		
		TextView notificationView = (TextView)findViewById(R.id.detail_view);
		//notificationView.setText(Notification Rule);
		
		
		List<Contact> contacts = db.getCircleContacts(cId, getApplicationContext());
		List<Long> contactIds = db.getCircleContactIds(cId); 
		
		if (contactIds != null){
			Log.i("DEBUG", "Number of contact ids: " + contactIds.size());
		}
		
		if (db.getCircleContacts(cId, getApplicationContext()) == null){
			Log.i("DEBUG", "NULL CONTACTS");
		}
		
		Log.i("DEBUG", "This is Circle id #" + cId);
		
		
		
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
				db.deleteCircle(cId);
				setResult(RESULT_OK);
				finish();
			}
		});
		
		Button editCircleButton = (Button)findViewById(R.id.edit_circle_button);
		editCircleButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//send information to edit circle class
				Intent editExistingCircleIntent = new Intent(getApplicationContext(), EditCircleActivity.class);
				editExistingCircleIntent.putExtra("circle_id", cId);
				editExistingCircleIntent.putExtra("circle_description", title);
				startActivityForResult(editExistingCircleIntent, 0); // HARDCODED; CHANGE LATER
			}
		});
		
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

			Log.i("DEBUG", "In edit stage");
			Bundle b = data.getExtras();
			final long cId = b.getLong("circle_id");
			final String t = b.getString("circle_description");
		
			Log.i("DEBUG", cId + "   " + t);
			
			List<Contact> contacts = db.getCircleContacts(cId, getApplicationContext());
			
			mAdapter = new ContactAdapter(this, R.layout.view_item);
			mAdapter.clear();
			listView3.setAdapter(mAdapter);
			
			TextView circleTitleView = (TextView)findViewById(R.id.circle_title_view);
			circleTitleView.setText(t);
			
			TextView notificationView = (TextView)findViewById(R.id.detail_view);
			//notificationView.setText(Notification Rule);
			
			if (contacts != null){
				for (int i = 0; i < contacts.size(); i++){	//NOT WORKING
					contacts.get(i);
					mAdapter.add(contacts.get(i));
				}
			} 

		
		
	}
	
}