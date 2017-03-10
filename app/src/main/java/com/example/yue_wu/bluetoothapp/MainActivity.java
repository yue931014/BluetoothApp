package com.example.yue_wu.bluetoothapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.yue_wu.bluetoothapp.bluetoothBle.BleDevice;
import com.example.yue_wu.bluetoothapp.bluetoothBle.BleScanDevicesAdpater;
import com.example.yue_wu.bluetoothapp.bluetoothBle.BleScanner;
import com.example.yue_wu.bluetoothapp.util.LogUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;

    private SwipeRefreshLayout mScanRefresh;

    private BleScanner mBleScanner;

    private BleScanDevicesAdpater mBleScanDevicesAdpater;

    private ArrayList<BleDevice> mDeviceList = new ArrayList<BleDevice>();
    public static final int REQUEST_ENABLE_BT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG,"onCreate Task id is "+getTaskId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mBleScanDevicesAdpater = new BleScanDevicesAdpater(mDeviceList);
        recyclerView.setAdapter(mBleScanDevicesAdpater);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },1);
        }else {
            openBluetooth();
        }
    }

    private void openBluetooth(){
        Message message = new Message();

        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter();

        // Is Bluetooth supported on this device?
        if (mBluetoothAdapter != null) {

            // Is Bluetooth turned on?
            if (mBluetoothAdapter.isEnabled()) {
                startBleScanResults();
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

    private void startBleScanResults(){
        mBleScanner = new BleScanner(mBluetoothAdapter,mBleScanDevicesAdpater,mDeviceList);
        mScanRefresh.setRefreshing(true);
        refreshScanResults();
    }

    private void refreshScanResults(){
        LogUtil.d(TAG,"onRefresh start scan results");
        mBleScanner.startScan();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(BleScanner.SCAN_PERIOD);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.d(TAG,"onRefresh end scan results");
                        mBleScanner.stopScan();
                        //mBleScanDevicesAdpater.notifyDataSetChanged();
                        mScanRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:

                if (resultCode == RESULT_OK) {
                    startBleScanResults();
                } else {

                    // User declined to enable Bluetooth, exit the app.
                    Toast.makeText(this, "bluetooth not enable",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }

            default:
                super.onActivityResult(requestCode, resultCode, data);
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
                }
                break;
            default:
                break;
        }
    }
}
