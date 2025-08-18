package br.pucrio.inf.lac.mobilehub.core.domain.repositories

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObjectDriver
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface MobileObjectDriverRepository {
    fun save(driver: MobileObjectDriver): Single<Long>

    fun list(): Single<List<MobileObjectDriver>>

    fun subscribeToLatest(): Flowable<MobileObjectDriver>

    fun findById(id: Long): Maybe<MobileObjectDriver>

    fun delete(id: Long): Completable

    fun loadByName(wpan: Int, name: String): Maybe<MobileObjectDriver>
}