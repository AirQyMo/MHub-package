package br.pucrio.inf.lac.cdp.android.sensors

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp.ContextSensor

object TemperatureSensor : ContextSensor {
    override val name: String = "temperature"

    override fun convert(data: FloatArray): List<Double> {
        val value: Float = data[0]
        return listOf(value.toDouble())
    }
}
