package com.example.yue_wu.bluetoothapp.bluetoothBle;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yue-wu on 2017/3/15.
 */

public class BleGattService {
    private String serviceName;
    private String serviceUuid;
    private String serviceId;
    private String serviceType;
    private List<BluetoothGattCharacteristic> bluetoothGattCharacteristics;
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceUuid(String uuid) {
        this.serviceUuid = uuid;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setBluetoothGattCharacteristics(List<BluetoothGattCharacteristic> bluetoothGattCharacteristics) {
        this.bluetoothGattCharacteristics = bluetoothGattCharacteristics;
    }

    public List<BluetoothGattCharacteristic> getBluetoothGattCharacteristics() {
        return bluetoothGattCharacteristics;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceUuid() {
        return serviceUuid;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceType() {
        return serviceType;
    }

}
