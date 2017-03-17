package com.example.yue_wu.bluetoothapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.yue_wu.bluetoothapp.bluetoothBle.BleDevice;
import com.example.yue_wu.bluetoothapp.bluetoothBle.BleScanDevicesAdpater;
import com.example.yue_wu.bluetoothapp.bluetoothBle.BleScanner;
import com.example.yue_wu.bluetoothapp.util.LogUtil;

import java.util.ArrayList;

public class BleDeviceListActivity extends BaseActivity {

    private static final String TAG = BleDeviceListActivity.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;

    private SwipeRefreshLayout mScanRefresh;

    private BleScanner mBleScanner;

    private BleScanDevicesAdpater mBleScanDevicesAdpater;

    private Handler mHandler;

    private Runnable mStopScanCallBack = new Runnable(){
        @Override
        public void run() {
            mBleScanner.stopScan();
            LogUtil.d(TAG,"onRefresh stop scan results");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mBleScanDevicesAdpater.notifyDataSetChanged();
                    mScanRefresh.setRefreshing(false);
                }
            });
        }
    };
    private ArrayList<BleDevice> mDeviceList = new ArrayList<BleDevice>();
    public static final int REQUEST_ENABLE_BT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG,"onCreate Task id is "+getTaskId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_device_list);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(R.string.device_list);
        }
        mScanRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        mScanRefresh.setColorSchemeResources(R.color.colorPrimary);
        mScanRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refreshScanResults();
            }
        });
        RecyclerView recyclerView;
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        mBleScanDevicesAdpater = new BleScanDevicesAdpater(mDeviceList);
        recyclerView.setAdapter(mBleScanDevicesAdpater);

        if(ContextCompat.checkSelfPermission(BleDeviceListActivity.this, Manifest.permission.
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(BleDeviceListActivity.this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },1);
        }else {
            openBluetooth();
        }
    }

    private void openBluetooth(){

        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter();

        // Is Bluetooth supported on this device?
        if (mBluetoothAdapter != null) {

            // Is Bluetooth turned on?
            if (mBluetoothAdapter.isEnabled()) {
                initBleScan();
            } else {
                // Prompt user to turn on Bluetooth (logic continues in onActivityResult()).
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            Toast.makeText(this,"bluetooth not support",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initBleScan(){
        mBleScanner = new BleScanner(mBluetoothAdapter,mBleScanDevicesAdpater,mDeviceList);
        mHandler = new Handler();
    }

    private void refreshScanResults(){
        LogUtil.d(TAG,"onRefresh start scan results");
        mDeviceList.clear();
        mBleScanner.startScan();
        mHandler.postDelayed(mStopScanCallBack, BleScanner.SCAN_PERIOD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    LogUtil.d(TAG,"bt enable");
                    initBleScan();
                } else {
                    // User declined to enable Bluetooth, exit the app.
                    Toast.makeText(this, "bluetooth not enable",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openBluetooth();
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }
    @Override
    protected void onResume() {
        LogUtil.d(TAG,"onResume");
        super.onResume();
        if(mBleScanner != null) {
            mScanRefresh.setRefreshing(true);
            refreshScanResults();
        }
    }

    @Override
    protected void onPause() {
        LogUtil.d(TAG,"onPause");
        super.onPause();
        if(mBleScanner != null) {
            mBleScanner.stopScan();
            mHandler.removeCallbacks(mStopScanCallBack);
        }
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG,"onDestroy");
        super.onDestroy();
    }
}
