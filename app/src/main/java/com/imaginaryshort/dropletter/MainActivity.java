package com.imaginaryshort.dropletter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check the device supports BLE
        //if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
        //    Toast.makeText(getApplicationContext(), "Your device does not supports Bluetooth Low Energy", Toast.LENGTH_LONG).show();
        //    finish();
        //}

        //NotificationServiceの起動
        intent = new Intent(this, NotificationService.class);
        startService(intent);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("NOTIFICATION_ACTION");
        registerReceiver(myReceiver, intentFilter);


    }

    @Override
    protected void onDestroy() {
        //NotificationServiceの終了
        stopService(intent);
        super.onDestroy();
    }

    public BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString("notification");
            Toast.makeText(context, "onReceive! " + message, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
}
