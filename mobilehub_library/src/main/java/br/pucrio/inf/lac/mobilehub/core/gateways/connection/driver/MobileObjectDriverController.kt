package br.pucrio.inf.lac.mobilehub.core.gateways.connection.driver

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.driver.SaveMobileObjectDriverUseCase
import br.pucrio.inf.lac.mobilehub.core.gateways.connection.base.BaseController
import br.pucrio.inf.lac.mobilehub.core.gateways.connection.base.RequestMapping
import com.google.gson.Gson
import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject

@Reusable
internal class MobileObjectDriverController @Inject constructor(
    private val saveMobileObjectDriverUseCase: SaveMobileObjectDriverUseCase,
    gson: Gson,
    wlanTechnology: WLAN
) : BaseController(gson, wlanTechnology, Topic.Driver) {
    @RequestMapping("save")
    fun save(body: MobileObjectDriverBody): Single<Long> {
        val entity = body.toEntity()
        return saveMobileObjectDriverUseCase(entity)
    }
}