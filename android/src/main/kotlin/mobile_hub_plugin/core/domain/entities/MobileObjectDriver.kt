package br.pucrio.inf.lac.mobilehub.core.domain.entities

data class MobileObjectDriver(
    val id: Long? = null,
    val wpan: Int,
    val name: String,
    val config: String?,
    val content: String
)