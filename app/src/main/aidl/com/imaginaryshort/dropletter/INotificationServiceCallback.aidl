package com.imaginaryshort.dropletter;

interface INotificationServiceCallback {
    void onNotify(in String str);
    void onNotificationRemoved(in String str);
    void checknotify();
}
