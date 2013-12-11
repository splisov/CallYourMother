package com.callyourmother;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import com.callyourmother.data.Circle;
import com.callyourmother.data.DatabaseClient;
import com.callyourmother.data.DatabaseTestFunctions;

public class MainActivity extends Activity {

	private static final int NOTIFICATION_DRAWER = 0;
	private static final int CREATE_NEW_CIRCLE = 1;
	private static final int EDIT_CIRCLE = 2;
	private CircleAdapter mAdapter;
	private ListView listView1;
	public static final String NOTIFIED = "None";
	private AlarmManager mAlarmManager;
	
	private DatabaseClient db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
				if (position < circles.size() && position > 0) {
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
}
