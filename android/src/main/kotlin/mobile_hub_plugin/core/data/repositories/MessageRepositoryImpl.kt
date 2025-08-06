package br.pucrio.inf.lac.mobilehub.core.data.repositories

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLANListener
import br.pucrio.inf.lac.mobilehub.core.domain.entities.Message
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.technology.TechnologyDisconnectedException
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MessageRepository
import br.pucrio.inf.lac.mobilehub.core.domain.entities.base.Either
import dagger.Reusable
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

@Reusable
internal class MessageRepositoryImpl @Inject constructor(
    private val wlanTechnology: WLAN
) : MessageRepository {
    private val subject: PublishSubject<Either<Message, Exception>> = PublishSubject.create()

    private inner class Listener : WLANListener {
        override fun onConnected() = Timber.i("Connected")

        override fun onDisconnected() = subject.onNext(Either.Right(TechnologyDisconnectedException()))

        override fun onNewMessage(message: Message) = subject.onNext(Either.Left(message))
    }

    override fun connect(): Flowable<Either<Message, Exception>> {
        wlanTechnology.connect()
        wlanTechnology.listener = Listener()
        return subject.toFlowable(BackpressureStrategy.LATEST)
            .doOnTerminate { wlanTechnology.disconnect() }
    }
}