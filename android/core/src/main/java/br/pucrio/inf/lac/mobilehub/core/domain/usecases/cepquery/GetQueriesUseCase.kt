package br.pucrio.inf.lac.mobilehub.core.domain.usecases.cepquery

import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.CepQueryRepository
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

internal class GetQueriesUseCase @Inject constructor(
    private val repository: CepQueryRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : SingleUseCase<List<CepQuery>, Void>(backgroundScheduler, foregroundScheduler) {
    override fun generateSingle(input: Void?): Single<List<CepQuery>> = repository.list()
}