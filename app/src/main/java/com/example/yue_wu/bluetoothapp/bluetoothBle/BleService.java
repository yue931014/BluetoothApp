package com.example.yue_wu.bluetoothapp.bluetoothBle;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


import com.example.yue_wu.bluetoothapp.util.LogUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BleService extends Service {

    private static final String TAG = BleService.class.getSimpleName();

    private static final int NO_CONNECT_ACTION = 0;
    private static final int CONNECT_ACTION = 1;
    private static final int DISCONNECT_ACTION = 2;


    public final static String ACTION_GATT_CONNECTED =
            "com.example.yue_wu.bluetoothapp.bluetoothBle.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.yue_wu.bluetoothapp.bluetoothBle.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.yue_wu.bluetoothapp.bluetoothBle.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED_FAIL =
            "com.example.yue_wu.bluetoothapp.bluetoothBle.ACTION_GATT_SERVICES_DISCOVERED_FAIL";
    public final static String ACTION_GATT_CONNECTED_FAIL =
            "com.example.yue_wu.bluetoothapp.bluetoothBle.ACTION_GATT_CONNECTED_FAIL";
    public final static String ACTION_GATT_DISCONNECTED_FAIL =
            "com.example.yue_wu.bluetoothapp.bluetoothBle.ACTION_GATT_DISCONNECTED_FAIL";
    public final static String ACTION_GATT_UPDATE_RSSI =
            "com.example.yue_wu.bluetoothapp.bluetoothBle.ACTION_GATT_UPDATE_RSSI";

    public final static String EXTRA_DATA_RSSI =
            "com.example.yue_wu.bluetoothapp.bluetoothBle.EXTRA_DATA_RSSI";
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mConnectDevice;
    private int mCurrentBleConnectAction = NO_CONNECT_ACTION;
    private Timer mUpdateRssiTimer;
    private final long READ_RSSI_PERIOD = 2000;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback(){
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    LogUtil.d(TAG, "onConnectionStateChange connected");
                    sendGattUpdateBroadcast(ACTION_GATT_CONNECTED);
                    mBluetoothGatt.discoverServices();
                    mCurrentBleConnectAction = NO_CONNECT_ACTION;
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    LogUtil.d(TAG, "onConnectionStateChange disconnected");
                    sendGattUpdateBroadcast(ACTION_GATT_DISCONNECTED);
                    mCurrentBleConnectAction = NO_CONNECT_ACTION;
                }
            }else{
                LogUtil.w(TAG, "onConnectionStateChange received: " + status);
                if(mCurrentBleConnectAction == CONNECT_ACTION){
                    sendGattUpdateBroadcast(ACTION_GATT_CONNECTED_FAIL);
                }
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                sendGattUpdateBroadcast(ACTION_GATT_SERVICES_DISCOVERED);
                LogUtil.d(TAG, "onServicesDiscovered");
            } else {
                sendGattUpdateBroadcast(ACTION_GATT_SERVICES_DISCOVERED_FAIL);
                LogUtil.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtil.d(TAG, "read Rssi: " + rssi);
                sendGattUpdateBroadcast(ACTION_GATT_UPDATE_RSSI,rssi);
            }else{
                LogUtil.w(TAG, "read Rssi fail!");
            }
        }
    };
    public BleService() {
    }
    private BleConnectBinder mBinder = new BleConnectBinder();
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "onBind");
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "onUnbind");
        mBinder.cancleConnect();
        mBinder.stopReadRssiTimer();
        return super.onUnbind(intent);
    }

    public class BleConnectBinder extends Binder{

        public void startConnect(String deviceAddr){
            handleActionConnect(deviceAddr);

        }

        public void cancleConnect(){
            handleActionDisconnect();

        }
        public void startReadRssiTimer(){
            mUpdateRssiTimer = new Timer();
            mUpdateRssiTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handleActionReadRssi();
                }
            },0,READ_RSSI_PERIOD);
        }

        public void stopReadRssiTimer(){
            if(mUpdateRssiTimer != null){
                mUpdateRssiTimer.cancel();
                mUpdateRssiTimer.purge();
            }
        }
        public List<BluetoothGattService> getSupportedGattServices() {
            if (mBluetoothGatt == null) {
                return null;
            }

            return mBluetoothGatt.getServices();
        }
    }

    /**
     * Handle action Connect in the provided background thread with the provided
     * parameters.
     */
    private void handleActionConnect(String address) {
        // TODO: Handle action Connect
        mBluetoothManager =  (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter =  mBluetoothManager.getAdapter();
        mConnectDevice = mBluetoothAdapter.getRemoteDevice(address);
        if (mConnectDevice == null) {
            LogUtil.w(TAG, "Device not found.  Unable to connect.");
            return ;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = mConnectDevice.connectGatt(this, false, mGattCallback);
        LogUtil.d(TAG, "Trying to create a new connection.");
        mBluetoothGatt.connect();
        mCurrentBleConnectAction = CONNECT_ACTION;
    }

    public void handleActionDisconnect() {
        if (mBluetoothGatt == null) {
            LogUtil.w(TAG, "mBluetoothGatt is null");
            return;
        }
        int state = mBluetoothManager.getConnectionState(mConnectDevice,BluetoothProfile.GATT);
        if( state== BluetoothProfile.STATE_CONNECTED
                || state == BluetoothProfile.STATE_CONNECTING){
            mBluetoothGatt.disconnect();
        }
        mCurrentBleConnectAction = DISCONNECT_ACTION;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    private void handleActionReadRssi() {
        if (mBluetoothGatt == null) {
            LogUtil.w(TAG, "mBluetoothGatt is null");
            return;
        }
        mBluetoothGatt.readRemoteRssi();
        LogUtil.d(TAG, "handleActionReadRssi");
    }

    private void sendGattUpdateBroadcast(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    private void sendGattUpdateBroadcast(final String action,int rssi) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA_RSSI,rssi);
        sendBroadcast(intent);
    }



}
