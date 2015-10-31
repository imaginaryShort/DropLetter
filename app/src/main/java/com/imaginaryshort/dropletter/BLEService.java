package com.imaginaryshort.dropletter;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BLEService extends Service{
    private final static String TAG = "BLEDevice";

    private static final long SCAN_PERIOD = 10000;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private ScanCallback mScanCallback;
    private Handler mHandler = new Handler();
    private BluetoothGatt bluetoothGatt;
    private List<BluetoothGattService> serviceList = new ArrayList<>();
    private Boolean isScanning;


    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        //Check the device supports BLE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getApplicationContext(), "Your device does not supports Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            stopSelf();
        }

        bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager == null){
            Toast.makeText(getApplicationContext(), "Unable to initialize BluetoothManager.", Toast.LENGTH_LONG).show();
            stopSelf();
        }
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Unable to initialize BluetoothAdapter.", Toast.LENGTH_LONG).show();
            stopSelf();
        }
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothLeScanner == null) {
            Toast.makeText(getApplicationContext(), "Unable to initialize BluetoothLeScanner.", Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startScan() {
        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if (result != null && result.getDevice() != null) {
                    if (isAdded(result.getDevice())) {
                        // No add
                    } else {
                        saveDevice(result.getDevice());
                        //mCallback.onScanSuccess(result);
                    }
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isScanning = false;
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        }, SCAN_PERIOD);

        isScanning = true;
        mBluetoothLeScanner.startScan(mScanCallback);
        //TODO:we can use scanFilter
        // mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
    }

    // スキャン停止
    public void stopScan() {
        if (mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    // スキャンしたデバイスのリスト保存
    private void saveDevice(BluetoothDevice device) {
        deviceList.add(device);
    }

    private boolean isAdded(BluetoothDevice device) {
        if (deviceList != null && deviceList.size() > 0) {
            return deviceList.contains(device);
        } else {
            return false;
        }
    }

    public void connect(Context context, BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(context, false, mGattCallback);
        bluetoothGatt.connect();
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            // 接続成功し、サービス取得
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bluetoothGatt = gatt;
                discoverService();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            serviceList = gatt.getServices();
            for (BluetoothGattService s : serviceList) {
                // サービス一覧を取得したり探したりする処理
                // あとキャラクタリスティクスを取得したり探したりしてもよい
            }
        }
    };

    public boolean write(String str){
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(GATTProfiles.SERVICE);
        if(bluetoothGattService == null){
            Log.e(TAG, "Can't get a BluetoothGattService");
            return false;
        }
        BluetoothGattCharacteristic characteristic = bluetoothGattService.getCharacteristic(GATTProfiles.TX);
        if(characteristic == null){
            Log.e(TAG, "Can't get a BluetoothGattCharacteristic");
            return false;
        }
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        characteristic.setValue(str);
        bluetoothGatt.writeCharacteristic(characteristic);
        return true;
    }

    // サービス取得要求
    public void discoverService() {
        if (bluetoothGatt != null) {
            bluetoothGatt.discoverServices();
        }
    }
}
