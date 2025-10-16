package br.pucrio.inf.lac.mobilehub.core

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import br.pucrio.inf.lac.mobilehub.core.data.buffer.BufferStrategy
import br.pucrio.inf.lac.mobilehub.core.data.buffer.TrafficStatsStrategy
import br.pucrio.inf.lac.mobilehub.core.data.processing.DefaultCEP
import br.pucrio.inf.lac.mobilehub.core.data.processing.DefaultContextDataProvider
import br.pucrio.inf.lac.mobilehub.core.di.MobileHubInjector
import br.pucrio.inf.lac.mobilehub.core.di.components.DaggerGraphComponent
import br.pucrio.inf.lac.mobilehub.core.di.injector
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp.ContextDataProvider
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEP
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wpan.WPAN
import br.pucrio.inf.lac.mobilehub.core.helpers.TimberHelper
import br.pucrio.inf.lac.mobilehub.core.helpers.components.RxBus
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

@SuppressLint("StaticFieldLeak")
object MobileHub {
    private lateinit var context: Context
    private lateinit var wlanTechnology: WLAN
    private var isWorkManagerInitialized = false

    private val dataTypes = listOf(
        Pair(SensorData.NAME, SensorData::class.java)
    )

    fun init(context: Context) = Builder(context)

    private fun build(builder: Builder) {
        this.context = builder.context.applicationContext
        this.wlanTechnology = builder.wlanTechnology!!

        TimberHelper.init(builder.isLogging)
        builder.cepTechnology.registerEventTypes(dataTypes)

        MobileHubInjector.component = DaggerGraphComponent.builder()
            .applicationContext(context)
            .bufferStrategy(builder.bufferStrategy)
            .wlanTechnology(builder.wlanTechnology!!)
            .wpanTechnologies(builder.wpanTechnologies)
            .cepTechnology(builder.cepTechnology)
            .contextDataProvider(builder.contextDataProvider)
            .build()

        initWorkManager()
    }

    private fun initWorkManager() {
        if (!isWorkManagerInitialized) {
            WorkManager.initialize(this.context, Configuration.Builder()
                //.setMinimumLoggingLevel(android.util.Log.INFO)
                .setWorkerFactory(injector.factory())
                .build()
            )
            isWorkManagerInitialized = true
        }
    }

    fun start() {
        requireNotNull(MobileHubInjector.component) { "You have to call build() first" }
        MobileHubService.startService(context)
    }

    fun stop() {
        requireNotNull(MobileHubInjector.component) { "You have to call build() first" }
        MobileHubService.stopService(context)
    }

    val isStarted: Boolean get() = MobileHubService.isStarted

    fun <E : MobileHubEvent> on(eventType: Class<E>): Observable<E> = RxBus.on(eventType)
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())

    fun sendMessage(topic: Topic, message: String) = wlanTechnology.publishMessage(topic, message)
        .subscribe({ Timber.i("Completed") }, Timber::e)

    fun updateContext(payload: List<String>) = wlanTechnology.updateContext(payload)
        .subscribe({ Timber.i("Completed") }, Timber::e)

    class Builder(val context: Context) {
        var bufferStrategy: BufferStrategy = TrafficStatsStrategy(); private set

        var wlanTechnology: WLAN? = null; private set

        val wpanTechnologies = hashMapOf<Int, WPAN>()

        var cepTechnology: CEP = DefaultCEP; private set

        var contextDataProvider: ContextDataProvider = DefaultContextDataProvider; private set

        var autoConnect = false

        var isLogging = false

        fun setBufferStrategy(bufferStrategy: BufferStrategy): Builder = this.apply {
            this.bufferStrategy = bufferStrategy
        }

        fun setWlanTechnology(wlanTechnology: WLAN): Builder = this.apply {
            this.wlanTechnology = wlanTechnology
        }

        fun addWpanTechnology(wpanTechnology: WPAN): Builder = this.apply {
            this.wpanTechnologies[wpanTechnology.id] = wpanTechnology
        }

        fun setCepTechnology(cepTechnology: CEP): Builder = this.apply {
            this.cepTechnology = cepTechnology
        }

        fun setContextDataProvider(contextDataProvider: ContextDataProvider): Builder = this.apply {
            this.contextDataProvider = contextDataProvider
        }

        fun setAutoConnect(autoConnect: Boolean): Builder = this.apply {
            this.autoConnect = autoConnect
        }

        fun setLog(isLogging: Boolean): Builder = this.apply {
            this.isLogging = isLogging
        }

        fun build() {
            requireNotNull(wlanTechnology) { "WLAN technology required" }
            return build(this)
        }
    }
}
