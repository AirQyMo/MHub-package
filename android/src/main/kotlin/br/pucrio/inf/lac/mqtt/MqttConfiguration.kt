package br.pucrio.inf.lac.mqtt

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.QoS
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.MqttConnectOptions

internal object MqttConfiguration {
    val connectionOptions = MqttConnectOptions().apply {
        isAutomaticReconnect = true
        val lastWillAndTestament = "Disconnected".toByteArray()
        setWill(Topic.Default, lastWillAndTestament, QoS.LeastOnce, false)
    }

    fun apply(client: MqttAndroidClient): Map<Topic, String> {
        setupDisconnectBuffer(client)
        publishConnection(client)
        return setupSubscriptions(client)
    }

    private fun setupDisconnectBuffer(client: MqttAndroidClient) {
        val options = DisconnectedBufferOptions().apply {
            isBufferEnabled = true
            isDeleteOldestMessages = true
        }

        client.setBufferOpts(options)
    }

    private fun publishConnection(client: MqttAndroidClient) {
        val identifier = client.clientId
        val message = mqttMessageOf(identifier, QoS.ExactlyOnce)
        client.publish(Topic.Connected, message)
    }

    private fun setupSubscriptions(client: MqttAndroidClient): Map<Topic, String> {
        val subscriptions = hashMapOf<Topic, String>()
        val identifier = client.clientId
        for (clazz in Topic::class.sealedSubclasses) {
            val topic = clazz.objectInstance as Topic
            val clientTopic = "${identifier}/${topic.value}"
            client.subscribe(clientTopic, QoS.ExactlyOnce)
            subscriptions[topic] = clientTopic
        }

        return subscriptions
    }
}
