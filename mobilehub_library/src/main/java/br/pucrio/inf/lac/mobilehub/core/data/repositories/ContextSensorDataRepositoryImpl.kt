package br.pucrio.inf.lac.mobilehub.core.data.repositories

import br.pucrio.inf.lac.mobilehub.core.domain.entities.Moid
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.ContextSensorDataRepository
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp.ContextDataListener
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp.ContextDataProvider
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEP
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

internal class ContextSensorDataRepositoryImpl @Inject constructor(
    private val contextProvider: ContextDataProvider,
    private val cepTechnology: CEP
) : ContextSensorDataRepository {
    private val subject: PublishSubject<List<SensorData>> = PublishSubject.create()

    private inner class Listener : ContextDataListener {
        override fun onContextChanged(context: List<SensorData>) = subject.onNext(context)
    }

    override fun subscribe(): Flowable<List<SensorData>> {
        contextProvider.listener = Listener()
        return subject.toFlowable(BackpressureStrategy.LATEST)
            .doOnSubscribe { contextProvider.start() }
            .doOnTerminate { contextProvider.stop() }
            .map { it.addIdentifier() }
    }

    private fun List<SensorData>.addIdentifier() = map { sensorData ->
        sensorData.apply { mobileObjectId = Moid(0, "localhost") }
    }

    private fun Flowable<SensorData>.andProcess() = doOnNext {
        cepTechnology.processEvent(it)
    }
}
