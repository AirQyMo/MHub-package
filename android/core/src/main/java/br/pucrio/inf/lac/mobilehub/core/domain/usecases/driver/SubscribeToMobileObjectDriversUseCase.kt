package br.pucrio.inf.lac.mobilehub.core.domain.usecases.driver

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObjectDriver
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MobileObjectDriverRepository
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler
import javax.inject.Inject

internal class SubscribeToMobileObjectDriversUseCase @Inject constructor(
    private val repository: MobileObjectDriverRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : FlowableUseCase<MobileObjectDriver, Void>(backgroundScheduler, foregroundScheduler) {
    override fun generateFlowable(input: Void?): Flowable<MobileObjectDriver> = repository.subscribeToLatest()
}