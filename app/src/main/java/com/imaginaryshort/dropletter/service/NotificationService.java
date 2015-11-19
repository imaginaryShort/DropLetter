package com.imaginaryshort.dropletter.service;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.imaginaryshort.dropletter.INotificationService;
import com.imaginaryshort.dropletter.INotificationServiceCallback;

public class NotificationService extends NotificationListenerService {

    private final static String TAG = "NotificationService";
    private INotificationServiceCallback iNotificationServiceCallback;
    private INotificationService.Stub notificationServiceInterface = new INotificationService.Stub() {
        @Override
        public void setCallbacks(INotificationServiceCallback callback) throws RemoteException {
            iNotificationServiceCallback = callback;
        }

        @Override
        public void removeCallbacks() throws RemoteException {
            iNotificationServiceCallback = null;
        }

        @Override
        public void checkCalbacks() throws RemoteException {

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return notificationServiceInterface;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, sbn.getNotification().tickerText.toString());
        if(iNotificationServiceCallback != null) {
            try {
                iNotificationServiceCallback.onNotify(sbn.getNotification().tickerText.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if(iNotificationServiceCallback != null) {
            try {
                iNotificationServiceCallback.onNotify(sbn.getNotification().tickerText.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
