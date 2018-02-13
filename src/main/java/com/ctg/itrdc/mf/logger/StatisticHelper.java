package com.ctg.itrdc.mf.logger;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

/**
 * Created by young on 2017/12/21.
 */

public class StatisticHelper {

    private static boolean isMainProgress = false;
    private static Context applicationContext = null;
    public static void init(Context appContext, final Statistic statistic) {
        isMainProgress = isMainProcess(appContext);
        applicationContext = appContext;
        if(!isMainProgress){
            return;
        }
        Logger.i("register receiver...");
        StatisticHelper.statistic = statistic;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Statistic.STATISTIC_COMPLETE);
        filter.addAction(Statistic.STATISTIC_ERROR);
        filter.addAction(Statistic.STATISTIC_START);

        appContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                String key = intent.getStringExtra(Statistic.STATISTIC_KEY);
                switch (action){
                    case Statistic.STATISTIC_START:{
                        if(statistic != null){
                            statistic.statStart(key, intent.getStringExtra(Statistic.STATISTIC_VALUE));
                        }
                        break;
                    }
                    case Statistic.STATISTIC_COMPLETE:{
                        if(statistic != null){
                            statistic.statComplete(key, intent.getStringExtra(Statistic.STATISTIC_VALUE));
                        }
                        break;
                    }
                    case Statistic.STATISTIC_ERROR:{
                        if(statistic != null){
                            statistic.statError(key, intent.getStringExtra(Statistic.STATISTIC_BUSINESS_ID));
                        }
                        break;
                    }
                }
            }
        }, filter);

    }


    private static String getAppName(int pID, Context appContext) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        if (l == null) {
            return null;
        }
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    if (c != null){
                        Log.i(StatisticHelper.class.getSimpleName(), c.toString());
                    }
                    processName = info.processName;
                    if (processName != null){
                        Log.i(StatisticHelper.class.getSimpleName(), processName.toString());
                    }
                    return processName;
                }
            } catch (Exception e) {
                Log.e("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }


    private static boolean isMainProcess(Context AppContext){
        int pid = android.os.Process.myPid();
        String processAppName = StatisticHelper.getAppName(pid, AppContext);
        Log.i("", "processAppName:" + processAppName);
        if (processAppName == null || processAppName.equals("")) {
            return false;
        }else{
            return true;
        }
    }

    private static Statistic statistic = null;


    /**
     * 开始统计
     * @param key 业务唯一的key
     * @param requestBusinessId 请求业务的唯一id
     * @return 记录成功返回true else false
     */
    public static boolean statStart(String key, String requestBusinessId){
        boolean res = false;
        if (isMainProgress){
            if (statistic == null){
                throw  new NullPointerException("you must call init  firstly!");
            }
            res = statistic.statStart(key, requestBusinessId);
        }else{
            if (applicationContext != null){
                Intent intent = new Intent(Statistic.STATISTIC_START);
                intent.putExtra(Statistic.STATISTIC_KEY, key);
                intent.putExtra(Statistic.STATISTIC_VALUE, requestBusinessId);
                applicationContext.sendBroadcast(intent);
            }
        }
        return res;
    }


    /**
     * 统计失败
     * @param key 业务的key
     * @param requestBusinessId 请求业务的唯一id
     * @return
     */
    public static boolean statError(String key, String requestBusinessId){
        boolean res = false;
        if (isMainProgress){
            if (statistic == null){
                throw  new NullPointerException("you must call init  firstly!");
            }
            res = statistic.statError(key, requestBusinessId);
        }else{
            if (applicationContext != null){
                Intent intent = new Intent(Statistic.STATISTIC_ERROR);
                intent.putExtra(Statistic.STATISTIC_KEY, key);
                intent.putExtra(Statistic.STATISTIC_BUSINESS_ID, requestBusinessId);
                applicationContext.sendBroadcast(intent);
                res = true;
            }
        }
        return res;
    }

    /**
     * 介绍统计
     * @param key 业务唯一的key
     * @param requestBusinessId 请求业务的唯一id
     * @return 记录成功返回true else false
     */
    public static boolean statComplete(String key, String requestBusinessId){
        boolean res = false;
        if (isMainProgress){
            if (statistic == null){
                throw  new NullPointerException("you must call init  firstly!");
            }
            res = statistic.statComplete(key, requestBusinessId);
        }else{
            if (applicationContext != null){
                Intent intent = new Intent(Statistic.STATISTIC_COMPLETE);
                intent.putExtra(Statistic.STATISTIC_KEY, key);
                intent.putExtra(Statistic.STATISTIC_VALUE, requestBusinessId);
                applicationContext.sendBroadcast(intent);
                res = true;
            }
        }
        return res;
    }



}
