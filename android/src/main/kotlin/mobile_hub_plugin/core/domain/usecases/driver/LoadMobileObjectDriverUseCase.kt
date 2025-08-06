package br.pucrio.inf.lac.mobilehub.core.domain.usecases.driver

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObjectDriver
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MobileObjectDriverRepository
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.MaybeUseCase
import io.reactivex.Maybe
import io.reactivex.Scheduler
import javax.inject.Inject

internal class LoadMobileObjectDriverUseCase @Inject constructor(
    private val repository: MobileObjectDriverRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : MaybeUseCase<MobileObjectDriver, LoadMobileObjectDriverUseCase.Params>(backgroundScheduler, foregroundScheduler) {
    override fun generateMaybe(input: Params?): Maybe<MobileObjectDriver> {
        requireNotNull(input) { "Params can't be null" }
        return repository.loadByName(input.wpan, input.name)
    }

    class Params(
        val wpan: Int,
        val name: String
    )
}