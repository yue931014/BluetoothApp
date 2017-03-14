package com.example.yue_wu.bluetoothapp.bluetoothBle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;

import com.example.yue_wu.bluetoothapp.R;
import com.example.yue_wu.bluetoothapp.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by yue-wu on 2017/3/9.
 */

public class BleScanner {

    private static final String TAG = BleScanner.class.getSimpleName();

    private ArrayList<BleDevice> mDeviceList;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothLeScanner mBluetoothLeScanner;

    private Handler mHandler;

    private ScanCallback mV21ScanCallback;

    private BluetoothAdapter.LeScanCallback mV18ScanCallback;

    private BleScanDevicesAdpater mbleScanDevicesAdpater;

    private boolean mScanning=false;
    /**
     * Stops scanning after 10 seconds.
     */
    public static final long SCAN_PERIOD = 10000;

    public BleScanner(BluetoothAdapter btAdapter,BleScanDevicesAdpater bleScanDevicesAdpater,ArrayList<BleDevice> deviceList){
        mBluetoothAdapter = btAdapter;
        mDeviceList = deviceList;
        mbleScanDevicesAdpater = bleScanDevicesAdpater;
        mHandler = new Handler();
        if(Build.VERSION.SDK_INT >= 21){
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mV21ScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    LogUtil.d(TAG,"v21 onScanResult");

                    super.onScanResult(callbackType, result);
                    addBleDevice(getBleDviceInfo(result));
                    mbleScanDevicesAdpater.notifyDataSetChanged();
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    LogUtil.d(TAG,"v21 onBatchScanResults");
                    super.onBatchScanResults(results);
                    for(ScanResult result:results){
                        addBleDevice(getBleDviceInfo(result));
                    }
                    mbleScanDevicesAdpater.notifyDataSetChanged();
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    LogUtil.d(TAG,"v21 onScanFailed");
                }
            };

        }else{
            mV18ScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(android.bluetooth.BluetoothDevice device, int rssi, byte[] scanRecord) {
                    LogUtil.d(TAG,"v18 onLeScan");
                    addBleDevice(getBleDviceInfo(device,rssi,scanRecord));
                    mbleScanDevicesAdpater.notifyDataSetChanged();
                }
            };
        }

    }

    public void startScan(){
       if(!mScanning){
           LogUtil.d(TAG,"startScan");
           if (mBluetoothAdapter.isEnabled()) {
               if (Build.VERSION.SDK_INT >= 21) {
                   mBluetoothLeScanner.startScan(mV21ScanCallback);
               } else {
                   mBluetoothAdapter.startLeScan(mV18ScanCallback);
               }
               mScanning = true;
           }
           /*
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                }
            }, SCAN_PERIOD);
            */

        }
    }

    public void stopScan(){
        LogUtil.d(TAG,"stopScan");
        if (mBluetoothAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT >= 21) {
                mBluetoothLeScanner.stopScan(mV21ScanCallback);
            } else {
                mBluetoothAdapter.stopLeScan(mV18ScanCallback);
            }
            mScanning = false;
        }

    }

    /**
     * Takes in a number of nanoseconds and returns a human-readable string giving a vague
     * description of how long ago that was.
     */
    private  String getUpdateTime(long timeNanoseconds) {
        String lastSeenText = "Update Time:" ;

        long timeSince = SystemClock.elapsedRealtimeNanos() - timeNanoseconds;
        long secondsSince = TimeUnit.SECONDS.convert(timeSince, TimeUnit.NANOSECONDS);

        if (secondsSince < 5) {
            lastSeenText += "just now";
        } else if (secondsSince < 60) {
            lastSeenText += secondsSince + " seconds ago";
        } else {
            long minutesSince = TimeUnit.MINUTES.convert(secondsSince, TimeUnit.SECONDS);
            if (minutesSince < 60) {
                if (minutesSince == 1) {
                    lastSeenText += minutesSince + " minute ago";
                } else {
                    lastSeenText += minutesSince + " minutes ago";
                }
            } else {
                long hoursSince = TimeUnit.HOURS.convert(minutesSince, TimeUnit.MINUTES);
                if (hoursSince == 1) {
                    lastSeenText += hoursSince + " hour ago" ;
                } else {
                    lastSeenText += hoursSince + " hours ago";
                }
            }
        }

        return lastSeenText;
    }

    private int getPosition(String address) {
        int position = -1;
        for (int i = 0; i < mDeviceList.size(); i++) {
            if (mDeviceList.get(i).getDeviceAddr().equals(address)) {
                position = i;
                break;
            }
        }
        return position;
    }

    private BleDevice getBleDviceInfo(ScanResult scanResult){
        BleDevice bleDevice = new BleDevice();
        if(Build.VERSION.SDK_INT >= 21) {
            bleDevice.setDeviceName(scanResult.getDevice().getName());
            bleDevice.setDeviceAddr(scanResult.getDevice().getAddress());
            bleDevice.setDeviceRssi(Integer.toString(scanResult.getRssi())+"dbm");
            bleDevice.setDeviceAdvertise(bytesToHexStr(scanResult.getScanRecord().getBytes()));
        }
        return bleDevice;
    }

    private BleDevice getBleDviceInfo(BluetoothDevice btDevice, int rssi, byte[] scanRecord){
        BleDevice bleDevice = new BleDevice();
        if(Build.VERSION.SDK_INT < 21) {
            bleDevice.setDeviceName(btDevice.getName());
            bleDevice.setDeviceAddr(btDevice.getAddress());
            bleDevice.setDeviceRssi(Integer.toString(rssi)+ R.string.device_list);
            bleDevice.setDeviceAdvertise(bytesToHexStr(scanRecord));
        }
        return bleDevice;
    }

    private void addBleDevice(BleDevice bleDevice){
        int position = getPosition(bleDevice.getDeviceAddr());
        if(position >= 0){
            mDeviceList.set(position, bleDevice);
        }else{
            mDeviceList.add(bleDevice);
        }
    }

    /**
     * byte[] to hex string
     */
    private String bytesToHexStr(byte[] b)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }
}
