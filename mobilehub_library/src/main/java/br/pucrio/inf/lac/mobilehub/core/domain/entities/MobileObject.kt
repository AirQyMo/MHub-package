package br.pucrio.inf.lac.mobilehub.core.domain.entities

data class MobileObject(
    val wpan: Int,
    val name: String?,
    val address: String,
    val rssi: Int
) {
    var packet: ByteArray? = null

    val id = Moid(wpan, address)
}