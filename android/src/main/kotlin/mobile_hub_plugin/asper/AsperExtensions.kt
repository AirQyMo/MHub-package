package br.pucrio.inf.lac.asper

import com.espertech.esper.client.Configuration

fun Configuration.addEventTypes(events: List<Pair<String, Class<*>>>) = events.forEach {
    addEventType(it.first, it.second.name)
}
