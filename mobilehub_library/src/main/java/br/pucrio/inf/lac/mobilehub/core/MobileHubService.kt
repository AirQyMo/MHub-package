package br.pucrio.inf.lac.mobilehub.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import br.pucrio.inf.lac.mobilehub.core.data.remote.BufferTransmissionWorker
import br.pucrio.inf.lac.mobilehub.core.di.injector
import br.pucrio.inf.lac.mobilehub.core.gateways.connection.ConnectionGateway
import br.pucrio.inf.lac.mobilehub.core.gateways.mepa.MEPAGateway
import br.pucrio.inf.lac.mobilehub.core.gateways.s2pa.S2PAGateway
import br.pucrio.inf.lac.mobilehub.core.helpers.components.RxBus
import br.pucrio.inf.lac.mobilehub.library.R
import javax.inject.Inject

internal class MobileHubService : Service() {
    companion object {
        private const val CHANNEL_ID = "mobilehub_channel_id"
        private const val CHANNEL_NAME = "mobilehub_channel_name"

        var isStarted: Boolean = false

        fun startService(context: Context) = context.startService(
            Intent(context, MobileHubService::class.java))

        fun stopService(context: Context) = context.stopService(
            Intent(context, MobileHubService::class.java))
    }

    @Inject
    lateinit var connectionGateway: ConnectionGateway

    @Inject
    lateinit var s2paGateway: S2PAGateway

    @Inject
    lateinit var mepaGateway: MEPAGateway

    override fun onCreate() {
        super.onCreate()
        injector.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        BufferTransmissionWorker.cancel(applicationContext)

        s2paGateway.release()
        connectionGateway.release()
        mepaGateway.release()

        isStarted = false
        RxBus.publish(MobileHubEvent.Status(isStarted))
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        displayNotification()

        connectionGateway.start()
        s2paGateway.start()
        mepaGateway.start()

        BufferTransmissionWorker.init(applicationContext)

        isStarted = true
        RxBus.publish(MobileHubEvent.Status(isStarted))

        return START_REDELIVER_INTENT
    }

    private fun displayNotification() {
        createNotificationChannel()
        val intent = Intent(this, MobileHubServiceStopReceiver::class.java)
        val flags = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> PendingIntent.FLAG_IMMUTABLE
            else -> PendingIntent.FLAG_UPDATE_CURRENT
        }

        val stopPendingIntent = PendingIntent.getBroadcast(this, 1, intent, flags)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_message))
            .setSmallIcon(R.mipmap.ic_router)
            .addAction(android.R.drawable.ic_media_pause, getString(R.string.action_stop), stopPendingIntent)
            .build()
        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(channel)
        }
    }
}
