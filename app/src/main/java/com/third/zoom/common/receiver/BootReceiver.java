package com.third.zoom.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 作者：Sky on 2018/3/5.
 * 用途：开机广播
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ZM","收到开机广播");
//        Intent _intent = new Intent(context, com.third.zoom.ytbus.activity.MainActivity.class);
//        _intent.setAction(Intent.ACTION_MAIN);
//        _intent.addCategory(Intent.CATEGORY_DEFAULT);
//        _intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(_intent);
//        PendingIntent mRestartIntent = PendingIntent.getActivity(
//                context.getApplicationContext(), 0, _intent,
//                Intent.FLAG_ACTIVITY_NEW_TASK);
//        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis(), mRestartIntent);
    }
}
