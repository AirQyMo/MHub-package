package br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp

interface ContextSensor {
    val name: String

    fun convert(data: FloatArray): List<Double>
}
