package com.callyourmother.data;

import java.util.Date;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.widget.Toast;

public class DatabaseTestFunctions {
	public static void TestCircles(Context context) {
		Circle circle = new Circle("test 1");
		DatabaseClient db = new DatabaseClient(context);
		db.saveCircle(circle);
		circle.setDescription("test 2");
		db.saveCircle(circle);
		db.deleteCircle(circle);
	}
	
	public static void TestNotificationData(Context context) {
		DatabaseClient db = new DatabaseClient(context);
		Circle circle = new Circle("Test");
		circle = db.saveCircle(circle);
		NotificationRule circleNotificationRule = new NotificationRule();
		circleNotificationRule.setDescription("Circle NotificationRule 1");
		circleNotificationRule.setInterval(NotificationRule.INTERVAL_MONTHS);
		circleNotificationRule.setType(NotificationRule.TYPE_REPEATING);
		circleNotificationRule.setStartDate(new Date());
		circleNotificationRule = db.saveCircleNotificationRule(circle.getCircleId(), circleNotificationRule);
		circleNotificationRule.setDescription("Circle NotificationRule 1a");
		circleNotificationRule = db.saveCircleNotificationRule(circle.getCircleId(), circleNotificationRule);
		NotificationOccurrence circleNotificationOccurrence = new NotificationOccurrence(circleNotificationRule.getNotificationRuleId(), new Date(), NotificationOccurrence.ACTION_COMPLETED);
		circleNotificationOccurrence = db.saveNotificationOccurrence(circleNotificationOccurrence);
		db.deleteNotificationRule(circleNotificationRule.getNotificationRuleId());
		db.deleteCircle(circle);
		
		List<Contact> contacts = AndroidUtility.getAndroidContacts(context);
		Random rand = new Random();
		Contact contact = contacts.get(rand.nextInt(contacts.size()-1));
		NotificationRule contactNotificationRule = new NotificationRule();
		contactNotificationRule.setDescription("Contact NotificationRule 1");
		contactNotificationRule.setInterval(NotificationRule.INTERVAL_DATE);
		contactNotificationRule.setType(NotificationRule.TYPE_ONETIME);
		contactNotificationRule.setStartDate(new Date());
		contactNotificationRule.setNotificationDate(new Date());
		contactNotificationRule = db.saveContactNotificationRule(contact.getContactId(), contactNotificationRule);
		contactNotificationRule.setDescription("Contact NotificationRule 1a");
		contactNotificationRule = db.saveCircleNotificationRule(contact.getContactId(), contactNotificationRule);
		NotificationOccurrence contactNotificationOccurrence = new NotificationOccurrence(contactNotificationRule.getNotificationRuleId(), new Date(), NotificationOccurrence.ACTION_IGNORED);
		contactNotificationOccurrence = db.saveNotificationOccurrence(contactNotificationOccurrence);
		db.deleteNotificationRule(contactNotificationRule.getNotificationRuleId());
	}
	
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
}