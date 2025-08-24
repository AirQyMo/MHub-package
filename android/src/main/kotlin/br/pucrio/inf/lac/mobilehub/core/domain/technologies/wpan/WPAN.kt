package br.pucrio.inf.lac.mobilehub.core.domain.technologies.wpan

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObjectDriver
import br.pucrio.inf.lac.mobilehub.core.domain.entities.Moid
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.driver.MobileObjectDriverException
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.mobileobject.MobileObjectInvalidConnectionException
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface WPAN {
    val id: Int

    val connectedMobileObjects: List<Moid>

    fun startScan(): Flowable<MobileObject>

    @Throws(MobileObjectInvalidConnectionException::class, MobileObjectDriverException::class)
    fun connect(mobileObject: MobileObject): Completable

    @Throws(MobileObjectInvalidConnectionException::class)
    fun connectWithDriver(mobileObject: MobileObject, driver: MobileObjectDriver): Completable

    fun subscribe(mobileObject: MobileObject): Flowable<SensorData>

    fun read(mobileObject: MobileObject, serviceName: String): Single<SensorData>

    fun disconnect(mobileObject: MobileObject)
}