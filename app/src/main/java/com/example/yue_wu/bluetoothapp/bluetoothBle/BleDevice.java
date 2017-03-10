package com.example.yue_wu.bluetoothapp.bluetoothBle;

/**
 * Created by yue-wu on 2017/3/9.
 */

public class BleDevice {
    private String deviceName;
    private String deviceAddr;
    private String deviceRssi;
    private String deviceUpdateTime;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddr() {
        return deviceAddr;
    }

    public void setDeviceAddr(String deviceAddr) {
        this.deviceAddr = deviceAddr;
    }

    public String getDeviceRssi() {
        return deviceRssi;
    }

    public void setDeviceRssi(String deviceRssi) {
        this.deviceRssi = deviceRssi;
    }

    public String getDeviceUpdateTime() {
        return deviceUpdateTime;
    }

    public void setDeviceUpdateTime(String deviceUpdateTime) {
        this.deviceUpdateTime = deviceUpdateTime;
    }
}
