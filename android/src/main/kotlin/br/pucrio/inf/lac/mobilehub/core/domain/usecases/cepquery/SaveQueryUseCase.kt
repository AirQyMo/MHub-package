package br.pucrio.inf.lac.mobilehub.core.domain.usecases.cepquery

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEP
import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.CepQueryRepository
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

internal class SaveQueryUseCase @Inject constructor(
    private val repository: CepQueryRepository,
    private val cepTechnology: CEP,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : SingleUseCase<Long, CepQuery>(backgroundScheduler, foregroundScheduler) {
    override fun generateSingle(input: CepQuery?): Single<Long> {
        requireNotNull(input) { "CepQuery can't be null" }

        return try {
            cepTechnology.registerQuery(input)
            repository.save(input)
        } catch (ex: Exception) {
            Single.error(ex)
        }
    }
}