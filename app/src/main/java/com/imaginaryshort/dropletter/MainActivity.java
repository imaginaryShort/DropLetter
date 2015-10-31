package com.imaginaryshort.dropletter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements MainFragment.OnFragmentInteractionListener {
    private Intent notificationIntent = null;
    private IBleService bleServiceInterface;

    private ServiceConnection bleServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleServiceInterface = IBleService.Stub.asInterface(service);
            try {
                bleServiceInterface.init();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try{
                bleServiceInterface.scan(callback, 10000);
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
        public void onScanResult(String uuid, String name) throws RemoteException {
            if(uuid != null)
            Log.d("MainActivity", "uuid = " + uuid + ", name = " + name);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //NotificationServiceの起動
        notificationIntent = new Intent(this, NotificationService.class);
        startService(notificationIntent);
        IntentFilter notificationIntentFilter = new IntentFilter();
        notificationIntentFilter.addAction("NOTIFICATION_ACTION");
        registerReceiver(notificationReceiver, notificationIntentFilter);

        if (savedInstanceState == null) {
            MainFragment mainFragment = new MainFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.container, mainFragment).commit();
        }

        Intent intent = new Intent(this, BleService.class);
        bindService(intent, bleServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(bleServiceConnection);
        //NotificationServiceの終了
        stopService(notificationIntent);
    }

    public BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString("notification");
            Toast.makeText(context, "onReceive! " + message, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
