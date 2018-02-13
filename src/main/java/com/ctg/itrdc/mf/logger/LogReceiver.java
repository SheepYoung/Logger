package com.ctg.itrdc.mf.logger;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import static com.ctg.itrdc.mf.logger.XLog.XLOG_FLUSH_CACHE;

/**
 * Created by young on 2017/11/23.
 */

public class LogReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("XLog", "receive:" +  intent.getAction());
        if (XLOG_FLUSH_CACHE.equals(intent.getAction())){
            XLog.appenderFlush();
        }
    }

    public static Intent register(Context app){
        IntentFilter filter = new IntentFilter(XLOG_FLUSH_CACHE);
        Log.e("XLog", "register:" + XLOG_FLUSH_CACHE);

        return app.registerReceiver(new LogReceiver(), filter);
    }

}
