package br.pucrio.inf.lac.mobilehub.core.data.repositories

import br.pucrio.inf.lac.mobilehub.core.data.buffer.BufferStrategy
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObjectDriver
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MobileObjectRepository
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEP
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wpan.WPAN
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

@Reusable
internal class MobileObjectRepositoryImpl @Inject constructor(
    private val bufferStrategy: BufferStrategy,
    private val wlanTechnology: WLAN,
    private val wpanTechnologies: HashMap<Int, WPAN>,
    private val cepTechnology: CEP
) : MobileObjectRepository {
    override fun scan(): Flowable<MobileObject> = wpanTechnologies.values
            .map { it.startScan().andPublish(Topic.Discovered) }
            .run { Flowable.merge(this) }

    override fun connect(mobileObject: MobileObject): Completable {
        val wpanTechnology = wpanTechnologies[mobileObject.wpan]!!
        return wpanTechnology.connect(mobileObject)
    }

    override fun connectWithDriver(mobileObject: MobileObject, driver: MobileObjectDriver): Completable {
        val wpanTechnology = wpanTechnologies[mobileObject.wpan]!!
        return wpanTechnology.connectWithDriver(mobileObject, driver)
    }

    override fun subscribe(mobileObject: MobileObject): Flowable<SensorData> {
        val wpanTechnology = wpanTechnologies[mobileObject.wpan]!!
        return wpanTechnology.subscribe(mobileObject)
            .addIdentifier(mobileObject)
            .andPublish(Topic.Data)
            .andProcess()
    }

    private fun Flowable<SensorData>.addIdentifier(mobileObject: MobileObject) = map { sensorData ->
        sensorData.apply { mobileObjectId = mobileObject.id }
    }

    private fun <T : Any> Flowable<T>.andPublish(topic: Topic) = doOnNext {
        wlanTechnology.queueMessage(topic, it)
        bufferStrategy.handleBuffer(wlanTechnology)
    }

    private fun Flowable<SensorData>.andProcess() = doOnNext {
        cepTechnology.processEvent(it)
    }
}
