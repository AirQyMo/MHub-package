package br.pucrio.inf.lac.asper

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEPListener
import br.pucrio.inf.lac.mobilehub.core.domain.entities.Event
import com.espertech.esper.client.EventBean
import com.espertech.esper.client.UpdateListener
import timber.log.Timber

internal class EventsListener(
    private val queryName: String,
    var listener: CEPListener?
) : UpdateListener {
    override fun update(newEvents: Array<EventBean>, oldEvents: Array<EventBean>?) {
        for (event in newEvents) {
            val eventObject = event.underlying
            val eventData = Event(queryName, eventObject)
            Timber.i("Event ($queryName) received: $eventObject")
            listener?.onNewEvent(eventData)
        }
    }
}
