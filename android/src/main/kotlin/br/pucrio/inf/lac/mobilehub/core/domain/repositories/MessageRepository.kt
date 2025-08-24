package br.pucrio.inf.lac.mobilehub.core.domain.repositories

import br.pucrio.inf.lac.mobilehub.core.domain.entities.Message
import br.pucrio.inf.lac.mobilehub.core.domain.entities.base.Either
import io.reactivex.Flowable

internal interface MessageRepository {
    fun connect(): Flowable<Either<Message, Exception>>
}