package br.pucrio.inf.lac.mrudp

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import ckafka.data.SwapData
import lac.cnclib.net.NodeConnection
import lac.cnclib.net.NodeConnectionListener
import lac.cnclib.sddl.message.Message
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.net.SocketAddress
import java.nio.charset.StandardCharsets
import java.util.*

internal class MrudpCallback(
    private val connected: () -> Unit = {},
    private val messageArrived: (Topic, String) -> Unit = { _, _ -> },
    private val connectionLost: () -> Unit = {}
) : NodeConnectionListener {
    companion object {
        private const val KEY_TOPIC = "topic"
        private const val KEY_PAYLOAD = "payload"
    }

    override fun connected(connection: NodeConnection) = connected()

    override fun reconnected(connection: NodeConnection, socket: SocketAddress, p2: Boolean, p3: Boolean) = connected()

    override fun disconnected(connection: NodeConnection) = connectionLost()

    override fun internalException(connection: NodeConnection, error: Exception) = Timber.e(error)

    override fun newMessageReceived(connection: NodeConnection, message: Message) {
        try {
            when (val content = message.contentObject) {
                is SwapData -> {
                    val payloadString = String(content.message, StandardCharsets.UTF_8)
                    try {
                        // First, assume it's a JSON payload from the Flutter side
                        val json = JSONObject(payloadString)
                        val topic = Topic.parse(json.getString(KEY_TOPIC))
                        val payload = json.getString(KEY_PAYLOAD)
                        messageArrived(topic, payload)
                    } catch (e: JSONException) {
                        // If it's not a JSON, assume it's the other format
                        // where the topic is in the SwapData object itself.
                        val topic = Topic.parse(content.topic)
                        messageArrived(topic, payloadString)
                    }
                }
                is String -> {
                    // This branch handles the case where the contentObject itself is the JSON string
                    val (topic, payload) = parseJsonPayload(content)
                    messageArrived(topic, payload)
                }
                else -> {
                    Timber.w("Received message with unexpected content type: ${content.javaClass.name}")
                }
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Error processing received message")
        }
    }

    private fun parseJsonPayload(jsonString: String): Pair<Topic, String> {
        val json = JSONObject(jsonString)
        val topic = Topic.parse(json.getString(KEY_TOPIC))
        val payload = json.getString(KEY_PAYLOAD)
        return Pair(topic, payload)
    }

    override fun unsentMessages(connection: NodeConnection, messages: MutableList<Message>) =
        Timber.d("${messages.size} unset messages")
}
