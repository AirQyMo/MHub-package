package br.pucrio.inf.lac.mobilehub.core.data.processing

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEP
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEPListener
import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import timber.log.Timber

internal object DefaultCEP : CEP {
    override var listener: CEPListener? = null

    override fun registerQuery(query: CepQuery) = Timber.i("registerQuery: ${query.name}")

    override fun updateQuery(queryName: String, updateQuery: CepQuery) = Timber.i("updateQuery: $queryName")

    override fun removeQuery(queryName: String) = Timber.i("removeQuery: $queryName")

    override fun registerEventType(name: String, clazz: Class<*>) = Timber.i("registerEventType: $name")

    override fun registerEventTypes(eventTypes: List<Pair<String, Class<*>>>) = Timber.i("registerEventTypes")

    override fun processEvent(event: Any) = Timber.i("newEvent: $event")

    override fun release() = Timber.i("Default release")
}
