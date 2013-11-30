package com.callyourmother;

import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.TextView;

public class UpdateContactTransactions extends Activity {

	TextView call;
	
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
		super.onStart();
		call = (TextView) findViewById(R.id.call);
		getCallDetails();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private void getCallDetails() {

		StringBuffer sb = new StringBuffer();
		Cursor managedCursor = managedQuery( CallLog.Calls.CONTENT_URI,null, null,null, null);
		int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER ); 
		int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
		int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
		sb.append( "Call Details :");
		while ( managedCursor.moveToNext() ) {
			String phNumber = managedCursor.getString( number );
			String callType = managedCursor.getString( type );
			String callDate = managedCursor.getString( date );
			Date callDayTime = new Date(Long.valueOf(callDate));
			String callDuration = managedCursor.getString( duration );
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
			sb.append( "\nPhone Number:--- "+phNumber +" \nCall Type:--- "+dir+" \nCall Date:--- "+callDayTime+" \nCall duration in sec :--- "+callDuration );
			sb.append("\n----------------------------------");
		}
		managedCursor.close();
		call.setText(sb);
	}


}
