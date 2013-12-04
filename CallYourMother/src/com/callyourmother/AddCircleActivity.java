package com.callyourmother;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.callyourmother.data.Circle;
import com.callyourmother.data.Contact;
import com.callyourmother.data.DatabaseClient;
import com.callyourmother.data.Contact.ContactNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class AddCircleActivity extends Activity {
	private static final int CONTACT_PICKER_RESULT = 1001;
	private static final String CONTACT_BASE_URI = "content://com.android.contacts/data/";
	
	ListView listView2;
	ContactAdapter mAdapter; 
	DatabaseClient db;
	ArrayList<Long> contactIdList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_circle);
		
		
		db = new DatabaseClient(this.getApplicationContext());
		mAdapter = new ContactAdapter(this, R.layout.contact_item);
	
		contactIdList = new ArrayList<Long>();
		
		// Set up View
		listView2 = (ListView)findViewById(R.id.listView2);		      
		View header = (View)getLayoutInflater().inflate(R.layout.add_header, null);
		listView2.addHeaderView(header);
		View footerView = (View)getLayoutInflater().inflate(R.layout.add_footer, null);
		listView2.addFooterView(footerView);
		listView2.setAdapter(mAdapter);
		
		// Set up an Occurance Spinner
		Spinner spinner = (Spinner) findViewById(R.id.reoccuranceSpinner);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
		        R.array.occurance_array, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		
		
		// Add a Contact to the List
		TextView addContact = (TextView)findViewById(R.id.plus_contact);
		addContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Phone.CONTENT_URI);  
	                startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);  
			}
		});
		
		// Cancel Add Circle Operation
		Button cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		// Submit New Circle
				Button submitButton = (Button) findViewById(R.id.submit);
				submitButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						EditText circleTitleText = (EditText)findViewById(R.id.circleText);
						String title = circleTitleText.getText().toString().trim();
						Spinner mySpinner = (Spinner)findViewById(R.id.reoccuranceSpinner);
						String reoccurance = mySpinner.getSelectedItem().toString();
						
						if (title.length() > 0){
							Circle newCircle = new Circle(title);
							db.saveCircle(newCircle);
							//set reoccurance for each contact
							setResult(RESULT_OK);
							finish();
						} else {
							Toast.makeText(getApplication(), "Please enter a circle name.", Toast.LENGTH_LONG).show();
						}
					}
				});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	    if (resultCode == RESULT_OK) {
	    	//get contact URI
	    	Uri contactData = data.getData();
	    	//parse contact ID from URI
	    	String contactId = contactData.toString().substring(CONTACT_BASE_URI.length());
    		
	    	Log.i("DEBUG", "AddCircleActivity: Contact selected with ID: " +  contactId); 
	    	
	         
	        try {
				Contact newContact = new Contact(Long.parseLong(contactId), this.getApplicationContext());
				mAdapter.add(newContact);
				//contactIdList.add(Long.parseLong(contactId));
			} catch (NumberFormatException e) {
				Log.i("DEBUG", "AddCircleActivity NumberFormatException " + e);
			} catch (ContactNotFoundException e) {
				Log.i("DEBUG", "AddCircleActivity ContactNotFoundException " + e);	
			}

	    } else {  
	        Log.i("DEBUG", "AddCircleActivity: activity result not ok. Contact id: " + CONTACT_PICKER_RESULT); 
	      
	    }  
	}  
	

}