package br.pucrio.inf.lac.ble

import br.pucrio.inf.lac.ble.device.BleSensor
import br.pucrio.inf.lac.ble.transcoder.lua.LuaTranscoderFactory
import junit.framework.TestCase.assertNotNull
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BleLuaTranscoderTest {
    private object Driver {
        const val SENSOR_TAG = "/CC2650SensorTag.lua"
    }

    private object Code {
        val ENABLE = byteArrayOf(1)
        val ENABLE_MOV = byteArrayOf(0x7F, 0x00)
    }

    private val sensorTagDriver: String = load(Driver.SENSOR_TAG)

    @Test
    fun loadIsCorrect() {
        val factory = LuaTranscoderFactory.create()
        val transcoder = factory.driverTranscoder()
        val device = transcoder.convert(sensorTagDriver)

        assertNotNull(device)

        device.sensors.forEach {
            val sensor = it.value
            assertArrayEquals(sensor.actualEnableCode, sensor.enable)

            val value = "testingaccelerometer".toByteArray()
            println(it.value.convert(value))
        }
    }

    private val BleSensor.actualEnableCode: ByteArray
        get() = when (name) {
            "accelerometer", "gyroscope", "magnetometer" -> Code.ENABLE_MOV
            else -> Code.ENABLE
        }
}