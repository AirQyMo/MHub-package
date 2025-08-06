package br.pucrio.inf.lac.ble.transcoder.lua

import br.pucrio.inf.lac.ble.device.BleDevice
import br.pucrio.inf.lac.ble.device.BleSensor
import br.pucrio.inf.lac.ble.extensions.asDoubleList
import br.pucrio.inf.lac.ble.extensions.asLua
import br.pucrio.inf.lac.ble.extensions.toList
import br.pucrio.inf.lac.ble.transcoder.Transcoder
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.Bit32Lib
import org.luaj.vm2.lib.PackageLib
import org.luaj.vm2.lib.jse.JseBaseLib
import org.luaj.vm2.lib.jse.JseMathLib

open class LuaTranscoder(private val globals: Globals) : Transcoder {
    private object Script {
        const val BYTES = "bytes.lua"
    }

    private object Attribute {
        const val NAME = "name"
        const val SENSORS = "sensors"
        const val CONVERTERS = "converters"
    }

    private object Method {
        const val TO_INPUT = "toInput"
    }

    init {
        loadLibraries()
    }

    private fun loadLibraries() = with(globals) {
        load(JseBaseLib())
        load(PackageLib())
        load(Bit32Lib())
        load(JseMathLib())
        load(LuaKotlinLib())
        loadfile(Script.BYTES)
    }

    override fun convert(encodedDriver: String): BleDevice {
        val chunk = globals.load(encodedDriver)
        val driver = chunk.call()
        val sensors = driver.loadSensors()

        return object : BleDevice {
            override val name: String = driver.get(Attribute.NAME).checkjstring()

            override val sensors = sensors
                .map { it.name to it }
                .toMap()
        }
    }

    private fun LuaValue.loadSensors(): List<BleSensor> {
        val sensors = get(Attribute.SENSORS).checktable()
        val converters = get(Attribute.CONVERTERS).checktable()

        return sensors.toList { key, sensor ->
            val converter = converters[key]
            sensor.convert = { value -> convert(converter, value) }
            sensor
        }
    }

    private fun LuaValue.convert(converter: LuaValue, value: ByteArray): List<Double> {
        val input = invokemethod(Method.TO_INPUT, value.asLua)
        val result = converter.invoke(input)
        return result.arg1().checktable().asDoubleList
    }
}
