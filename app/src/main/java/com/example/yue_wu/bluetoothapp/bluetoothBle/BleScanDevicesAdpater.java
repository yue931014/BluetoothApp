package com.example.yue_wu.bluetoothapp.bluetoothBle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.yue_wu.bluetoothapp.R;

import java.util.ArrayList;


/**
 * Created by yue-wu on 2017/3/9.
 */

public class BleScanDevicesAdpater  extends RecyclerView.Adapter<BleScanDevicesAdpater.ViewHolder>{

    private ArrayList<BleDevice> mDeviceList;

    public BleScanDevicesAdpater(ArrayList<BleDevice> deviceList){
        mDeviceList = deviceList;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView deviceName;
        private TextView deviceAddr;
        private TextView deviceRssi;
        private TextView deviceAdvertise;
        public ViewHolder(View view){
            super(view);
            deviceName = (TextView)view.findViewById(R.id.device_name);
            deviceAddr = (TextView)view.findViewById(R.id.device_addr);
            deviceRssi = (TextView)view.findViewById(R.id.device_rssi);
            deviceAdvertise = (TextView)view.findViewById(R.id.device_advertise);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bluetooth_device_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BleDevice device = mDeviceList.get(position);

        holder.deviceName.setText(device.getDeviceName());
        holder.deviceAddr.setText(device.getDeviceAddr());
        holder.deviceRssi.setText(device.getDeviceRssi());
        holder.deviceAdvertise.setText(device.getDeviceAdvertise());
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }
}
