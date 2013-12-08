package com.callyourmother;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.TextView;

public class UpdateContactTransactions extends Activity {

	TextView call;
	private HashMap<String, Integer> timesContactCalled;
	private HashMap<String, Integer> contactNumberDate;
	private HashMap<String, String> contactNameNumber;
	private int minCallLength = 15;
	
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
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart(); //Fix this
		call = (TextView) findViewById(R.id.call);
		if (null == timesContactCalled){
			timesContactCalled = new HashMap<String, Integer>();
			contactNameNumber = new HashMap<String, String>();
		}
		getCallDetails();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private void updateNotification(String number){
		
	}
	
	@SuppressWarnings("deprecation")
	private void getCallDetails() {

		StringBuffer sb = new StringBuffer();
		Cursor managedCursor = managedQuery( CallLog.Calls.CONTENT_URI,null, null,null, CallLog.Calls.DATE+" DESC");
		int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER ); 
		int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
		int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
		int name = managedCursor.getColumnIndex( CallLog.Calls.CACHED_NAME);
		
		sb.append( "Call Details :");
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
			}
			sb.append( "\nPhone Number:--- "+phNumber +" \nCall Type:--- "+dir+" \nCall Date:--- "+callDayTime+" \nCall duration in sec :--- "+callDuration );
			sb.append("\n----------------------------------");
		}
		
		//Collections.sort(arr);
		
		StringBuffer out = new StringBuffer();
		for (String s:arr){
			out.append(contactNameNumber.get(s) + ": " + timesContactCalled.get(s)+ "\n");
			updateNotification(contactNameNumber.get(s));
		}
		managedCursor.close();
		call.setText(out);
	}


}
