package com.ctg.itrdc.mf.logger;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Logger is a wrapper of {@link com.tencent.mars.xlog.Log}

 * @author Young
 */
public final class Logger {

    //log 调用到方法的深度
    private final static int TRACE_OFFSET = 4;


    //no instance
    private Logger() {
    }



    /**
     * 初始化logger
     * @param context
     */
    public static void  init(Context context, String prefix) {
        XLog.initLog(context, prefix);
        isDebugApk = XLog.isApkDebugable(context);

    }

    private static boolean isDebugApk = false;



    /**
     * 设置log级别
     * @param jsonConfig
     * @see {@link LoggerConfig#hardcodeString}
     */
    public static void setLevel(String jsonConfig){
        if (!TextUtils.isEmpty(jsonConfig)){
            try {
                LoggerConfig.initConfig(new JSONObject(jsonConfig));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    /********************************************************************************************************/

    public static void d( String message, Object... args) {
        getLogger().d(message, args);
    }

    private static Printer getLogger() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        String className = "";
        if (trace.length > TRACE_OFFSET){
            StackTraceElement element = trace[TRACE_OFFSET];
            className = element.getClassName();
        }
        return LoggerHelper.getSingleton().getLogger(className);
    }

    public static void e(String message, Object... args) {
        getLogger().e(message, args);
    }

    public static void e( Throwable throwable, String message, Object... args) {
        getLogger().e(throwable, message, args);
    }

    public static void i( String message, Object... args) {
        getLogger().i(message, args);
    }

    public static void v( String message, Object... args) {
        getLogger().v(message, args);
    }

    public static void w( String message, Object... args) {
        getLogger().w(message, args);
    }

    public static void w( Throwable throwable, String message, Object... args) {
        getLogger().w(throwable, message, args);
    }

    public static void wtf( String message, Object... args) {
        getLogger().wtf(message, args);
    }



    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public static void json(String json) {
        getLogger().json(json);
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public static void xml(String xml) {
        getLogger().xml(xml);
    }

    final static int GET_LOGGER_OFFSET = TRACE_OFFSET - 1;

    /**
     * get a pretty logger
     * @return
     */
    public static Printer getPrettyLogger(){
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        String className = "";
        if (trace.length > GET_LOGGER_OFFSET){
            StackTraceElement element = trace[GET_LOGGER_OFFSET];
            className = element.getClassName();
        }
        return LoggerHelper.getSingleton().getPrettyLogger(className);
    }

    public static Printer getSampleLogger(){
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        String className = "";
        if (trace.length > GET_LOGGER_OFFSET){
            StackTraceElement element = trace[GET_LOGGER_OFFSET];
            className = element.getClassName();
        }
        return LoggerHelper.getSingleton().getSampleLogger(className);
    }

    public static int getGlobleLevel() {
        return LoggerConfig.getGlobalLevel();
    }

    public static boolean isDebug() {
        return isDebugApk;
    }
}
