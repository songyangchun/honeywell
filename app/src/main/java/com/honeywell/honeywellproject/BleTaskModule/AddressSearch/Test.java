package com.honeywell.honeywellproject.BleTaskModule.AddressSearch;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.honeywell.honeywellproject.BaseActivity.BaseApplication;
import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BleTaskModule.AddressSearch.Adapter.ItemAdapter;
import com.honeywell.honeywellproject.BleTaskModule.AddressSearch.SeRightTop.RightTopAdapter;
import com.honeywell.honeywellproject.BleTaskModule.AddressSearch.SeRightTop.RightTopBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.AddressDialogUtil;
import com.honeywell.honeywellproject.Util.AudioUtil;
import com.honeywell.honeywellproject.Util.BiduTTS.control.InitConfig;
import com.honeywell.honeywellproject.Util.BiduTTS.control.MySyntherizer;
import com.honeywell.honeywellproject.Util.BiduTTS.control.NonBlockSyntherizer;
import com.honeywell.honeywellproject.Util.BiduTTS.listener.MessageListener;
import com.honeywell.honeywellproject.Util.CommonBleUtil;
import com.honeywell.honeywellproject.Util.ConstantUtil;
import com.honeywell.honeywellproject.Util.DataHandler;
import com.honeywell.honeywellproject.Util.ELUtil;
import com.honeywell.honeywellproject.Util.LogUtil;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.Util.SystemUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;
import com.honeywell.honeywellproject.WidgeView.indicatordialog.IndicatorBuilder;
import com.honeywell.honeywellproject.WidgeView.indicatordialog.IndicatorDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import jxl.common.Logger;

import static com.honeywell.honeywellproject.Util.DataHandler.Alone2Hex;
import static com.honeywell.honeywellproject.Util.DataHandler.CheckVB;
import static java.lang.Integer.parseInt;


public class Test extends ToolBarActivity {


    private static Logger log = Logger.getLogger(Test.class); //日志打印
    private List<RightTopBean> rightTopList = new ArrayList<>();
    private ProgressDialog progressdialog;
    private IndicatorDialog dialog;
    private BleDevice bleDevice;
    private CommonBleUtil commonBleUtil;
    private boolean isAppExit;
    protected MySyntherizer synthesizer;
    private RecyclerView MyRecyclerView;
    private ItemAdapter mMyAdapter;
    private List<Integer> intInfos = new ArrayList<Integer>();
    private List<Integer> intInfos1 = new ArrayList<Integer>();
    String protopolType;
    private   Runnable runnable;
    private   Runnable runnable1;
    /**
     * 编址类型，CLIP 10X(101,102)\DLIP 20X(201,202)\FlashScan 30X
     * 且当ADDRESSTYPE==0 是代表没有任何读写操作，空闲状态
     */
    private static int ADDRESSTYPE = 0;
    /**
     * 写电池命令
     */
    private static final int LOOPCARDBATTERY = 401;
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode = TtsMode.MIX;
    private final static int messageOK = 1;
    private final static int messageError = 2;

    /**
     * CLIP协议轮询状态码
     */
    private static final int addressNoExist = 1001;
    private static final int addressExist = 1002;
    private static final int addressRepeat = 1003;
    private static final int over = 9999;

    private static final int repetscan= 1004;

    /*
     * DLIP协议轮询状态码
     * */
    private static final int dlip_over = 10001;
    private static final int dlipfinsh = 10002;


    /**
     * 重置
     */
    private static final int Reset = 119;

    /*
     * 轮询灯颜色显示
     */
    private static final int gray = 200;
    private static final int green = 201;
    private static final int yellow = 202;

    //地址

    public int addrstr = 0;
    public int onaddressnum = 0;//在线数量显示
    public int repeataddressnum = 0;//重码数量显示

    public int lampnum = 0;
    public int groupnum = 0;
    public int index = 0;
    //查询速度
    public int speed = 50;

    boolean isfirst = false;

    @BindView(R.id.iv_singleaddress_blestate2)
    ImageView ivSingleaddressBlestate2;
    @BindView(R.id.iv_singleaddress_blerssi2)
    ImageView ivSingleaddressBlerssi2;
    @BindView(R.id.iv_singleaddress_blebattery2)
    ImageView ivSingleaddressBlebattery2;
    //扫描按钮

    private Button button1 = null;
    private TextView onnum = null;
    private TextView renum = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolbarTitle().setText("地址搜索");
        initView();
        try {
            initialTts();
        } catch (Exception e) {
        }
        initBle();
    }

    @Override
    public int getContentViewId() {
        return R.layout.circle;
    }

    private void initView() {
        //init intInfos
        for (int i = 0; i < 240; i++) {
            intInfos.add(3);
        }
//        getBetry();
        initCommonBleUtil();
        getToolbarTitle().setText("地址搜索");
        getSubTitle().setVisibility(View.INVISIBLE);
        progressdialog = new ProgressDialog(this, R.style.progressDialog);  //转圈
        MyRecyclerView = (RecyclerView) findViewById(R.id.rv_test);
        MyRecyclerView.setNestedScrollingEnabled(false);  //禁止recyclerView嵌套滑动
        MyRecyclerView.setLayoutManager(new GridLayoutManager(this, 10));
        onnum = (TextView) findViewById(R.id.onnum);//在线设备
        renum = (TextView) findViewById(R.id.renum);//重码设备
        mMyAdapter = new ItemAdapter(intInfos);
        MyRecyclerView.setAdapter(mMyAdapter);

        mMyAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int newposition) {
                if (intInfos.get(newposition) == 1 && protopolType.equals("DLIP")) {
                    showTwoButton("注意：新地址必须为1-239内空地址位，否则修改失败", "原地址：             " + newposition, newposition);
                }
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////
        //点击开始扫描
        button1 = (Button) findViewById(R.id.btn_scan);
        button1.setTag(false);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (button1.getText().toString().equals("扫描")) {
                    intInfos();
                    reset();
                    //开始轮询
                    mMyAdapter.notifyDataSetChanged();
                    button1.setText("停止");//按钮上变为停止
                    button1.setTag(true);
                    protopolType = SharePreferenceUtil.getStringSP("ProtopolType", "CLIP");
//                    DlipcomdSend();
                    initWrite();
                    ////////////定时下滑效果
                } else {
                    button1.setText("扫描");//按钮上变为扫描
                    handler.removeMessages(addressExist);
                    handler.removeMessages(addressNoExist);
                    handler.removeMessages(addressRepeat);
                    handler.removeCallbacksAndMessages(null);
                    button1.setTag(false);
                    mMyAdapter.notifyDataSetChanged();
                    isfirst=true;
                }

            }

        });

    }

    public void showTwoButton(String tips, String position, final int newposition) {
        AddressDialogUtil.showTwoButtonDialog(this, tips, position, new AddressDialogUtil.OnTipsClick() {

            private void Writing(String newadd, int type, int index) {
                String command = null;
                //DLIP,第index条命令
                ADDRESSTYPE = type;
                if (index == 1) {
                    DataHandler.DLIP_ADDR_NEW = newadd;
                    command = DataHandler.DLIP_WRITE(String.valueOf(newposition), newadd, 1);
                    commonBleUtil.writeDevice(bleDevice, command);
                    ToastUtil.showToastShort("地址修改成功");

                }
            }


            @Override
            public void click(Dialog dialog, String newadd, String position) {

                if (TextUtils.isEmpty(newadd)) {
                    ToastUtil.showToastShort("新地址不能为空，请输入有效地址");
                    return;
                }
                if (Integer.parseInt(newadd) < 1 || Integer.parseInt(newadd) > 239) {
                    ToastUtil.showToastShort("不在1-239地址范围内，请重新输入");
                    return;
                }

                for (int i = 0; i < intInfos.size(); i++) {
                    if (intInfos.get(i) == 1 && i == Integer.parseInt(newadd)) {
                        ToastUtil.showToastShort("该地址已经存在，请重新输入");
                        return;
                    }
                }

                commonBleUtil.notifyDevice(bleDevice);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Integer.parseInt(newadd) >= 1 && Integer.parseInt(newadd) <= 239) {
                    //1-239 DLIP
                    ADDRESSTYPE = 201;
                    Writing(newadd, ADDRESSTYPE, 1);
                    dialog.dismiss();
                    mMyAdapter.notifyItemChanged(addrstr);
                }
            }

            @Override
            public void clickCancel(Dialog dialog) {
                dialog.dismiss();
            }
        });


    }


    //重置变量
    private void reset() {
        addrstr = 0;
        //address忘记清空了，导致每点一次扫描，在线地址都在累加
        address.clear();
        comdList.clear();
        groupnum = 0;
        lampnum = 0;
        onaddressnum = 0;
        repeataddressnum = 0;
        onnum.setText(0 + "");
        renum.setText(repeataddressnum + "");

    }

    //数组赋值----3代表蓝色
    private void intInfos() {
        //init intInfos
        for (int i = 0; i < 240; i++) {
            if (intInfos.get(i) != 3) {
                intInfos.set(i, 3);
            }
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_menu:
                TopRightClick();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_menu).setVisible(true);
        log.info("onPrepareOptionsMenu");
        return true;
    }

    private RightTopAdapter rightTopAdapter;

    private void TopRightClick() {
        rightTopList.clear();
        rightTopList.add(new RightTopBean(1));
        //rightTopList.add(new RightTopBean(2));
        rightTopList.add(new RightTopBean(2));
        if (rightTopAdapter == null) {
            rightTopAdapter = new RightTopAdapter(rightTopList, Test.this);
        }
        if (dialog == null) {
            dialog = new IndicatorBuilder(Test.this)  //must be activity
                    .width(PhoneUtil.getScreenWidth(Test.this) * 3 / 5)                           // the dialog width in px
                    .height(PhoneUtil.getScreenHeight(Test.this) / 3)                          // the dialog max height in px or -1 (means auto fit)
                    .ArrowDirection(IndicatorBuilder.TOP)
                    .bgColor(getResources().getColor(R.color.white))
                    .dimEnabled(true)
                    .gravity(IndicatorBuilder.GRAVITY_RIGHT)
                    .radius(10)
                    .ArrowRectage(0.9f)
                    .layoutManager(new LinearLayoutManager(Test.this, LinearLayoutManager.VERTICAL, false))
                    .adapter(rightTopAdapter).create();
        }

        dialog.setCanceledOnTouchOutside(true);

        dialog.show(getMenuView());
    }

    /**
     * 初始化蓝牙连接操作
     */
    private void initBle() {
        bleDevice = BaseApplication.bleDevice;
        if (!BleManager.getInstance().isBlueEnable()) {
            BleManager.getInstance().enableBluetooth();
        }
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        checkPermissions(permissions);
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 String[] permissions,
                                                 int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 12:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void checkPermissions(String[] permissions) {

        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, 12);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Scan();
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                OutPutCSV();
                break;
            default:
        }
    }

    /**
     * 扫描
     */
    private void Scan() {
        if (BleManager.getInstance().isConnected(bleDevice)) {
            ivSingleaddressBlestate2.setImageResource(R.drawable.connect_50);
            OpenNotify(true);
            BatteryThread();
//            getLoopCardBatteryThread();
            BleManager.getInstance().readRssi(bleDevice, new BleRssiCallback() {
                @Override
                public void onRssiFailure(BleException exception) {

                }

                @Override
                public void onRssiSuccess(int rssi) {
                    ivSingleaddressBlerssi2.setImageResource(R.drawable.xinhao_3);
                }
            });
            return;
        }
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                try {
                    if (progressdialog != null && !progressdialog.isShowing()) {
                        progressdialog.setMessage("蓝牙打开和设备扫描中...");
                        progressdialog.show();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onScanning(BleDevice result) {

            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                if (progressdialog != null) {
                    progressdialog.dismiss();
                }
                if (scanResultList == null || scanResultList.size() == 0) {
                    return;
                }
                for (BleDevice device : scanResultList) {
                    if (device.getName() == null) {
                        continue;
                    }
                    if (device.getName().equals(ConstantUtil.BLE_NAME)) {
                        bleDevice = BaseApplication.bleDevice = device;
                        break;
                    }
                }
                BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
                    @Override
                    public void onStartConnect() {
                        try {
                            if (progressdialog != null && !progressdialog.isShowing()) {
                                progressdialog.setMessage("编址器连接中...");
                                progressdialog.show();
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onConnectFail(BleException exception) {
                        try {
                            if (progressdialog != null && progressdialog.isShowing()) {
                                progressdialog.dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onConnectSuccess(final BleDevice bleDevice, BluetoothGatt gatt, int status) {
                        try {
                            if (progressdialog != null && progressdialog.isShowing()) {
                                progressdialog.dismiss();
                            }
                        } catch (Exception e) {
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                OpenNotify(true);
                                BatteryThread();
//                                getLoopCardBatteryThread();
                                ivSingleaddressBlestate2.setImageResource(R.drawable.connect_50);
                                BleManager.getInstance().readRssi(bleDevice, new BleRssiCallback() {
                                    @Override
                                    public void onRssiFailure(BleException exception) {
                                    }

                                    @Override
                                    public void onRssiSuccess(int rssi) {
                                        ivSingleaddressBlerssi2.setImageResource(R.drawable.xinhao_3);
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                        if (progressdialog != null) {
                            progressdialog.dismiss();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                BleManager.getInstance().cancelScan();
//                                ivSingleaddressBlerssi2.setImageResource(R.drawable.xinhao_wu);
                                ivSingleaddressBlestate2.setImageResource(R.drawable.unconnect_50);
//                                ivSingleaddressBlebattery2.setImageResource(R.drawable.battery_null);
                            }
                        });
                    }
                });
            }
        });
    }

    private void OpenNotify(boolean isDelay) {
        if (isDelay) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        commonBleUtil.notifyDevice(bleDevice);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /*
     * 给设备发送电池电量命令
     * */
    public void getBattery() {
//        SystemUtil.vibrate(Test.this, 100); //震动
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //获取回路卡电池电量
        commonBleUtil.writeDevice(bleDevice, LOOPCARDBattery_WRITE());
    }



    /*
     * 每隔两分钟发送一次
     * */
    private void BatteryThread(){
        runnable = new Runnable(){
            @Override
            public void run(){
                getBattery();
                //延迟 2 mins执行
                handler.postDelayed(this,  4*60*1000);
            }
//            }
        };
        handler.postDelayed(runnable,1000);
    }

    private void DlipcomdSend(){
        runnable1 = new Runnable(){
            @Override
            public void run(){
                commonBleUtil.writeDevice(bleDevice, groupQuery(groupnum));
                //延迟 2 mins执行
                handler.postDelayed(this,  500);
            }
//            }
        };
        handler.post(runnable1);
    }


    /**
     * 电池电量最大5.9V，显示的时候可以分成4个档位；
     * 0.00=<电压<5.0v，提醒没电，4格全空；
     * 5.0=<电压<5.4, 1格电；
     * 5.4=<电压<5.9, 2格电；
     * 5.9=<电压       3格电；
     *
     * @param batteryValue 为0.00是代表获取失败
     */
    private void setBatteryIcon(final double batteryValue) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (batteryValue < 5.0) {
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_null));
                } else if (5.0 <= batteryValue && batteryValue < 5.3) {
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_1));
                } else if (5.3 <= batteryValue && batteryValue < 5.6) {
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_2));
                }  else if (5.6 <= batteryValue && batteryValue < 5.9) {
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_3));
                } else if (5.9 <= batteryValue) {
                    ivSingleaddressBlebattery2.setImageDrawable(ResourceUtil.getDrawable(R.drawable.battery_4));
                }

            }
        });
    }

    public void initialTts() {
        LoggerProxy.printable(true); // 日志打印在logcat中
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        SpeechSynthesizerListener listener = new MessageListener();
        Map<String, String> params = AudioUtil.getParams(this);

        InitConfig initConfig = new InitConfig(ConstantUtil.baiduappId, ConstantUtil.baiduappKey,
                ConstantUtil.baidusecretKey, ttsMode, params, listener);
        synthesizer = new NonBlockSyntherizer(this, initConfig, null); // 此处可以改为MySyntherizer 了解调用过程
    }

    /**
     * 使用百度的SDK播放语音
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     *
     * @param isFlush 是否需要清空队列，直播放最新加入的声音
     */
    private void speak(String content, boolean isFlush) {
        if (synthesizer != null) {
            if (isFlush) {
                synthesizer.stop();
            }
            synthesizer.speak(content);
        }
    }

    @Override
    protected void onDestroy() {
        ADDRESSTYPE = 0;
        if (synthesizer != null) {
            synthesizer.release();
        }
        handler.removeCallbacksAndMessages(null);
        handler.removeCallbacks(runnable);
        handler.removeCallbacks(runnable1);
        isAppExit = true;
        if (progressdialog != null) {
            progressdialog.dismiss();
            progressdialog = null;
        }
        System.gc();
        super.onDestroy();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @OnClick({R.id.btn_scan, R.id.ll_singleaddress_top})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            /*case R.id.togglebutton:
                //编址
                initWrite();
                break;*/
            case R.id.ll_singleaddress_top:
                //失败重连
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    Scan();
                }
                break;
            default:
        }
    }




    /**
     * 发送的每一条命令都在这里实时监听
     */
    private  void initCommonBleUtil() {
        commonBleUtil = new CommonBleUtil();
        commonBleUtil.setResultListener(new CommonBleUtil.OnResultListtener() {

            @Override
            public void writeOnResult(boolean result) {

                LogUtil.e("Write--->" + result);
            }

            @Override
            public void notifyOnResult(boolean result) {
                LogUtil.e("Notify--->" + result);
            }

            @Override
            public void notifyOnSuccess(String values, String UUID) {
                String[] datas = new String[values.length() / 2];

                //数据长度不固定，注意处理0x55的情况
                for (int i = 0, j = 0; i < values.length(); i += 2, j++) {
                    datas[j] = values.substring(i, i + 2);
                    if (j != 0 && "00".equals(datas[j]) && "55".equals(datas[j - 1])) {
                        //出现0x55 0x00的情况则干掉00保留55,这样能保证数据长度不变，便于取值
                        j--;
                    }
                }
                //获取电量命令信息类型位，用信息类型判断发的是哪条命令
                String malfunction = datas[3];
                boolean result;
                int poll;
                if ("81".equals(malfunction)) {
                    //获取设备电量
//                    ToastUtil.showToastShort("接收电池电量:" + DataHandler.LOOPCARDBattery_READ(values));
                    setBatteryIcon(DataHandler.LOOPCARDBattery_READ(values));
                    if (ADDRESSTYPE==1000 && lampnum<200){
                        handler.sendEmptyMessageDelayed(repetscan, speed);
                    }
                }
               if (ADDRESSTYPE == 501) {
                    //写序列号成功
                    result = DataHandler.Series_READ(values);
                    if (result) {
                        handler.sendEmptyMessage(messageOK);
                    } else {
                        handler.sendEmptyMessage(messageError);
                    }
                } else if (ADDRESSTYPE == 1000 && "80".equals(malfunction)) {
                    //clip轮询读
                        speed = Integer.parseInt(SharePreferenceUtil.getStringSP("progressData", "50"));
                        poll = CLIP_READLOOP(values);
                        lampnum++;
                        if (poll == green) {
                            handler.sendEmptyMessageDelayed(addressExist, speed);
                        } else if (poll == gray) {
                            handler.sendEmptyMessageDelayed(addressNoExist, speed);
                        } else if (poll == yellow) {
                            handler.sendEmptyMessageDelayed(addressRepeat, speed);
                        }
                    if (lampnum == 200 ) {
                        handler.sendEmptyMessage(over);
                    }

                } else if (ADDRESSTYPE == 2000 && "85".equals(malfunction)) {
                    //dlip轮询读
                        DLIP_READGroupQuery(values);
                        groupnum++;
                    if (groupnum == 14) {
                        handler.sendEmptyMessageDelayed(dlip_over, 300);
                        handler.sendEmptyMessageDelayed(dlipfinsh,400);
                    }


                } else if (ADDRESSTYPE == 201) {
                    //dlip修改地址
                    DLIP_WRITE(values);
                }
//            }
            }
        });
    }


    /**
     * 开始编址1
     */
    private void initWrite() {
        try {
            if (protopolType.equals("CLIP")) {
                ADDRESSTYPE = 1000;
                Writing("CLIP", ADDRESSTYPE);
            } else if (protopolType.equals("DLIP")) {
                ADDRESSTYPE = 2000;
                DlipcomdSend();
//                Writing("DLIP", ADDRESSTYPE);
            }
        } catch (Exception e) {
            ToastUtil.showToastLong(e.getMessage());
        }
    }

    public void Writing(String protocolType, int type) {
        try {
            Thread.sleep(80);
            if (protopolType.equals("CLIP")) {
                commonBleUtil.writeDevice(bleDevice, CLIP_WRITE(lampnum));
            }
            if (protopolType.equals("DLIP")) {
                commonBleUtil.writeDevice(bleDevice, groupQuery(groupnum));

            }
        } catch (InterruptedException e) {
            ToastUtil.showToastLong(e.getMessage());
        }
    }

    /*
     * clip轮询读发送给设备的命令
     * */
    public String CLIP_WRITE(int addr) {
        String Y = null;
        String addr2 = null;
        String[] strs;
        //编址命令
        addr2 = Alone2Hex(Integer.toHexString(addr));
        strs = new String[]{"55", "AA", "01", "80", "FF", addr2, "05", "00", "55", "5A"};
        if (addr >= 0 && addr <= 99) {
            addr2 = Alone2Hex(Integer.toHexString(addr));
            Y = "00";
        } else if (addr >= 100 && addr <= 199) {
            addr2 = Alone2Hex(Integer.toHexString(addr % 100));
            Y = "01";
        } else {
            return null;
        }
        strs = new String[]{"55", "AA", "01", "80", Y, addr2, "05", "00", "55", "5A"};

        //检验位
        strs[7] = CheckVB(2, 6, strs);
        StringBuffer sb = new StringBuffer();
        for (int k = 0; k < strs.length; k++) {
            sb.append(strs[k]);
            //再判断里面是否含有0x55，若有需要后面补00
            if (k >= 2 && k <= 7) {
                if ("55".equals(strs[k])) {
                    sb.append("00");
                }
            }
        }
        return sb.toString();
    }

    /**
     * CLIP 连续编址的读设备返回的命令
     */
    public static int CLIP_READLOOP(String values) {
        String[] datas = new String[values.length() / 2];
        //数据长度不固定，注意处理0x55的情况
        for (int i = 0, j = 0; i < values.length(); i += 2, j++) {
            datas[j] = values.substring(i, i + 2);
            if (j != 0 && "00".equals(datas[j]) && "55".equals(datas[j - 1])) {
                //出现0x55 0x00的情况则干掉00保留55,这样能保证数据长度不变，便于取值
                j--;
            }
        }
        //标识位
        String massagetype = datas[3];
        //故障位
        String malfunction = datas[4];
        String pW1 = datas[5];
        //暂时修改一下，5位有一个不为00就行
        int H = Integer.parseInt(pW1, 16);
        if ((H * 256) > 100 && "00".equals(malfunction)&& "80".equals(massagetype) ) {
            return green;
        } else if ("00".equals(massagetype)) {
            return yellow;
        }
        return gray;
    }


    //存放命令的集合
    ArrayList<String> comdList = new ArrayList<String>();
    /**
     * 获取回路卡电池电压
     * */
    public String LOOPCARDBattery_WRITE(){
        String[] strs;
        strs=new String[]{"55","AA","01","81","33","01","B2","55","5A"};
        //检验位
        strs[6]=CheckVB(2,5,strs);
        StringBuffer sb = new StringBuffer();
        for(int k = 0; k < strs.length; k++){
            sb. append(strs[k]);
        }
        return sb.toString();
    }

    /**
     * dlip按组轮询读，给设备发送的命令
     *
     * @param groupnum int
     * @return Command  String
     */
    public String groupQuery(int groupnum) {
        String[] strs = new String[11];
        String addr3 = null;
        addr3 = Alone2Hex(Integer.toHexString(groupnum));
        strs = new String[]{"55", "AA", "01", "85", "FB", addr3, "F5", "F5", strs[8], "55", "5A"};
        strs[8] = CheckVB(2, 7, strs);
        StringBuffer sb1 = new StringBuffer();
        for (int k = 0; k < strs.length; k++) {
            sb1.append(strs[k]);
            //再判断里面是否含有0x55，若有需要后面补00
            if (k >= 2 && k <= 8) {
                if ("55".equals(strs[k])) {
                    sb1.append("00");
                }
            }
        }
        return sb1.toString();
    }

    static ArrayList<Integer> address = new ArrayList<Integer>(); // 放地址的list

    /**
     * compute read data from String to String[]
     *
     * @param values String
     * @return String[]
     * @version 2019-01-11
     */
    public String[] computeReadDatas(String values) {
        String[] datas = new String[values.length() / 2];
        //数据长度不固定，注意处理0x55的情况
        for (int i = 0, j = 0; i < values.length(); i += 2, j++) {
            datas[j] = values.substring(i, i + 2);
            if (j != 0 && "00".equals(datas[j]) && "55".equals(datas[j - 1])) {
                //出现0x55 0x00的情况则干掉00保留55,这样能保证数据长度不变，便于取值
                j--;
            }
        }
        return datas;
    }

    /**
     * process the first Read Data  into queue for writting group
     *
     * @param values String
     * @version 2019-01-11
     */
    /**
     * write from DLIP
     * dlip修改地址
     */

    public void DLIP_WRITE(String values) {

        String[] datas = computeReadDatas(values);
        if (datas.length < 10) {
            return;
        }
        String malfunction = datas[3];
        String DATAL = datas[8];
        String DATAH = datas[5];
        Integer b = Integer.parseInt(DATAH, 16);
        Integer c = Integer.parseInt(DATAL, 16);
        address.add(c);
        if (!b.equals("") && !c.equals("") && "85".equals(malfunction)) {
            intInfos.set(b, 0);
            mMyAdapter.notifyItemChanged(b);
            intInfos.set(c, 1);
            mMyAdapter.notifyItemChanged(c);
        }
        LogUtil.e(values);
    }

    /**
     * dlip读地址设备返回的命令
     *
     * @param values String
     * @return int
     * @version 2019-01-11
     * @author mayiengly
     */

    public void DLIP_READGroupQuery(String values) {
        String[] datas = computeReadDatas(values);
        LogUtil.e(values);
        //故障位
        if (datas.length < 10) {
            return;
        }
        String messagetype = datas[3];
        String DATAH = datas[7];
        String DATAL = datas[8];
        String BinaryH = ELUtil.hex2binary(DATAH);
        String BinaryL = ELUtil.hex2binary(DATAL);
        String BinaryALL = BinaryH + BinaryL;
        ArrayList<Integer> group1 = new ArrayList<Integer>(); // 放1的位置的list
        int result1 = 0;
        for (int i = BinaryALL.length() - 1; i >= 0; i--) {
            if (BinaryALL.charAt(i) == '1') {
                result1 = 15 - i;
                group1.add(result1);
            }
        }

        String hex = datas[6];
        Integer x = Integer.parseInt(hex, 16);
        for (int j = 0; j < group1.size(); j++) {
            addrstr = x * 16 + group1.get(j);
            address.add(addrstr);
        }
        for (int i = x * 16; i <  16 + x * 16; i++) {
            intInfos.set(i, 0);
            mMyAdapter.notifyItemChanged(i);
            if (address.size() == 0) {
                intInfos.set(i, 0);
                mMyAdapter.notifyItemChanged(i);
            }
            for (int z = 0; z < address.size(); z++) {
                if (i == address.get(z)) {
                    intInfos.set(i, 1);
                    mMyAdapter.notifyItemChanged(i);
                    //在线数量有问题,第二次点扫描会出现多个地址在线
                    onaddressnum++; //原因：存放地址的集合address忘记清空了
                    onnum.setText(onaddressnum + "");
//                    i++;
                }
            }
        }
        address.clear();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (false == (Boolean) button1.getTag()) {
                //点击扫描按钮之后才会执行下边的代码
                return;
            }
            if (groupnum == 15) {
                return;
            }
            if (addrstr == 240) {
                return;
            }
            switch (msg.what) {
//
                case dlipfinsh:
                    handler.removeCallbacks(runnable1);
                    break;
                case dlip_over:
                    Toast.makeText(Test.this, "扫描完成", Toast.LENGTH_LONG).show();
                    button1.setText("扫描");
                    break;

                case repetscan:
                    Writing("CLIP", ADDRESSTYPE);
                    break;
                case addressNoExist:

                    intInfos.set(addrstr, 0);
                    mMyAdapter.notifyItemChanged(addrstr);
                    addrstr++;
                    Writing("CLIP", ADDRESSTYPE);
                    break;
                case addressExist:
                    intInfos.set(addrstr, 1);
                    mMyAdapter.notifyItemChanged(addrstr);
                    addrstr++;
                    onaddressnum++;
                    onnum.setText(onaddressnum + "");
                    Writing("CLIP", ADDRESSTYPE);
                    break;
                case addressRepeat:
                    intInfos.set(addrstr, 2);
                    mMyAdapter.notifyItemChanged(addrstr);
                    addrstr++;
                    repeataddressnum++;
                    Writing("", ADDRESSTYPE);
                    break;
                case over:
                    Toast.makeText(Test.this, "扫描完成", Toast.LENGTH_LONG).show();
                    button1.setText("扫描");
                    break;
                default:
            }
            //显示重码数量
            renum.setText(repeataddressnum + "");
        }
    };
}