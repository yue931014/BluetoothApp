package com.example.yue_wu.bluetoothapp;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yue_wu.bluetoothapp.bluetoothBle.BleGattCharacteristic;
import com.example.yue_wu.bluetoothapp.bluetoothBle.BleGattService;
import com.example.yue_wu.bluetoothapp.bluetoothBle.BleService;
import com.example.yue_wu.bluetoothapp.bluetoothBle.BleServiceListAdapter;
import com.example.yue_wu.bluetoothapp.util.LogUtil;

import java.util.ArrayList;
import java.util.List;


public class BleServiceListActivity extends BaseActivity {
    private static final String TAG = BleServiceListActivity.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private String mDeviceName;
    private String mDeviceAddress;
    private BleService.BleConnectBinder mBleConnectBinder;
    private ProgressDialog mProgressDialog;
    private ArrayList<BleGattService> mBleGattServiceList = new ArrayList<>();
    private BleServiceListAdapter mBleServiceListAdapter;
    private MenuItem mMenuBleConnectState;
    private MenuItem mMenuBleRssi;

    private final BroadcastReceiver mGattUpdateBroadcastReceiver= new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action){
                case BleService.ACTION_GATT_CONNECTED:
                    mProgressDialog.setMessage(getResources().getString(R.string.discovery_service));
                    mMenuBleConnectState.setTitle("Connected");
                    mBleConnectBinder.startReadRssiTimer();
                    Toast.makeText(context,"Connect successfully",Toast.LENGTH_SHORT).show();
                    break;
                case BleService.ACTION_GATT_DISCONNECTED:
                    mMenuBleConnectState.setTitle("Disconnected");
                    Toast.makeText(context,"Disconnect successfully",Toast.LENGTH_SHORT).show();
                    break;
                case BleService.ACTION_GATT_SERVICES_DISCOVERED:
                    showServiceList();
                    break;
                case BleService.ACTION_GATT_CONNECTED_FAIL:
                    mProgressDialog.dismiss();
                    Toast.makeText(context,"Connect fail",Toast.LENGTH_SHORT).show();
                    break;
                case BleService.ACTION_GATT_SERVICES_DISCOVERED_FAIL:
                    mProgressDialog.dismiss();
                    Toast.makeText(context,"Discover service fail",Toast.LENGTH_SHORT).show();
                    break;
                case BleService.ACTION_GATT_UPDATE_RSSI:
                    mMenuBleRssi.setTitle(new Integer(intent.getIntExtra(BleService.EXTRA_DATA_RSSI,0)).toString());
                    break;
                default:
                    break;
            }

        }
    };
    private ServiceConnection mBleServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBleConnectBinder = (BleService.BleConnectBinder)service;
            mBleConnectBinder.startConnect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_service_list);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(R.string.service_list);
        }
        //actionBar.setDisplayHomeAsUpEnabled(true);
        mBleServiceListAdapter = new BleServiceListAdapter(BleServiceListActivity.this,
                R.layout.bluetooth_device_service_item,mBleGattServiceList);
        ListView listView = (ListView)findViewById(R.id.ble_service_list_view);
        listView.setAdapter(mBleServiceListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BleGattService bleGattService = mBleGattServiceList.get(position);
                actionStartBleCharacteristicActivity(bleGattService);
            }
        });
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        startBleService();
        getProgressDialog(mDeviceName).show();
    }
    private ProgressDialog getProgressDialog(String title){
        mProgressDialog = new ProgressDialog(BleServiceListActivity.this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(getResources().getString(R.string.connect_device));
        mProgressDialog.setCancelable(true);

        return mProgressDialog;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.title,menu);
        mMenuBleConnectState = menu.findItem(R.id.ble_connection_state);
        mMenuBleRssi = menu.findItem(R.id.ble_rssi);
        return true;
    }

    private void startBleService(){
        Intent intent = new Intent(this, BleService.class);

        bindService(intent,mBleServiceConnection,BIND_AUTO_CREATE);
    }

    private void showServiceList(){
        BleGattService bleGattService = new BleGattService();
        List<BluetoothGattService> bluetoothGattServices;
        mProgressDialog.dismiss();
        bluetoothGattServices = mBleConnectBinder.getSupportedGattServices();
        for(BluetoothGattService bluetoothGattService:bluetoothGattServices){
            bleGattService.setServiceName(null);
            bleGattService.setServiceUuid(bluetoothGattService.getUuid().toString());
            bleGattService.setServiceId("Instance id:"+bluetoothGattService.getInstanceId());
            bleGattService.setServiceType("Type:"+bluetoothGattService.getType());

            bleGattService.setBluetoothGattCharacteristics(bluetoothGattService.getCharacteristics());
            mBleGattServiceList.add(bleGattService);
        }
        mBleServiceListAdapter.notifyDataSetChanged();
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED_FAIL);
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED_FAIL);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED_FAIL);
        intentFilter.addAction(BleService.ACTION_GATT_UPDATE_RSSI);
        return intentFilter;
    }

    private void actionStartBleCharacteristicActivity(BleGattService bleGattService){
        Intent intent = new Intent(this,BleCharacteristicListActivity.class);
        List<BluetoothGattCharacteristic> bluetoothGattCharacteristics =
                bleGattService.getBluetoothGattCharacteristics();
        ArrayList<BleGattCharacteristic> bleGattCharacteristicArrayList=new ArrayList<>();
        BleGattCharacteristic bleGattCharacteristic = new BleGattCharacteristic();
        /*Parcelable[] bleCharacteristic=bluetoothGattCharacteristics.
                toArray(new Parcelable[bluetoothGattCharacteristics.size()]);
        for(BluetoothGattCharacteristic bluetoothGattCharacteristic:bluetoothGattCharacteristics){
            bluetoothGattCharacteristicArrayList.add(bluetoothGattCharacteristic);
        }*/
        if(bluetoothGattCharacteristics == null){
            return;
        }
        for(BluetoothGattCharacteristic bluetoothGattCharacteristic:bluetoothGattCharacteristics){
            bleGattCharacteristic.setCharacteristicName(null);
            bleGattCharacteristic.setCharacteristicUuid(
                    bluetoothGattCharacteristic.getUuid().toString());
            bleGattCharacteristic.setCharacteristicId("Instance id:"
                    +bluetoothGattCharacteristic.getInstanceId());
            bleGattCharacteristic.setCharacteristicProperty("Property:"
                    +bluetoothGattCharacteristic.getProperties());
            bleGattCharacteristic.setCharacteristicPermission("Permission:"
                    +bluetoothGattCharacteristic.getPermissions());
            bleGattCharacteristicArrayList.add(bleGattCharacteristic);
        }
        intent.putParcelableArrayListExtra(BleCharacteristicListActivity.EXTRAS_CHARACTERISTICS,
                bleGattCharacteristicArrayList);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        LogUtil.d(TAG,"onResume");
        super.onResume();
        registerReceiver(mGattUpdateBroadcastReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        LogUtil.d(TAG,"onPause");
        super.onPause();
        unregisterReceiver(mGattUpdateBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG,"onDestroy");
        super.onDestroy();
        unbindService(mBleServiceConnection);
    }
}
