package br.pucrio.inf.lac.ble

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import com.polidea.rxandroidble2.scan.ScanResult

internal object MobileObjectMapper {
    fun from(wpanId: Int, scanResult: ScanResult) = MobileObject(
        wpan = wpanId,
        name = scanResult.bleDevice.name,
        address = scanResult.bleDevice.macAddress,
        rssi = scanResult.rssi
    ).apply {
        packet = scanResult.scanRecord.bytes
    }
}
