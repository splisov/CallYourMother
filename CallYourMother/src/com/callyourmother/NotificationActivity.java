package com.callyourmother;

import java.util.Date;
import java.util.List;

import com.callyourmother.data.AndroidUtility;
import com.callyourmother.data.Circle;
import com.callyourmother.data.Contact;
import com.callyourmother.data.Contact.ContactNotFoundException;
import com.callyourmother.data.DatabaseClient;
import com.callyourmother.data.NotificationOccurrence;
import com.callyourmother.data.NotificationRule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationActivity extends Activity {
	ListView listView3;
	NotificationContactAdapter mAdapter;

	static int numDaysSince = 0;

	public static void resetNotification(final Context context, CharSequence name, final CharSequence number) {
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		// 2. Chain together various setter methods to set the dialog
		// characteristics
		builder.setMessage("You can't dismiss loved ones! Call " + name + " now?").setTitle(
				"Go and call them!");

		builder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK button
					}
				});
		builder.setPositiveButton("Ok, fine",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
						String uri = "tel:" + number.toString();
					    Intent intent = new Intent(Intent.ACTION_CALL);

					    intent.setData(Uri.parse(uri));
					    context.startActivity(intent);
					}
				});

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_drawer);

		DatabaseClient dbc = new DatabaseClient(getApplicationContext());
		mAdapter = new NotificationContactAdapter(this, R.id.listView3);

		List<Circle> allCircles = dbc.getCircles();

		List<Contact> contacts;

		for (Circle circle : allCircles) {
			contacts = dbc.getCircleContacts(circle.getCircleId(),
					getApplicationContext());

			Contact newContact;
			for (int i = 0; i < contacts.size(); i++) {
				List<NotificationRule> contactRules = dbc
						.getContactNotificationRules(contacts.get(i)
								.getContactId());
				long contactID = contacts.get(i).getContactId();
				try {
					for (NotificationRule rule : contactRules) {
						Date ruleTime = rule.getNextNotification(dbc
								.getNotificationOccurrences(rule
										.getNotificationRuleId()));

						Date date = new Date(
								System.currentTimeMillis() - 1200000);

						if (ruleTime != null && ruleTime.before(date)) {

							Log.v("Callyourmother",
									"Ruletime: " + ruleTime.toString());

							Log.v("Callyourmother", "Ruletime, beforedate : "
									+ date.toString());

							long diffdate = date.getTime() - ruleTime.getTime();
							diffdate = diffdate / 86400000;
							numDaysSince = (int) diffdate;
							newContact = new Contact(contactID,
									this.getApplicationContext());
							newContact.setDaysSince(numDaysSince);
							if (!mAdapter.includes(newContact.getContactId()))
								mAdapter.add(newContact);
						}
					}
				} catch (ContactNotFoundException e) {
					e.printStackTrace();
				}

			}
		}

		View header;

		listView3 = (ListView) findViewById(R.id.listView3);
		if (mAdapter.isEmpty()) {
			View notifFooterView = (View) getLayoutInflater().inflate(
					R.layout.notification_footer_view, null);

			listView3.addFooterView(notifFooterView);
			listView3.setAdapter(mAdapter);

		} else {

			header = (View) getLayoutInflater().inflate(
					R.layout.notification_header, null);
			TextView header_text = (TextView) header
					.findViewById(R.id.notif_drawer_id);

			header_text.setText("");
			listView3.addHeaderView(header);

			listView3.setAdapter(mAdapter);

		}
	}
}