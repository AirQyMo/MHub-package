package br.pucrio.inf.lac.mrudp

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import ckafka.data.SwapData
import lac.cnclib.net.mrudp.MrUdpNodeConnection
import lac.cnclib.sddl.message.ApplicationMessage
import lac.cnclib.sddl.message.ClientLibProtocol.PayloadSerialization
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.*

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode

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
                Timber.d("Client ID = $clientId")
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

            val messageContent = jsonPayload.toString().toByteArray(StandardCharsets.UTF_8)

            val data = SwapData()
            data.message = messageContent

            data.topic = "AppModel"
            // data.topic = "PrivateMessageTopic"
            // data.recipient = "01111111-1111-1111-1111-111111111111"


            val message = ApplicationMessage().apply {
                contentObject = data
                senderID = clientId
            }

            connection.sendMessage(message)
        } catch (ex: IOException) {
            Timber.e(ex)
        }
    }

    fun updateContext(payload: List<String>) {
        val topic = Topic.parse("GroupReportTopic")

        try {
            val jsonPayload = mapOf(
                KEY_TOPIC to topic.value,
                KEY_PAYLOAD to payload,
                KEY_TIMESTAMP to System.currentTimeMillis()
            ).let { JSONObject(it) }

            val messageContent = jsonPayload.toString().toByteArray(StandardCharsets.UTF_8)

            val data = SwapData()
            data.message = messageContent

            val objMapper = ObjectMapper()
            val contextObj = objMapper.createObjectNode()

            // val beaconsListString = listOf("Beacon 1", "Beacon 2").joinToString(prefix="[", postfix="]")
            val beaconsListString = payload.joinToString(prefix="[", postfix="]")
            Timber.i("Beacons found in context $beaconsListString")
            contextObj.put("beacons", beaconsListString)

            data.setContext(contextObj)
            data.setDuration(60)
            data.topic = "GroupReportTopic"

            val message = ApplicationMessage().apply {
                contentObject = data
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
