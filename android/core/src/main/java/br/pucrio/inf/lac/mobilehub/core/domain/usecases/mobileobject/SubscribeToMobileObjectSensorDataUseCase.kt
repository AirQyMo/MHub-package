package br.pucrio.inf.lac.mobilehub.core.domain.usecases.mobileobject

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MobileObjectRepository
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler
import javax.inject.Inject

internal class SubscribeToMobileObjectSensorDataUseCase @Inject constructor(
    private val repository: MobileObjectRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : FlowableUseCase<SensorData, MobileObject>(backgroundScheduler, foregroundScheduler) {
    override fun generateFlowable(input: MobileObject?): Flowable<SensorData> {
        requireNotNull(input) { "Parameter can't be null" }

        return repository.subscribe(input)
    }
}
