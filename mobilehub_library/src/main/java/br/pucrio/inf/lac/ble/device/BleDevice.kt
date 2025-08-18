package br.pucrio.inf.lac.ble.device

import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import com.polidea.rxandroidble2.RxBleConnection
import io.reactivex.Observable
import io.reactivex.Single

interface BleDevice {
    val name: String

    val sensors: Map<String, BleSensor>

    fun enableSensorNotifications(connection: RxBleConnection): Observable<SensorData> =
        Observable.fromIterable(sensors.values).flatMap { sensor ->
            connection.writeCharacteristic(sensor.conf, sensor.enable)
                .flatMapObservable { connection.setupNotification(sensor.data) }
                .flatMap { observable -> observable }
                .map { sensor.convert(it) }
                .map { SensorData(sensor.name, it) }
        }

    fun read(connection: RxBleConnection, serviceName: String) = Single.just(sensors[serviceName])
        .flatMap { sensor ->
            connection.readCharacteristic(sensor.data)
                .map { sensor.convert(it) }
                .map { SensorData(sensor.name, it) }
        }
}