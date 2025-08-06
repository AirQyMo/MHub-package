package br.pucrio.inf.lac.mobilehub.core.domain.usecases.event

import br.pucrio.inf.lac.mobilehub.core.domain.entities.Event
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.EventRepository
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler
import javax.inject.Inject

internal class ListenEventsUseCase @Inject constructor(
    private val repository: EventRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : FlowableUseCase<Event, Void>(backgroundScheduler, foregroundScheduler) {
    override fun generateFlowable(input: Void?): Flowable<Event> = repository.getEvents()
}