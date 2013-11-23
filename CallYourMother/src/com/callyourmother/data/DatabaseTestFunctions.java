package com.callyourmother.data;

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
