package com.example.timerapplication.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.timerapplication.MainActivity
import java.util.*

class PrefUtil {
    // member defined in the companion object are like static objects
    companion object {

        fun getTimerLength(context: Context): Int{
            return 10
        }

        private const val STREAK_COUNTER = "com.example.timerapplication.streak_counter"

        fun getStreak(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(STREAK_COUNTER, 0)
        }

        fun setStreak(num: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(STREAK_COUNTER, num)
            editor.apply()
        }

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.example.timerapplication.timer.previous_timer_length_seconds"

        fun getPreviousTimerLengthSeconds(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }


        private const val TIMER_STATE_ID = "com.example.timerapplication.timer.timer_state"

        fun getTimerState(context: Context): MainActivity.TimerState{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return MainActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(state: MainActivity.TimerState, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }


        private const val SECONDS_REMAINING_ID = "com.example.timerapplication.timer.seconds_remaining"

        fun getSecondsRemaining(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID = "com.example.timerapplication.backgrounded_time"

        fun getAlarmSetTime(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            // return value with key ALARM_SET_TIME_ID otherwise return 0
            return preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }
    }
}