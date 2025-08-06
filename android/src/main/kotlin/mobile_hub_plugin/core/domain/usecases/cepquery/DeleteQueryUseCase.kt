package br.pucrio.inf.lac.mobilehub.core.domain.usecases.cepquery

import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.general.NotFoundException
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.CepQueryRepository
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import javax.inject.Inject

internal class DeleteQueryUseCase @Inject constructor(
    private val repository: CepQueryRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : CompletableUseCase<Long>(backgroundScheduler, foregroundScheduler) {
    override fun generateCompletable(input: Long?): Completable {
        requireNotNull(input) { "ID can't be null" }

        return repository.findById(input)
            .switchIfEmpty(Maybe.error(NotFoundException("Cep query not found")))
            .flatMapCompletable {
                repository.delete(input)
            }
    }
}