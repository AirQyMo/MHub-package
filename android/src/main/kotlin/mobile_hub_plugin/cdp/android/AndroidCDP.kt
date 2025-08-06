package br.pucrio.inf.lac.cdp.android

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import br.pucrio.inf.lac.cdp.android.sensors.*
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp.ContextDataListener
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp.ContextDataProvider
import br.pucrio.inf.lac.mobilehub.core.helpers.extensions.kotlin.isNotNull
import br.pucrio.inf.lac.mobilehub.core.helpers.extensions.kotlin.isNull
import timber.log.Timber

class AndroidCDP(context: Context) : ContextDataProvider, SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
    private val latestContext = mutableMapOf<String, List<Double>>()
    private val sensorTransformers = mapOf(
        Sensor.TYPE_LIGHT to LightSensor,
        Sensor.TYPE_ACCELEROMETER to AccelerometerSensor,
        Sensor.TYPE_LINEAR_ACCELERATION to AccelerometerSensor,
        Sensor.TYPE_AMBIENT_TEMPERATURE to TemperatureSensor,
        Sensor.TYPE_GRAVITY to GravitySensor,
        Sensor.TYPE_GYROSCOPE to GyroscopeSensor,
        Sensor.TYPE_PROXIMITY to ProximitySensor,
        Sensor.TYPE_PRESSURE to PressureSensor,
        Sensor.TYPE_RELATIVE_HUMIDITY to HumiditySensor,
        Sensor.TYPE_STEP_COUNTER to StepsSensor,
        Sensor.TYPE_MAGNETIC_FIELD to MagnetometerSensor,
        Sensor.TYPE_ROTATION_VECTOR to RotationSensor,
        Sensor.TYPE_GAME_ROTATION_VECTOR to RotationSensor
    )

    override var listener: ContextDataListener? = null

    override fun start() = sensors.forEach { sensor ->
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun stop() = sensorManager.unregisterListener(this)

    override val contextData: List<SensorData>
        get() = latestContext.map { SensorData(it.key, it.value) }

    class Builder(private val context: Context) {
        fun build(): ContextDataProvider = AndroidCDP(context)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val transformer = sensorTransformers[event.sensor.type] ?: return
        val newSensorData = transformer.convert(event.values)
        if (newSensorData != latestContext[transformer.name]) {
            latestContext[transformer.name] = newSensorData
            listener?.onContextChanged(contextData)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) =
        Timber.i("Accuracy(${sensor.name}): $accuracy")
}
