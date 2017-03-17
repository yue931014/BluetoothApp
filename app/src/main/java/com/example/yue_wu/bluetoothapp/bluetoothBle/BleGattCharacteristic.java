package com.example.yue_wu.bluetoothapp.bluetoothBle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yue-wu on 2017/3/15.
 */

public class BleGattCharacteristic implements Parcelable{
    private String characteristicName;
    private String characteristicUuid;
    private String characteristicId;
    private String characteristicProperty;
    private String characteristicPermission;

    public void setCharacteristicName(String characteristicName) {
        this.characteristicName = characteristicName;
    }

    public void setCharacteristicUuid(String characteristicUuid) {
        this.characteristicUuid = characteristicUuid;
    }

    public void setCharacteristicId(String characteristicId) {
        this.characteristicId = characteristicId;
    }

    public void setCharacteristicProperty(String characteristicProperty) {
        this.characteristicProperty = characteristicProperty;
    }

    public void setCharacteristicPermission(String characteristicPermission) {
        this.characteristicPermission = characteristicPermission;
    }

    public String getCharacteristicName() {
        return characteristicName;
    }

    public String getCharacteristicUuid() {
        return characteristicUuid;
    }

    public String getCharacteristicId() {
        return characteristicId;
    }

    public String getCharacteristicProperty() {
        return characteristicProperty;
    }

    public String getCharacteristicPermission() {
        return characteristicPermission;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(characteristicName);
        dest.writeString(characteristicUuid);
        dest.writeString(characteristicId);
        dest.writeString(characteristicProperty);
        dest.writeString(characteristicPermission);
    }

    public static final Parcelable.Creator<BleGattCharacteristic> CREATOR =
            new Parcelable.Creator<BleGattCharacteristic>(){
                @Override
                public BleGattCharacteristic createFromParcel(Parcel source) {
                    BleGattCharacteristic bleGattCharacteristic = new BleGattCharacteristic();
                    bleGattCharacteristic.setCharacteristicName(source.readString());
                    bleGattCharacteristic.setCharacteristicUuid(source.readString());
                    bleGattCharacteristic.setCharacteristicId(source.readString());
                    bleGattCharacteristic.setCharacteristicProperty(source.readString());
                    bleGattCharacteristic.setCharacteristicPermission(source.readString());
                    return bleGattCharacteristic;
                }

                @Override
                public BleGattCharacteristic[] newArray(int size) {
                    return new BleGattCharacteristic[size];
                }
            };
}
