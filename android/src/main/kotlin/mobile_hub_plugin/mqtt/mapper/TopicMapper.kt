package br.pucrio.inf.lac.mqtt.mapper

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import info.mqtt.android.service.MqttAndroidClient

private const val TOPIC_DELIMITER = "/"

val String.asTopic: Topic
    get() {
        val value = substringAfterLast(TOPIC_DELIMITER)
        return Topic.parse(value)
    }

fun Topic.asMqttResponseTopic(client: MqttAndroidClient): String {
    val identifier = client.clientId
    return "${identifier}/$value/response"
}
