package br.pucrio.inf.lac.mobilehub.core.gateways.mepa

import br.pucrio.inf.lac.mobilehub.core.MobileHubEvent
import br.pucrio.inf.lac.mobilehub.core.domain.entities.Event
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.event.ListenEventsUseCase
import br.pucrio.inf.lac.mobilehub.core.helpers.components.RxBus
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

@Reusable
internal class MEPAGateway @Inject constructor(
    private val listenEventsUseCase: ListenEventsUseCase
) {
    private val disposables = CompositeDisposable()

    fun start() {
        listenEventsUseCase()
            .subscribe(::onNewEvent, ::onError)
            .let { disposables += it }
    }

    private fun onNewEvent(event: Event) {
        RxBus.publish(MobileHubEvent.NewEvent(event))
        Timber.i("${event.queryName}: ${event.payload}")
    }

    private fun onError(throwable: Throwable) = Timber.e(throwable.localizedMessage)

    fun release() = disposables.clear()
}
