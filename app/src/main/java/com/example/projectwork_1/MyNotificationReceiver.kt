package com.example.projectwork_1

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import com.example.projectwork_1.utils.DetailsNotifications
import com.example.projectwork_1.utils.NotificationConstants
import com.example.projectwork_1.view.activities.MainActivity

class MyNotificationReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val filmId = intent.getIntExtra(NotificationConstants.EXTRA_FILM_ID, -1)
        val filmTitle = intent.getStringExtra(NotificationConstants.EXTRA_FILM_TITLE) ?: "фильм"

        val notification = DetailsNotifications(context, NotificationConstants.CHANNEL_ID, filmTitle, filmId).notification

        when(intent.action) {

            NotificationConstants.ALARM_NOTIFICATION_ACTION -> {
                notificationManager.notify(
                    filmId,
                    notification.build()
                )
            }

            NotificationConstants.ACTION_OPEN -> {
                val intent1 = Intent(context, MainActivity::class.java).apply {
                    putExtra(NotificationConstants.EXTRA_FILM_ID, filmId)
                }
                intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent1)
            }

            NotificationConstants.ACTION_DELETE -> {
                notificationManager.cancelAll()
            }

        }
    }
}