package br.pucrio.inf.lac.mobilehub.presentation.ui.main.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.pucrio.inf.lac.asper.AsperCEP
import br.pucrio.inf.lac.ble.BleWPAN
import br.pucrio.inf.lac.ble.transcoder.lua.LuaTranscoderFactory
import br.pucrio.inf.lac.cdp.android.AndroidCDP
import br.pucrio.inf.lac.mobilehub.core.MobileHub
import br.pucrio.inf.lac.mobilehub.core.MobileHubEvent
import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.domain.entity.ConfigurationEntity
import br.pucrio.inf.lac.mobilehub.domain.entity.Wlan
import br.pucrio.inf.lac.mobilehub.domain.repository.ConfigurationRepository
import br.pucrio.inf.lac.mobilehub.drivers.CC2650SensorTag
import br.pucrio.inf.lac.mqtt.MqttWLAN
import br.pucrio.inf.lac.mrudp.MrudpWLAN
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

internal class HomeViewModel @Inject constructor(
    private val configurationRepository: ConfigurationRepository
) : ViewModel() {
    var state by mutableStateOf(HomeState())
        private set

    private val disposables = CompositeDisposable()

    init {
        state = state.copy(isMobileHubRunning = MobileHub.isStarted)
        MobileHub.on(MobileHubEvent.MobileObjectDiscovered::class.java)
            .subscribe(::addDiscoveredMobileObject, Timber::e)
            .let { disposables += it }
    }

    fun loadSettings(context: Context) {
        state = state.copy(isInitialized = true)
        viewModelScope.launch {
            val settings = configurationRepository.getSettings()
            val wlan = settings.wlanTechnology(context)

            val ble = BleWPAN.Builder(context)
                .setTranscoderFactory(LuaTranscoderFactory.create())
                .addDriver(CC2650SensorTag())
                .build()

            val androidCDP = AndroidCDP.Builder(context)
                .build()

            val testQuery = CepQuery(
                name = "TestQuery",
                statement = "SELECT avg(serviceData[0]) as value FROM SensorData(serviceName='temperature').win:time_batch(10 sec)"
            )

            val asperCEP = AsperCEP.Builder()
                .addEventType(SensorData.NAME, SensorData::class.java)
                .addQuery(testQuery)
                .build()

            MobileHub.init(context)
                .setWlanTechnology(wlan)
                .addWpanTechnology(ble)
                .setContextDataProvider(androidCDP)
                .setCepTechnology(asperCEP)
                .setAutoConnect(true)
                .setLog(true)
                .build()
        }
    }

    private fun ConfigurationEntity.wlanTechnology(context: Context) =
        when (wlan) {
            Wlan.MQTT -> MqttWLAN.Builder(context)
                .ipAddress(ipAddress)
                .port(port!!)
                .build()

            Wlan.MR_UDP -> MrudpWLAN.Builder()
                .ipAddress(ipAddress)
                .port(port!!)
                .build()
        }

    private fun addDiscoveredMobileObject(event: MobileHubEvent.MobileObjectDiscovered) {
        val discoveredDevices = state.discoveredDevices.toMutableMap()
        discoveredDevices[event.mobileObject.id] = event.mobileObject
        state = state.copy(discoveredDevices = discoveredDevices)
    }

    fun startMobileHub() {
        MobileHub.start()
        state = state.copy(isMobileHubRunning = true)
    }

    fun stopMobileHub() {
        MobileHub.stop()
        state = state.copy(isMobileHubRunning = false)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}
