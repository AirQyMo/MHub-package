package br.pucrio.inf.lac.mrudp

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import lac.cnclib.net.mrudp.MrUdpNodeConnection
import lac.cnclib.sddl.message.ApplicationMessage
import lac.cnclib.sddl.message.ClientLibProtocol.PayloadSerialization
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.net.InetSocketAddress
import java.util.*

internal class MrudpAndroidClient private constructor(hostname: String, port: Int) {
    companion object {
        private const val KEY_TOPIC = "topic"
        private const val KEY_PAYLOAD = "payload"
        private const val KEY_TIMESTAMP = "timestamp"

        fun create(hostname: String, port: Int) = MrudpAndroidClient(hostname, port)
    }

    private lateinit var worker: Thread
    private val socket = InetSocketAddress(hostname, port)
    private val connection = MrUdpNodeConnection()

    val clientId: UUID = UUID.randomUUID()

    fun connect(listener: MrudpCallback) {
        worker = Thread {
            try {
                connection.addNodeConnectionListener(listener)
                connection.connect(socket)
            } catch (ex: IOException) {
                Timber.e(ex)
                listener.disconnected(connection)
            }
        }
        worker.start()
    }

    fun publish(topic: Topic, payload: String) {
        try {
            val jsonPayload = mapOf(
                KEY_TOPIC to topic.value,
                KEY_PAYLOAD to payload,
                KEY_TIMESTAMP to System.currentTimeMillis()
            ).let { JSONObject(it) }

            val message = ApplicationMessage().apply {
                setPayloadType(PayloadSerialization.JSON)
                contentObject = jsonPayload.toString()
                tagList = ArrayList()
                senderID = clientId
            }

            connection.sendMessage(message)
        } catch (ex: IOException) {
            Timber.e(ex)
        }
    }

    fun disconnect() {
        connection.disconnect()
        worker.interrupt()
    }
}
