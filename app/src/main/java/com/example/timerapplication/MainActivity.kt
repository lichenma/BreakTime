package com.example.timerapplication

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import com.google.android.material.snackbar.Snackbar
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.timerapplication.util.NotificationUtil
import com.example.timerapplication.util.PrefUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        // Immutable property
        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }

    enum class TimerState{
        Running, Succeeded, Done
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds: Long = PrefUtil.getTimerLength(this) * 60L
    private var timerState = TimerState.Running
    private var secondsRemaining: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = "      🖐🤚 Minutes"
        PrefUtil.setTimerState(TimerState.Done, this)
        initTimer()
    }

    /*
     * onResume(): This is an indicator that the activity became active and ready to receive input
     */
    override fun onResume() {
        super.onResume()
        initTimer()
    }

    /*
     *  onPause(): Part of the Activity lifecycle when the user no longer actively interacts with the activity,
     *             but it is still visible on screen
     */
    @TargetApi(20)
    override fun onPause() {
        super.onPause()
        timer.cancel()

        when(timerState) {
            TimerState.Running -> {
                PrefUtil.setAlarmSetTime(nowSeconds, this)
                var pm = this.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
                if (pm.isInteractive){
                    // This means we left the app with time to spare - we want to remove the streak
                    PrefUtil.setStreak(0, this)
                    PrefUtil.setTimerState(TimerState.Done, this)
                    PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
                } else {
                    // This means the user probably put the app to sleep we want to allow this action
                    PrefUtil.setSecondsRemaining(secondsRemaining, this)
                    PrefUtil.setTimerState(timerState, this)
                }
            }
            TimerState.Done -> {
                PrefUtil.setTimerState(timerState, this)
            }
            TimerState.Succeeded -> {
                var pm = this.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
                if (pm.isInteractive) {
                    // This means we left the app - we want to reset to the Done state
                    PrefUtil.setTimerState(TimerState.Done, this)
                } else {
                    // User just put the app to sleep we want to remain in this state until the user leaves the app
                    PrefUtil.setTimerState(TimerState.Succeeded, this)
                }
            }
        }
    }

    private fun initTimer(){
        timerState = PrefUtil.getTimerState(this)

        if (timerState == TimerState.Done || timerState == TimerState.Running){
            startTimer()
            PrefUtil.setTimerState(TimerState.Running, this)
            progress_countdown.progress = 0
        }

        secondsRemaining = if (timerState == TimerState.Running)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)

        // alarm was set if the value is greater than 0
        if (alarmSetTime > 0)
            // gives us the amount of time the app was running in the background
            secondsRemaining -= nowSeconds - alarmSetTime
        else {
            // finished in the background
            onTimerFinished()
        }

        updateCountdownUI()
    }
    @TargetApi(20)
    private fun onTimerFinished(){
        // means user has stayed for 10 mins and we can increment the counter
        var streak = PrefUtil.getStreak(this)
        streak += 1
        PrefUtil.setStreak(streak, this)


        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds

        updateCountdownUI()
    }

    private fun startTimer(){
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun updateCountdownUI(){
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        textView_countdown.text = "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
        var streak = PrefUtil.getStreak(this)
        streak_count.text = "Current Streak is: ${streak}"

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
