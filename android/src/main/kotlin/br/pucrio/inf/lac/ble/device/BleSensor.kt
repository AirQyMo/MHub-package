package br.pucrio.inf.lac.ble.device

import java.util.UUID

class BleSensor(
    val name: String,
    val data: UUID,
    val conf: UUID,
    val enable: ByteArray
) {
    lateinit var convert: (ByteArray) -> List<Double>
}