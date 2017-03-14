package com.example.yue_wu.bluetoothapp.bluetoothBle;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;

import com.example.yue_wu.bluetoothapp.BleDeviceListActivity;
import com.example.yue_wu.bluetoothapp.R;
import com.example.yue_wu.bluetoothapp.util.ActivityCollector;
import com.example.yue_wu.bluetoothapp.util.LogUtil;

public class BluetoothStateChangeReceiver extends BroadcastReceiver {
    private static final String TAG = BluetoothStateChangeReceiver.class.getSimpleName();
    public BluetoothStateChangeReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        switch(intent.getAction()){
            case BluetoothAdapter.ACTION_STATE_CHANGED:{
                int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,0);
                if(BluetoothAdapter.STATE_TURNING_OFF == btState){
                    LogUtil.d(TAG,"bluetooth is turn off");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.bt_turn_off_title);
                    builder.setMessage(R.string.bt_turn_off_message);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.bt_turn_off_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCollector.finishAll();
                            Intent intent = new Intent(context, BleDeviceListActivity.class);
                            context.startActivity(intent);
                        }
                    });
                    builder.show();
                }
            }
                break;
            default:
                break;
        }
    }


}
