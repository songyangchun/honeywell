package com.honeywell.honeywellproject.BleTaskModule.AddressSearch;

public class BleData {
    private String writeData="";//write Data
    private String readData="";// the ble device return  data after write finished
    private boolean isReadFinish=false;//is the ble device return data finished;

    public String getWriteData() {
        return writeData;
    }

    public void setWriteData(String writeData) {
        this.writeData = writeData;
    }

    public String getReadData() {
        return readData;
    }

    public void setReadData(String readData) {
        this.readData = readData;
    }

    public boolean isReadFinish() {
        return isReadFinish;
    }

    public void setReadFinish(boolean readFinish) {
        isReadFinish = readFinish;
    }

}
