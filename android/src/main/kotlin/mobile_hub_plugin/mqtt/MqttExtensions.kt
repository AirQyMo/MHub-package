package br.pucrio.inf.lac.mqtt

import android.content.Context
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.QoS
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import br.pucrio.inf.lac.mobilehub.core.helpers.extensions.json.toByteArray
import info.mqtt.android.service.Ack
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONObject

private const val KEY_PAYLOAD = "payload"
private const val KEY_TIMESTAMP = "timestamp"

fun Context.buildMqttClient(ipAddress: String, port: Int, callback: MqttCallback? = null): MqttAndroidClient {
    val url = "tcp://$ipAddress:$port"
    val id = MqttClient.generateClientId()

    return MqttAndroidClient(this, url, id, Ack.AUTO_ACK).apply {
        callback?.let { setCallback(it) }
    }
}

fun IMqttToken.subscribe(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
    actionCallback = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken) = onSuccess()
        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) = onFailure(exception)
    }
}

fun mqttMessageOf(payload: String, qos: QoS): MqttMessage {
    val jsonPayload = mapOf(
        KEY_PAYLOAD to payload,
        KEY_TIMESTAMP to System.currentTimeMillis()
    ).let { JSONObject(it) }

    val encodedPayload = jsonPayload.toByteArray()
    return MqttMessage(encodedPayload).apply {
        this.qos = qos.value
    }
}

fun MqttConnectOptions.setWill(topic: Topic, payload: ByteArray, qos: QoS, retained: Boolean) =
    setWill(topic.value, payload, qos.value, retained)

fun MqttAndroidClient.subscribe(topic: String, qos: QoS): IMqttToken = subscribe(topic, qos.value)

fun MqttAndroidClient.publish(topic: Topic, message: MqttMessage): IMqttToken = publish(topic.value, message)
