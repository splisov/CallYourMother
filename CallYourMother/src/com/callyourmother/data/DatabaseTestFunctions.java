package com.callyourmother.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import com.callyourmother.data.Contact.ContactNotFoundException;

import android.content.Context;


public class DatabaseTestFunctions {
	/*
	 * Tests saving an deleting of Circle objects
	 */
	public static void TestCircles(Context context) {
		Circle circle = new Circle("test 1");
		DatabaseClient db = new DatabaseClient(context);
		db.saveCircle(circle);
		circle.setDescription("test 2");
		db.saveCircle(circle);
		db.deleteCircle(circle);
	}
	
	/*
	 * Tests saving an deleting of NotificationRule and NotificationOccurrence objects for notification rules added to both circles and contacts (requires android have at least 1 contact)
	 */
	public static void TestNotificationData(Context context) {
		DatabaseClient db = new DatabaseClient(context);
		Circle circle = new Circle("Test");
		circle = db.saveCircle(circle);
		NotificationRule circleNotificationRule = new NotificationRule("Circle NotificationRule 1", NotificationRule.INTERVAL_MONTHS, 1, new Date());
		circleNotificationRule = db.saveCircleNotificationRule(circle.getCircleId(), circleNotificationRule);
		circleNotificationRule.setDescription("Circle NotificationRule 1a");
		circleNotificationRule = db.saveCircleNotificationRule(circle.getCircleId(), circleNotificationRule);
		NotificationOccurrence circleNotificationOccurrence = new NotificationOccurrence(circleNotificationRule.getNotificationRuleId(), new Date(), NotificationOccurrence.ACTION_COMPLETED);
		circleNotificationOccurrence = db.saveNotificationOccurrence(circleNotificationOccurrence);
		db.getNotificationOccurrences(circleNotificationOccurrence.getNotificationOccurrenceId());
		db.deleteNotificationRule(circleNotificationRule.getNotificationRuleId());
		db.deleteCircle(circle);
		
		List<Contact> contacts = AndroidUtility.getAndroidContacts(context);
		Contact contact;
		if(contacts.size() > 0) {
			//test reloading contact
			try {
				contact = new Contact(contacts.get(0).getContactId(), context);
			} catch (ContactNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Random rand = new Random();
			int contactIndex = 0;
			while(contactIndex == 0) {
				contactIndex = rand.nextInt(contacts.size()-1);
			}
			contact = contacts.get(contactIndex);
		} else {
			contact = Contact.createDummyContact();
		}
		NotificationRule contactNotificationRule = new NotificationRule("Contact NotificationRule 1", NotificationRule.INTERVAL_DATE, 0, new Date());
		contactNotificationRule = db.saveContactNotificationRule(contact.getContactId(), contactNotificationRule);
		contactNotificationRule.setDescription("Contact NotificationRule 1a");
		contactNotificationRule = db.saveCircleNotificationRule(contact.getContactId(), contactNotificationRule);
		NotificationOccurrence contactNotificationOccurrence = new NotificationOccurrence(contactNotificationRule.getNotificationRuleId(), new Date(), NotificationOccurrence.ACTION_IGNORED);
		contactNotificationOccurrence = db.saveNotificationOccurrence(contactNotificationOccurrence);
		db.getNotificationOccurrences(contactNotificationOccurrence.getNotificationOccurrenceId());
		db.deleteNotificationRule(contactNotificationRule.getNotificationRuleId());
	}
	
	/*
	 * Creates a sample set of empty circles
	 */
	public static void CreateCircles(Context context) {
		DatabaseClient db = new DatabaseClient(context);
		db.saveCircle(new Circle("Family"));
		db.saveCircle(new Circle("Friends"));
		db.saveCircle(new Circle("School"));
		db.saveCircle(new Circle("Work"));
	}
	
	/*
	 * Randomly adds up to 3x as many contacts as there are groups to each group
	 */
	public static void AddRandomContactsToCircles(Context context) {
		DatabaseClient db = new DatabaseClient(context);
		List<Circle> circles = db.getCircles();
		Random rand = new Random();
		List<Contact> contacts = AndroidUtility.getAndroidContacts(context);
		if(contacts.size() > 0) {
			for(Circle c : circles) {
				int x = rand.nextInt(circles.size()*3);
				for(int y=0;y<x;y++) {
					db.saveCircleContact(c.getCircleId(), contacts.get(rand.nextInt(contacts.size()-1)).getContactId());
				}
			}
		}
	}
	
	/*
	 * Tests that adding a NotificationRule and then a NotificationOccurrence correctly calculates the next scheduled notification date 
	 */
	private class TestNotificationRuleDateSettings {
		public String key;
		public int interval;
		public int intervalIncrement;
		public int calendarField;
		public int firstOccurrence;
		public int secondOccurrence;
		public int thirdOccurrence;
		
		public TestNotificationRuleDateSettings(String key, int interval, int intervalIncrement, int calendarField, int firstOccurrence, int secondOccurrence, int thirdOccurrence) {
			this.key = key;
			this.interval = interval;
			this.intervalIncrement = intervalIncrement;
			this.calendarField = calendarField;
			this.firstOccurrence = firstOccurrence;
			this.secondOccurrence = secondOccurrence;
			this.thirdOccurrence = thirdOccurrence;
		}
	}
	public static Hashtable<String,Boolean> TestNotificationRuleDateLogic(Context context) {
		Hashtable<String,Boolean> testResults = new Hashtable<String,Boolean>();
		
		DatabaseClient db = new DatabaseClient(context);
		
		Circle circle = new Circle("Test Circle");
		circle = db.saveCircle(circle);
		
		Date startDate;
		NotificationRule notificationRule;
		NotificationOccurrence notificationOccurrence;
		Date nextNotification;
		Date expectedDate;
		Date firstOccurrence;
		Date secondOccurrence;
		Date thirdOccurrence;
		Calendar calendar = Calendar.getInstance();

		//test day rule (notify every n days)
		ArrayList<TestNotificationRuleDateSettings> testSettings = new ArrayList<TestNotificationRuleDateSettings>();
		testSettings.add(new DatabaseTestFunctions().new TestNotificationRuleDateSettings("Days", NotificationRule.INTERVAL_DAYS, 9, Calendar.DATE, 16, 25, 40));
		testSettings.add(new DatabaseTestFunctions().new TestNotificationRuleDateSettings("Weeks", NotificationRule.INTERVAL_WEEKS, 3, Calendar.DATE, 2, 4, 6));
		testSettings.add(new DatabaseTestFunctions().new TestNotificationRuleDateSettings("Months", NotificationRule.INTERVAL_MONTHS, 2, Calendar.MONTH, 3, 6, 9));
		testSettings.add(new DatabaseTestFunctions().new TestNotificationRuleDateSettings("Years", NotificationRule.INTERVAL_YEARS, 1, Calendar.YEAR, 2, 3, 4));
		
		for(TestNotificationRuleDateSettings data : testSettings) {
			//create notification rule
			startDate = new Date();
			notificationRule = new NotificationRule("Test NotificationRule Logic: "+data.key, data.interval, data.intervalIncrement, startDate);
			db.saveCircleNotificationRule(circle.getCircleId(), notificationRule);
			
			//get next date before any occurrences
			nextNotification = notificationRule.getNextNotification(null);
			calendar.setTime(startDate);
			if(data.interval == NotificationRule.INTERVAL_WEEKS) {
				calendar.add(data.calendarField, notificationRule.getIntervalIncrement()*7);
			} else {
				calendar.add(data.calendarField, notificationRule.getIntervalIncrement());
			}
			expectedDate = calendar.getTime();
			testResults.put(data.key+"(0)", nextNotification.equals(expectedDate));
			
			//get next date after 1st occurrence COMPLETED
			calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.DATE, data.firstOccurrence);
			firstOccurrence = calendar.getTime();
			notificationOccurrence = new NotificationOccurrence(notificationRule.getNotificationRuleId(), firstOccurrence, NotificationOccurrence.ACTION_COMPLETED); //create occurrence
			db.saveNotificationOccurrence(notificationOccurrence); //save occurrence
			nextNotification = notificationRule.getNextNotification(db.getNotificationOccurrences(notificationRule.getNotificationRuleId())); //get the next notification date
			if(data.interval == NotificationRule.INTERVAL_WEEKS) {
				calendar.add(data.calendarField, notificationRule.getIntervalIncrement()*7);
			} else {
				calendar.add(data.calendarField, notificationRule.getIntervalIncrement());
			}
			expectedDate = calendar.getTime();
			testResults.put(data.key+"(1)", nextNotification.equals(expectedDate));

			//get next date after 2nd occurrence IGNORED (which ignores the notification for 1 day) 
			calendar.setTime(firstOccurrence);
			calendar.add(Calendar.DATE, data.secondOccurrence);
			secondOccurrence = calendar.getTime();
			notificationOccurrence = new NotificationOccurrence(notificationRule.getNotificationRuleId(), secondOccurrence, NotificationOccurrence.ACTION_IGNORED); //create occurrence
			db.saveNotificationOccurrence(notificationOccurrence); //save occurrence
			nextNotification = notificationRule.getNextNotification(db.getNotificationOccurrences(notificationRule.getNotificationRuleId())); //get the next notification date
			calendar.add(Calendar.DATE, 1);	//add 1 day for ignoring
			expectedDate = calendar.getTime();
			testResults.put(data.key+"(2)", nextNotification.equals(expectedDate));

			//get next date after 3rd occurrence COMPLETED
			calendar.setTime(secondOccurrence);
			calendar.add(Calendar.DATE, data.thirdOccurrence);
			thirdOccurrence = calendar.getTime();
			notificationOccurrence = new NotificationOccurrence(notificationRule.getNotificationRuleId(), thirdOccurrence, NotificationOccurrence.ACTION_COMPLETED); //create occurrence
			db.saveNotificationOccurrence(notificationOccurrence); //save occurrence
			nextNotification = notificationRule.getNextNotification(db.getNotificationOccurrences(notificationRule.getNotificationRuleId())); //get the next notification date
			if(data.interval == NotificationRule.INTERVAL_WEEKS) {
				calendar.add(data.calendarField, notificationRule.getIntervalIncrement()*7);
			} else {
				calendar.add(data.calendarField, notificationRule.getIntervalIncrement());
			}
			expectedDate = calendar.getTime();
			testResults.put(data.key+"(3)", nextNotification.equals(expectedDate));
		}
		
		//check 1 time notification
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 15);
		Date notificationDate = calendar.getTime();
		notificationRule = new NotificationRule("Test NotificationRule Logic: one time on a specific date", NotificationRule.INTERVAL_DATE, 0, notificationDate);
		db.saveCircleNotificationRule(circle.getCircleId(), notificationRule);
		expectedDate = notificationDate;
		nextNotification = notificationRule.getNextNotification(db.getNotificationOccurrences(notificationRule.getNotificationRuleId())); //get the next notification date
		testResults.put("One time(1)", nextNotification.equals(expectedDate));
		calendar.setTime(notificationDate);
		calendar.add(Calendar.DATE, 15);
		firstOccurrence = calendar.getTime();
		notificationOccurrence = new NotificationOccurrence(notificationRule.getNotificationRuleId(), firstOccurrence, NotificationOccurrence.ACTION_IGNORED); //create occurrence
		db.saveNotificationOccurrence(notificationOccurrence); //save occurrence
		calendar.setTime(firstOccurrence);
		calendar.add(Calendar.DATE, 1);
		expectedDate = calendar.getTime();
		nextNotification = notificationRule.getNextNotification(db.getNotificationOccurrences(notificationRule.getNotificationRuleId())); //get the next notification date
		testResults.put("One time(2)", nextNotification.equals(expectedDate));
		secondOccurrence = calendar.getTime();
		notificationOccurrence = new NotificationOccurrence(notificationRule.getNotificationRuleId(), secondOccurrence, NotificationOccurrence.ACTION_COMPLETED); //create occurrence
		db.saveNotificationOccurrence(notificationOccurrence); //save occurrence
		nextNotification = notificationRule.getNextNotification(db.getNotificationOccurrences(notificationRule.getNotificationRuleId())); //get the next notification date
		testResults.put("One time(3)", nextNotification == null);
		
		
		//delete the circle (and its data including the notification rule and occurrences)
		db.deleteCircle(circle);
		
		return testResults;
	}
};