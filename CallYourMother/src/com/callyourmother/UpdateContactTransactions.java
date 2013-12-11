package com.callyourmother;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.callyourmother.data.AndroidUtility;
import com.callyourmother.data.Contact;
import com.callyourmother.data.DatabaseClient;
import com.callyourmother.data.NotificationOccurrence;
import com.callyourmother.data.NotificationRule;


import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;
import android.widget.TextView;

public class UpdateContactTransactions extends Activity {

	TextView call;
	private HashMap<String, Integer> timesContactCalled;
	private HashMap<String, Long> contactNameDate;
	private HashMap<String, String> contactNameNumber;
	private int minCallLength = 15;
	private final Handler mHandler= new Handler();
	
	private Runnable mHandlerTask = new Runnable() {
		
		@Override
		public void run() {
			new GetCallDetailsTask().execute();
			
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_contact_transactions);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//mHandler.postDelayed(mHandlerTask, 1);
		new GetCallDetailsTask().execute();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart(); //Fix this
		call = (TextView) findViewById(R.id.call);
		if (null == timesContactCalled){
			timesContactCalled = new HashMap<String, Integer>();
			contactNameNumber = new HashMap<String, String>();
			contactNameDate = new HashMap<String, Long>();
		}
		getCallDetails();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private void updateNotification(String displayName, List<Contact> contacts){
		//DatabaseClient.getContactNotificationRules(contactID)
		//getNotificationOccurrences(long notificationRuleId)
		//saveNotificationOccurrence(NotificationOccurrence notificationOccurrence)
		Contact contact = findContact(contacts, displayName);
		if (contact == null){
			return;
		}
		long contactID = contact.getContactId();

		if (contactID != 0){
			DatabaseClient client = new DatabaseClient(getApplicationContext());
			List<NotificationRule> rules = client.getContactNotificationRules(contactID);
			Date ruleTime = getLastRuleUpdate(rules);
			// if contact has a notification rule that would need to update the next notification
			if (!rules.isEmpty()){
				//String s = rules.toString();
				//Calendar c = Calendar.getInstance(); 
				//long now = c.getTime().getTime();
				//long diff = then-now;
				long then = contactNameDate.get(displayName);
				if (then > ruleTime.getTime()){
					Date newTime = new Date(then);
					contact.updateLastDateContacted(newTime);
				}
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

	private Contact findContact(List<Contact> contacts, String displayName){
		long contactID = 0;
		for (Contact c: contacts){
			String currName =c.getDisplayName();
			if (currName != null && currName.compareTo(displayName)==0){
				contactID = c.getContactId();
				return c;
			}
		}
		return null;
	}


	private void getCallDetails() {
		/*
//		StringBuffer sb = new StringBuffer();
//		Cursor managedCursor = managedQuery( CallLog.Calls.CONTENT_URI,null, null,null, CallLog.Calls.DATE+" DESC");
//		int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER ); 
//		int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
//		int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
//		int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
//		int name = managedCursor.getColumnIndex( CallLog.Calls.CACHED_NAME);
//		
//		sb.append( "Call Details :");
//		ArrayList<String> arr = new ArrayList<String>(); 
//		while ( managedCursor.moveToNext() ) {
//			String phNumber = managedCursor.getString( number );
//			String callType = managedCursor.getString( type );
//			String callDate = managedCursor.getString( date );
//			Date callDayTime = new Date(Long.valueOf(callDate));
//			
//			String callDuration = managedCursor.getString( duration );
//			String callName = managedCursor.getString( name );
//			
//			
//			
//			if (Integer.valueOf(callDuration)<minCallLength){
//				continue;
//			}
//			
//			
//			String dir = null;
//			int dircode = Integer.parseInt( callType );
//			switch( dircode ) {
//			case CallLog.Calls.OUTGOING_TYPE:
//				dir = "OUTGOING";
//				break;
//
//			case CallLog.Calls.INCOMING_TYPE:
//				dir = "INCOMING";
//				break;
//
//			case CallLog.Calls.MISSED_TYPE:
//				dir = "MISSED";
//				break;
//			}
//			if (callName == null){
//				callName = phNumber;
//			}
//			if (timesContactCalled.containsKey(phNumber)){
//				timesContactCalled.put(phNumber, timesContactCalled.get(phNumber)+1);
//				if (!arr.contains(phNumber)&&!arr.contains(callName)){
//					arr.add(phNumber);
//				}
//			} else {
//				timesContactCalled.put(phNumber,1);
//				contactNameNumber.put(phNumber, callName);
//				contactNameDate.put(phNumber, (Long) callDayTime.getTime());
//			}
//			// sb.append( "\nPhone Number:--- "+phNumber +" \nCall Type:--- "+dir+" \nCall Date:--- "+callDayTime+" \nCall duration in sec :--- "+callDuration );
//			// sb.append("\n----------------------------------");
//		}
//		
//		//Collections.sort(arr);
//		StringBuffer out = new StringBuffer();
//		List<Contact> contacts = AndroidUtility.getAndroidContacts(getApplicationContext());		
//		for (String s:arr){
//			out.append(contactNameNumber.get(s) + ": " + timesContactCalled.get(s)+ "\n");
//			updateNotification(contactNameNumber.get(s),contacts);
//			
//		}
//		managedCursor.close();
//		call.setText(out);
		 */		
	}


	private class GetCallDetailsTask extends AsyncTask<Void, Void, StringBuffer> {

		@Override
		protected StringBuffer doInBackground(Void... params) {
			Log.v("GetCallDetailsTask","started");
			@SuppressWarnings("deprecation")
			Cursor managedCursor = managedQuery( CallLog.Calls.CONTENT_URI,null, null,null, CallLog.Calls.DATE+" DESC");
			int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER ); 
			int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
			int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
			int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
			int name = managedCursor.getColumnIndex( CallLog.Calls.CACHED_NAME);

			ArrayList<String> arr = new ArrayList<String>(); 
			while ( managedCursor.moveToNext() ) {
				String phNumber = managedCursor.getString( number );
				String callType = managedCursor.getString( type );
				String callDate = managedCursor.getString( date );
				Date callDayTime = new Date(Long.valueOf(callDate));

				String callDuration = managedCursor.getString( duration );
				String callName = managedCursor.getString( name );



				if (Integer.valueOf(callDuration)<minCallLength){
					continue;
				}


				String dir = null;
				int dircode = Integer.parseInt( callType );
				switch( dircode ) {
				case CallLog.Calls.OUTGOING_TYPE:
					dir = "OUTGOING";
					break;

				case CallLog.Calls.INCOMING_TYPE:
					dir = "INCOMING";
					break;

				case CallLog.Calls.MISSED_TYPE:
					dir = "MISSED";
					break;
				}
				if (callName == null){
					callName = phNumber;
				}
				if (timesContactCalled.containsKey(phNumber)){
					timesContactCalled.put(phNumber, timesContactCalled.get(phNumber)+1);
					if (!arr.contains(phNumber)&&!arr.contains(callName)){
						arr.add(phNumber);
					}
				} else {
					timesContactCalled.put(phNumber,1);
					contactNameNumber.put(phNumber, callName);
					contactNameDate.put(phNumber, (Long) callDayTime.getTime());
				}
			}

			StringBuffer out = new StringBuffer();
			List<Contact> contacts = AndroidUtility.getAndroidContacts(getApplicationContext());		
			for (String s:arr){
				out.append(contactNameNumber.get(s) + ": " + timesContactCalled.get(s)+ "\n");
				updateNotification(contactNameNumber.get(s),contacts);

			}
			managedCursor.close();
			Log.v("GetCallDetailsTask",out.toString());
			return out;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(StringBuffer result) {

		}
	}

}
