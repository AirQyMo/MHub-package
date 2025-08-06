package br.pucrio.inf.lac.mobilehub.core.data.remote.source

import br.pucrio.inf.lac.mobilehub.core.data.repositories.MobileObjectDriverRemoteDataSource
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import br.pucrio.inf.lac.mobilehub.core.gateways.connection.base.Envelope
import dagger.Reusable
import io.reactivex.Completable
import javax.inject.Inject

@Reusable
internal class MobileObjectDriverRemoteDataSourceImpl @Inject constructor(
    private val wlanTechnology: WLAN,
) : MobileObjectDriverRemoteDataSource {
    private object Action {
        const val GET_DRIVER = "getDriver"
    }

    private object Key {
        const val WPAN = "wpan"
        const val NAME = "name"
    }

    override fun requestDriver(wpan: Int, name: String): Completable {
        val body = mapOf<String, Any>(
            Key.WPAN to wpan,
            Key.NAME to name
        )

        val request = Envelope(
            action = Action.GET_DRIVER,
            body = body
        )

        return wlanTechnology.publishRequest(Topic.Driver, request)
    }

    /*override fun fetch(wpan: Int, name: String): Single<MobileObjectDriverDto> {
        /*val body = mapOf(
            Key.WPAN to wpan,
            Key.NAME to name
        )*/
        if (name == "CC2650 SensorTag") {
            return Single.just(
                MobileObjectDriverDto(
                    id = 1,
                    wpan = 1,
                    name = "CC2650 SensorTag",
                    content = "require 'bytes'\n" +
                            "\n" +
                            "local UUID = luajava.bindClass('java.util.UUID')\n" +
                            "local Sensor = luajava.bindClass('br.pucrio.inf.lac.ble.device.BleSensor')\n" +
                            "\n" +
                            "CC2650SensorTag = {}\n" +
                            "\n" +
                            "function CC2650SensorTag:new()\n" +
                            "    local uuidIrtData = UUID:fromString(\"f000aa01-0451-4000-b000-000000000000\")\n" +
                            "    local uuidIrtConf = UUID:fromString(\"f000aa02-0451-4000-b000-000000000000\")\n" +
                            "\n" +
                            "    local uuidHumData = UUID:fromString(\"f000aa21-0451-4000-b000-000000000000\")\n" +
                            "    local uuidHumConf = UUID:fromString(\"f000aa22-0451-4000-b000-000000000000\")\n" +
                            "\n" +
                            "    local uuidOptData = UUID:fromString(\"f000aa71-0451-4000-b000-000000000000\")\n" +
                            "    local uuidOptConf = UUID:fromString(\"f000aa72-0451-4000-b000-000000000000\")\n" +
                            "\n" +
                            "    local uuidBarData = UUID:fromString(\"f000aa41-0451-4000-b000-000000000000\")\n" +
                            "    local uuidBarConf = UUID:fromString(\"f000aa42-0451-4000-b000-000000000000\")\n" +
                            "\n" +
                            "    local uuidMovData = UUID:fromString(\"f000aa81-0451-4000-b000-000000000000\")\n" +
                            "    local uuidMovConf = UUID:fromString(\"f000aa82-0451-4000-b000-000000000000\")\n" +
                            "\n" +
                            "    local enableCode = string.char(1)\n" +
                            "    local enableMovCode = string.char(0x7F, 0x00)\n" +
                            "\n" +
                            "    local function extractAmbientTemperature(bytes)\n" +
                            "        local offset = 3\n" +
                            "        return shortUnsignedAtOffset(bytes, offset) / 128\n" +
                            "    end\n" +
                            "\n" +
                            "    local function extractTargetTemperature(bytes, ambient)\n" +
                            "        local twoByteValue = shortSignedAtOffset(bytes, 1)\n" +
                            "        local vObj2 = twoByteValue * 0.00000015625\n" +
                            "        local tDie = ambient + 273.15\n" +
                            "        local s0 = 5.593E-14 -- Calibration factor\n" +
                            "        local a1 = 1.75E-3\n" +
                            "        local a2 = -1.678E-5\n" +
                            "        local b0 = -2.94E-5\n" +
                            "        local b1 = -5.7E-7\n" +
                            "        local b2 = 4.63E-9\n" +
                            "        local c2 = 13.4\n" +
                            "        local tRef = 298.15\n" +
                            "        local s = s0 * (1 + a1 * (tDie - tRef) + a2 *  math.pow(tDie - tRef, 2))\n" +
                            "        local vOs = b0 + b1 * (tDie - tRef) + b2 * math.pow(tDie - tRef, 2)\n" +
                            "        local fObj = vObj2 - vOs + c2 * math.pow(vObj2 - vOs, 2)\n" +
                            "        local tObj = math.pow(math.pow(tDie, 4) + fObj / s, 0.25)\n" +
                            "        return tObj - 273.15\n" +
                            "    end\n" +
                            "\n" +
                            "    local function extractTargetTemperatureTMP007(bytes)\n" +
                            "        local offset = 1\n" +
                            "        return shortUnsignedAtOffset(bytes, offset) / 128.0\n" +
                            "    end\n" +
                            "\n" +
                            "    local function convertTemperature(value)\n" +
                            "        local ambient = extractAmbientTemperature(value)\n" +
                            "        local target = extractTargetTemperature(value, ambient)\n" +
                            "        local targetNewSensor = extractTargetTemperatureTMP007(value)\n" +
                            "\n" +
                            "        return { ambient, target, targetNewSensor }\n" +
                            "    end\n" +
                            "\n" +
                            "    local function convertHumidity(value)\n" +
                            "        local a = shortUnsignedAtOffset(value, 3)\n" +
                            "        return { 100.0 * (a / 65535.0) }\n" +
                            "    end\n" +
                            "\n" +
                            "    local function convertAccelerometer(value)\n" +
                            "        local scale = 4096.0\n" +
                            "        local x = lshift(value[8], 8) + value[7]\n" +
                            "        local y = lshift(value[10], 8) + value[9]\n" +
                            "        local z = lshift(value[12], 8) + value[11]\n" +
                            "\n" +
                            "        return { x / scale * -1, y / scale, z / scale * -1 }\n" +
                            "    end\n" +
                            "\n" +
                            "    local function convertGyroscope(value)\n" +
                            "        local scale = 128.0\n" +
                            "        local x = lshift(value[2], 8) + value[1]\n" +
                            "        local y = lshift(value[4], 8) + value[3]\n" +
                            "        local z = lshift(value[6], 8) + value[5]\n" +
                            "\n" +
                            "        return { x / scale, y / scale, z / scale }\n" +
                            "    end\n" +
                            "\n" +
                            "    local function convertMagnetometer(value)\n" +
                            "        local scale = math.floor(32768 / 4912)\n" +
                            "        if #value >= 18 then\n" +
                            "            local x = lshift(value[14], 8) + value[13]\n" +
                            "            local y = lshift(value[16], 8) + value[15]\n" +
                            "            local z = lshift(value[18], 8) + value[17]\n" +
                            "            return { x / scale, y / scale, z / scale }\n" +
                            "        end\n" +
                            "\n" +
                            "        return { 0.0, 0.0, 0.0 }\n" +
                            "    end\n" +
                            "\n" +
                            "    local function convertLuxometer(value)\n" +
                            "        local transformed = shortUnsignedAtOffset(value, 1)\n" +
                            "        local mantissa = bitoper(transformed, 0x0FFF, AND)\n" +
                            "        local exponent = bitoper(rshift(transformed, 12), 0xFF, AND)\n" +
                            "        local magnitude = math.pow(2.0, exponent)\n" +
                            "        local output = mantissa * magnitude\n" +
                            "        return { output / 100.0 }\n" +
                            "    end\n" +
                            "\n" +
                            "    local function convertBarometer(value)\n" +
                            "        if #value > 4 then\n" +
                            "            local result = twentyFourBitUnsignedAtOffset(value, 3)\n" +
                            "            return { result / 100 }\n" +
                            "        else\n" +
                            "            local transformed = shortUnsignedAtOffset(value, 3)\n" +
                            "            local mantissa = bitoper(transformed, 0x0FFF, AND)\n" +
                            "            local exponent = bitoper(rshift(transformed, 12), 0xFF, AND)\n" +
                            "            local magnitude = math.pow(2.0, exponent)\n" +
                            "            local output = mantissa * magnitude\n" +
                            "            return { output / 100.0 }\n" +
                            "        end\n" +
                            "    end\n" +
                            "\n" +
                            "    local this = {\n" +
                            "        name = \"CC2650 SensorTag\",\n" +
                            "        sensors = {\n" +
                            "            Sensor.new(\"temperature\", uuidIrtData, uuidIrtConf, enableCode),\n" +
                            "            Sensor.new(\"humidity\", uuidHumData, uuidHumConf, enableCode),\n" +
                            "            Sensor.new(\"accelerometer\", uuidMovData, uuidMovConf, enableMovCode),\n" +
                            "            Sensor.new(\"gyroscope\", uuidMovData, uuidMovConf, enableMovCode),\n" +
                            "            Sensor.new(\"magnetometer\", uuidMovData, uuidMovConf, enableMovCode),\n" +
                            "            Sensor.new(\"luxometer\", uuidOptData, uuidOptConf, enableCode),\n" +
                            "            Sensor.new(\"barometer\", uuidBarData, uuidBarConf, enableCode)\n" +
                            "        },\n" +
                            "        converters = {\n" +
                            "            convertTemperature,\n" +
                            "            convertHumidity,\n" +
                            "            convertAccelerometer,\n" +
                            "            convertGyroscope,\n" +
                            "            convertMagnetometer,\n" +
                            "            convertLuxometer,\n" +
                            "            convertBarometer\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    function this:toInput(value)\n" +
                            "        return { string.byte(value, 1,-1) }\n" +
                            "    end\n" +
                            "\n" +
                            "    return this\n" +
                            "end\n" +
                            "\n" +
                            "return CC2650SensorTag:new()"
                ))
        } else {
            return Single.error(NotFoundException())
        }
    }*/
}