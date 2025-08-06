package br.pucrio.inf.lac.mrudp

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import lac.cnclib.net.NodeConnection
import lac.cnclib.net.NodeConnectionListener
import lac.cnclib.sddl.message.Message
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.net.SocketAddress

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
        if (message.contentObject is String) {
            try {
                val (topic, payload) = message.unpack()
                messageArrived(topic, payload)
            } catch (ex: JSONException) {
                Timber.e(ex)
            }
        }
    }

    private fun Message.unpack(): Pair<Topic, String> {
        val json = JSONObject(contentObject.toString())
        val topic = json.getString(KEY_TOPIC)
        val payload = json.getString(KEY_PAYLOAD)
        return Pair(Topic.parse(topic), payload)
    }

    override fun unsentMessages(connection: NodeConnection, messages: MutableList<Message>) =
        Timber.d("${messages.size} unset messages")
}
