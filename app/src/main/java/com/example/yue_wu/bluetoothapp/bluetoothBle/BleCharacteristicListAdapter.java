package com.example.yue_wu.bluetoothapp.bluetoothBle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.yue_wu.bluetoothapp.R;

import java.util.List;

/**
 * Created by yue-wu on 2017/3/15.
 */

public class BleCharacteristicListAdapter  extends ArrayAdapter<BleGattCharacteristic> {
    private int resourceId;
    public BleCharacteristicListAdapter(Context context, int resource, List<BleGattCharacteristic> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BleGattCharacteristic bleGattCharacteristic = getItem(position);
        View view;
        BleCharacteristicListAdapter.ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new BleCharacteristicListAdapter.ViewHolder();
            viewHolder.characteristicName = (TextView)view.findViewById(R.id.characteristic_name);
            viewHolder.characteristicUuid = (TextView)view.findViewById(R.id.characteristic_uuid);
            viewHolder.characteristicId = (TextView)view.findViewById(R.id.characteristic_id);
            viewHolder.characteristicProperty = (TextView)view.findViewById(R.id.characteristic_property);
            viewHolder.characteristicPermission = (TextView)view.findViewById(R.id.characteristic_permission);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (BleCharacteristicListAdapter.ViewHolder)view.getTag();
        }
        viewHolder.characteristicName.setText(bleGattCharacteristic.getCharacteristicName());
        viewHolder.characteristicUuid.setText(bleGattCharacteristic.getCharacteristicUuid());
        viewHolder.characteristicId.setText(bleGattCharacteristic.getCharacteristicId());
        viewHolder.characteristicProperty.setText(bleGattCharacteristic.getCharacteristicProperty());
        viewHolder.characteristicPermission.setText(bleGattCharacteristic.getCharacteristicPermission());
        return view;
    }
    class ViewHolder{
        TextView characteristicName;
        TextView characteristicUuid;
        TextView characteristicId;
        TextView characteristicProperty;
        TextView characteristicPermission;
    }
}
