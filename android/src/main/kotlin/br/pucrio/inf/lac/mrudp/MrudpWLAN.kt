package br.pucrio.inf.lac.mrudp

import br.pucrio.inf.lac.mobilehub.core.data.remote.ConnectionStatus
import br.pucrio.inf.lac.mobilehub.core.domain.entities.Message
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.QoS
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLANListener
import br.pucrio.inf.lac.mobilehub.core.gateways.connection.base.Envelope
import br.pucrio.inf.lac.mobilehub.core.helpers.components.circularqueue.CircularQueue
import br.pucrio.inf.lac.mobilehub.core.helpers.components.circularqueue.LimitedSizedQueue
import com.google.gson.Gson
import io.reactivex.Completable
import timber.log.Timber

class MrudpWLAN private constructor(
    private val client: MrudpAndroidClient,
    private val gson: Gson
): WLAN {
    private var status: ConnectionStatus = ConnectionStatus.Disconnected
    private val messages: CircularQueue<Pair<Topic, String>> = LimitedSizedQueue()

    private val connectionCallback = MrudpCallback(
        connected = ::onConnected,
        messageArrived = ::onNewMessage,
        connectionLost = ::onDisconnected
    )

    override var listener: WLANListener? = null

    override fun connect(): Boolean {
        if (status.isConnectingOrConnected) {
            Timber.w("Already connecting or connected")
            return false
        }

        status = ConnectionStatus.Connecting
        client.connect(connectionCallback)
        return true
    }

    private fun onConnected() {
        publishConnection()
        status = ConnectionStatus.Connected
        listener?.onConnected()
    }

    private fun publishConnection() {
        val identifier = client.clientId.toString()
        client.publish(Topic.Connected, identifier)
    }

    override fun <T : Any> publishRequest(topic: Topic, payload: Envelope<T>) =
        publishMessage(topic, payload, QoS.ExactlyOnce)

    override fun publishResponse(topic: Topic, payload: Any) = publishMessage(topic, payload, QoS.ExactlyOnce)

    override fun publishMessage(topic: Topic, payload: Any, qos: QoS) = Completable.fromAction {
        val json = gson.toJson(payload)
        client.publish(topic, json)
    }

    override fun queueMessage(topic: Topic, payload: Any, qos: QoS) {
        val json = gson.toJson(payload)
        messages.add(Pair(topic, json))
    }

    override fun publishQueuedMessages() {
        val collection = mutableListOf<Pair<Topic, String>>()
        messages.drainTo(collection)

        val iterator = collection.iterator()
        while (iterator.hasNext()) {
            val (topic, message) = iterator.next()
            publishMessage(topic, message)
        }
    }

    private fun onNewMessage(topic: Topic, payload: String) {
        val message = Message(topic, payload)
        listener?.onNewMessage(message)
    }

    override fun updateContext(payload: List<String>): Completable = Completable.fromAction {
        client.updateContext(payload)
    }

    override fun disconnect() = client.disconnect()

    private fun onDisconnected() {
        status = ConnectionStatus.Disconnected
        listener?.onDisconnected()
    }

    class Builder {
        private var ipAddress: String? = null
        private var port: Int? = null
        private var gson: Gson = Gson()

        fun ipAddress(ipAddress: String) = this.also { this.ipAddress = ipAddress }

        fun port(port: Int): Builder = this.also { this.port = port }

        fun gson(gson: Gson) = this.also { this.gson = gson }

        fun build(): WLAN {
            requireNotNull(ipAddress) { "Ip address required" }
            requireNotNull(port) { "Port required" }

            val client = MrudpAndroidClient.create(ipAddress!!, port!!)
            return MrudpWLAN(client, gson)
        }
    }
}
