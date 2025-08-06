package br.pucrio.inf.lac.mobilehub.core.domain.repositories

import br.pucrio.inf.lac.mobilehub.core.domain.entities.Event
import io.reactivex.Flowable

internal interface EventRepository {
    fun getEvents(): Flowable<Event>
}