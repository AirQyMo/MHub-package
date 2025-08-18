package br.pucrio.inf.lac.mobilehub.domain.entity

data class ConfigurationEntity(
    val wlan: Wlan,
    val ipAddress: String,
    val port: Int?
)
