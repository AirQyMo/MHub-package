package br.pucrio.inf.lac.mobilehub.core.domain.usecases.message

import br.pucrio.inf.lac.mobilehub.core.domain.entities.Message
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MessageRepository
import br.pucrio.inf.lac.mobilehub.core.domain.entities.base.Either
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

internal class ConnectUseCase @Inject constructor(
    private val repository: MessageRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : FlowableUseCase<Message, Void>(backgroundScheduler, foregroundScheduler) {
    override fun generateFlowable(input: Void?): Flowable<Message> {
        return repository.connect()
            .flatMap { it.flat() }
    }

    private fun Either<Message, Exception>.flat() = when (this) {
        is Either.Left -> Flowable.just(a)
        is Either.Right -> {
            Timber.e(b)
            Flowable.empty()
        }
    }
}