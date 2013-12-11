package com.callyourmother;

import java.util.Date;
import java.util.List;

import com.callyourmother.data.Circle;
import com.callyourmother.data.Contact;
import com.callyourmother.data.DatabaseClient;
import com.callyourmother.data.NotificationRule;
import com.callyourmother.data.Contact.ContactNotFoundException;

import android.app.Activity;
import android.app.IntentService;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;

public class UpdateTransactionsService extends IntentService {

	public UpdateTransactionsService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Gather call data
		Uri allCalls = Uri.parse("content://call_log/calls");
		CursorLoader c = new CursorLoader(this.getApplicationContext(),
				allCalls, null, null, null, null);

		DatabaseClient dbc = new DatabaseClient(getApplicationContext());
		Log.v("Callyourmother",
				"Superman reached into service!");

		List<Circle> allCircles = dbc.getCircles();

		List<Contact> contacts;

		for (Circle circle : allCircles) {
			contacts = dbc.getCircleContacts(circle.getCircleId(),
					getApplicationContext());

			for (int i = 0; i < contacts.size(); i++) {
				List<NotificationRule> contactRules = dbc
						.getContactNotificationRules(contacts.get(i)
								.getContactId());

				for (NotificationRule rule : contactRules) {
					Date ruleTime = rule.getNextNotification(dbc
							.getNotificationOccurrences(rule
									.getNotificationRuleId()));

					Date date = new Date(System.currentTimeMillis() - 1200000);

					Log.v("Callyourmother", "Ruletime: " + ruleTime.toString());

					Log.v("Callyourmother",
							"Ruletime, beforedate : " + date.toString());

					if (ruleTime != null && ruleTime.before(date)) {

						long diffdate = date.getTime() - ruleTime.getTime();
						diffdate = diffdate / 86400000;
						
						NotifyUser.setData(contacts.get(i).getDisplayName(), (int) diffdate);
						Intent notiIntent = new Intent(this, NotifyUser.class);

						startService(notiIntent);
					}
				}

			}
		}

		// Cursor c = managedQuery(allCalls, null, null, null, null);
		// for number
		// String num= c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
		// for name
		// String name=
		// c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));
		// for duration
		// String duration =
		// c.getString(c.getColumnIndex(CallLog.Calls.DURATION));
		// for call type, Incoming or out going
		// int type =
		// Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.TYPE)));
	}

}
