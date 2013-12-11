package com.callyourmother.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class NotificationRule {
	
	/*
	 * Occurs once on a specific date
	 */
	public static final int INTERVAL_DATE = 1;
	/*
	 * Occurs every n hours, where n = intervalIncrement
	 */
	public static final int INTERVAL_HOURS = 2;
	/*
	 * Occurs every n days, where n = intervalIncrement
	 */
	public static final int INTERVAL_DAYS = 3;
	/*
	 * Occurs every n weeks, where n = intervalIncrement
	 */
	public static final int INTERVAL_WEEKS = 4;
	/*
	 * Occurs every n months, where n = intervalIncrement
	 */
	public static final int INTERVAL_MONTHS = 5;
	/*
	 * Occurs every n years, where n = intervalIncrement
	 */
	public static final int INTERVAL_YEARS = 6;
	
	
	private long notificationRuleId = -1;
	private int interval = -1;
	private int intervalIncrement = 0;
	private Date startDate = null;
	private String description;
	
	public NotificationRule(String description, int interval, int intervalIncrement, Date startDate) {
		this.interval = interval;
		this.intervalIncrement = intervalIncrement;
		this.startDate = startDate;
	}
	public NotificationRule(long notificationRuleId, String description, int interval, int intervalIncrement, Date startDate) {
		this.notificationRuleId = notificationRuleId;
		this.interval = interval;
		this.intervalIncrement = intervalIncrement;
		this.startDate = startDate;
	}

	
	public void setNotificationRuleId(long notificationRuleId) {
		this.notificationRuleId = notificationRuleId;
	}
	
	public long getNotificationRuleId() {
		return notificationRuleId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getInterval() {
		return interval;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	public int getIntervalIncrement() {
		return intervalIncrement;
	}
	
	public void setIntervalIncrement(int intervalIncrement) {
		this.intervalIncrement = intervalIncrement;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/*
	 * Returns the date for the next notification based on the history of notifications or null if no more notifications are necessary
	 * If no history is available, the next notification will be based on the startDate
	 */
	public Date getNextNotification(List<NotificationOccurrence> notificationOccurrenceHistory, long contactId) {
		ArrayList<NotificationOccurrence> contactNotificationOccurrenceHistory = new ArrayList<NotificationOccurrence>();
		for(NotificationOccurrence no : notificationOccurrenceHistory) {
			if(no.getContactId() == contactId) {
				contactNotificationOccurrenceHistory.add(no);
			}
		}
		return getNextNotification(contactNotificationOccurrenceHistory);
	}
	/*
	 * Returns the date for the next notification based on the history of notifications or null if no more notifications are necessary
	 * If no history is available, the next notification will be based on the startDate
	 */
	public Date getNextNotification(List<NotificationOccurrence> notificationOccurrenceHistory) {
		if(interval == NotificationRule.INTERVAL_DATE) {
			if(notificationOccurrenceHistory == null || notificationOccurrenceHistory.size() == 0) {
				return startDate;
			} else {
				NotificationOccurrence lastOccurrence = getMostRecentNotificationOccurrence(notificationOccurrenceHistory);
				if(lastOccurrence.getAction() == NotificationOccurrence.ACTION_COMPLETED) {
					return null;
				} else {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(lastOccurrence.getDate());
					calendar.add(Calendar.DATE, 1);
					return calendar.getTime();
				}
			}
		} else {
			NotificationOccurrence lastOccurrence = getMostRecentNotificationOccurrence(notificationOccurrenceHistory); 
			Date lastOccurrenceDate;
			if(lastOccurrence != null) {
				lastOccurrenceDate = lastOccurrence.getDate();
			} else {
				lastOccurrenceDate = startDate;
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lastOccurrenceDate);
			if(lastOccurrence != null && lastOccurrence.getAction() == NotificationOccurrence.ACTION_IGNORED) {
				calendar.add(Calendar.DATE, 1);
			} else {
				switch(interval) {
					case NotificationRule.INTERVAL_HOURS:
						calendar.add(Calendar.HOUR, intervalIncrement);
						break;
					case NotificationRule.INTERVAL_DAYS:
						calendar.add(Calendar.DATE, intervalIncrement);
						break;
					case NotificationRule.INTERVAL_WEEKS:
						calendar.add(Calendar.DATE, intervalIncrement*7);
						break;
					case NotificationRule.INTERVAL_MONTHS:
						calendar.add(Calendar.MONTH, intervalIncrement);
						break;
					case NotificationRule.INTERVAL_YEARS:
						calendar.add(Calendar.YEAR, intervalIncrement);
						break;
					default:
						return null;
				}
			}
			return calendar.getTime();
		}
	}
	
	public NotificationOccurrence getMostRecentNotificationOccurrence(List<NotificationOccurrence> notificationOccurrenceHistory) {
		NotificationOccurrence mostRecent = null;
		if(notificationOccurrenceHistory != null && notificationOccurrenceHistory.size() > 0) {
			for(NotificationOccurrence no : notificationOccurrenceHistory) {
				if(mostRecent == null || mostRecent.getDate().before(no.getDate())) {
					mostRecent = no;
				}
			}
		}
		return mostRecent;
	}
}
