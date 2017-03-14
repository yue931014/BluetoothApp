package com.example.yue_wu.bluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.yue_wu.bluetoothapp.bluetoothBle.BluetoothStateChangeReceiver;
import com.example.yue_wu.bluetoothapp.util.ActivityCollector;

public class BaseActivity extends AppCompatActivity {
    private BluetoothStateChangeReceiver bluetoothStateChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBtSwitchStateReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(bluetoothStateChangeReceiver != null){
            unregisterReceiver(bluetoothStateChangeReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void registerBtSwitchStateReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        bluetoothStateChangeReceiver = new BluetoothStateChangeReceiver();
        registerReceiver(bluetoothStateChangeReceiver,intentFilter);
    }
}
