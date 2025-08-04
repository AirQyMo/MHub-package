package br.pucrio.inf.lac.mobilehub.core.domain.entities

data class SensorData(
    val serviceName: String,
    val serviceData: List<Double>
) {
    companion object {
        const val NAME = "SensorData"
    }

    lateinit var mobileObjectId: Moid
}