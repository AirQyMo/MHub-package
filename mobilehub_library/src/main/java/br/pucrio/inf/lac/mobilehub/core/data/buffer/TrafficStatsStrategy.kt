package br.pucrio.inf.lac.mobilehub.core.data.buffer

import android.net.TrafficStats
import android.os.SystemClock
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import br.pucrio.inf.lac.mobilehub.core.helpers.extensions.traffic.KB
import br.pucrio.inf.lac.mobilehub.core.helpers.extensions.traffic.MB
import br.pucrio.inf.lac.mobilehub.core.helpers.extensions.traffic.toSpeed

class TrafficStatsStrategy: BufferStrategy {
    companion object {
        private const val MIN_ELAPSED_TIME = 15 * 1000

        private var lastTimeReading: Long = 0
        private var previousUpStream: Long = -1
        private var previousDownStream: Long = -1
    }

    override fun handleBuffer(wlanTechnology: WLAN) {
        val (upStream: Double, downStream: Double) = readTrafficStats()
        val elapsedTime = SystemClock.elapsedRealtime() - lastTimeReading

        if (elapsedTime <= MIN_ELAPSED_TIME) {
            val upStreamSpeed = upStream.toSpeed()
            val downStreamSpeed = downStream.toSpeed()

            if (canTransfer(upStreamSpeed) || canTransfer(downStreamSpeed)) {
                wlanTechnology.publishQueuedMessages()
            }
        }
    }

    private fun readTrafficStats(): Pair<Double, Double> {
        val newBytesUpStream = TrafficStats.getTotalTxBytes() * 1024
        val newBytesDownStream = TrafficStats.getTotalRxBytes() * 1024
        val byteDiffUpStream = newBytesUpStream - previousUpStream
        val byteDiffDownStream = newBytesDownStream - previousDownStream

        var bandwidthUpStream = 0.0
        var bandwidthDownStream = 0.0

        synchronized(this) {
            val currentTime = SystemClock.elapsedRealtime()

            if (previousUpStream >= 0) {
                bandwidthUpStream = byteDiffUpStream * 1.0 / (currentTime - lastTimeReading)
            }

            if (previousDownStream >= 0) {
                bandwidthDownStream = byteDiffDownStream * 1.0 / (currentTime - lastTimeReading)
            }

            lastTimeReading = currentTime
        }

        previousDownStream = newBytesDownStream
        previousUpStream = newBytesUpStream

        return Pair(bandwidthUpStream, bandwidthDownStream)
    }

    private fun canTransfer(speed: Double): Boolean {
        return when {
            speed < KB -> false
            speed < MB -> true
            else -> true
        }
    }
}