package com.example.yue_wu.bluetoothapp;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.support.v7.app.ActionBar;

import android.os.Bundle;
import android.widget.ListView;

import com.example.yue_wu.bluetoothapp.bluetoothBle.BleCharacteristicListAdapter;
import com.example.yue_wu.bluetoothapp.bluetoothBle.BleGattCharacteristic;

import java.util.ArrayList;

public class BleCharacteristicListActivity extends BaseActivity {
    private ArrayList<BleGattCharacteristic> mBleGattCharacteristicList = null;
    private BleCharacteristicListAdapter mBleCharacteristicListAdapter;
    public static final String EXTRAS_CHARACTERISTICS = "CHARACTERISTICS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_characteristic_list);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(R.string.characteristic_list);
        }
        //actionBar.setDisplayHomeAsUpEnabled(true);
        initBleCharacteristicList();
        mBleCharacteristicListAdapter = new BleCharacteristicListAdapter(BleCharacteristicListActivity.this,
                R.layout.bluetooth_device_characteristic_item,mBleGattCharacteristicList);
        ListView listView = (ListView)findViewById(R.id.ble_characteristic_list_view);
        listView.setAdapter(mBleCharacteristicListAdapter);
    }
    private void initBleCharacteristicList(){
        final Intent intent = getIntent();
        if(intent != null){
            mBleGattCharacteristicList = intent.getParcelableArrayListExtra(EXTRAS_CHARACTERISTICS);
        }
    }

}
