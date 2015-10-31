package com.imaginaryshort.dropletter;

interface IBleServiceCallback {
    void onFind(in String address, in String name);
    void onReceive(in String str);
}
