package br.pucrio.inf.lac.cdp.android.sensors

import android.hardware.SensorManager
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp.ContextSensor

object RotationSensor : ContextSensor {
    override val name: String = "rotation"

    override fun convert(data: FloatArray): List<Double> {
        val rotMatrix = FloatArray(9)
        val rotValues = FloatArray(3)

        SensorManager.getRotationMatrixFromVector(rotMatrix, data)
        SensorManager.remapCoordinateSystem(rotMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, rotMatrix)
        SensorManager.getOrientation(rotMatrix, rotValues)

        val azimuth = Math.toDegrees(rotValues[0].toDouble()).toFloat()
        val pitch = Math.toDegrees(rotValues[1].toDouble()).toFloat()
        val roll = Math.toDegrees(rotValues[2].toDouble()).toFloat()
        return listOf(azimuth.toDouble(), pitch.toDouble(), roll.toDouble())
    }
}
