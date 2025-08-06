package br.pucrio.inf.lac.mobilehub.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import br.pucrio.inf.lac.mobilehub.core.data.local.models.CepQueryModel
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
internal abstract class CepQueryDao : BaseDao<CepQueryModel> {
    @Query("SELECT * FROM cep_queries ORDER BY created")
    abstract fun findAll(): Single<List<CepQueryModel>>

    @Query("SELECT * FROM cep_queries WHERE id = :id")
    abstract fun findById(id: Long): Maybe<CepQueryModel>

    @Query("DELETE FROM cep_queries WHERE id = :id")
    abstract fun deleteById(id: Long): Completable

    @Query("DELETE FROM cep_queries")
    abstract fun deleteAll(): Completable
}