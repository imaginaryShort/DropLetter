package com.imaginaryshort.dropletter.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
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
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.imaginaryshort.dropletter.GATTProfiles;
import com.imaginaryshort.dropletter.IBleService;
import com.imaginaryshort.dropletter.IBleServiceCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BleService extends Service{
    private final static String TAG = "BLEDevice";
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private ScanCallback mScanCallback;
    private Handler mHandler = new Handler();
    private BluetoothGatt bluetoothGatt;
    private List<BluetoothGattService> serviceList = new ArrayList<>();
    private Boolean isScanning;
    private IBleServiceCallback iBleServiceCallback;
    private String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    private IBleService.Stub bleServiceInterface = new IBleService.Stub() {
        @Override
        public void init() throws RemoteException {
            initBle();
        }

        @Override
        public void setCallbacks(IBleServiceCallback callback) throws RemoteException {
            iBleServiceCallback = callback;
        }

        @Override
        public void removeCallbacks() throws RemoteException {
            iBleServiceCallback = null;
        }

        @Override
        public void scan(long scanPeriodMs) throws RemoteException {
            startScan(iBleServiceCallback, scanPeriodMs);
        }

        @Override
        public void connect(String address) throws RemoteException {
            for(BluetoothDevice bd : deviceList){
                if(bd.getAddress().equals(address)) {
                    connectBle(getApplicationContext(), bd);
                }
            }
        }

        @Override
        public void write(String str) throws RemoteException {
            writeRx(str);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return bleServiceInterface;
    }

    private void initBle() {
        //Check the device supports BLE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getApplicationContext(), "Your device does not supports Bluetooth Low Energy", Toast.LENGTH_LONG).show();
        }
        bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager == null){
            Toast.makeText(getApplicationContext(), "Unable to initialize BluetoothLeScanner.", Toast.LENGTH_LONG).show();
        }
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Unable to initialize BluetoothLeScanner.", Toast.LENGTH_LONG).show();
        }
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothLeScanner == null) {
            Toast.makeText(getApplicationContext(), "Unable to initialize BluetoothLeScanner.", Toast.LENGTH_LONG).show();
        }
    }

    private void startScan(final IBleServiceCallback callback, final long scanPeriod) {
        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if (result != null && result.getDevice() != null) {
                    if (isAdded(result.getDevice())) {
                        // No add
                    } else {
                        saveDevice(result.getDevice());
                        if(callback != null) {
                            BluetoothDevice bd = result.getDevice();
                            try {
                                callback.onFind(bd.getAddress(), bd.getName());
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
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
                try {
                    callback.onFind(null, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                isScanning = false;
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        }, scanPeriod);

        isScanning = true;
        mBluetoothLeScanner.startScan(mScanCallback);
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

    private void connectBle(Context context, BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(getApplicationContext(), false, mGattCallback);
        bluetoothGatt.connect();
        Log.i(TAG, "Connected to the GATT server.");
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            // 接続成功し、サービス取得
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bluetoothGatt = gatt;
                Log.i(TAG, "Got bluetooth gatt");
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
                Log.i(TAG, "Service = " + s.getUuid().toString());
                for (BluetoothGattCharacteristic c : s.getCharacteristics()){
                    Log.i(TAG, "Characteristic = " + c.getUuid().toString());
                }
            }
            enableTxNotification();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i("onCharacteristicChanged", characteristic.getStringValue(0));
            if(characteristic.getUuid().toString().equals(GATTProfiles.RX.toString())){
                try {
                    Log.i("onCharacteristicChanged", characteristic.getStringValue(0));
                    iBleServiceCallback.onReceive(characteristic.getStringValue(0));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private boolean enableTxNotification() {
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(GATTProfiles.SERVICE));
        if(bluetoothGattService == null){
            Log.e(TAG, "Can't get a BluetoothGattService");
            return false;
        }
        BluetoothGattCharacteristic characteristic = bluetoothGattService.getCharacteristic(UUID.fromString(GATTProfiles.TX));
        if(characteristic == null){
            Log.e(TAG, "Can't get a BluetoothGattCharacteristic");
            return false;
        }

        boolean registerd= bluetoothGatt.setCharacteristicNotification(characteristic, true);
        if(registerd) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(descriptor);
            Log.i(TAG, "Set tx notification");
        }
        return true;
    }

    private boolean writeRx(String str){
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(GATTProfiles.SERVICE));
        if(bluetoothGattService == null){
            Log.e(TAG, "Can't get a BluetoothGattService");
            return false;
        }
        BluetoothGattCharacteristic characteristic = bluetoothGattService.getCharacteristic(UUID.fromString(GATTProfiles.RX));
        if(characteristic == null){
            Log.e(TAG, "Can't get a BluetoothGattCharacteristic");
            return false;
        }
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        characteristic.setValue(str);
        boolean flag = bluetoothGatt.writeCharacteristic(characteristic);
        Log.i(TAG, "Send : " + str  + ", " + flag);
        return true;
    }

    // サービス取得要求
    private void discoverService() {
        if (bluetoothGatt != null) {
            bluetoothGatt.discoverServices();
        }
    }
}
