package br.pucrio.inf.lac.mobilehub.core.domain.usecases.cepquery

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEP
import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.general.NotFoundException
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.CepQueryRepository
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

internal class UpdateQueryUseCase @Inject constructor(
    private val repository: CepQueryRepository,
    private val cepTechnology: CEP,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : SingleUseCase<Long, UpdateQueryUseCase.Params>(backgroundScheduler, foregroundScheduler) {
    private lateinit var currentQueryName: String

    override fun generateSingle(input: Params?): Single<Long> {
        requireNotNull(input) { "Parameter can't be null" }

        return repository.findById(input.id)
            .switchIfEmpty(Single.error(NotFoundException("Cep query not found")))
            .map {
                currentQueryName = it.name
                it.update(input.query)
            }
            .flatMap {
                cepTechnology.updateQuery(currentQueryName, it)
                repository.save(it)
            }
    }

    private fun CepQuery.update(query: CepQuery) = apply {
        name = query.name
        statement = query.statement
    }

    class Params(val id: Long, val query: CepQuery)
}