package com.imaginaryshort.dropletter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Menu;
import android.widget.Toast;

import com.imaginaryshort.dropletter.fragment.DeviceListFragment;
import com.imaginaryshort.dropletter.fragment.MainFragment;
import com.imaginaryshort.dropletter.service.BleService;
import com.imaginaryshort.dropletter.service.NotificationService;

public class MainActivity extends Activity implements MainFragment.OnFragmentInteractionListener,
        DeviceListFragment.OnFragmentInteractionListener {
    private DeviceListFragment deviceListFragment;
    private IBleService bleServiceInterface;
    private NotificationBroadcastReceiver receiver;
    private IntentFilter filter;

    private ServiceConnection bleServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleServiceInterface = IBleService.Stub.asInterface(service);
            try {
                bleServiceInterface.setCallbacks(callback);
                bleServiceInterface.init();
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
            if (address != null)
                deviceListFragment.addDevice(address, name);
        }

        @Override
        public void onReceive(String str) throws RemoteException {

        }
    };

    private class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String packageName = bundle.getString("PackageName");
            String text = bundle.getString("Text");
            int value = text.length() < 255 ? text.length() : 255;
            Toast.makeText(context, packageName + ", " + text, Toast.LENGTH_LONG).show();
            try {
                bleServiceInterface.write("value:" + value);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private class SQLiteHelper extends SQLiteOpenHelper {
        static final String DB = "dropletter.db";
        static final int DB_VERSION = 1;
        static final String CREATE_TABLE = "create table informant (" +
                "id integer primary key autoincrement," +
                "package_name text not null," +
                "count integer not null," +
                "last_notified integer not null," +
                "importance integer not null," +
                ");";
        static final String DROP_TABLE = "drop table informant;";

        public SQLiteHelper(Context c) {
            super(c, DB, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceListFragment = new DeviceListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.container, deviceListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        receiver = new NotificationBroadcastReceiver();
        filter = new IntentFilter("com.imaginaryshort.onNotificationPosted");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent b = new Intent(this, BleService.class);
        bindService(b, bleServiceConnection, BIND_AUTO_CREATE);

        Intent n = new Intent(this, NotificationService.class);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        try {
            bleServiceInterface.removeCallbacks();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(bleServiceConnection);
        unregisterReceiver(receiver);
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
    public void connect(String address) {
        try {
            bleServiceInterface.connect(address);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(String str) {
        try {
            bleServiceInterface.write(str);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify_button() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                builder.setTicker("ticker");
                builder.setContentTitle("title");
                builder.setContentText("text");
                builder.setSmallIcon(android.R.drawable.ic_dialog_info);
                Notification notification = builder.build();
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(0, notification);
            }
        }, 5000);
    }
}
