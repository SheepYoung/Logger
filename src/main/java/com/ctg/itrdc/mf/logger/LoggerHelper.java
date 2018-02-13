package com.ctg.itrdc.mf.logger;

import android.util.Log;

/**
 * Created by YoungSheep on 2017/3/23.
 */

public class LoggerHelper {


    final private static LoggerHelper mSingleton = new LoggerHelper();

    static LoggerHelper getSingleton(){
        return mSingleton;
    }


    private LoggerHelper(){

    }

    private static final Printer prettyPrinter = new LoggerPrinter();
    private static final Printer samplePrinter = new SampleLoggerPrinter();
    private static final Printer androidPrinter = new AndroidLoggerPrinter();


    Printer getLogger(String clazzName){
        Printer printer = androidPrinter;
        if (XLog.isInit()){
            printer = samplePrinter;
        }



        int loglevel = LoggerConfig.getModuleLevel(clazzName);
        if (Logger.isDebug()){
            loglevel = Log.VERBOSE;
        }
        if (loglevel> Log.INFO){
            printer = prettyPrinter;
        }
        return printer.setLevel(loglevel).setTag(clazzName);
    }



    Printer getPrettyLogger(String clazzName){
        int loglevel = LoggerConfig.getModuleLevel(clazzName);
        return prettyPrinter.setLevel(loglevel)
                .setTag(clazzName);
    }

    Printer getSampleLogger(String clazzName){
        int loglevel = LoggerConfig.getModuleLevel(clazzName);
        return samplePrinter.setLevel(loglevel)
                .setTag(clazzName);
    }

}
