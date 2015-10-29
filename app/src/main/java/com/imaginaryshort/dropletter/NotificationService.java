package com.imaginaryshort.dropletter;

import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Intent intent = new Intent();
        intent.putExtra("notification", sbn.getNotification().tickerText);
        intent.setAction("NOTIFICATION_ACTION");
        getBaseContext().sendBroadcast(intent);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //super.onNotificationRemoved(sbn);
    }
}
