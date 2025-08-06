package br.pucrio.inf.lac.mobilehub.core.domain.repositories

import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

internal interface CepQueryRepository {
    fun save(query: CepQuery): Single<Long>

    fun list(): Single<List<CepQuery>>

    fun findById(id: Long): Maybe<CepQuery>

    fun delete(id: Long): Completable
}