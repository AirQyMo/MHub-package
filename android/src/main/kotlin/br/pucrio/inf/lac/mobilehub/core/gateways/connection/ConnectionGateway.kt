package br.pucrio.inf.lac.mobilehub.core.gateways.connection

import br.pucrio.inf.lac.mobilehub.core.MobileHubEvent
import br.pucrio.inf.lac.mobilehub.core.domain.entities.Message
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.message.ConnectUseCase
import br.pucrio.inf.lac.mobilehub.core.gateways.connection.cep.CepController
import br.pucrio.inf.lac.mobilehub.core.gateways.connection.cep.CepQueryBody
import br.pucrio.inf.lac.mobilehub.core.gateways.connection.driver.MobileObjectDriverBody
import br.pucrio.inf.lac.mobilehub.core.gateways.connection.driver.MobileObjectDriverController
import br.pucrio.inf.lac.mobilehub.core.helpers.components.RxBus
import com.google.gson.Gson
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

@Reusable
internal class ConnectionGateway @Inject constructor(
    private val gson: Gson,
    private val connectUseCase: ConnectUseCase,
    private val cepController: CepController,
    private val driverController: MobileObjectDriverController
) {
    private val disposables = CompositeDisposable()

    fun start() {
        connectUseCase()
            .subscribe(::onNewMessage, ::onError)
            .let { disposables += it }
    }

    private fun onNewMessage(message: Message) {
        RxBus.publish(MobileHubEvent.NewMessage(message))
        Timber.i("Connection gateway ${message.topic.value}: ${message.payload}")

        try {
            message.routeToController()
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    private fun Message.routeToController() = when (topic) {
        Topic.Cep -> cepController.route<CepQueryBody>(payload)
        Topic.Driver -> driverController.route<MobileObjectDriverBody>(payload)
        
        else -> Timber.w("Not handled yet")
    }

    private fun onError(throwable: Throwable) = Timber.e(throwable.localizedMessage)

    fun release() = disposables.clear()
}
