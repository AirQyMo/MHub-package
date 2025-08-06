package br.pucrio.inf.lac.mobilehub.core.gateways.connection.cep

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.cepquery.*
import br.pucrio.inf.lac.mobilehub.core.gateways.connection.base.BaseController
import br.pucrio.inf.lac.mobilehub.core.gateways.connection.base.RequestMapping
import com.google.gson.Gson
import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject

@Reusable
internal class CepController @Inject constructor(
    private val getQueriesUseCase: GetQueriesUseCase,
    private val getQueryUseCase: GetQueryUseCase,
    private val saveQueryUseCase: SaveQueryUseCase,
    private val updateQueryUseCase: UpdateQueryUseCase,
    private val deleteQueryUseCase: DeleteQueryUseCase,
    gson: Gson,
    wlanTechnology: WLAN
) : BaseController(gson, wlanTechnology, Topic.Cep) {
    @RequestMapping("getAll")
    fun getAll() = getQueriesUseCase()

    @RequestMapping("get")
    fun get(id: Long) = getQueryUseCase(id)

    @RequestMapping("save")
    fun save(body: CepQueryBody): Single<Long> {
        val entity = body.toEntity()
        return saveQueryUseCase(entity)
    }

    @RequestMapping("update")
    fun update(id: Long, body: CepQueryBody): Single<Long> {
        val entity = body.toEntity()
        val params = UpdateQueryUseCase.Params(id, entity)
        return updateQueryUseCase(params)
    }

    @RequestMapping("delete")
    fun deleteQuery(id: Long) = deleteQueryUseCase(id)
}