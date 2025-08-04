package br.pucrio.inf.lac.mobilehub.core.gateways.s2pa

import android.util.LruCache
import br.pucrio.inf.lac.mobilehub.core.MobileHubEvent
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObjectDriver
import br.pucrio.inf.lac.mobilehub.core.domain.entities.Moid
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.driver.MobileObjectDriverException
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.contextsensor.SubscribeToContextSensorDataUseCase
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.driver.LoadMobileObjectDriverUseCase
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.driver.SubscribeToMobileObjectDriversUseCase
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.mobileobject.ConnectMobileObjectUseCase
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.mobileobject.ConnectMobileObjectWithDriverUseCase
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.mobileobject.ScanMobileObjectsUseCase
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.mobileobject.SubscribeToMobileObjectSensorDataUseCase
import br.pucrio.inf.lac.mobilehub.core.helpers.components.RxBus
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@Reusable
internal class S2PAGateway @Inject constructor(
    private val scanMobileObjectsUseCase: ScanMobileObjectsUseCase,
    private val connectMobileObjectUseCase: ConnectMobileObjectUseCase,
    private val connectMobileObjectWithDriverUseCase: ConnectMobileObjectWithDriverUseCase,
    private val loadMobileObjectDriverUseCase: LoadMobileObjectDriverUseCase,
    private val subscribeToMobileObjectSensorDataUseCase: SubscribeToMobileObjectSensorDataUseCase,
    private val subscribeToMobileObjectDriversUseCase: SubscribeToMobileObjectDriversUseCase,
    private val subscribeToContextSensorDataUseCase: SubscribeToContextSensorDataUseCase
) {
    private object Limit {
        const val CONNECTIONS = 5
    }

    private val pendingMobileObjectConnections = LruCache<Moid, MobileObject>(Limit.CONNECTIONS)
    private val disposables = CompositeDisposable()

    fun start() {
        release()

        subscribeToMobileObjectDriversUseCase()
            .subscribe(::onNewDriver, ::onError)
            .let { disposables += it }

        scanMobileObjectsUseCase()
            .parallel()
            .runOn(Schedulers.computation())
            .map(::onDiscovered)
            .sequential()
            .subscribe({}, ::onError)
            .let { disposables += it }

        subscribeToContextSensorDataUseCase()
            .subscribe(::onNewContextData, ::onError)
            .let { disposables += it }
    }

    private fun onNewDriver(driver: MobileObjectDriver) {
        val mobileObjects = pendingMobileObjectConnections.getMobileObjectsForDriver(driver)
        mobileObjects.forEach { mobileObject ->
            pendingMobileObjectConnections.remove(mobileObject.id)
            connectWithDriver(mobileObject, driver)
        }
    }

    private fun LruCache<Moid, MobileObject>.getMobileObjectsForDriver(driver: MobileObjectDriver) =
        snapshot().values.filter { it.name == driver.name }

    private fun connectWithDriver(mobileObject: MobileObject, driver: MobileObjectDriver) {
        val param = ConnectMobileObjectWithDriverUseCase.Params(mobileObject, driver)
        connectMobileObjectWithDriverUseCase(param)
            .subscribe({ onConnected(mobileObject) }, ::onConnectionError)
            .let { disposables += it }
    }

    private fun onDiscovered(mobileObject: MobileObject) {
        Timber.i(mobileObject.toString())
        RxBus.publish(MobileHubEvent.MobileObjectDiscovered(mobileObject))
        connectMobileObjectUseCase(mobileObject)
            .subscribe({ onConnected(mobileObject) }, ::onConnectionError)
            .let { disposables += it }
    }

    private fun onConnected(mobileObject: MobileObject) {
        RxBus.publish(MobileHubEvent.MobileObjectConnected(mobileObject))
        subscribeToMobileObjectSensorDataUseCase(mobileObject)
            .subscribe({ onNewSensorData(mobileObject.name, it) }, ::onError)
            .let { disposables += it }
    }

    private fun onNewSensorData(name: String?, sensorData: SensorData) {
        RxBus.publish(MobileHubEvent.NewSensorData(sensorData))
        Timber.i("$name ${sensorData.serviceName} ${sensorData.serviceData.joinToString()}")
    }

    private fun onConnectionError(throwable: Throwable) = when (throwable) {
        is MobileObjectDriverException ->  {
            val mobileObject = throwable.mobileObject
            pendingMobileObjectConnections.put(mobileObject.id, mobileObject)
            loadDriver(mobileObject)
        }
        else -> onError(throwable)
    }

    private fun loadDriver(mobileObject: MobileObject) {
        val params = LoadMobileObjectDriverUseCase.Params(mobileObject.wpan, mobileObject.name!!)
        loadMobileObjectDriverUseCase(params)
            .subscribe({ driver ->
                pendingMobileObjectConnections.remove(mobileObject.id)
                connectWithDriver(mobileObject, driver)
            }, ::onError)
            .let { disposables += it }
    }

    private fun onNewContextData(contextData: List<SensorData>) {
        RxBus.publish(MobileHubEvent.NewContextData(contextData))
        contextData.forEach {
            Timber.i("Context Data ${it.serviceName} ${it.serviceData.joinToString()}")
        }
    }

    private fun onError(throwable: Throwable) = Timber.w(throwable)

    fun release() = disposables.clear()
}
