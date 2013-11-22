package com.callyourmother;

import android.app.Activity;
import android.app.IntentService;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

public class UpdateTransactionsService extends IntentService {

	public UpdateTransactionsService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Gather call data
		Uri allCalls = Uri.parse("content://call_log/calls");
		CursorLoader c = new CursorLoader(this.getApplicationContext(), allCalls, null, null, null, null);
		// Cursor c = managedQuery(allCalls, null, null, null, null);
		// for  number
		//String num= c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
		// for name
		//String name= c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));
		// for duration
		//String duration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));
		// for call type, Incoming or out going
		//int type = Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.TYPE)));
	}

}
