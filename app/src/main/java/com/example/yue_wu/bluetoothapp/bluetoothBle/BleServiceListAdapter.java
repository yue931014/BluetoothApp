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

public class BleServiceListAdapter extends ArrayAdapter<BleGattService> {
    private int resourceId;
    public BleServiceListAdapter(Context context, int resource, List<BleGattService> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BleGattService bleGattService = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.serviceName = (TextView)view.findViewById(R.id.service_name);
            viewHolder.serviceUuid = (TextView)view.findViewById(R.id.service_uuid);
            viewHolder.serviceId = (TextView)view.findViewById(R.id.service_id);
            viewHolder.serviceType = (TextView)view.findViewById(R.id.service_type);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.serviceName.setText(bleGattService.getServiceName());
        viewHolder.serviceUuid.setText(bleGattService.getServiceUuid());
        viewHolder.serviceId.setText(bleGattService.getServiceId());
        viewHolder.serviceType.setText(bleGattService.getServiceType());
        return view;
    }
    class ViewHolder{
        TextView serviceName;
        TextView serviceUuid;
        TextView serviceId;
        TextView serviceType;
    }
}
