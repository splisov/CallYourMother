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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class NotificationActivity extends Activity {
	ListView listView3;
	NotificationContactAdapter mAdapter; 
	
	static int numDaysSince = 0;

	public void resetNotification(){
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_drawer);
		
		mAdapter = new NotificationContactAdapter(this, R.id.listView3);
		

		List<Contact> contacts = AndroidUtility.getAndroidContacts(getApplicationContext());	
		for (int i = 0; i < contacts.size(); i++){

			
			long contactID = contacts.get(i).getContactId();

			
			Contact newContact;
			try {
				List<NotificationRule> notrules = DatabaseClient.getContactNotificationRules(contactID);
				

				Date ruleTime = getLastRuleUpdate(notrules);
				Date date = new Date();
				
				if(ruleTime != null && ruleTime.before(date) ){
					long diffdate = date.getTime() - ruleTime.getTime();
					diffdate = diffdate / 86400000;
					numDaysSince = (int) diffdate;
				}
				newContact = new Contact(contactID, this.getApplicationContext());
				newContact.setDaysSince(numDaysSince);
				mAdapter.add(newContact);
			} catch (ContactNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		View header;

		listView3 = (ListView) findViewById(R.id.listView3);	
		if(mAdapter.isEmpty()){
			
			
			
			Toast.makeText(getApplicationContext(), "Num of contacts: " + contacts.size(), Toast.LENGTH_LONG).show();
			View header1 = LayoutInflater.from(this).inflate(R.layout.notification_footer_view,
					null);
			
			listView3.addFooterView(header1);
			
		}else{

			// Set up View	      
			header = (View)getLayoutInflater().inflate(R.layout.notification_header, null);
			TextView header_text = (TextView) header.findViewById(R.id.notif_drawer_id);
			
			header_text.setText("");
			listView3.addHeaderView(header);
			listView3.setAdapter(mAdapter);
			
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