package com.ctg.itrdc.mf.logger;

import android.util.Log;

/**
 * @author Orhan Obut
 */
public final class Settings {

    private int methodCount = 2;
    private boolean showThreadInfo = true;

    /**
     * Determines how logs will printed
     */
    private int logLevel = Log.VERBOSE;

    public Settings hideThreadInfo() {
        showThreadInfo = false;
        return this;
    }

    public Settings setMethodCount(int methodCount) {
        this.methodCount = methodCount;
        return this;
    }

    public Settings setLogLevel(int logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public int getMethodCount() {
        return methodCount;
    }

    public boolean isShowThreadInfo() {
        return showThreadInfo;
    }

    public int getLogLevel() {
        return logLevel;
    }
}
