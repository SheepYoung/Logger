package com.ctg.itrdc.mf.logger;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by YoungSheep on 2017/3/22.
 */

public class LoggerConfig {



    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final String VERBOSE = "v";

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final String DEBUG = "d";

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final String INFO = "i";

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final String WARN = "w";

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final String ERROR = "e";

    /**
     * Priority constant for the println method.
     */
    public static final String WTF = "wtf";


    /**
     * globalLevel, 全局的level: v/d/i/w/e/wtf   (must)
     * modulesLevel, 指定类名 level: v/d/i/w/e/wtf   (opt)
     *
     */
    static String hardcodeString = "    {\n" +
            "        \"globalLevel\":\"i\",\n" +
            "            \"modulesLevel\":[\n" +
            "        {\n" +
            "            \"className1\":\"i\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"className2\":\"i\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"className3\":\"i\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"className4\":\"d\"\n" +
            "        }\n" +
            "    ]\n" +
            "    }";

    /**
     * 全局的logger level
     * @return
     */
    public static int getGlobalLevel() {
        return globalLevel;
    }

    static private int globalLevel = Logger.isDebug() ? Log.VERBOSE : Log.INFO;

    static class ModuleLogLevelInfo {
        String moduleName;
        int level;

        public ModuleLogLevelInfo(String moduleName, int level) {
            this.moduleName = moduleName;
            this.level = level;
        }
    }

    static HashMap<String, ModuleLogLevelInfo> moduleLogLevelMap = new HashMap<>();

    private synchronized static void putToConfig(String className, int level){
        moduleLogLevelMap.put(className, new ModuleLogLevelInfo(className, level));
    }

    synchronized static int getModuleLevel(String className){

        ModuleLogLevelInfo moduleLogleveInfo = moduleLogLevelMap.get(className);
        int level = globalLevel;
        if (moduleLogleveInfo !=null){ //精确匹配
            level = moduleLogleveInfo.level;
        }else{
            //模糊匹配
            String fineName = null;
            Set<String> keys = moduleLogLevelMap.keySet();
            Iterator<String> it = keys.iterator();

            while (it.hasNext()){
                String key = it.next();
                if(className.contains(key)){
                    fineName = key;
                    break;
                }
            }
            if (fineName != null){
                moduleLogleveInfo = moduleLogLevelMap.get(fineName);
                level = moduleLogleveInfo.level;
                putToConfig(className, level);
            }else{
                putToConfig(className, LoggerConfig.getGlobalLevel());
            }

        }

        return level;
    }

    synchronized static void initConfig(JSONObject json){
        JSONObject jsonObject = json;

        if(Logger.isDebug()){
            Logger.w("debug mode, do not set log level");
            return;
        }

        globalLevel = getLogLevel(jsonObject.optString("globalLevel"));
        XLog.setLevel(globalLevel);

        JSONArray moudlesLevel = jsonObject.optJSONArray("modulesLevel");
        moduleLogLevelMap.clear();
        if (moudlesLevel != null){
            for (int i = 0; i < moudlesLevel.length(); i++) {
                JSONObject moduleLevel = moudlesLevel.optJSONObject(i);
                if (moduleLevel == null) {
                    continue;
                }
                Iterator<String> it = moduleLevel.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    String levelString = moduleLevel.optString(key);
                    putToConfig(key, getLogLevel(levelString));
                }
            }
        }

    }


    public static int getLogLevel(String logLevelString) {
        int level = Log.VERBOSE;

        if (logLevelString.equals(VERBOSE)){
            level = Log.VERBOSE;
        }else if (logLevelString.equals(INFO)){
            level = Log.INFO;
        }else if (logLevelString.equals(DEBUG)){
            level = Log.DEBUG;
        }else if (logLevelString.equals(WARN)){
            level = Log.WARN;
        }else if (logLevelString.equals(ERROR)){
            level = Log.ERROR;
        }else if (logLevelString.equals(WTF)){
            level = Log.ASSERT;
        }
        return level;
    }


}
