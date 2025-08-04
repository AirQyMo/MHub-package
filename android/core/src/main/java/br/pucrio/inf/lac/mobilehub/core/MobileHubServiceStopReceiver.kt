package br.pucrio.inf.lac.mobilehub.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

internal class MobileHubServiceStopReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        MobileHubService.stopService(context)
    }
}