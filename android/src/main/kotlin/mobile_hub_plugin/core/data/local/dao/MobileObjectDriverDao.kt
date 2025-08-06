package br.pucrio.inf.lac.mobilehub.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import br.pucrio.inf.lac.mobilehub.core.data.local.models.MobileObjectDriverModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
internal abstract class MobileObjectDriverDao : BaseDao<MobileObjectDriverModel> {
    @Query("SELECT * FROM mobile_object_driver ORDER BY updated DESC")
    abstract fun findAll(): Single<List<MobileObjectDriverModel>>

    @Query("SELECT * FROM mobile_object_driver ORDER BY updated DESC LIMIT 1")
    abstract fun subscribeToLatest(): Flowable<MobileObjectDriverModel>

    @Query("SELECT * FROM mobile_object_driver WHERE id = :id")
    abstract fun findById(id: Long): Maybe<MobileObjectDriverModel>

    @Query("SELECT * FROM mobile_object_driver WHERE wpan = :wpan AND name = :name")
    abstract fun findByName(wpan: Int, name: String): Maybe<MobileObjectDriverModel>

    @Query("DELETE FROM mobile_object_driver WHERE id = :id")
    abstract fun deleteById(id: Long): Completable

    @Query("DELETE FROM mobile_object_driver")
    abstract fun deleteAll(): Completable
}