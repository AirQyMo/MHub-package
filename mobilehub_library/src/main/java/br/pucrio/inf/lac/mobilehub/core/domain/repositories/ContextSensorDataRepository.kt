package br.pucrio.inf.lac.mobilehub.core.domain.repositories

import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import io.reactivex.Flowable

internal interface ContextSensorDataRepository {
    fun subscribe(): Flowable<List<SensorData>>
}
