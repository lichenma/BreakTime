package com.example.timerapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.timerapplication.util.NotificationUtil
import com.example.timerapplication.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)
        PrefUtil.setTimerState(MainActivity.TimerState.Done, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}
