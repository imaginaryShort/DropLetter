package com.imaginaryshort.dropletter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements MainFragment.OnFragmentInteractionListener {
    private Intent notificationIntent = null;
    private Intent bleIntent = null;

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

        //BLEServiceの起動
        bleIntent = new Intent(this, BLEService.class);
        startService(bleIntent);
        IntentFilter bleIntentFilter = new IntentFilter();
        bleIntentFilter.addAction("NOTIFICATION_ACTION");
        registerReceiver(bleReceiver, bleIntentFilter);


        if (savedInstanceState == null) {
            MainFragment mainFragment = new MainFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.container, mainFragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        //NotificationServiceの終了
        stopService(notificationIntent);
        stopService(bleIntent);
        super.onDestroy();
    }

    public BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString("notification");
            Toast.makeText(context, "onReceive! " + message, Toast.LENGTH_LONG).show();
        }
    };

    public BroadcastReceiver bleReceiver = new BroadcastReceiver() {
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
