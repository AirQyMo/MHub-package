package br.pucrio.inf.lac.mobilehub.core.domain.repositories

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObjectDriver
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import io.reactivex.Completable
import io.reactivex.Flowable

internal interface MobileObjectRepository {
    fun scan(): Flowable<MobileObject>

    fun connect(mobileObject: MobileObject): Completable

    fun connectWithDriver(mobileObject: MobileObject, driver: MobileObjectDriver): Completable

    fun subscribe(mobileObject: MobileObject): Flowable<SensorData>
}