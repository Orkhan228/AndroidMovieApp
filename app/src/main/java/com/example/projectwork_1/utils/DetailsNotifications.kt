package com.example.projectwork_1.utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import com.example.domain_room_api.entity.Film
import com.example.projectwork_1.MyNotificationReceiver
import com.example.projectwork_1.R

class DetailsNotifications(val context: Context, channelID: String?, film: Film) {

    private val intentOpen = Intent(context, MyNotificationReceiver::class.java).apply {
        action = NotificationConstants.ACTION_OPEN
        putExtra(NotificationConstants.EXTRA_FILM_ID, film)
    }
    private val pendingIntentOpen = PendingIntent.getBroadcast(context, film.id, intentOpen,
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    private val actionOpen = Notification.Action.Builder(
        null,
        "Открыть",
        pendingIntentOpen
    ).build()

    private val intentDelete = Intent(context, MyNotificationReceiver::class.java).apply {
        action = NotificationConstants.ACTION_DELETE
    }
    private val pendingIntentDelete = PendingIntent.getBroadcast(context, 1, intentDelete,
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    private val actionDelete = Notification.Action.Builder(
        null,
        "Удалить",
        pendingIntentDelete
    ).build()


     val notification =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, channelID)
                .setContentTitle("Эй! Глянь этот фильм на досуге:")
                .setContentText(film.title)
                .setSmallIcon(Icon.createWithResource(context, R.drawable.ic_notification_film))
                .setShowWhen(true)
                .addAction(actionOpen)
                .addAction(actionDelete)
                .setContentIntent(pendingIntentOpen)
                .setAutoCancel(true)
        } else {
            Notification.Builder(context)
                .setContentTitle("Эй! Глянь этот фильм на досуге:")
                .setContentText(film.title)
                .setSmallIcon(Icon.createWithResource(context, R.drawable.ic_notification_film))
                .setShowWhen(true)
                .addAction(actionOpen)
                .addAction(actionDelete)
                .setContentIntent(pendingIntentOpen)
                .setAutoCancel(true)
        }

}