package com.imaginaryshort.dropletter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity implements MainFragment.OnFragmentInteractionListener,
        DeviceListFragment.OnFragmentInteractionListener {

    private INotificationService notificationServiceInterface;
    private ServiceConnection notificationConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            notificationServiceInterface = INotificationService.Stub.asInterface(service);
            try {
                notificationServiceInterface.setCallbacks(notificationCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                notificationServiceInterface.removeCallbacks();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private IBleService bleServiceInterface;
    private ServiceConnection bleServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleServiceInterface = IBleService.Stub.asInterface(service);
            try {
                bleServiceInterface.init();
                bleServiceInterface.setCallbacks(callback);
                bleServiceInterface.scan(10000);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleServiceInterface = null;
        }
    };

    private IBleServiceCallback callback = new IBleServiceCallback.Stub() {
        @Override
        public void onFind(String address, String name) throws RemoteException {
            if(address != null)
                Log.d("MainActivity", "uuid = " + address + ", name = " + name);
        }

        @Override
        public void onReceive(String str) throws RemoteException {

        }
    };

    private INotificationServiceCallback notificationCallback = new INotificationServiceCallback.Stub() {
        @Override
        public void onNotify(String str) throws RemoteException {
            Log.d("Notification", str);
        }

        @Override
        public void onNotificationRemoved(String str) throws RemoteException {
            Log.d("Notification", str);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DeviceListFragment deviceListFragment = new DeviceListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.container, deviceListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent b = new Intent(this, BleService.class);
        bindService(b, bleServiceConnection, BIND_AUTO_CREATE);

        Intent n = new Intent(this, NotificationService.class);
        bindService(n, notificationConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        try {
            bleServiceInterface.removeCallbacks();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(bleServiceConnection);

        try {
            notificationServiceInterface.removeCallbacks();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(notificationConnection);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void next() {
        MainFragment mainFragment = new MainFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mainFragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
