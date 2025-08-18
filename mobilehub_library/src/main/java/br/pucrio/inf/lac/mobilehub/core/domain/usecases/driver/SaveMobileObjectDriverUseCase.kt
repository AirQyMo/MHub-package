package br.pucrio.inf.lac.mobilehub.core.domain.usecases.driver

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObjectDriver
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MobileObjectDriverRepository
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

internal class SaveMobileObjectDriverUseCase @Inject constructor(
    private val repository: MobileObjectDriverRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : SingleUseCase<Long, MobileObjectDriver>(backgroundScheduler, foregroundScheduler) {
    override fun generateSingle(input: MobileObjectDriver?): Single<Long> {
        requireNotNull(input) { "Driver can't be null" }
        return repository.save(input)
    }
}