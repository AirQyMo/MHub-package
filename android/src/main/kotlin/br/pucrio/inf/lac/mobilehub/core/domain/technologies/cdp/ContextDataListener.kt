package br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp

import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData

interface ContextDataListener {
    fun onContextChanged(context: List<SensorData>)
}
