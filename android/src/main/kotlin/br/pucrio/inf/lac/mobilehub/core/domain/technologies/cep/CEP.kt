package br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep

import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.cep.CepQueryBadFormatException
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.general.DuplicateException

interface CEP {
    var listener: CEPListener?

    @Throws(CepQueryBadFormatException::class, DuplicateException::class)
    fun registerQuery(query: CepQuery)

    @Throws(CepQueryBadFormatException::class, DuplicateException::class)
    fun updateQuery(queryName: String, updateQuery: CepQuery)

    fun removeQuery(queryName: String)

    fun registerEventType(name: String, clazz: Class<*>)

    fun registerEventTypes(eventTypes: List<Pair<String, Class<*>>>)

    fun processEvent(event: Any)

    fun release()
}
