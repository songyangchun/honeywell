package com.honeywell.honeywellproject.ELModule.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.clj.fastble.BleManager;
import com.honeywell.honeywellproject.BaseActivity.BaseApplication;
import com.honeywell.honeywellproject.BaseActivity.BaseFragment;
import com.honeywell.honeywellproject.ELModule.ELActivityNewUI;
import com.honeywell.honeywellproject.ELModule.data.LightEvent;
import com.honeywell.honeywellproject.ELModule.data.bigLightState;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.ELUtil;
import com.honeywell.honeywellproject.Util.EventBusUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;
import com.honeywell.honeywellproject.WidgeView.unInterceptLinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Fragment3 extends BaseFragment {

    public List<bigLightState> bigLightStateList = new ArrayList<bigLightState>();
    HashMap<Integer, Boolean> ImageMap = new HashMap<>();
    @BindView(R.id.leftrun_dark3)
    ImageView               leftrunDark3;
    @BindView(R.id.voice_dark)
    ImageView               voiceDark;
    @BindView(R.id.ll_light_1)
    unInterceptLinearLayout llLight1;
    Unbinder unbinder;
    private com.honeywell.honeywellproject.Util.Loading_view loading;



    @Override
    public int getContentViewId() {
        return R.layout.fragment_3;
    }

    @Override
    public void initView(View view) {

        if (!ImageMap.isEmpty()) {
            return;
        }
        ImageMap.put(R.id.voice_dark, false);
        ImageMap.put(R.id.leftrun_dark3, false);
        bigLightStateList.clear();
        bigLightStateList.add(new bigLightState(true));
        llLight1.setIntercept(false);
        llLight1.setBackgroundColor(ResourceUtil.getColor(R.color.blue_shadow));
    }

    int clickCountCenter = 0;
    int clickCountLeft = 0;

    public static class Utils {
        // 两次点击按钮之间的点击间隔不能少于2000毫秒
        private static  long lastClickTime;
        public static boolean isFastClick(long ClickIntervalTime) {
            long ClickingTime = System.currentTimeMillis();
            if ( ClickingTime - lastClickTime < ClickIntervalTime) {
                return true;
            }
            lastClickTime = ClickingTime;
            return false;
        }

  }

 public void loading(View v){//点击加载并按钮模仿网络请求
        loading = new com.honeywell.honeywellproject.Util.Loading_view(getActivity(), R.style.CustomDialog);
        loading.show();
        new Handler().postDelayed(new Runnable() {//定义延时任务模仿网络请求
            @Override
            public void run() {
                loading.dismiss();//4秒后调用关闭加载的方法
            }
        }, 4000);
    }

    /**
     * @param view
     */
    @OnClick({R.id.voice_dark, R.id.leftrun_dark3})
    public void onViewClicked(View view) {
        if(!((ELActivityNewUI)getActivity()).inIR){
            //如果没有内置红外
            if(!BleManager.getInstance().isConnected(BaseApplication.elDevice)){
                //且没有连接红外设备
                ToastUtil.showToastShort("红外设备未连接");
                return;
            }
        }
        switch (view.getId()) {
            case R.id.voice_dark:
                if ( Utils.isFastClick(1000)){
                    return;
                }
                clickCountLeft++;
                    if (clickCountLeft % 3 == 1) {
                        loading(view);
//                     Toast toast=Toast.makeText(getActivity(),"语音播放中，请稍后...", Toast.LENGTH_LONG);
//                      toast.setGravity(Gravity.CENTER, 0, 0);  //将提示框居中显示
//                       showMyToast(toast, 4000);

                        EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "04"), "04FB04FB"));
                        openImage(ImageMap, voiceDark, R.id.voice_dark, R.drawable.voice_light_noenter);
                    }

                    else if (clickCountLeft % 3 == 2) {
                        loading(view);
//                        Toast toast=Toast.makeText(getActivity(),"语音播放中，请稍后...", Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.CENTER, 0, 0);  //将提示框居中显示
//                        showMyToast(toast, 4000);
                        EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "08"), "04FB08F7"));
                        openImage(ImageMap, voiceDark, R.id.voice_dark, R.drawable.voice_light_exit);

                    }

                    else if (clickCountLeft % 3 == 0) {
                    //关闭
                      //  ToastUtil.showToastShort("语音已关闭");
                    EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "0C"), "04FB0CF3"));
                    closeImage(ImageMap, voiceDark, R.id.voice_dark, R.drawable.voice_dark);
                }
                break;
            case R.id.leftrun_dark3:
                if (Utils.isFastClick(500)){
                    return;
                }
                    clickCountCenter++;
                    if (clickCountCenter % 3 == 1) {
                        //常亮
                        EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "05"), "04FB05FA"));
                        openImage(ImageMap, leftrunDark3, R.id.leftrun_dark3, R.drawable.leftrun_light);
                    } else if (clickCountCenter % 3 == 2) {
                        //闪烁
                        EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "01"), "04FB01FE"));
                        doCenterGlintRunable();
                    } else if (clickCountCenter % 3 == 0) {
                        //关闭
                        EventBusUtil.postSync(new LightEvent(ELUtil.getParam("04", "09"), "04FB09F6"));
                        closeImage(ImageMap, leftrunDark3, R.id.leftrun_dark3, R.drawable.leftrun_dark);
                        stopCenterGlintRunable();
                    }
                    break;

                    default:
        }
    }
    private    Handler handler           = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    /**
     * 中灯闪烁的定时任务
     */
    private int     centerGlientCount = 0;
    private Runnable centerGlientRunable;
    private void doCenterGlintRunable() {
        centerGlientRunable = new Runnable() {
            @Override
            public void run() {
                if (centerGlientCount % 2 == 0) {
                    leftrunDark3.setImageDrawable(ResourceUtil.getDrawable(R.drawable.leftrun_light));
                } else {
                    leftrunDark3.setImageDrawable(ResourceUtil.getDrawable(R.drawable.leftrun_dark));
                }
                handler.postDelayed(this, 400);
                centerGlientCount++;
            }
        };
        handler.post(centerGlientRunable);
    }
    /**
     * 停止中灯闪烁
     */
    private void stopCenterGlintRunable() {
        centerGlientCount = 0;
        handler.removeCallbacks(centerGlientRunable);
    }
    public void closeAllImage() {
        closeImage(ImageMap, voiceDark, R.id.voice_dark, R.drawable.voice_dark);
        closeImage(ImageMap, leftrunDark3, R.id.leftrun_dark3, R.drawable.leftrun_dark);
        clickCountCenter = 0;
        clickCountLeft = 0;

        stopCenterGlintRunable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopCenterGlintRunable();
        ImageMap.clear();
        unbinder.unbind();
    }
}
