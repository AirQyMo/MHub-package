package br.pucrio.inf.lac.mobilehub.core.domain.usecases.cepquery

import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.CepQueryRepository
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.MaybeUseCase
import io.reactivex.Maybe
import io.reactivex.Scheduler
import javax.inject.Inject

internal class GetQueryUseCase @Inject constructor(
    private val repository: CepQueryRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : MaybeUseCase<CepQuery, Long>(backgroundScheduler, foregroundScheduler) {
    override fun generateMaybe(input: Long?): Maybe<CepQuery> {
        requireNotNull(input) { "ID can't be null" }
        return repository.findById(input)
    }
}