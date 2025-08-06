package br.pucrio.inf.lac.ble.transcoder

import br.pucrio.inf.lac.ble.device.BleDevice

interface Transcoder {
    fun convert(encodedDriver: String): BleDevice

    interface Factory {
        fun driverTranscoder(config: String? = null): Transcoder
    }
}