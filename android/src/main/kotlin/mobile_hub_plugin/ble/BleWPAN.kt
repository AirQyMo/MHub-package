package br.pucrio.inf.lac.ble

import android.content.Context
import android.util.LruCache
import br.pucrio.inf.lac.ble.device.BleDevice
import br.pucrio.inf.lac.ble.transcoder.Transcoder
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObjectDriver
import br.pucrio.inf.lac.mobilehub.core.domain.entities.Moid
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.driver.MobileObjectDriverException
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.general.PermissionException
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.mobileobject.MobileObjectInvalidConnectionException
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.technology.TechnologyNotEnabledException
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.technology.TechnologyNotSupportedException
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wpan.WPAN
import br.pucrio.inf.lac.mobilehub.core.helpers.extensions.kotlin.isNotNull
import br.pucrio.inf.lac.mobilehub.core.helpers.extensions.kotlin.isNull
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleClient.State.*
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

class BleWPAN(
    private val bleClient: RxBleClient,
    private val transcoderFactory: Transcoder.Factory?,
    cacheSize: Int,
    drivers: List<BleDevice>
) : WPAN {
    private object Cache {
        const val DEFAULT_SIZE = 20
    }

    private val connectionsCache = LruCache<Moid, RxBleConnection>(cacheSize)
    private val driversCache = LruCache<String, BleDevice>(cacheSize)
    private val disposables = CompositeDisposable()

    init {
        drivers.forEach { driver ->
            driversCache.put(driver.name, driver)
        }

        if (!bleClient.isScanRuntimePermissionGranted) {
            Timber.e("Required permissions not granted")
        }
    }

    override val id: Int = 1

    override val connectedMobileObjects: List<Moid>
        get() = connectionsCache.snapshot().keys.toList()

    override fun startScan(): Flowable<MobileObject> = configureScan()
        .retryWhen { it.observeIfStateIsReady() }
        .map { MobileObjectMapper.from(id, it) }
        .toFlowable(BackpressureStrategy.LATEST)

    private fun configureScan(): Observable<ScanResult> = bleClient.scanBleDevices(
        ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
            .build()
    )

    private fun Observable<Throwable>.observeIfStateIsReady(): Observable<ScanResult> = flatMap {
        bleClient.observeStateChanges()
            .switchMap { configureScanIfReady(it) }
            .doOnError { Timber.e(it.localizedMessage) }
            .onErrorResumeNext(Observable.empty())
    }

    private fun configureScanIfReady(state: RxBleClient.State): Observable<ScanResult> = when (state) {
        READY -> configureScan()
        BLUETOOTH_NOT_AVAILABLE -> throw TechnologyNotSupportedException("Bluetooth not available")
        LOCATION_PERMISSION_NOT_GRANTED -> throw PermissionException("Location not granted")
        BLUETOOTH_NOT_ENABLED -> throw TechnologyNotEnabledException("Bluetooth not enabled")
        LOCATION_SERVICES_NOT_ENABLED -> throw TechnologyNotEnabledException("Location not enabled")
        else -> throw IllegalStateException(state.name)
    }

    override fun connect(mobileObject: MobileObject) = Completable.create { emitter ->
        if (!mobileObject.canConnect(emitter)) {
            return@create
        }

        bleClient.getBleDevice(mobileObject.address)
            .establishConnection(false)
            .doOnNext { connection -> connectionsCache.put(mobileObject.id, connection) }
            .doOnError { connectionsCache.remove(mobileObject.id) }
            .subscribe({ emitter.onComplete() }, { error ->
                if (!emitter.isDisposed) {
                    emitter.onError(error)
                }
            })
            .let { disposables += it }
    }

    private fun MobileObject.canConnect(emitter: CompletableEmitter): Boolean = when {
        name.isNull() -> {
            emitter.onError(MobileObjectInvalidConnectionException("The device name cannot be null"))
            false
        }
        driversCache[name].isNull() -> {
            emitter.onError(MobileObjectDriverException(this))
            false
        }
        connectionsCache[id].isNotNull() -> {
            emitter.onComplete()
            false
        }
        else -> true
    }

    override fun connectWithDriver(mobileObject: MobileObject, driver: MobileObjectDriver): Completable {
        addDriver(driver)
        return connect(mobileObject)
    }

    private fun addDriver(driverConfig: MobileObjectDriver) = Completable.fromAction {
        requireNotNull(transcoderFactory) { "Missing transcoder factory" }

        val transcoder = transcoderFactory.driverTranscoder(driverConfig.config)
        val driver = transcoder.convert(driverConfig.content)
        driversCache.put(driver.name, driver)
    }

    override fun subscribe(mobileObject: MobileObject): Flowable<SensorData> {
        requireNotNull(mobileObject.name) { "The device name cannot be null" }

        val connection = connectionsCache[mobileObject.id]
        val driver = driversCache[mobileObject.name]
        return driver.enableSensorNotifications(connection)
            .toFlowable(BackpressureStrategy.LATEST)
    }

    override fun read(mobileObject: MobileObject, serviceName: String): Single<SensorData> {
        TODO("Not yet implemented")
    }

    override fun disconnect(mobileObject: MobileObject) {
        TODO("Not yet implemented")
    }

    class Builder(private val context: Context, private val cacheSize: Int = Cache.DEFAULT_SIZE) {
        private var client: RxBleClient? = null

        private var transcoderFactory: Transcoder.Factory? = null

        private var drivers = mutableListOf<BleDevice>()

        fun setClient(client: RxBleClient) = this.also {
            this.client = client
        }

        fun setTranscoderFactory(factory: Transcoder.Factory) = this.also {
            this.transcoderFactory = factory
        }

        fun addDriver(driver: BleDevice) = this.also {
            require(drivers.size < cacheSize) { "You cannot provide more than ${Cache.DEFAULT_SIZE} drivers" }
            this.drivers.add(driver)
        }

        fun setDrivers(drivers: List<BleDevice>) = this.also {
            require(drivers.size <= cacheSize) { "You cannot provide more than ${Cache.DEFAULT_SIZE} drivers" }
            this.drivers = drivers.toMutableList()
        }

        fun build() = BleWPAN(
            bleClient = client ?: RxBleClient.create(context),
            transcoderFactory = transcoderFactory,
            cacheSize = cacheSize,
            drivers = drivers
        )
    }
}