package br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan

sealed class QoS(val value: Int) {
    object MostOnce: QoS(0)
    object LeastOnce: QoS(1)
    object ExactlyOnce: QoS(2)
}