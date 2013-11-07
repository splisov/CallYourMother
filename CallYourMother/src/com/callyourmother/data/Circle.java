package com.callyourmother.data;

import java.util.ArrayList;
import java.util.List;

public class Circle {
	private long circleId = -1;
	private String displayName;
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private ArrayList<NotificationRule> notificationRules = new ArrayList<NotificationRule>();

	public Circle(String displayName) {
		this.displayName = displayName;
	}

	public Circle(long circleId, String displayName) {
		this.circleId = circleId;
		this.displayName = displayName;
	}
	
	public long getCircleId() {
		return circleId;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	/*
	 * returns a list of notification rules assigned specifically to this circle
	 */
	public List<NotificationRule> getNotificationRules() {
		ArrayList<NotificationRule> list = new ArrayList<NotificationRule>();
		list.addAll(notificationRules);
		return list;
	}
	
	public boolean hasNotificationRules() {
		return notificationRules.size() > 0;
	}
	
	/*
	 * adds a notification rule for this specific circle
	 */
	public boolean addNotificationRule(NotificationRule notificationRule) {
		if(!notificationRules.contains(notificationRule)) {
			return notificationRules.add(notificationRule);
		} else {
			return false;
		}
	}

	/*
	 * removes a notification rule assigned specifically to this circle
	 */
	public boolean removeNotificationRule(NotificationRule notificationRule) {
		return notificationRules.remove(notificationRule);
	}
	
	public boolean addContact(Contact contact) {
		return contacts.add(contact);
	}
	
	public boolean removeContact(Contact contact) {
		return contacts.remove(contact);
	}
	
	public List<Contact> getContacts() {
		ArrayList<Contact> list = new ArrayList<Contact>();
		list.addAll(contacts);
		return list;
	}
}
