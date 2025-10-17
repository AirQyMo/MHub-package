
package br.pucrio.inf.lac

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(private val context: Context) {
    private val channelId = "mrudp_messages"
    private val channelName = "MR-UDP Messages"
    private var notificationIdCounter = 1

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for incoming MR-UDP messages"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(title: String, content: String) {
        try {
            val iconResId = context.resources.getIdentifier("ic_router", "mipmap", context.packageName)

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(if (iconResId != 0) iconResId else android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(notificationIdCounter++, builder.build())
            }
        } catch (e: Exception) {
            android.util.Log.e("NotificationHelper", "Error showing notification", e)
        }
    }
}
