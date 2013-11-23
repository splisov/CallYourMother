package com.callyourmother.data;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotificationRule {
	
	public static final int INTERVAL_DATE = 1;
	public static final int INTERVAL_HOURS = 2;
	public static final int INTERVAL_DAYS = 3;
	public static final int INTERVAL_WEEKS = 4;
	public static final int INTERVAL_MONTHS = 5;
	public static final int INTERVAL_YEARS = 6;
	
	public static final int TYPE_ONETIME = 1;
	public static final int TYPE_REPEATING = 2;
	
	
	private long notificationRuleId = -1;
	private int type = -1;
	private Date notificationDate = null;
	private int interval = -1;
	private Date startDate = null;
	private String description;
	
	public NotificationRule() { }
	public NotificationRule(long notificationRuleId, String description, int type, int interval, Date notificationDate, Date startDate) {
		this.notificationRuleId = notificationRuleId;
		this.type = type;
		this.interval = interval;
		this.notificationDate = notificationDate;
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
	
	public void setType(int type) {
		this.type = type;
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

	public Date getNotificationDate() {
		return notificationDate;
	}

	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getNextNotification(List<NotificationOccurrence> notificationOccurrenceHistory) {
		if(interval == NotificationRule.INTERVAL_DATE) {
			if(notificationOccurrenceHistory == null || notificationOccurrenceHistory.size() == 0) {
				return null;
			} else {
				return notificationDate;
			}
		} else {
			NotificationOccurrence lastOccurrence = (notificationOccurrenceHistory!=null&&notificationOccurrenceHistory.size()>0?notificationOccurrenceHistory.get(notificationOccurrenceHistory.size()-1):null);
			Date lastOccurrenceDate;
			if(lastOccurrence != null) {
				lastOccurrenceDate = lastOccurrence.getDate();
			} else {
				lastOccurrenceDate = startDate;
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lastOccurrenceDate);
			switch(interval) {
				case NotificationRule.INTERVAL_HOURS:
					calendar.add(Calendar.HOUR, interval);
					break;
				case NotificationRule.INTERVAL_DAYS:
					calendar.add(Calendar.DATE, interval);
					break;
				case NotificationRule.INTERVAL_WEEKS:
					calendar.add(Calendar.DATE, interval*7);
					break;
				case NotificationRule.INTERVAL_MONTHS:
					calendar.add(Calendar.MONTH, interval);
					break;
				case NotificationRule.INTERVAL_YEARS:
					calendar.add(Calendar.YEAR, interval);
					break;
				default:
					return null;
			}
			return calendar.getTime();
		}
	}
}
