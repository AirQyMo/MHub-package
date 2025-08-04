package br.pucrio.inf.lac.mobilehub.core.domain.usecases.contextsensor

import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.ContextSensorDataRepository
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler
import javax.inject.Inject

internal class SubscribeToContextSensorDataUseCase @Inject constructor(
    private val repository: ContextSensorDataRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : FlowableUseCase<List<SensorData>, Unit>(backgroundScheduler, foregroundScheduler) {
    override fun generateFlowable(input: Unit?): Flowable<List<SensorData>> =
        repository.subscribe()
}
