package com.imaginaryshort.dropletter;

import com.imaginaryshort.dropletter.INotificationServiceCallback;

interface INotificationService {
    void setCallbacks(INotificationServiceCallback callback);
    void removeCallbacks();
    void checkCalbacks();
}
