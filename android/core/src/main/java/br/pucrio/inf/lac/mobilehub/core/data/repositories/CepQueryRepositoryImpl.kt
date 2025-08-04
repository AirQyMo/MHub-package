package br.pucrio.inf.lac.mobilehub.core.data.repositories

import br.pucrio.inf.lac.mobilehub.core.data.local.mapper.toModel
import br.pucrio.inf.lac.mobilehub.core.data.local.mapper.toEntity
import br.pucrio.inf.lac.mobilehub.core.data.local.models.CepQueryModel
import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.CepQueryRepository
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

@Reusable
internal class CepQueryRepositoryImpl @Inject constructor(
    private val localDataSource: CepQueryLocalDataSource
) : CepQueryRepository {
    override fun save(query: CepQuery): Single<Long> {
        val queryDto = query.toModel()
        return localDataSource.save(queryDto)
    }

    override fun list(): Single<List<CepQuery>> = localDataSource.list()
        .map { it.toEntities() }

    private fun List<CepQueryModel>.toEntities(): List<CepQuery> = map { it.toEntity() }

    override fun findById(id: Long): Maybe<CepQuery> = localDataSource.findById(id)
        .map { it.toEntity() }

    override fun delete(id: Long): Completable = localDataSource.delete(id)
}

internal interface CepQueryLocalDataSource {
    fun save(query: CepQueryModel): Single<Long>

    fun list(): Single<List<CepQueryModel>>

    fun findById(id: Long): Maybe<CepQueryModel>

    fun delete(id: Long): Completable
}
