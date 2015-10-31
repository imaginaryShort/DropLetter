package com.imaginaryshort.dropletter;

import com.imaginaryshort.dropletter.IBleServiceCallback;

interface IBleService {
    void init();
    void setCallbacks(IBleServiceCallback callback);
    void removeCallbacks();
    void scan(long scanPeriodMs);
    void connect(String address);
    void write(String str);
}
