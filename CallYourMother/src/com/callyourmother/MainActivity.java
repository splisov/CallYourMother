package com.callyourmother;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.callyourmother.data.AndroidUtility;
import com.callyourmother.data.Circle;
import com.callyourmother.data.Contact;
import com.callyourmother.data.DatabaseClient;
import com.callyourmother.data.DatabaseTestFunctions;
import com.callyourmother.data.NotificationRule;

public class MainActivity extends Activity {

	private static final int NOTIFICATION_DRAWER = 0;
	private static final int CREATE_NEW_CIRCLE = 1;
	private static final int EDIT_CIRCLE = 2;
	private CircleAdapter mAdapter;
	private ListView listView1;
	public static final String NOTIFIED = "None";
	private AlarmManager mAlarmManager;

	private DatabaseClient db;

	private HashMap<String, Integer> timesContactCalled;
	private HashMap<String, Long> contactNameDate;
	private HashMap<String, String> contactNameNumber;
	private int minCallLength = 15;
	private StringBuffer callDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (null == timesContactCalled){
			timesContactCalled = new HashMap<String, Integer>();
			contactNameNumber = new HashMap<String, String>();
			contactNameDate = new HashMap<String, Long>();
		}
		//creates the database client
		db = new DatabaseClient(getApplicationContext());

		// Set up adapter, add logo header, notification drawer footer, and footer buttons
		mAdapter = new CircleAdapter(this, R.layout.circle_item, db.getCircles());
		listView1 = (ListView)findViewById(R.id.listView1);		   
		View header = (View)getLayoutInflater().inflate(R.layout.header, null);  
		listView1.addHeaderView(header);
		View notifFooterView = (View)getLayoutInflater().inflate(R.layout.notification_footer_view, null);
		listView1.addFooterView(notifFooterView);
		View circleFooterView = (View)getLayoutInflater().inflate(R.layout.add_circle_footer_view, null);
		listView1.addFooterView(circleFooterView);
		listView1.setAdapter(mAdapter);

		//Add Listener for "Add a New Circle" Button
		Button addCircleButton = (Button)findViewById(R.id.add_circle_button);
		addCircleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(MainActivity.this, AddCircleActivity.class), CREATE_NEW_CIRCLE);	
			}
		});

		// Add a Listener for the Notification indicator
		notifFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(MainActivity.this, NotificationActivity.class), NOTIFICATION_DRAWER);
			}
		});


		//Listener for listView item selection
		listView1.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Log.i("DEBUG", "Item selected at position " + position);

				List<Circle> circles = db.getCircles();
				if (position <= circles.size() && position > 0) {
					Circle curr = circles.get(position - 1);
					Intent listItemIntent = new Intent(MainActivity.this, ViewCircle.class);
					listItemIntent.putExtra("circle_id", curr.getCircleId());
					listItemIntent.putExtra("circle_description", curr.getDescription());
					startActivityForResult(listItemIntent, EDIT_CIRCLE);
				}
			}}
				);

		// Tests Notification bar Notifications
		Button notiTest = (Button) findViewById(R.id.notification_test);
		notiTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NotifyUser.setData("Matt", 10);
				Intent notiIntent = new Intent(MainActivity.this, NotifyUser.class);
				Bundle data = new Bundle();
				data.putString("data", callDetails.toString());
				notiIntent.putExtra("data", data);
				startService(notiIntent);
			}
		});

		// Listener for grabbing  user calls
		Button getCallsTest = (Button) findViewById(R.id.get_calls_test);
		getCallsTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent showCalls = new Intent(MainActivity.this, UpdateContactTransactions.class);
				startActivity(showCalls);
			}
		});
	}





	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu);

		menu.findItem(R.id.action_data_circles_test).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						DatabaseTestFunctions.TestCircles(getApplicationContext());
						Toast.makeText(getApplicationContext(), "Circles Test Completed", Toast.LENGTH_SHORT).show();
						return false;
					}
				});

		menu.findItem(R.id.action_data_android_database_delete).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						DatabaseClient db = new DatabaseClient(getApplicationContext());
						db.deleteDatabase();
						Toast.makeText(getApplicationContext(), "Database Deleted", Toast.LENGTH_SHORT).show();
						return false;
					}
				});

		menu.findItem(R.id.action_data_android_circles_create).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						DatabaseTestFunctions.CreateCircles(getApplicationContext());
						Toast.makeText(getApplicationContext(), "Sample Circles Created", Toast.LENGTH_SHORT).show();
						return false;
					}
				});

		menu.findItem(R.id.action_data_android_add_random_contacts).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						DatabaseTestFunctions.AddRandomContactsToCircles(getApplicationContext());
						Toast.makeText(getApplicationContext(), "Random Contacts Added", Toast.LENGTH_SHORT).show();
						return false;
					}
				});

		menu.findItem(R.id.action_data_notifications_test).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						DatabaseTestFunctions.TestNotificationData(getApplicationContext());
						Toast.makeText(getApplicationContext(), "Notification Test Successful", Toast.LENGTH_SHORT).show();
						return false;
					}
				});

		menu.findItem(R.id.action_data_row_counts_test).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Hashtable<String,Long> counts = db.getTableRowCounts();
						if(counts.size() == 0) {
							(new AlertDialog.Builder(MainActivity.this)).setTitle("Table Row Counts").setMessage("No tables exist in database").create().show();
						} else {
							StringBuilder sb = new StringBuilder();
							for(String tableName : counts.keySet()) {
								sb.append(tableName+" = "+counts.get(tableName)+"\r\n");
							}
							(new AlertDialog.Builder(MainActivity.this)).setTitle("Table Row Counts").setMessage(sb.toString().trim()).create().show();
						}

						return false;
					}
				});

		menu.findItem(R.id.action_data_notifications_schedule_test).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Hashtable<String,Boolean> testResults = DatabaseTestFunctions.TestNotificationRuleDateLogic(getApplicationContext());
						StringBuilder sb = new StringBuilder();
						for(String testName : testResults.keySet()) {
							sb.append(testName+" = "+testResults.get(testName)+"\r\n");
						}
						(new AlertDialog.Builder(MainActivity.this)).setTitle("Notification Logic Test Results").setMessage(sb.toString().trim()).create().show();

						return false;
					}
				});

		return true;
	}

	@Override
	protected void onResume(){
		super.onResume();
		Log.i("DEBUG", "In on resume");
		new GetCallDetailsTask(this.getApplicationContext()).execute();

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		mAdapter = new CircleAdapter(this, R.layout.circle_item, db.getCircles());  //manually update listview
		listView1.setAdapter(mAdapter);


	}


	public void checkCallLog(){
		long delay = 1000L;
		Intent startCheckContactDataIntent = new Intent(this, UpdateTransactionsService.class);
		PendingIntent pIntent = PendingIntent.getService(this,  1, startCheckContactDataIntent, PendingIntent.FLAG_ONE_SHOT);
		mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_DAY, delay, pIntent);

		//Using loader to get call info
		//getLoaderManager().initLoader(0, null, this);
	}

	private class GetCallDetailsTask extends AsyncTask<Void, Void, StringBuffer> {

		private Context mContext;
		public GetCallDetailsTask(Context applicationContext) {
			mContext = applicationContext;
		}

		@Override
		protected StringBuffer doInBackground(Void... params) {
			Log.v("GetCallDetailsTask","started");
			Cursor managedCursor = mContext.getContentResolver().query( CallLog.Calls.CONTENT_URI,null, null,null, CallLog.Calls.DATE+" DESC");
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
			callDetails = result;
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
}
