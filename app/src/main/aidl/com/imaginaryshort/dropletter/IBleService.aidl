package com.imaginaryshort.dropletter;

import com.imaginaryshort.dropletter.IBleServiceCallback;

interface IBleService {
    void init();
    void scan(IBleServiceCallback callback, long scanPeriodMs);
}
