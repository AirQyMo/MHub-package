package br.pucrio.inf.lac.mobilehub.core.data.local.source

import br.pucrio.inf.lac.mobilehub.core.data.local.dao.CepQueryDao
import br.pucrio.inf.lac.mobilehub.core.data.local.models.CepQueryModel
import br.pucrio.inf.lac.mobilehub.core.data.repositories.CepQueryLocalDataSource
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

@Reusable
internal class CepQueryLocalDataSourceImpl @Inject constructor(
    private val dao: CepQueryDao
) : CepQueryLocalDataSource {
    override fun save(query: CepQueryModel): Single<Long> = dao.insert(query)

    override fun list(): Single<List<CepQueryModel>> = dao.findAll()

    override fun findById(id: Long): Maybe<CepQueryModel> = dao.findById(id)

    override fun delete(id: Long): Completable = dao.deleteById(id)
}
