package com.callyourmother.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import com.callyourmother.data.Contact.ContactNotFoundException;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.*;

public class DatabaseClient {
	
	private CYMDatabase db;
	private Context mContext;
	/*
	 * initialize database client
	 */
	public DatabaseClient(Context context) {
		db = new CYMDatabase(context);
		mContext = context;
	}
	
	/*
	 * saves circle to database
	 */
	public Circle saveCircle(Circle circle) {
		SQLiteDatabase sqldb = db.getWritableDatabase();
		sqldb.beginTransaction();
		try {
			if(circle.getCircleId() > 0) {
				//update
				sqldb.execSQL("UPDATE Circles SET description = ? WHERE circleId = ?", new Object[] { circle.getDescription(), circle.getCircleId() });
			} else {
				//insert
				sqldb.execSQL("INSERT INTO Circles(description) VALUES(?)", new Object[] { circle.getDescription() });
				Cursor c = sqldb.rawQuery("SELECT MAX(circleId) FROM Circles", null);
				if(c.moveToFirst()) {
					circle.setCircleId(c.getLong(0));
				}
				c.close();
			}
			sqldb.setTransactionSuccessful();
		} finally {
			sqldb.endTransaction();
			sqldb.close();
		}
		
		return circle;
	}
	
	public void deleteCircle(Circle circle) {
		if(circle.getCircleId() > 0) {
			SQLiteDatabase sqldb = db.getWritableDatabase();
			sqldb.beginTransaction();
			try {
				//delete
				sqldb.execSQL("DELETE FROM CircleContacts WHERE circleId = ?", new Object[] { circle.getCircleId() });
				sqldb.execSQL("DELETE FROM NotificationOccurrences WHERE notificationRuleId IN(SELECT notificationRuleId FROM CircleNotificationRules WHERE circleId = ?)", new Object[] { circle.getCircleId() });
				sqldb.execSQL("DELETE FROM NotificationRules WHERE notificationRuleId IN(SELECT notificationRuleId FROM CircleNotificationRules WHERE circleId = ?)", new Object[] { circle.getCircleId() });
				sqldb.execSQL("DELETE FROM CircleNotificationRules WHERE circleId = ?", new Object[] { circle.getCircleId() });
				sqldb.execSQL("DELETE FROM Circles WHERE circleId = ?", new Object[] { circle.getCircleId() });
				sqldb.setTransactionSuccessful();
			} finally {
				sqldb.endTransaction();
				sqldb.close();
			}
		}
	}

	/*
	 * returns a list of circles from database
	 */
	public List<Circle> getCircles() {
		List<Circle> circles = new ArrayList<Circle>();

		SQLiteDatabase sqldb = db.getReadableDatabase();
		try {
			Cursor c = sqldb.rawQuery("SELECT circleId, description FROM Circles", null);
			c.moveToFirst();
			while(!c.isAfterLast()) {
				circles.add(new Circle(c.getLong(0), c.getString(1)));
				c.moveToNext();
			}
			c.close();
		} finally {
			sqldb.close();
		}
		
		return circles;
	}
	
	/*
	 * returns a list of members for a circle from the database and return their contactIds
	 */
	public List<Long> getCircleContactIds(long circleId) {
		List<Long> contactIds = new ArrayList<Long>();

		SQLiteDatabase sqldb = db.getReadableDatabase();
		try {
			Cursor c = sqldb.rawQuery("SELECT contactId FROM CircleContacts WHERE circleId = ?", new String[] { String.valueOf(circleId) });
			c.moveToFirst();
			while(!c.isAfterLast()) {
				contactIds.add(c.getLong(0));
				c.moveToNext();
			}
			c.close();
		} finally {
			sqldb.close();
		}
		
		return contactIds;
	}

	/*
	 * returns a list of members for a circle from the database and return their contactIds
	 * if a contact is not found, the contact's data is deleted from the database
	 */
	public List<Contact> getCircleContacts(long circleId, Context context) {
		List<Long> contactIds = getCircleContactIds(circleId);
		List<Contact> contacts = new ArrayList<Contact>(contactIds.size());
		for(Long contactId : contactIds) {
			try {
				contacts.add(new Contact(contactId, context));
			} catch (ContactNotFoundException e) {
				deleteContactData(contactId);
			}
		}
		return contacts;
	}
	
	/*
	 * Adds contact to circle
	 */
	public void saveCircleContact(long circleId, long contactId) {
		SQLiteDatabase sqldb = db.getWritableDatabase();
		sqldb.beginTransaction();
		try {
			//check if contact already exists in circle
			Cursor c = sqldb.rawQuery("SELECT * FROM CircleContacts WHERE contactId = ? AND circleId = ?", new String[] { String.valueOf(contactId), String.valueOf(circleId) });
			if(!c.moveToFirst()) {
				sqldb.execSQL("INSERT INTO CircleContacts(contactId, circleId) VALUES(?, ?)", new Object[] { contactId, circleId });
			}
			sqldb.setTransactionSuccessful();
		} finally {
			sqldb.endTransaction();
			sqldb.close();
		}
		
	}

	/*
	 * Deletes contact from circle
	 */
	public void deleteCircleContact(long circleId, long contactId) {
		SQLiteDatabase sqldb = db.getWritableDatabase();
		sqldb.beginTransaction();
		try {
			//delete
			sqldb.execSQL("DELETE FROM CircleContacts WHERE contactId = ? AND circleId = ?", new Object[] { contactId, circleId });
			sqldb.setTransactionSuccessful();
		} finally {
			sqldb.endTransaction();
			sqldb.close();
		}
	}

	/*
	 * deletes all data from the specified contactId from the database
	 */
	public void deleteContactData(long contactId) {
		SQLiteDatabase sqldb = db.getWritableDatabase();
		sqldb.beginTransaction();
		try {
			//delete
			sqldb.execSQL("DELETE FROM NotificationOccurrences WHERE notificationRuleId IN(SELECT notificationRuleId FROM ContactNotificationRules WHERE contactId = ?)", new Object[] { contactId });
			sqldb.execSQL("DELETE FROM NotificationRules WHERE notificationRuleId IN(SELECT notificationRuleId FROM ContactNotificationRules WHERE contactId = ?)", new Object[] { contactId });
			sqldb.execSQL("DELETE FROM ContactNotificationRules WHERE contactId = ?", new Object[] { contactId });
			sqldb.execSQL("DELETE FROM CircleContacts WHERE contactId = ?", new Object[] { contactId });
			sqldb.setTransactionSuccessful();
		} finally {
			sqldb.endTransaction();
			sqldb.close();
		}
	}
	
	/*
	 * returns a list of circleIds for the contactId from the database
	 */
	public List<Long> getContactCircleIds(long contactId) {
		List<Long> circleIds = new ArrayList<Long>();

		SQLiteDatabase sqldb = db.getReadableDatabase();
		try {
			Cursor c = sqldb.rawQuery("SELECT circleId FROM CircleContacts WHERE contactId = ?", new String[] { String.valueOf(contactId) });
			c.moveToFirst();
			while(!c.isAfterLast()) {
				circleIds.add(c.getLong(0));
				c.moveToNext();
			}
			c.close();
		} finally {
			sqldb.close();
		}
		
		return circleIds;
	}
	
	/*
	 * returns a list of notification rules for the specified contactId
	 */
	public List<NotificationRule> getContactNotificationRules(long contactId) {
		List<NotificationRule> notificationRules = new ArrayList<NotificationRule>();

		SQLiteDatabase sqldb = db.getReadableDatabase();
		try {
			Cursor c = sqldb.rawQuery("SELECT notificationRuleId, description, interval, intervalIncrement, startDate FROM NotificationRules WHERE notificationRuleId IN(SELECT notificationRuleId FROM ContactNotificationRules WHERE contactId = ?)", new String[] { String.valueOf(contactId) });
			c.moveToFirst();
			while(!c.isAfterLast()) {
				notificationRules.add( new NotificationRule(c.getLong(0), c.getString(1), c.getInt(2), c.getInt(3), (!c.isNull(4)?new Date(c.getLong(4)):null) ));
				c.moveToNext();
			}
			c.close();
		} finally {
			sqldb.close();
		}
		
		return notificationRules;
	}

	/*
	 * returns a list of notification rules for the specified circleId
	 */
	public List<NotificationRule> getCircleNotificationRules(long circleId) {
		List<NotificationRule> notificationRules = new ArrayList<NotificationRule>();

		SQLiteDatabase sqldb = db.getReadableDatabase();
		try {
			Cursor c = sqldb.rawQuery("SELECT notificationRuleId, description, interval, intervalIncrement, startDate FROM NotificationRules WHERE notificationRuleId IN(SELECT notificationRuleId FROM CircleNotificationRules WHERE circleId = ?)", new String[] { String.valueOf(circleId) });
			c.moveToFirst();
			while(!c.isAfterLast()) {
				notificationRules.add( new NotificationRule(c.getLong(0), c.getString(1), c.getInt(2), c.getInt(3), (!c.isNull(4)?new Date(c.getLong(4)):null) ));
				c.moveToNext();
			}
			c.close();
		} finally {
			sqldb.close();
		}
		
		return notificationRules;
	}

	/*
	 * saves notification rule to database
	 */
	public NotificationRule saveContactNotificationRule(long contactId, NotificationRule notificationRule) {
		SQLiteDatabase sqldb = db.getWritableDatabase();
		sqldb.beginTransaction();
		try {
			if(notificationRule.getNotificationRuleId() > 0) {
				//update
				if(notificationRule.getStartDate() != null) {
					sqldb.execSQL("UPDATE NotificationRules SET description = ?, interval = ?, intervalIncrement = ?, startDate = ? WHERE notificationRuleId = ?", new Object[] { notificationRule.getDescription(), notificationRule.getInterval(), notificationRule.getIntervalIncrement(), notificationRule.getStartDate().getTime(), notificationRule.getNotificationRuleId() });
				} else {
					sqldb.execSQL("UPDATE NotificationRules SET description = ?, interval = ?, intervalIncrement = ?, startDate = NULL WHERE notificationRuleId = ?", new Object[] { notificationRule.getDescription(), notificationRule.getInterval(), notificationRule.getIntervalIncrement(), notificationRule.getNotificationRuleId() });
				}
			} else {
				//insert
				if(notificationRule.getStartDate() != null) {
					sqldb.execSQL("INSERT INTO NotificationRules(description,interval,intervalIncrement,startDate) VALUES(?,?,?,?)", new Object[] { notificationRule.getDescription(), notificationRule.getInterval(), notificationRule.getIntervalIncrement(), notificationRule.getStartDate().getTime() });
				} else {
					sqldb.execSQL("INSERT INTO NotificationRules(description,interval,intervalIncrement,startDate) VALUES(?,?,?,NULL)", new Object[] { notificationRule.getDescription(), notificationRule.getInterval(), notificationRule.getIntervalIncrement() });
				}
				Cursor c = sqldb.rawQuery("SELECT MAX(notificationRuleId) FROM NotificationRules", null);
				if(c.moveToFirst()) {
					notificationRule.setNotificationRuleId(c.getLong(0));
				}
				c.close();
				sqldb.execSQL("INSERT INTO ContactNotificationRules(contactId, notificationRuleId) VALUES(?,?)", new Object[] { contactId, notificationRule.getNotificationRuleId() });
			}
			sqldb.setTransactionSuccessful();
		} finally {
			sqldb.endTransaction();
			sqldb.close();
		}
		
		return notificationRule;
	}

	/*
	 * saves notification rule to database
	 */
	public NotificationRule saveCircleNotificationRule(long circleId, NotificationRule notificationRule) {
		SQLiteDatabase sqldb = db.getWritableDatabase();
		sqldb.beginTransaction();
		try {
			if(notificationRule.getNotificationRuleId() > 0) {
				//update
				if(notificationRule.getStartDate() != null) {
					sqldb.execSQL("UPDATE NotificationRules SET description = ?, interval = ?, startDate = ? WHERE notificationRuleId = ?", new Object[] { notificationRule.getDescription(), notificationRule.getInterval(), notificationRule.getStartDate().getTime(), notificationRule.getNotificationRuleId() });
				} else {
					sqldb.execSQL("UPDATE NotificationRules SET description = ?, interval = ?, startDate = NULL WHERE notificationRuleId = ?", new Object[] { notificationRule.getDescription(), notificationRule.getInterval(), notificationRule.getNotificationRuleId() });
				}
			} else {
				//insert
				if(notificationRule.getStartDate() != null) {
					sqldb.execSQL("INSERT INTO NotificationRules(description,interval,intervalIncrement,startDate) VALUES(?,?,?,?)", new Object[] { notificationRule.getDescription(), notificationRule.getInterval(), notificationRule.getIntervalIncrement(), notificationRule.getStartDate().getTime() });
				} else {
					sqldb.execSQL("INSERT INTO NotificationRules(description,interval,intervalIncrement,startDate) VALUES(?,?,?,NULL)", new Object[] { notificationRule.getDescription(), notificationRule.getInterval(), notificationRule.getIntervalIncrement() });
				}
				
				Cursor c = sqldb.rawQuery("SELECT MAX(notificationRuleId) FROM NotificationRules", null);
				if(c.moveToFirst()) {
					notificationRule.setNotificationRuleId(c.getLong(0));
				}
				c.close();
				sqldb.execSQL("INSERT INTO CircleNotificationRules(circleId, notificationRuleId) VALUES(?,?)", new Object[] { circleId, notificationRule.getNotificationRuleId() });
			}
			sqldb.setTransactionSuccessful();
		} finally {
			sqldb.endTransaction();
			sqldb.close();
		}
		
		return notificationRule;
	}
	
	/*
	 * deletes notification rule from database
	 */
	public void deleteNotificationRule(long notificationRuleId) {
		SQLiteDatabase sqldb = db.getWritableDatabase();
		sqldb.beginTransaction();
		try {
			//delete
			sqldb.execSQL("DELETE FROM NotificationOccurrences WHERE notificationRuleId = ?", new Object[] { notificationRuleId });
			sqldb.execSQL("DELETE FROM NotificationRules WHERE notificationRuleId = ?", new Object[] { notificationRuleId });
			sqldb.execSQL("DELETE FROM ContactNotificationRules WHERE notificationRuleId = ?", new Object[] { notificationRuleId });
			sqldb.execSQL("DELETE FROM CircleNotificationRules WHERE notificationRuleId = ?", new Object[] { notificationRuleId });
			sqldb.setTransactionSuccessful();
		} finally {
			sqldb.endTransaction();
			sqldb.close();
		}
	}	
	
	/*
	 * returns a stack of notification occurrences with the most recent at the top of the stack
	 */
	public Stack<NotificationOccurrence> getNotificationOccurrences(long notificationRuleId) {
		Stack<NotificationOccurrence> notificationOccurrences = new Stack<NotificationOccurrence>();

		SQLiteDatabase sqldb = db.getReadableDatabase();
		try {
			Cursor c = sqldb.rawQuery("SELECT notificationOccurrenceId, date, action FROM NotificationOccurrences WHERE notificationRuleId = ? ORDER BY date", new String[] { String.valueOf(notificationRuleId) });
			c.moveToFirst();
			while(!c.isAfterLast()) {
				notificationOccurrences.push(new NotificationOccurrence(c.getLong(0), notificationRuleId, new Date(c.getLong(1)), c.getInt(2) ) );
				c.moveToNext();
			}
			c.close();
		} finally {
			sqldb.close();
		}
		
		return notificationOccurrences;
	}
	
	/*
	 * saves notification occurrence to database
	 */
	public NotificationOccurrence saveNotificationOccurrence(NotificationOccurrence notificationOccurrence) {
		SQLiteDatabase sqldb = db.getWritableDatabase();
		sqldb.beginTransaction();
		try {
			if(notificationOccurrence.getNotificationOccurrenceId() > 0) {
				//update
				sqldb.execSQL("UPDATE NotificationOccurrences SET notificationRuleId = ?, date = ?, action = ? WHERE notificationOccurrenceId = ?", new Object[] { notificationOccurrence.getNotificationRuleId(), notificationOccurrence.getDate().getTime(), notificationOccurrence.getAction(), notificationOccurrence.getNotificationOccurrenceId() });
			} else {
				//insert
				sqldb.execSQL("INSERT INTO NotificationOccurrences(notificationRuleId, date, action) VALUES(?,?,?)", new Object[] { notificationOccurrence.getNotificationRuleId(), notificationOccurrence.getDate().getTime(), notificationOccurrence.getAction() });
				Cursor c = sqldb.rawQuery("SELECT MAX(notificationOccurrenceId) FROM NotificationOccurrences", null);
				if(c.moveToFirst()) {
					notificationOccurrence.setNotificationOccurrenceId(c.getLong(0));
				}
				c.close();
			}
			sqldb.setTransactionSuccessful();
		} finally {
			sqldb.endTransaction();
			sqldb.close();
		}
		
		return notificationOccurrence;
	}

	/*
	 * Deletes the database completely
	 */
	public void deleteDatabase() {
		mContext.deleteDatabase(CYMDatabase.DATABASE_NAME);
	}
	
	/*
	 * Returns a hashtable containing the name of each table in the database as the key and the number of rows in that table as the value 
	 */
	public Hashtable<String, Long> getTableRowCounts() {
		Hashtable<String, Long> counts = new Hashtable<String, Long>();
		
		SQLiteDatabase sqldb = db.getReadableDatabase();
		try {
			//TODO get counts from all tables (see if there is a way to query the names of the tables rather than hardcoding them
			Cursor c = sqldb.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
			if(c.moveToFirst()) {
				while(!c.isAfterLast()) {
					counts.put(c.getString(0), 0l);
					c.moveToNext();
				}
			}
			c.close();
			for(String tableName : counts.keySet()) {
				counts.put(tableName, getTableRowCount(tableName, sqldb));
			}
		}
		finally {
			sqldb.close();
		}
		
		return counts;
	}

	/*
	 * Returns a count of the number of rows in the specified table 
	 */
	public long getTableRowCount(String tableName) {
		SQLiteDatabase sqldb = db.getReadableDatabase();
		long count = 0;
		try {
			count = getTableRowCount(tableName, sqldb);
		}
		finally {
			sqldb.close();
		}
		return count;
	}
	
	/*
	 * Returns a count of the number of rows in the specified table using the provided open readable database
	 */
	private long getTableRowCount(String tableName, SQLiteDatabase readableDatabase) {
		Cursor c = readableDatabase.rawQuery("SELECT COUNT(*) FROM "+tableName, null);
		long count = 0;
		if(c.moveToFirst()) {
			count = c.getLong(0);
		}
		c.close();
		return count;
	}


	
	
	/*********************************************************************************************************
	The code below handles SQLite database management, configuration, and internal interaction
	*********************************************************************************************************/
	private class CYMDatabase extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "callyourmother.db";
		private static final int DATABASE_VERSION = 1;
		private Context mContext;
		
		public CYMDatabase(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

			mContext = context;
		}


		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.beginTransaction();

				//read create definition from assets
				AssetManager assetManager = mContext.getAssets();
				String filename = "database"+File.separator+"v"+DATABASE_VERSION+File.separator+"create.sql";
				InputStream inputStream = assetManager.open(filename, AssetManager.ACCESS_BUFFER);
			    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			    StringBuilder sql = new StringBuilder();
			    String line;
			    while ((line = reader.readLine()) != null) {
			        sql.append(line);
			    }			
				
			    //run create database SQL
			    String[] sqlStatements = sql.toString().split(";");
			    for(String sqlStatement : sqlStatements){
		           db.execSQL(sqlStatement);
		        }
				
				db.setTransactionSuccessful();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			finally {
				db.endTransaction();
			}
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}	
	}
}
