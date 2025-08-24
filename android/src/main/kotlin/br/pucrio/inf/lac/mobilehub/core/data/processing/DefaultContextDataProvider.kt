package br.pucrio.inf.lac.mobilehub.core.data.processing

import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp.ContextDataListener
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp.ContextDataProvider
import timber.log.Timber

internal object DefaultContextDataProvider: ContextDataProvider {
    override val contextData: List<SensorData> = listOf()

    override var listener: ContextDataListener? = null

    override fun start() = Timber.i("start")

    override fun stop() = Timber.i("stop")
}
