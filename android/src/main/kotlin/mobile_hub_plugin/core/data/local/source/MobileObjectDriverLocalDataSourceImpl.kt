package br.pucrio.inf.lac.mobilehub.core.data.local.source

import br.pucrio.inf.lac.mobilehub.core.data.local.dao.MobileObjectDriverDao
import br.pucrio.inf.lac.mobilehub.core.data.local.models.MobileObjectDriverModel
import br.pucrio.inf.lac.mobilehub.core.data.repositories.MobileObjectDriverLocalDataSource
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

@Reusable
internal class MobileObjectDriverLocalDataSourceImpl @Inject constructor(
    private val dao: MobileObjectDriverDao
) : MobileObjectDriverLocalDataSource {
    override fun saveDriver(driver: MobileObjectDriverModel): Single<Long> = dao.insert(driver)

    override fun listDrivers(): Single<List<MobileObjectDriverModel>> = dao.findAll()

    override fun subscribeToLatest(): Flowable<MobileObjectDriverModel> = dao.subscribeToLatest()

    override fun findDriverById(id: Long): Maybe<MobileObjectDriverModel> = dao.findById(id)

    override fun loadDriverByName(wpan: Int, name: String): Maybe<MobileObjectDriverModel> = dao.findByName(wpan, name)

    override fun deleteDriver(id: Long): Completable = dao.deleteById(id)
}
