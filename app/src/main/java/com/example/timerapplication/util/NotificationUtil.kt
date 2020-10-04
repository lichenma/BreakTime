package com.example.timerapplication.util

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.timerapplication.MainActivity
import com.example.timerapplication.R
import com.example.timerapplication.TimerNotificationActionReceiver
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtil {
    // creating the companion object allows us to call this function
    // from other classes without needing to create an instance of the
    // NotificationUtil class
    companion object{
        // required when working with OREO api 26
        private const val CHANNEL_ID_TIMER = "menu_timer"
        private const val CHANNEL_NAME_TIMER = "Timer App Timer"
        private const val TIMER_ID = 0

        fun showTimerExpired(context: Context){
            // we want to be control timer from notification
            // allow starting the timer from notification
            val startIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            // string
            startIntent.action = AppConstants.ACTION_START
            val startPendingIntent = PendingIntent.getBroadcast(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
            nBuilder.setContentTitle("Timer Expired!")
                .setContentText("Start again?")
                .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
                // calls the pending intent when clicked
                .addAction(R.drawable.ic_play, "Start", startPendingIntent)

            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
            nManager.notify(TIMER_ID, nBuilder.build())
        }

        fun showTimerRunning(context: Context, wakeUpTime: Long){
            // we want to be control timer from notification
            // allow starting the timer from notification
            val stopIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            // string
            stopIntent.action = AppConstants.ACTION_STOP
            val stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val pauseIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            // string
            pauseIntent.action = AppConstants.ACTION_PAUSE
            val pausePendingIntent = PendingIntent.getBroadcast(context, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
            nBuilder.setContentTitle("Timer is Running!")
                .setContentText("End: ${df.format(Date(wakeUpTime))}")
                .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
                // makes it so the user cannot dismiss the notification menu
                .setOngoing(true)
                .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
                .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)


            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
            nManager.notify(TIMER_ID, nBuilder.build())
        }


        fun showTimerPaused(context: Context){
            // we want to be control timer from notification
            // allow starting the timer from notification
            val resumeIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            // string
            resumeIntent.action = AppConstants.ACTION_RESUME
            val resumePendingIntent = PendingIntent.getBroadcast(context, 0, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
            nBuilder.setContentTitle("Timer is paused.")
                .setContentText("Resume?")
                .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
                .setOngoing(true)
                // calls the pending intent when clicked
                .addAction(R.drawable.ic_play, "Resume", resumePendingIntent)

            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
            nManager.notify(TIMER_ID, nBuilder.build())
        }

        fun hideTimerNotification(context: Context){
            val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.cancel(TIMER_ID)
        }

        private fun getBasicNotificationBuilder(context: Context, channelId: String, playSound: Boolean): NotificationCompat.Builder {
            val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            // auto cancel ensures when user clicks on notification it will be dismissed
            val nBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_timer)
                .setAutoCancel(true)
                .setDefaults(0)
            if (playSound) nBuilder.setSound(notificationSound)
            return nBuilder
        }

        // activity two layers deep - when user clicks on notification, it
        // will take user to that activity - when back button is clicked
        // it takes user to activity one layer lower

        // just like if the activity that was opened before was not opened
        private fun <T> getPendingIntentWithStack(context: Context, javaClass: Class<T>):PendingIntent{
            val resultIntent = Intent(context, javaClass)
            // if activity we are calling is already open we are not creating it again
            resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(javaClass)
            stackBuilder.addNextIntent(resultIntent)

            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        // extension function which extends a class by one function
        // allows us to edit the code of the class - in this case the class
        // notification manager
        @TargetApi(26)
        private fun NotificationManager.createNotificationChannel(channelId: String, channelName: String, playSound: Boolean){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
                else NotificationManager.IMPORTANCE_LOW
                val nChannel = NotificationChannel(channelId, channelName, channelImportance)
                nChannel.enableLights(true)
                nChannel.lightColor = Color.BLUE
                this.createNotificationChannel(nChannel)
            }
        }

    }
}