package com.example.projectwork_1

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Bundle
import com.example.domain_room_api.entity.Film
import com.example.projectwork_1.utils.NotificationConstants
import com.example.projectwork_1.view.activities.MainActivity
import com.example.projectwork_1.view.fragments.DetailsFragment

class MyNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        when(intent.action) {
            NotificationConstants.ACTION_OPEN -> {
                val film =  intent.getParcelableExtra<Film>(NotificationConstants.EXTRA_FILM_ID)!!
                val intent1 = Intent(context, MainActivity::class.java).apply {
                    putExtra(NotificationConstants.EXTRA_FILM_ID, film)
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