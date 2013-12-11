package com.callyourmother;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.callyourmother.data.Circle;
import com.callyourmother.data.Contact;
import com.callyourmother.data.DatabaseClient;
import com.callyourmother.data.NotificationRule;
import com.callyourmother.data.Contact.ContactNotFoundException;
import com.callyourmother.data.NotificationRule;
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


public class EditCircleActivity extends Activity {
	
	private static final int CONTACT_PICKER_RESULT = 1001;
	private static final String CONTACT_BASE_URI = "content://com.android.contacts/data/";
	
	static ListView listView2;
	static EditAdapter mAdapter; 
	static DatabaseClient db;
	static ArrayList<Long> contactIdList;
	long global_cid;
	static List<Contact> contactList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_circle);
		
		
		db = new DatabaseClient(this.getApplicationContext());
		mAdapter = new EditAdapter(this, R.layout.contact_item);
		contactIdList = new ArrayList<Long>();
		contactList = new ArrayList<Contact>();
		
		Bundle b = getIntent().getExtras();
		final long cId = b.getLong("circle_id");
		global_cid = cId;
		final String title = b.getString("circle_description");
		List <Contact> contacts = db.getCircleContacts(cId, getApplicationContext());
		
		// Set up View
		listView2 = (ListView)findViewById(R.id.listView2);		      
		View header = (View)getLayoutInflater().inflate(R.layout.add_header, null);
		listView2.addHeaderView(header);
		View footerView = (View)getLayoutInflater().inflate(R.layout.add_footer, null);
		listView2.addFooterView(footerView);
		listView2.setAdapter(mAdapter);
		
		//Set title
		EditText titleView = (EditText)findViewById(R.id.circleText);
		titleView.setText(title); 
		
		//Add exisiting group contacts
		for (int i = 0; i<contacts.size(); i++){
			contactIdList.add(contacts.get(i).getContactId());
			mAdapter.add(contacts.get(i));
			contactList.add(contacts.get(i));
			
		}
		
		// Set up an Occurance Spinner
		Spinner spinner = (Spinner) findViewById(R.id.reoccuranceSpinner);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
		        R.array.occurance_array, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		spinner.setSelection(1); //HARDCODED; FIX LATER
		
		
		
		// Add a Contact to the List
		TextView addContact = (TextView)findViewById(R.id.plus_contact);
		addContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Phone.CONTENT_URI);  
	                startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);  
			}
		});
		
		//ADD CONTACTS
		
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
							String newTitle = circleTitleText.getText().toString().trim();
							Spinner mySpinner = (Spinner)findViewById(R.id.reoccuranceSpinner);
							String reoccuranceString = mySpinner.getSelectedItem().toString();
							int reoccurance = notificationRule(reoccuranceString);
							NotificationRule notifRule = new NotificationRule(title, 1, reoccurance, new Date(System.currentTimeMillis()));
							
							long newCId = cId;
							if (title.length() > 0){
								if (!title.equals(newTitle)){
									db.deleteCircle(cId);
									Circle newCircle = new Circle(newTitle);
									db.saveCircle(newCircle);
									newCId = newCircle.getCircleId();
									global_cid = newCId;
								} 
								for (int j =0; j < contactIdList.size(); j++){
									db.saveCircleContact(newCId, contactIdList.get(j));
								}
								db.saveCircleNotificationRule(newCId, notifRule);
								//update existing circle
								//if different name, delete old circle and add new
								//set reoccurance for each contact
								Intent i = new Intent();
								i.putExtra("circle_id", newCId);
								i.putExtra("circle_description", newTitle);
								setResult(RESULT_OK, i);
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
			    	long contactId = Long.parseLong(contactData.toString().substring(CONTACT_BASE_URI.length()));
		    		
			    	Log.i("DEBUG", "AddCircleActivity: Contact selected with ID: " + contactData.toString() + " " + contactId); 
		        
			        try {
						Contact newContact = new Contact(contactId, this.getApplicationContext());
						mAdapter.add(newContact);
						contactIdList.add(contactId);
						contactList.add(newContact);
					} catch (NumberFormatException e) {
						Log.i("DEBUG", "AddCircleActivity NumberFormatException " + e);
					} catch (ContactNotFoundException e) {
						Log.i("DEBUG", "AddCircleActivity ContactNotFoundException " + e);	
					}

			    } else {  
			        Log.i("DEBUG", "AddCircleActivity: activity result not ok. Contact id: " + CONTACT_PICKER_RESULT); 
			      
			    }  
			}  
			
			public static void deleteTempContact(Context c, Contact contact){
				Log.i("DEBUG", "In AddCircleActivity for deletion of contact " + contact.getContactId());
				db.deleteContactData(contact.getContactId());
				mAdapter.remove(contact);
				contactIdList.remove(contact.getContactId());
				contactList.remove(contact);
				mAdapter = new EditAdapter(c, R.layout.contact_item);
				listView2.setAdapter(mAdapter);
				
				
				for(int i = 0; i < contactList.size(); i++){
					mAdapter.add(contactList.get(i));
				}
				
				
			
			}
			
			private int notificationRule(String s){
				
				if (s.equals("Once")){
					return 1;
				} else if (s.equals("Daily")){
					return 3;
				} else if (s.equals("Weekly")){
					return 4;
				} else if (s.equals("Monthly")){
					return 5;
				} else if (s.equals("Yearly")){
					return 6;
				}
				return 0;		
			}


		}