package br.pucrio.inf.lac.mobilehub.core.data.repositories

import br.pucrio.inf.lac.mobilehub.core.data.local.mapper.toEntity
import br.pucrio.inf.lac.mobilehub.core.data.local.mapper.toModel
import br.pucrio.inf.lac.mobilehub.core.data.local.models.MobileObjectDriverModel
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObjectDriver
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MobileObjectDriverRepository
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wpan.WPAN
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

@Reusable
internal class MobileObjectDriverRepositoryImpl @Inject constructor(
    private val localDataSource: MobileObjectDriverLocalDataSource,
    private val remoteDataSource: MobileObjectDriverRemoteDataSource
) : MobileObjectDriverRepository {
    override fun save(driver: MobileObjectDriver): Single<Long> {
        val driverModel = driver.toModel()
        return localDataSource.saveDriver(driverModel)
    }

    override fun list(): Single<List<MobileObjectDriver>> = localDataSource.listDrivers()
        .map { it.toEntities() }

    private fun List<MobileObjectDriverModel>.toEntities(): List<MobileObjectDriver> = map {
        it.toEntity()
    }

    override fun subscribeToLatest(): Flowable<MobileObjectDriver> = localDataSource.subscribeToLatest()
        .map { it.toEntity() }

    override fun findById(id: Long) = localDataSource.findDriverById(id)
        .map { it.toEntity() }

    override fun delete(id: Long): Completable = localDataSource.deleteDriver(id)

    override fun loadByName(wpan: Int, name: String)= localDataSource.loadDriverByName(wpan, name)
        .map { it.toEntity() }
        .switchIfEmpty(requestDriver(wpan, name))

    private fun requestDriver(wpan: Int, name: String) = remoteDataSource.requestDriver(wpan, name)
        .andThen(Maybe.empty<MobileObjectDriver>())

    /*override fun loadByName(wpan: Int, name: String) = localDataSource.loadDriverByName(wpan, name)
    .switchIfEmpty(
        remoteDataSource.requestDriver(wpan, name)
    )
    //.doAfterSuccess { localDataSource.updateDriverRequiredDate(it) }
    .map { it.toEntity() }
    .flatMapCompletable { wpanTechnologies[wpan]!!.addDriver(it) }*/

    private fun MobileObjectDriverLocalDataSource.updateDriverRequiredDate(driver: MobileObjectDriverModel) {
        driver.updateLastRequired()
        saveDriver(driver)
    }
}

internal interface MobileObjectDriverLocalDataSource {
    fun saveDriver(driver: MobileObjectDriverModel): Single<Long>

    fun listDrivers(): Single<List<MobileObjectDriverModel>>

    fun subscribeToLatest(): Flowable<MobileObjectDriverModel>

    fun findDriverById(id: Long): Maybe<MobileObjectDriverModel>

    fun loadDriverByName(wpan: Int, name: String): Maybe<MobileObjectDriverModel>

    fun deleteDriver(id: Long): Completable
}

internal interface MobileObjectDriverRemoteDataSource {
    fun requestDriver(wpan: Int, name: String): Completable
}