package com.callyourmother;

import java.util.Date;
import java.util.List;

import com.callyourmother.data.AndroidUtility;
import com.callyourmother.data.Contact;
import com.callyourmother.data.Contact.ContactNotFoundException;
import com.callyourmother.data.DatabaseClient;
import com.callyourmother.data.NotificationOccurrence;
import com.callyourmother.data.NotificationRule;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;



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
		TextView header_text = (TextView) header.findViewById(R.id.notif_drawer_id);
		
		header_text.setText("");
		listView3.addHeaderView(header);
		listView3.setAdapter(mAdapter);
		

		List<Contact> contacts = AndroidUtility.getAndroidContacts(getApplicationContext());	
		for (int i = 0; i < contacts.size(); i++){

			long contactID = contacts.get(i).getContactId();

			
			Contact newContact;
			try {

				newContact = new Contact(contactID, this.getApplicationContext());
				mAdapter.add(newContact);
				
				List<NotificationRule> notrules = DatabaseClient.getContactNotificationRules(contactID);
				

				Date ruleTime = getLastRuleUpdate(notrules);
				Date date = new Date();
				if(ruleTime != null && ruleTime.before(date)){

				}
				
			} catch (ContactNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		
	}
	
	private Date getLastRuleUpdate(List<NotificationRule> rules){
		long shortestTime = Long.MAX_VALUE;
		Date smallestDate = null;
		for (NotificationRule rule:rules){
			if (rule.getStartDate().getTime()<shortestTime){
				shortestTime = rule.getStartDate().getTime();
				smallestDate = rule.getStartDate();
			}
		}
		return smallestDate;
	}
	
}