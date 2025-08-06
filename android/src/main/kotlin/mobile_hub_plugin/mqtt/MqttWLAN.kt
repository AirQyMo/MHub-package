package br.pucrio.inf.lac.mqtt

import android.content.Context
import br.pucrio.inf.lac.mobilehub.core.data.remote.ConnectionStatus
import br.pucrio.inf.lac.mobilehub.core.domain.entities.Message
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.technology.TechnologyDisconnectedException
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.QoS
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLANListener
import br.pucrio.inf.lac.mobilehub.core.gateways.connection.base.Envelope
import br.pucrio.inf.lac.mobilehub.core.helpers.components.circularqueue.CircularQueue
import br.pucrio.inf.lac.mobilehub.core.helpers.components.circularqueue.LimitedSizedQueue
import br.pucrio.inf.lac.mobilehub.core.helpers.extensions.error.ignoreException
import br.pucrio.inf.lac.mobilehub.core.helpers.extensions.kotlin.isNotNull
import br.pucrio.inf.lac.mqtt.mapper.asMqttResponseTopic
import br.pucrio.inf.lac.mqtt.mapper.asTopic
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.subjects.SingleSubject
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import timber.log.Timber

class MqttWLAN(
    private val client: MqttAndroidClient,
    private val gson: Gson
): WLAN {
    companion object {
        private const val TOPIC_KEY_RESPONSE = "response_topic"
    }

    private var status: ConnectionStatus = ConnectionStatus.Disconnected
    private val messages: CircularQueue<Pair<Topic, MqttMessage>> = LimitedSizedQueue()
    private val subject: SingleSubject<String> = SingleSubject.create()
    private lateinit var subscriptions: Map<Topic, String>

    init {
        client.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) = onNewMessage(topic, mqttMessage)

            override fun connectionLost(cause: Throwable?) = onDisconnected(cause)

            override fun deliveryComplete(token: IMqttDeliveryToken?) =
                Timber.i("Delivery complete: ${token.toString()}")
        })
    }

    override var listener: WLANListener? = null

    override fun connect(): Boolean {
        if (status.isConnectingOrConnected) {
            Timber.w("Already connecting or connected")
            return false
        }

        status = ConnectionStatus.Connecting

        return try {
            val options = MqttConfiguration.connectionOptions
            client.connect(options)
                .subscribe(::onConnected, ::onDisconnected)
            true
        } catch (ex: MqttException) {
            Timber.e(ex)
            onDisconnected()
            false
        }
    }

    private fun onConnected() {
        subscriptions = MqttConfiguration.apply(client)
        status = ConnectionStatus.Connected
        listener?.onConnected()
    }

    private fun onDisconnected(cause: Throwable? = null) {
        Timber.e(cause)
        status = ConnectionStatus.Disconnected
        listener?.onDisconnected()
    }

    override fun <T : Any> publishRequest(topic: Topic, payload: Envelope<T>): Completable = Completable.fromAction {
        validateInitialization()
    }.andThen {
        val requestPayload = payload.addResponseTopic(topic)
        publishMessage(topic, requestPayload)
    }

    private fun validateInitialization() {
        if (!::subscriptions.isInitialized) {
            throw TechnologyDisconnectedException("Mqtt is not connected")
        }
    }

    private fun <T : Any> Envelope<T>.addResponseTopic(topic: Topic): Any {
        val responseTopic = subscriptions[topic]
        if (responseTopic.isNotNull()) {
            return gson.toJsonTree(this)
                .asJsonObject
                .addProperty(TOPIC_KEY_RESPONSE, responseTopic)
        }

        return this
    }

    override fun publishResponse(topic: Topic, payload: Any): Completable {
        val json = gson.toJson(payload)
        return Completable.fromAction {
            val message = mqttMessageOf(json, QoS.ExactlyOnce)
            val responseTopic = topic.asMqttResponseTopic(client)
            client.publish(responseTopic, message)
        }
    }

    override fun publishMessage(topic: Topic, payload: Any, qos: QoS): Completable {
        val json = gson.toJson(payload)
        return Completable.fromAction {
            val message = mqttMessageOf(json, qos)
            publishMessage(topic, message)
        }
    }

    private fun publishMessage(topic: Topic, message: MqttMessage) {
        try {
            client.publish(topic, message)
        } catch (exception: MqttException) {
            Timber.e(exception)
            messages.add(Pair(topic, message))
        }
    }

    override fun queueMessage(topic: Topic, payload: Any, qos: QoS) {
        val json = gson.toJson(payload)
        val message = mqttMessageOf(json, qos)
        messages.add(Pair(topic, message))
    }

    override fun publishQueuedMessages() {
        val collection = mutableListOf<Pair<Topic, MqttMessage>>()
        messages.drainTo(collection)

        val iterator = collection.iterator()
        while (iterator.hasNext()) {
            val (topic, message) = iterator.next()
            publishMessage(topic, message)
        }
    }

    private fun onNewMessage(mqttTopic: String, mqttMessage: MqttMessage) {
        val topic = mqttTopic.asTopic
        val payload = String(mqttMessage.payload)
        val message = Message(topic, payload)
        listener?.onNewMessage(message)
    }

    override fun disconnect() {
        if (status.isConnected) {
            ignoreException {
                client.disconnect()
            }
            onDisconnected()
        }
    }

    class Builder(private val context: Context) {
        private var ipAddress: String? = null
        private var port: Int? = null
        private var gson: Gson = Gson()

        fun ipAddress(ipAddress: String) = this.also { this.ipAddress = ipAddress }

        fun port(port: Int): Builder = this.also { this.port = port }

        fun gson(gson: Gson) = this.also { this.gson = gson }

        fun build(): WLAN {
            requireNotNull(ipAddress) { "Ip address required" }
            requireNotNull(port) { "Port required" }

            val client = context.buildMqttClient(ipAddress!!, port!!)
            return MqttWLAN(client, gson)
        }
    }
}
