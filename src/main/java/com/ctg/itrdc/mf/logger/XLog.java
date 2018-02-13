package com.ctg.itrdc.mf.logger;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Process;

import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by YoungSheep on 2017/2/21.
 */

public class XLog {


    public static final String XLOG_FLUSH_CACHE = "com.ctg.itrdc.mf.logger.flush";

    private static boolean isInit = false;

    static {
        System.loadLibrary("stlport_shared");
        System.loadLibrary("marsxlog");
    }

    private static Xlog __singalton;


    /**
     * 初始化XLog
     * @param context
     */
    static void initLog(Context context, String prefix){
        File filePath = context.getExternalFilesDir(null);
        filePath = new File(filePath, "logs");
        if (!filePath.exists()){
            filePath.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
        String timeFormat = sdf.format(new Date(System.currentTimeMillis()));
        String processName = getAppName(Process.myPid(), context);
        processName = processName.replace(":", "-");
        String fileName = prefix +   Process.myPid() + "_" + processName + "_" + timeFormat;

        int logMode = Xlog.AppednerModeAsync;
        if(isApkDebugable(context)){
            logMode = Xlog.AppednerModeSync;
            Xlog.setConsoleLogOpen(true);
        }
        android.util.Log.i("XLog", "open log Xlog.LEVEL_VERBOSE:" + Xlog.LEVEL_VERBOSE+ " Xlog.AppednerModeAsync:" + logMode + " path: " + filePath.getAbsolutePath()
                + " name:" + fileName);
        Xlog.appenderOpen(Xlog.LEVEL_VERBOSE,  logMode, "", filePath.getAbsolutePath(), fileName);
        if (__singalton == null){
            __singalton = new Xlog();
        }
        Log.setLogImp(__singalton);

        isInit = true;

        initReceiver(context);
    }

    private static void initReceiver(Context context) {
        LogReceiver.register(context);
    }


    public static  boolean isInit(){
        return isInit;
    }

    public static void appenderFlush(){
        if (__singalton != null) {
            try{
                __singalton.appenderFlush(true);
            }catch (Throwable throwable){
                Logger.e(throwable, throwable.getMessage());
            }
        }
    }


    static void setLevel(int level){
        //Xlog 始终打开最大日志， 由我们自己控制
        //Log.setLevel(translateAndroidLevelToMar(level), true);
    }

    private static int translateAndroidLevelToMar(int level) {
        int res = Log.LEVEL_INFO;

        switch (level){
            case android.util.Log.VERBOSE:
                res = Log.LEVEL_VERBOSE;
                break;
            case android.util.Log.DEBUG:
                res = Log.LEVEL_DEBUG;
                break;
            case android.util.Log.INFO:
                res = Log.LEVEL_INFO;
                break;
            case android.util.Log.WARN:
                res = Log.LEVEL_WARNING;
                break;
            case android.util.Log.ERROR:
                res = Log.LEVEL_ERROR;
                break;
            case android.util.Log.ASSERT:
                res = Log.LEVEL_FATAL;
                break;

            default:
                res = Log.LEVEL_INFO;

        }

        return res;
    }

    /**
     * 判断是否是debug版本
     * @param context
     * @return
     */
    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info= context.getApplicationInfo();
            return (info.flags& ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        } catch (Exception e) {

        }
        return false;
    }


    public static String getAppName(int pID, Context appContext) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                     processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                android.util.Log.e("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

}
