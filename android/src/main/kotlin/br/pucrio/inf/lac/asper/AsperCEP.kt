package br.pucrio.inf.lac.asper

import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.cep.CepQueryBadFormatException
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.general.DuplicateException
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEP
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEPListener
import com.espertech.esper.client.*
import java.util.concurrent.ConcurrentHashMap

class AsperCEP(
    queries: List<CepQuery>,
    eventTypes: List<Pair<String, Class<*>>>
) : CEP {
    private var provider: EPServiceProvider

    private var administrator: EPAdministrator

    private var engine: EPRuntime

    private val cepQueries: ConcurrentHashMap<String, EPStatement> = ConcurrentHashMap()

    private val eventListeners = mutableListOf<EventsListener>()

    override var listener: CEPListener? = null
        set(value) {
            eventListeners.forEach {
                it.listener = value
            }

            field = value
        }

    init {
        val configuration = Configuration().apply { addEventTypes(eventTypes) }
        provider = EPServiceProviderManager.getDefaultProvider(configuration)
        administrator = provider.epAdministrator
        engine = provider.epRuntime

        queries.forEach(::registerQuery)
    }

    @Throws(CepQueryBadFormatException::class, DuplicateException::class)
    override fun registerQuery(query: CepQuery) {
        validateQueryNameNoDuplicate(query.name)
        attemptToDeployQuery(query)
    }

    @Throws(DuplicateException::class)
    private fun validateQueryNameNoDuplicate(queryName: String) {
        if (cepQueries.containsKey(queryName)) {
            throw DuplicateException("The cep query already exists")
        }
    }

    @Throws(CepQueryBadFormatException::class)
    private fun attemptToDeployQuery(query: CepQuery) {
        try {
            val queryName = query.name
            val statement = administrator.createEPL(query.statement, queryName)
            val listener = EventsListener(queryName, listener)
            statement.addListener(listener)
            eventListeners.add(listener)
            cepQueries[queryName] = statement
        } catch (ex: EPException) {
            throw CepQueryBadFormatException(ex.message!!)
        }
    }

    @Throws(CepQueryBadFormatException::class, DuplicateException::class)
    override fun updateQuery(queryName: String, updateQuery: CepQuery) {
        removeQuery(queryName)
        registerQuery(updateQuery)
    }

    override fun removeQuery(queryName: String) {
        val statement = cepQueries.remove(queryName)
        statement?.removeAllListeners()
        statement?.destroy()
    }

    override fun registerEventType(name: String, clazz: Class<*>) {
        administrator.configuration.addEventType(name, clazz.name)
    }

    override fun registerEventTypes(eventTypes: List<Pair<String, Class<*>>>) {
        eventTypes.forEach { eventType ->
            registerEventType(eventType.first, eventType.second)
        }
    }

    override fun processEvent(event: Any) = engine.sendEvent(event)

    override fun release() {
        cepQueries.clear()
        administrator.stopAllStatements()
        administrator.destroyAllStatements()
        provider.removeAllStatementStateListeners()
        provider.removeAllServiceStateListeners()
        provider.destroy()
    }

    class Builder {
        private var queries = mutableListOf<CepQuery>()
        private var eventTypes = mutableListOf<Pair<String, Class<*>>>()

        fun addQuery(query: CepQuery) = this.also {
            this.queries.add(query)
        }

        fun setQueries(queries: List<CepQuery>) = this.also {
            this.queries = queries.toMutableList()
        }

        fun addEventType(name: String, clazz: Class<*>) = this.also {
            this.eventTypes.add(Pair(name, clazz))
        }

        fun setEventTypes(eventTypes: List<Pair<String, Class<*>>>) = this.also {
            this.eventTypes = eventTypes.toMutableList()
        }

        fun build() = AsperCEP(
            queries = queries,
            eventTypes = eventTypes
        )
    }
}
