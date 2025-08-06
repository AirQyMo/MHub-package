package br.pucrio.inf.lac.mobilehub.core.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import io.reactivex.Completable
import io.reactivex.Single

internal interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(element: T): Single<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(elements: List<T>): Completable

    @Update
    fun update(vararg element: T): Completable

    @Delete
    fun delete(element: T): Completable

    @Delete
    fun deleteAll(elements: List<T>): Completable
}