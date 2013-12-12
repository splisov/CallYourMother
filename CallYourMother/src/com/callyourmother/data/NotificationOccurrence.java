package com.callyourmother.data;

import java.util.Date;

public class NotificationOccurrence {
	public static final int ACTION_COMPLETED = 1;
	public static final int ACTION_IGNORED = 2;
	
	private long notificationOccurrenceId = -1;
	private long notificationRuleId;
	private long contactId = -1;
	private Date date;
	private int action;
	
	public NotificationOccurrence(long notificationRuleId, long contactId, Date date, int action) {
		this.notificationRuleId = notificationRuleId;
		this.contactId = contactId;
		this.date = date;
		this.action = action;
	}
	public NotificationOccurrence(long notificationOccurrenceId, long notificationRuleId, long contactId, Date date, int action) {
		this.notificationOccurrenceId = notificationOccurrenceId;
		this.notificationRuleId = notificationRuleId;
		this.contactId = contactId;
		this.date = date;
		this.action = action;
	}
	
	public void setNotificationOccurrenceId(long notificationOccurrenceId) {
		this.notificationOccurrenceId = notificationOccurrenceId;
	}

	public long getNotificationOccurrenceId() {
		return notificationOccurrenceId;
	}

	public long getNotificationRuleId() {
		return notificationRuleId;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setAction(int action){
		this.action = action;
	}
	
	public int getAction() {
		return action;
	}
	
	public long getContactId() {
		return contactId;
	}
}