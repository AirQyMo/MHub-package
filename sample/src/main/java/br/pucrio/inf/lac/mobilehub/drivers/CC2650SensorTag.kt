package br.pucrio.inf.lac.mobilehub.drivers

import br.pucrio.inf.lac.ble.device.BleDevice
import br.pucrio.inf.lac.ble.device.BleSensor
import br.pucrio.inf.lac.ble.extensions.shortSignedAtOffset
import br.pucrio.inf.lac.ble.extensions.shortUnsignedAtOffset
import br.pucrio.inf.lac.ble.extensions.twentyFourBitUnsignedAtOffset
import java.util.*
import kotlin.math.pow

internal class CC2650SensorTag : BleDevice {
    companion object {
        private val UUID_IRT_DATA = UUID.fromString("f000aa01-0451-4000-b000-000000000000")
        private val UUID_IRT_CONF = UUID.fromString("f000aa02-0451-4000-b000-000000000000")

        private val UUID_HUM_DATA = UUID.fromString("f000aa21-0451-4000-b000-000000000000")
        private val UUID_HUM_CONF = UUID.fromString("f000aa22-0451-4000-b000-000000000000")

        private val UUID_OPT_DATA = UUID.fromString("f000aa71-0451-4000-b000-000000000000")
        private val UUID_OPT_CONF = UUID.fromString("f000aa72-0451-4000-b000-000000000000")

        private val UUID_BAR_DATA = UUID.fromString("f000aa41-0451-4000-b000-000000000000")
        private val UUID_BAR_CONF = UUID.fromString("f000aa42-0451-4000-b000-000000000000")

        private val UUID_MOV_DATA = UUID.fromString("f000aa81-0451-4000-b000-000000000000")
        private val UUID_MOV_CONF = UUID.fromString("f000aa82-0451-4000-b000-000000000000")

        private val ENABLE_CODE = byteArrayOf(1)
        private val ENABLE_MOV_CODE = byteArrayOf(0x7F, 0x00)
    }

    override val name: String = "CC2650 SensorTag"

    override val sensors: Map<String, BleSensor>
        get() = listOf(
            temperature,
            humidity,
            accelerometer,
            gyroscope,
            magnetometer,
            luxometer,
            barometer
        ).map {
            it.name to it
        }.toMap()

    private val temperature = BleSensor("temperature", UUID_IRT_DATA, UUID_IRT_CONF, ENABLE_CODE).apply {
        convert = { value ->
            val ambient = extractAmbientTemperature(value)
            val target = extractTargetTemperature(value, ambient)
            val targetNewSensor = extractTargetTemperatureTMP007(value)
            listOf(ambient, target, targetNewSensor)
        }
    }

    private fun extractAmbientTemperature(bytes: ByteArray): Double {
        val offset = 2
        return bytes.shortUnsignedAtOffset(offset) / 128.0
    }

    private fun extractTargetTemperature(bytes: ByteArray, ambient: Double): Double {
        val twoByteValue: Int = bytes.shortSignedAtOffset(0)
        var vObj2 = twoByteValue.toDouble()
        vObj2 *= 0.00000015625
        val tDie = ambient + 273.15
        val s0 = 5.593E-14 // Calibration factor
        val a1 = 1.75E-3
        val a2 = -1.678E-5
        val b0 = -2.94E-5
        val b1 = -5.7E-7
        val b2 = 4.63E-9
        val c2 = 13.4
        val tRef = 298.15
        val s: Double = s0 * (1 + a1 * (tDie - tRef) + a2 * (tDie - tRef).pow(2))
        val vOs: Double = b0 + b1 * (tDie - tRef) + b2 * (tDie - tRef).pow(2)
        val fObj: Double = vObj2 - vOs + c2 * (vObj2 - vOs).pow(2)
        val tObj: Double = (tDie.pow(4) + fObj / s).pow(.25)
        return tObj - 273.15
    }

    private fun extractTargetTemperatureTMP007(bytes: ByteArray): Double {
        val offset = 0
        return bytes.shortUnsignedAtOffset(offset) / 128.0
    }

    private val humidity = BleSensor("humidity", UUID_HUM_DATA, UUID_HUM_CONF, ENABLE_CODE).apply {
        convert = { value ->
            val a: Int = value.shortUnsignedAtOffset(2)
            listOf(100.0 * (a / 65535.0))
        }
    }

    private val accelerometer = BleSensor("accelerometer", UUID_MOV_DATA, UUID_MOV_CONF, ENABLE_MOV_CODE).apply {
        convert = { value ->
            // Range 8G
            val scale = 4096.0
            val x: Int = (value[7].toInt() shl 8) + value[6]
            val y: Int = (value[9].toInt() shl 8) + value[8]
            val z: Int = (value[11].toInt() shl 8) + value[10]
            listOf(x / scale * -1, y / scale, z / scale * -1)
        }
    }

    private val gyroscope = BleSensor("gyroscope", UUID_MOV_DATA, UUID_MOV_CONF, ENABLE_MOV_CODE).apply {
        convert = { value ->
            val scale = 128.0
            val x: Int = (value[1].toInt() shl 8) + value[0]
            val y: Int = (value[3].toInt() shl 8) + value[2]
            val z: Int = (value[5].toInt() shl 8) + value[4]
            listOf(x / scale, y / scale, z / scale)
        }
    }

    private val magnetometer = BleSensor("magnetometer", UUID_MOV_DATA, UUID_MOV_CONF, ENABLE_MOV_CODE).apply {
        convert = { value ->
            val scale = (32768 / 4912).toDouble()
            if (value.size >= 18) {
                val x: Int = (value[13].toInt() shl 8) + value[12]
                val y: Int = (value[15].toInt() shl 8) + value[14]
                val z: Int = (value[17].toInt() shl 8) + value[16]
                listOf(x / scale, y / scale, z / scale)
            } else {
                listOf(0.0, 0.0, 0.0)
            }
        }
    }

    private val luxometer = BleSensor("luxometer", UUID_OPT_DATA, UUID_OPT_CONF, ENABLE_CODE).apply {
        convert = { value ->
            val transformed: Int = value.shortUnsignedAtOffset(0)
            val mantissa = transformed and 0x0FFF
            val exponent = transformed shr 12 and 0xFF
            val output: Double
            val magnitude = 2.0f.pow(exponent).toDouble()
            output = mantissa * magnitude
            listOf(output / 100.0f)
        }
    }

    private val barometer = BleSensor("barometer", UUID_BAR_DATA, UUID_BAR_CONF, ENABLE_CODE).apply {
        convert = { value ->
            if (value.size > 4) {
                val result: Int = value.twentyFourBitUnsignedAtOffset(2)
                listOf(result.toDouble() / 100.0)
            } else {
                val mantissa: Int
                val exponent: Int
                val transformed: Int = value.shortUnsignedAtOffset(2)
                mantissa = transformed and 0x0FFF
                exponent = transformed shr 12 and 0xFF
                val output: Double
                val magnitude = 2.0f.pow(exponent).toDouble()
                output = mantissa * magnitude
                listOf(output / 100.0f)
            }
        }
    }
}
