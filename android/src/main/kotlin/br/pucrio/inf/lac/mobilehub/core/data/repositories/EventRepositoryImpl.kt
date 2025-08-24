package br.pucrio.inf.lac.mobilehub.core.data.repositories

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEP
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEPListener
import br.pucrio.inf.lac.mobilehub.core.domain.entities.Event
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.EventRepository
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import dagger.Reusable
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

@Reusable
internal class EventRepositoryImpl @Inject constructor(
    private val cepTechnology: CEP,
    private val wlanTechnology: WLAN
) : EventRepository {
    private val subject: PublishSubject<Event> = PublishSubject.create()

    private inner class Listener : CEPListener {
        override fun onNewEvent(event: Event) = subject.onNext(event)
    }

    override fun getEvents(): Flowable<Event> {
        cepTechnology.listener = Listener()
        return subject.toFlowable(BackpressureStrategy.LATEST)
            .doOnTerminate { cepTechnology.release() }
            .andPublish(Topic.Cep)
    }

    private fun <T : Any> Flowable<T>.andPublish(topic: Topic) = doOnNext {
        wlanTechnology.publishMessage(topic, it)
    }
}
