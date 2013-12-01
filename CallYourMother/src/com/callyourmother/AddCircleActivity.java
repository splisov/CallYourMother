package com.callyourmother;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.callyourmother.data.Contact;
import com.callyourmother.data.Contact.ContactNotFoundException;

import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class AddCircleActivity extends Activity {
	ListView listView2;
	ContactAdapter mAdapter; //placeholder
	private static final int CONTACT_PICKER_RESULT = 1001;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_circle);
		
		listView2 = (ListView)findViewById(R.id.listView2);		      
		View header = (View)getLayoutInflater().inflate(R.layout.add_header, null);
		listView2.addHeaderView(header);
		View footerView = (View)getLayoutInflater().inflate(R.layout.add_footer, null);
		listView2.addFooterView(footerView);
		listView2.setAdapter(mAdapter);
		
		// set up occurance spinner
		Spinner spinner = (Spinner) findViewById(R.id.reoccuranceSpinner);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
		        R.array.occurance_array, android.R.layout.simple_spinner_item);
	
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		
		TextView addContact = (TextView)findViewById(R.id.plus_contact);
		
		addContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Phone.CONTENT_URI);  
	                startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);  
			}
		});
	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	    if (resultCode == RESULT_OK) {  
	   
	    	Uri contactData = data.getData();
	    	Cursor cur = managedQuery(contactData, null, null, null, null);
	    	
	 

	    	if (cur.moveToFirst()) {
	 	    	    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
	 	    	    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	    		
		    	Log.i("DEBUG", "AddCircleActivity: Contact selected: " + name +" with ID: " + id); 
	        
	        
		        try {
					Contact newContact = new Contact(Long.parseLong(id), getApplicationContext());
					mAdapter.add(newContact);
				} catch (NumberFormatException e) {
					Log.i("DEBUG", "AddCircleActivity NumberFormatException " + e);
				} catch (ContactNotFoundException e) {
					Log.i("DEBUG", "AddCircleActivity ContactNotFoundException " + e);						// BEING THROWN!!!??
				}
	        }
	    	
	    
	    } else {  
	        Log.i("DEBUG", "AddCircleActivity: activity result not ok. Contact id: " + CONTACT_PICKER_RESULT); 
	      
	    }  
	}  
	

}