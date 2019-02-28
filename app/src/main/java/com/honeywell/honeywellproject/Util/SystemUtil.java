package com.honeywell.honeywellproject.Util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Vibrator;

import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by QHT on 2017-05-03.
 */
public class SystemUtil {

    /**
     * 获取当前应用程序的包名
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppPackageName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }
    public static void vibrate (Context context,long time){
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }
}
