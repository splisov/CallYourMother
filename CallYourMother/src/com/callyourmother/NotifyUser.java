package com.callyourmother;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

public class NotifyUser extends IntentService {
	
	private static String names = "";
	private static int days = 0;

	public NotifyUser() {
		super("NotifyUser");
	}
	
	public static void setData(String name, int day){
		names = name;
		days = day;
	}

	private void notify(boolean success) {
		// TODO: 1. Area notifications; 2. sendBroadcast or sendOrderBroadcast

		Notification.Builder mBuilder = new Notification.Builder(this)
				.setTicker("Caller Reminder!")
				.setSmallIcon(R.drawable.ic_stat_callnotification)
				.setContentTitle("Call " + names + "!")
				.setContentText(
						"It's been " + days + " days since you've called "
								+ names);

		Intent resultIntent = new Intent(this, MainActivity.class);
		// Because clicking the notification opens a new ("special") activity,
		// there's
		// no need to create an artificial back stack.
		resultIntent.putExtra(MainActivity.NOTIFIED, "Notified");
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
				resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);

		int mNotificationId = 001;
		// Gets an instance of the NotificationManager service
		mBuilder.setAutoCancel(true);
		NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
		mNotifyMgr.notify(mNotificationId, mBuilder.getNotification());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		this.notify(true);
	}

}
