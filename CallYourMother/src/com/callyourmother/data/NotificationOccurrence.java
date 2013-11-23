package com.callyourmother.data;

import java.util.Date;

public class NotificationOccurrence {
	public static final int ACTION_COMPLETED = 1;
	public static final int ACTION_IGNORED = 1;
	
	private long notificationOccurrenceId = -1;
	private long notificationRuleId;
	private Date date;
	private int action;
	
	public NotificationOccurrence(long notificationOccurrenceId, long notificationRuleId, Date date, int action) {
		this.notificationRuleId = notificationRuleId;
		this.date = date;
		this.action = action;
	}
	public NotificationOccurrence(long notificationRuleId, Date date, int action) {
		this.notificationRuleId = notificationRuleId;
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
	
	public int getAction() {
		return action;
	}
	
}