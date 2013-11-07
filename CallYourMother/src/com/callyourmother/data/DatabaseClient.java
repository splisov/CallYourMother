package com.callyourmother.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.*;

public class DatabaseClient extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "callyourmother.db";
	private static final int DATABASE_VERSION = 1;
	private Context mContext;
	
	/*
	 * initialize database client
	 */
	public DatabaseClient(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		mContext = context;
	}
	
	/*
	 * saves contact to database
	 */
	public void saveContact(Contact contact) {
		//TODO 
	}
	
	/*
	 * deletes contact from database
	 */
	public void deleteContact(Contact contact) {
		//TODO 
	}
	
	/*
	 * saves notification rule to database
	 */
	public void saveContactNotificationRule(long contactId, NotificationRule notificationRule) {
		//TODO 
	}

	/*
	 * saves notification rule to database
	 */
	public void saveCircleNotificationRule(long circleId, NotificationRule notificationRule) {
		//TODO 
	}
	
	/*
	 * deletes notification rule from database
	 */
	public void deleteNotificationRule(long notificationRuleId) {
		//TODO 
	}

	/*
	 * returns a list of all contacts from database and load their properties from android
	 */
	public List<Contact> getContacts() {
		//TODO
		return null;
	}

	/*
	 * returns a list of circles from database
	 */
	public List<Contact> getCircles() {
		//TODO
		return null;
	}
	
	/*
	 * returns a list of members for a circle from the database and return their contactIds
	 */
	public List<Long> getCircleContactIds(long circleId) {
		//TODO
		return null;
	}
	
	/*
	 * returns a list of circleIds for the contactId from the database
	 */
	public List<Long> getContactCircleIds(long contactId) {
		//TODO
		return null;
	}
	
	/*
	 * returns a list of notification rules for the specified contactId
	 */
	public List<NotificationRule> getContactNotificationRules(long contactId) {
		//TODO
		return null;
	}

	/*
	 * returns a list of notification rules for the specified circleId
	 */
	public List<NotificationRule> getCircleNotificationRules(long circleId) {
		//TODO
		return null;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if(true) {
			//THIS DOES NOT WORK YET, PREVENTING ACCIDENTAL EXECUTION 
			return;
		}
		try {
			db.beginTransaction();

			//read create definition from assets
			AssetManager assetManager = mContext.getAssets();
			InputStream inputStream = assetManager.open(String.format("%1$s", File.separator, DATABASE_VERSION), AssetManager.ACCESS_BUFFER);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		    StringBuilder sql = new StringBuilder();
		    String line;
		    while ((line = reader.readLine()) != null) {
		        sql.append(line);
		    }			
			
			//run create database SQL
		    db.execSQL(sql.toString());
			
			db.setTransactionSuccessful();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			db.close();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
