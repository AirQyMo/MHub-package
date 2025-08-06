package br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp

import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData

interface ContextDataProvider {
    val contextData: List<SensorData>

    var listener: ContextDataListener?

    fun start()

    fun stop()
}
