package br.pucrio.inf.lac.ble

import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.mockrxandroidble.RxBleClientMock

class BleClientBuilder {
    private val mockDeviceBuilder = RxBleClientMock.DeviceBuilder()
        .scanRecord(byteArrayOf(0x00))
        .rssi(-50)

    private val mockDevices = mutableListOf<RxBleDevice>()

    fun addDevice(macAddress: String, name: String) {
        val mockDevice = mockDeviceBuilder.deviceMacAddress(macAddress)
            .deviceName(name)
            .build()
        mockDevices.add(mockDevice)
    }

    fun build(): RxBleClientMock = RxBleClientMock.Builder().apply {
        mockDevices.forEach { addDevice(it) }
    }.build()
}

