package com.honeywell.honeywellproject.BleTaskModule.AddressSearch;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.honeywell.honeywellproject.Util.CommonBleUtil;
import com.honeywell.honeywellproject.Util.LogUtil;

import java.util.LinkedList;
import java.util.Queue;

/**
 * DLIP Write Thread
 */
public class DLIPWriteThread extends Thread {

    private Handler _dlipHandler;
    private CommonBleUtil _commonBleUtil;
    public Queue<BleData> getQueueBleData() {
        return _queueBleData;
    }

    private Queue<BleData> _queueBleData;//bleQueueDataList
    private int _msgToSend;

    public DLIPWriteThread(Handler dlipHandler, CommonBleUtil commonBleUtil, int msgToSend) {
        _dlipHandler = dlipHandler;
        _commonBleUtil = commonBleUtil;
        _queueBleData = new LinkedList<BleData>();
        _msgToSend = msgToSend;
    }

    public void run() {
        Looper.prepare();
        while (true) {
            try {
                Thread.sleep(500);
                synchronized (_queueBleData) {
                    if (_queueBleData.isEmpty()) continue;
                    BleData bdfirst = _queueBleData.element();
                    if (bdfirst.isReadFinish() == false) {
                        continue;
                    }
                    _queueBleData.remove();
                    if (_queueBleData.isEmpty()) continue;
                    String command = _queueBleData.element().getWriteData();
                    LogUtil.e(command);
                    Message msg = new Message();
                    msg.what = _msgToSend;
                    msg.obj = command;
                    _dlipHandler.sendMessage(msg);
                    //commonBleUtil.writeDevice(bleDevice, command);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
