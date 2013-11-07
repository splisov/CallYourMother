package com.callyourmother.data;

import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

public class NotificationRule {
	
	public static final int TYPE_INTERVAL_DAYS = 1;
	public static final int TYPE_DATE_ONCE = 2;
	
	private long notificationRuleId = -1;
	private int type = -1;
	private Date notificationDate = null;
	private int interval = -1;
	private Stack<NotificationOccurrence> occurrenceHistory = new Stack<NotificationOccurrence>();
	private Date startDate = null;
	
	public NotificationRule() { }
	public NotificationRule(long notificationRuleId, int type, int interval, Date notificationDate, Date startDate) {
		this.notificationRuleId = notificationRuleId;
		this.type = type;
		this.interval = interval;
		this.notificationDate = notificationDate;
		this.startDate = startDate;
	}
	
	public long getNotificationRuleId() {
		return notificationRuleId;
	}
	
	public int getType() {
		return type;
	}
	
	public int getInterval() {
		return interval;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}
	
	public Date getCreatedDate() {
		return startDate;
	}
	
	public boolean hasOccurred() {
		return occurrenceHistory.size() > 0;
	}
	
	public NotificationOccurrence getLastOccurrence() {
		if(hasOccurred()) {
			return occurrenceHistory.firstElement();
		} else {
			return null;
		}
	}
	
	public void logOccurrence(int notificationOccurrenceAction) {
		occurrenceHistory.push(new NotificationOccurrence(notificationRuleId, new Date(), notificationOccurrenceAction));
	}
	
	public Date getNextNotification() {
		switch(type) {
		case NotificationRule.TYPE_DATE_ONCE:
			if(hasOccurred()) {
				return null;
			} else {
				return notificationDate;
			}
		case NotificationRule.TYPE_INTERVAL_DAYS:
			NotificationOccurrence lastOccurrence = getLastOccurrence();
			Date lastOccurrenceDate;
			if(lastOccurrence != null) {
				lastOccurrenceDate = lastOccurrence.getDate();
			} else {
				lastOccurrenceDate = startDate;
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lastOccurrenceDate);
			calendar.add(Calendar.DATE, interval);
			return calendar.getTime();
		default:
			return null;
		}
	}
	
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
}
