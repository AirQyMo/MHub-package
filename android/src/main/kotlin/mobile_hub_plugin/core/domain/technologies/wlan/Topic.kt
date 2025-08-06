package br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan

sealed class Topic(val value: String) {
    object Discovered : Topic("discovered")
    object Connected : Topic("connected")
    object Data : Topic("data")
    object Cep : Topic("cep")
    object Driver : Topic("driver")
    object Default : Topic("default")

    companion object {
        fun parse(topic: String): Topic = when (topic) {
            Discovered.value -> Discovered
            Connected.value -> Connected
            Data.value -> Data
            Cep.value -> Cep
            Driver.value -> Driver
            else -> Default
        }
    }
}
