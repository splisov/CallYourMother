package com.callyourmother;

import java.util.Hashtable;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.callyourmother.data.*;

public class MainActivity extends Activity {

	private static final int NOTIFICATION_DRAWER = 0;
	private static final int CREATE_NEW_CIRCLE = 1;
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
		
		//Circle circle_data[] = new Circle[]{new Circle("Test"),new Circle("Showers")};
		mAdapter = new CircleAdapter(this, R.layout.circle_item, db.getCircles());
			         
		listView1 = (ListView)findViewById(R.id.listView1);		      
		View header = (View)getLayoutInflater().inflate(R.layout.header, null);
		listView1.addHeaderView(header);
		View notifFooterView = (View)getLayoutInflater().inflate(R.layout.notification_footer_view, null);
		View circleFooterView = (View)getLayoutInflater().inflate(R.layout.add_circle_footer_view, null);
		listView1.addFooterView(notifFooterView);
		listView1.addFooterView(circleFooterView);
		listView1.setAdapter(mAdapter);
		
	/*	notifFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(MainActivity.this,
						NotificationActivity.class), NOTIFICATION_DRAWER);
			}
		});*/
		
		
		
		
		circleFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,
						AddCircleActivity.class));
			}
		});
		
		Button notiTest = (Button) findViewById(R.id.notification_test);

		notiTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent notiIntent = new Intent(MainActivity.this, NotifyUser.class);
				startService(notiIntent);
				
			}
		});
		
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

	public void checkCallLog(){
		long delay = 1000L;
		Intent startCheckContactDataIntent = new Intent(this, UpdateTransactionsService.class);
		PendingIntent pIntent = PendingIntent.getService(this,  1, startCheckContactDataIntent, PendingIntent.FLAG_ONE_SHOT);
		mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_DAY, delay, pIntent);
		
		//Using loader to get call info
		//getLoaderManager().initLoader(0, null, this);
	}
}
