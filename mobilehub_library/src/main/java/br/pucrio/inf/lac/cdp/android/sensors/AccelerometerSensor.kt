package br.pucrio.inf.lac.cdp.android.sensors

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp.ContextSensor

object AccelerometerSensor : ContextSensor {
    override val name: String = "accelerometer"

    override fun convert(data: FloatArray): List<Double> {
        val x: Float = data[0]
        val y: Float = data[1]
        val z: Float = data[2]
        return listOf(x.toDouble(), y.toDouble(), z.toDouble())
    }
}
