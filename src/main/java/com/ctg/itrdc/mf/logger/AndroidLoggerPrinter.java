package com.ctg.itrdc.mf.logger;

import android.util.Log;

/**
 * Logger is a wrapper of {@link android.util.Log}
 * But more pretty, simple and powerful
 *
 * @author Orhan Obut
 */
final class AndroidLoggerPrinter extends AbsLogger {
    @Override
    protected void logChunk(int logType, String tag, String chunk) {
        String finalTag = tag;
        switch (logType) {
            case android.util.Log.ERROR:
                Log.e(finalTag, chunk);
                break;
            case android.util.Log.INFO:
                Log.i(finalTag, chunk);
                break;
            case android.util.Log.VERBOSE:
                Log.v(finalTag, chunk);
                break;
            case android.util.Log.WARN:
                Log.w(finalTag, chunk);
                break;
            case android.util.Log.ASSERT:
                Log.e(finalTag, chunk);
                break;
            default:
                Log.d(finalTag, chunk);
                break;
        }
    }
}
