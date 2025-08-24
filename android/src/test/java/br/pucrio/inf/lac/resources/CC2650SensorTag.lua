require 'bytes'

local UUID = luajava.bindClass('java.util.UUID')
local Sensor = luajava.bindClass('br.pucrio.inf.lac.ble.device.BleSensor')

CC2650SensorTag = {}

function CC2650SensorTag:new()
    local uuidIrtData = UUID:fromString("f000aa01-0451-4000-b000-000000000000")
    local uuidIrtConf = UUID:fromString("f000aa02-0451-4000-b000-000000000000")

    local uuidHumData = UUID:fromString("f000aa21-0451-4000-b000-000000000000")
    local uuidHumConf = UUID:fromString("f000aa22-0451-4000-b000-000000000000")

    local uuidOptData = UUID:fromString("f000aa71-0451-4000-b000-000000000000")
    local uuidOptConf = UUID:fromString("f000aa72-0451-4000-b000-000000000000")

    local uuidBarData = UUID:fromString("f000aa41-0451-4000-b000-000000000000")
    local uuidBarConf = UUID:fromString("f000aa42-0451-4000-b000-000000000000")

    local uuidMovData = UUID:fromString("f000aa81-0451-4000-b000-000000000000")
    local uuidMovConf = UUID:fromString("f000aa82-0451-4000-b000-000000000000")

    local enableCode = string.char(1)
    local enableMovCode = string.char(0x7F, 0x00)

    local function extractAmbientTemperature(bytes)
        local offset = 3
        return shortUnsignedAtOffset(bytes, offset) / 128
    end

    local function extractTargetTemperature(bytes, ambient)
        local twoByteValue = shortSignedAtOffset(bytes, 1)
        local vObj2 = twoByteValue * 0.00000015625
        local tDie = ambient + 273.15
        local s0 = 5.593E-14 -- Calibration factor
        local a1 = 1.75E-3
        local a2 = -1.678E-5
        local b0 = -2.94E-5
        local b1 = -5.7E-7
        local b2 = 4.63E-9
        local c2 = 13.4
        local tRef = 298.15
        local s = s0 * (1 + a1 * (tDie - tRef) + a2 *  math.pow(tDie - tRef, 2))
        local vOs = b0 + b1 * (tDie - tRef) + b2 * math.pow(tDie - tRef, 2)
        local fObj = vObj2 - vOs + c2 * math.pow(vObj2 - vOs, 2)
        local tObj = math.pow(math.pow(tDie, 4) + fObj / s, 0.25)
        return tObj - 273.15
    end

    local function extractTargetTemperatureTMP007(bytes)
        local offset = 1
        return shortUnsignedAtOffset(bytes, offset) / 128.0
    end

    local function convertTemperature(value)
        local ambient = extractAmbientTemperature(value)
        local target = extractTargetTemperature(value, ambient)
        local targetNewSensor = extractTargetTemperatureTMP007(value)

        return { ambient, target, targetNewSensor }
    end

    local function convertHumidity(value)
        local a = shortUnsignedAtOffset(value, 3)
        return { 100.0 * (a / 65535.0) }
    end

    local function convertAccelerometer(value)
        local scale = 4096.0
        local x = lshift(value[8], 8) + value[7]
        local y = lshift(value[10], 8) + value[9]
        local z = lshift(value[12], 8) + value[11]

        return { x / scale * -1, y / scale, z / scale * -1 }
    end

    local function convertGyroscope(value)
        local scale = 128.0
        local x = lshift(value[2], 8) + value[1]
        local y = lshift(value[4], 8) + value[3]
        local z = lshift(value[6], 8) + value[5]

        return { x / scale, y / scale, z / scale }
    end

    local function convertMagnetometer(value)
        local scale = math.floor(32768 / 4912)
        if #value >= 18 then
            local x = lshift(value[14], 8) + value[13]
            local y = lshift(value[16], 8) + value[15]
            local z = lshift(value[18], 8) + value[17]
            return { x / scale, y / scale, z / scale }
        end

        return { 0.0, 0.0, 0.0 }
    end

    local function convertLuxometer(value)
        local transformed = shortUnsignedAtOffset(value, 1)
        local mantissa = bitoper(transformed, 0x0FFF, AND)
        local exponent = bitoper(rshift(transformed, 12), 0xFF, AND)
        local magnitude = math.pow(2.0, exponent)
        local output = mantissa * magnitude
        return { output / 100.0 }
    end

    local function convertBarometer(value)
        if #value > 4 then
            local result = twentyFourBitUnsignedAtOffset(value, 3)
            return { result / 100 }
        else
            local transformed = shortUnsignedAtOffset(value, 3)
            local mantissa = bitoper(transformed, 0x0FFF, AND)
            local exponent = bitoper(rshift(transformed, 12), 0xFF, AND)
            local magnitude = math.pow(2.0, exponent)
            local output = mantissa * magnitude
            return { output / 100.0 }
        end
    end

    local this = {
        name = "CC2650 SensorTag",
        sensors = {
            Sensor.new("temperature", uuidIrtData, uuidIrtConf, enableCode),
            Sensor.new("humidity", uuidHumData, uuidHumConf, enableCode),
            Sensor.new("accelerometer", uuidMovData, uuidMovConf, enableMovCode),
            Sensor.new("gyroscope", uuidMovData, uuidMovConf, enableMovCode),
            Sensor.new("magnetometer", uuidMovData, uuidMovConf, enableMovCode),
            Sensor.new("luxometer", uuidOptData, uuidOptConf, enableCode),
            Sensor.new("barometer", uuidBarData, uuidBarConf, enableCode)
        },
        converters = {
            convertTemperature,
            convertHumidity,
            convertAccelerometer,
            convertGyroscope,
            convertMagnetometer,
            convertLuxometer,
            convertBarometer
        }
    }

    function this:toInput(value)
        return { string.byte(value, 1,-1) }
    end

    return this
end

return CC2650SensorTag:new()