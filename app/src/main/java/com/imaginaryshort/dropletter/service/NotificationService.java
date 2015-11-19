package com.imaginaryshort.dropletter.service;

import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationService extends NotificationListenerService {
    private final static String TAG = "NotificationService";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Intent intent = new Intent();
        Bundle bundle = sbn.getNotification().extras;
        String title = bundle.getString("android.title");
        String text = bundle.getString("android.text");
        String subText = bundle.getString("android.subText");
        String packageName = sbn.getPackageName();
        intent.putExtra("Title", title);
        intent.putExtra("Text", text);
        intent.putExtra("SubText", subText);
        intent.putExtra("PackageName", packageName);
        intent.setAction("com.imaginaryshort.onNotificationPosted");
        sendBroadcast(intent);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Intent intent = new Intent();
        Bundle bundle = sbn.getNotification().extras;
        String title = bundle.getString("android.title");
        String text = bundle.getString("android.text");
        String subText = bundle.getString("android.subText");
        String packageName = sbn.getPackageName();
        intent.putExtra("Title", title);
        intent.putExtra("Text", text);
        intent.putExtra("SubText", subText);
        intent.putExtra("PackageName", packageName);
        intent.setAction("com.imaginaryshort.onNotificationRemoved");
        sendBroadcast(intent);
    }
}
