package br.pucrio.inf.lac.mobilehub.core.helpers.components

import br.pucrio.inf.lac.mobilehub.core.MobileHubEvent
import br.pucrio.inf.lac.mobilehub.core.helpers.components.bus.Bus
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

internal object RxBus : Bus<MobileHubEvent> {
    private val publisher = PublishSubject.create<MobileHubEvent>()

    override fun publish(event: MobileHubEvent) = publisher.onNext(event)

    override fun <E : MobileHubEvent> on(eventType: Class<E>): Observable<E> = publisher.ofType(eventType)
}
