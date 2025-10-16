package br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan

import br.pucrio.inf.lac.mobilehub.core.gateways.connection.base.Envelope
import io.reactivex.Completable

interface WLAN {
    var listener: WLANListener?

    fun connect(): Boolean

    fun <T : Any> publishRequest(topic: Topic, payload: Envelope<T>): Completable

    fun publishResponse(topic: Topic, payload: Any): Completable

    fun publishMessage(topic: Topic, payload: Any, qos: QoS = QoS.MostOnce): Completable

    fun queueMessage(topic: Topic, payload: Any, qos: QoS = QoS.MostOnce)

    fun publishQueuedMessages()

    fun disconnect()

    fun updateContext(payload: List<String>): Completable
}