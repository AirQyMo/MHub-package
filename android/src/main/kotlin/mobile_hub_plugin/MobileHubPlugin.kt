package com.mhub.mobile.mobile_hub_plugin

import android.content.Context
import br.pucrio.inf.lac.ble.BleWPAN
import br.pucrio.inf.lac.mobilehub.core.MobileHub
import br.pucrio.inf.lac.mobilehub.core.MobileHubEvent
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.technology.TechnologyNotEnabledException
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.technology.TechnologyNotSupportedException
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.general.PermissionException
import br.pucrio.inf.lac.mobilehub.core.helpers.components.RxBus
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

// Core MQTT classes
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttToken
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken

// Android Service and Callback classes
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended

/** MobileHubPlugin */
class MobileHubPlugin : FlutterPlugin, MethodChannel.MethodCallHandler, EventChannel.StreamHandler {

    private lateinit var applicationContext: Context
    private lateinit var methodChannel: MethodChannel

    // Event Channels
    private lateinit var messageEventChannel: EventChannel
    private lateinit var bleDiscoveredEventChannel: EventChannel
    private lateinit var sensorDataEventChannel: EventChannel
    private lateinit var connectionStatusEventChannel: EventChannel
    private lateinit var hubEventsEventChannel: EventChannel

    // Event Sinks for sending data back to Dart
    private var messageEventSink: EventChannel.EventSink? = null
    private var bleDiscoveredEventSink: EventChannel.EventSink? = null
    private var sensorDataEventSink: EventChannel.EventSink? = null
    private var connectionStatusEventSink: EventChannel.EventSink? = null
    private var hubEventsEventSink: EventChannel.EventSink? = null

    // RxJava disposables to manage subscriptions
    private val disposables = CompositeDisposable()

    // --- Mobile Hub Core Instances ---
    // IMPORTANT: In a real-world scenario with Dagger, you would inject these
    // instances rather than creating them directly.
    // For now, we'll instantiate a basic MobileHub.
    private lateinit var mobileHub: MobileHub
    private lateinit var bleWPAN: BleWPAN // Assuming you'll need direct access for some BLE methods

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        applicationContext = binding.applicationContext

        // Initialize Method Channel
        methodChannel = MethodChannel(binding.binaryMessenger, "mobile_hub/methods")
        methodChannel.setMethodCallHandler(this)

        // Initialize Event Channels
        messageEventChannel = EventChannel(binding.binaryMessenger, "mobile_hub/events/messages")
        messageEventChannel.setStreamHandler(this)

        bleDiscoveredEventChannel = EventChannel(binding.binaryMessenger, "mobile_hub/events/ble_discovered_devices")
        bleDiscoveredEventChannel.setStreamHandler(this)

        sensorDataEventChannel = EventChannel(binding.binaryMessenger, "mobile_hub/events/sensor_data")
        sensorDataEventChannel.setStreamHandler(this)

        connectionStatusEventChannel = EventChannel(binding.binaryMessenger, "mobile_hub/events/connection_status")
        connectionStatusEventChannel.setStreamHandler(this)

        hubEventsEventChannel = EventChannel(binding.binaryMessenger, "mobile_hub/events/hub_events")
        hubEventsEventChannel.setStreamHandler(this)

        // --- Initialize MobileHub and other components ---
        // TODO: Replace this direct instantiation with Dagger injection.
        // You would typically get your Dagger component here and inject dependencies.
        mobileHub = MobileHub.Builder(applicationContext).build()
        bleWPAN = BleWPAN.Builder(applicationContext).build() // Assuming BleWPAN needs to be directly accessible for some calls

        // Start listening to RxBus events and push them to Flutter
        setupEventBusListeners()
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel.setMethodCallHandler(null)
        messageEventChannel.setStreamHandler(null)
        bleDiscoveredEventChannel.setStreamHandler(null)
        sensorDataEventChannel.setStreamHandler(null)
        connectionStatusEventChannel.setStreamHandler(null)
        hubEventsEventChannel.setStreamHandler(null)

        disposables.clear() // Clear all RxJava subscriptions
    }

    // --- MethodChannel.MethodCallHandler Implementation ---
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "initHub" -> {
                mobileHub.init()
                    .subscribe({ result.success(true) }, { error ->
                        handlePlatformException(result, "initHub", error)
                    })
                    .addTo(disposables)
            }
            "startHub" -> {
                mobileHub.start()
                    .subscribe({ result.success(true) }, { error ->
                        handlePlatformException(result, "startHub", error)
                    })
                    .addTo(disposables)
            }
            "stopHub" -> {
                mobileHub.stop()
                    .subscribe({ result.success(true) }, { error ->
                        handlePlatformException(result, "stopHub", error)
                    })
                    .addTo(disposables)
            }
            "publishMessage" -> {
                val topic = call.argument<String>("topic")
                val payloadJson = call.argument<String>("payload")
                val qos = call.argument<Int>("qos")

                if (topic != null && payloadJson != null && qos != null) {
                    // mobileHub.publishMessage expects Map<String, Any>, so parse JSON string
                    val payloadMap = MobileObjectMapper.fromJson(payloadJson)
                    mobileHub.publishMessage(topic, payloadMap, qos)
                        .subscribe({ result.success(true) }, { error ->
                            handlePlatformException(result, "publishMessage", error)
                        })
                        .addTo(disposables)
                } else {
                    result.error("INVALID_ARGUMENTS", "Missing topic, payload, or qos for publishMessage", null)
                }
            }
            "publishQueuedMessages" -> {
                mobileHub.publishQueuedMessages()
                    .subscribe({ result.success(true) }, { error ->
                        handlePlatformException(result, "publishQueuedMessages", error)
                    })
                    .addTo(disposables)
            }
            "startScan" -> {
                // This is the method that was causing the MissingPluginException
                bleWPAN.startScan() // Assuming BleWPAN is the direct source for BLE scans
                    .subscribe({ mobileObject ->
                        // Send discovered device to Flutter via EventChannel
                        bleDiscoveredEventSink?.success(mapOf(
                            "name" to mobileObject.name,
                            "id" to mobileObject.id.id,
                            "address" to mobileObject.address
                        ))
                    }, { error ->
                        handlePlatformException(result, "startScan", error)
                    })
                    .addTo(disposables)
                result.success(true) // Indicate that the scan command was initiated successfully
            }
            "stopBleScan" -> {
                // Assuming BleWPAN has a stopScan method
                // You might need to implement this in BleWPAN if it's not there
                // For now, we'll just return success as a placeholder if no direct stop method exists.
                // If mobileHub handles stopScan, call mobileHub.stopBleScan()
                result.success(true) // Placeholder
            }
            "connectBleDevice" -> {
                val deviceId = call.argument<String>("deviceId")
                if (deviceId != null) {
                    // You'll need to retrieve the MobileObject from a list of discovered devices
                    // For demonstration, let's create a dummy MobileObject.
                    // In a real app, you'd fetch this from your scan results.
                    val mobileObjectToConnect = MobileObject(Moid(deviceId), "DummyDeviceName", deviceId) // Replace with actual object
                    mobileHub.connectMobileObject(mobileObjectToConnect)
                        .subscribe({ result.success(true) }, { error ->
                            handlePlatformException(result, "connectBleDevice", error)
                        })
                        .addTo(disposables)
                } else {
                    result.error("INVALID_ARGUMENTS", "Missing deviceId for connectBleDevice", null)
                }
            }
            "disconnectBleDevice" -> {
                val deviceId = call.argument<String>("deviceId")
                if (deviceId != null) {
                    // Similar to connect, you might need to retrieve the actual MobileObject
                    val mobileObjectToDisconnect = MobileObject(Moid(deviceId), "DummyDeviceName", deviceId)
                    mobileHub.disconnectMobileObject(mobileObjectToDisconnect)
                        .subscribe({ result.success(true) }, { error ->
                            handlePlatformException(result, "disconnectBleDevice", error)
                        })
                        .addTo(disposables)
                } else {
                    result.error("INVALID_ARGUMENTS", "Missing deviceId for disconnectBleDevice", null)
                }
            }
            "subscribeToSensorData" -> {
                val deviceId = call.argument<String>("deviceId")
                if (deviceId != null) {
                    val mobileObject = MobileObject(Moid(deviceId), "DummyDeviceName", deviceId) // Replace with actual object
                    mobileHub.subscribeToMobileObjectSensorData(mobileObject)
                        .subscribe({ sensorData ->
                            // Send sensor data to Flutter via EventChannel
                            sensorDataEventSink?.success(sensorData.toMap()) // Assuming SensorData has a toMap() extension or similar
                        }, { error ->
                            handlePlatformException(result, "subscribeToSensorData", error)
                        })
                        .addTo(disposables)
                    result.success(true) // Indicate subscription initiated
                } else {
                    result.error("INVALID_ARGUMENTS", "Missing deviceId for subscribeToSensorData", null)
                }
            }
            "readSensorData" -> {
                val deviceId = call.argument<String>("deviceId")
                val serviceName = call.argument<String>("serviceName")
                if (deviceId != null && serviceName != null) {
                    val mobileObject = MobileObject(Moid(deviceId), "DummyDeviceName", deviceId) // Replace with actual object
                    mobileHub.readMobileObjectSensorData(mobileObject, serviceName)
                        .subscribe({ sensorData ->
                            result.success(sensorData.toMap()) // Assuming SensorData has a toMap() extension or similar
                        }, { error ->
                            handlePlatformException(result, "readSensorData", error)
                        })
                        .addTo(disposables)
                } else {
                    result.error("INVALID_ARGUMENTS", "Missing deviceId or serviceName for readSensorData", null)
                }
            }
            "addMobileObjectDriver" -> {
                val driverConfigJson = call.argument<String>("driverConfigJson")
                if (driverConfigJson != null) {
                    mobileHub.addMobileObjectDriver(driverConfigJson)
                        .subscribe({ result.success(true) }, { error ->
                            handlePlatformException(result, "addMobileObjectDriver", error)
                        })
                        .addTo(disposables)
                } else {
                    result.error("INVALID_ARGUMENTS", "Missing driverConfigJson for addMobileObjectDriver", null)
                }
            }
            else -> result.notImplemented()
        }
    }

    // --- EventChannel.StreamHandler Implementation ---
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        when (arguments as String) {
            "mobile_hub/events/messages" -> messageEventSink = events
            "mobile_hub/events/ble_discovered_devices" -> bleDiscoveredEventSink = events
            "mobile_hub/events/sensor_data" -> sensorDataEventSink = events
            "mobile_hub/events/connection_status" -> connectionStatusEventSink = events
            "mobile_hub/events/hub_events" -> hubEventsEventSink = events
        }
    }

    override fun onCancel(arguments: Any?) {
        when (arguments as String) {
            "mobile_hub/events/messages" -> messageEventSink = null
            "mobile_hub/events/ble_discovered_devices" -> bleDiscoveredEventSink = null
            "mobile_hub/events/sensor_data" -> sensorDataEventSink = null
            "mobile_hub/events/connection_status" -> connectionStatusEventSink = null
            "mobile_hub/events/hub_events" -> hubEventsEventSink = null
        }
    }

    // --- Helper for RxBus listeners and sending data to Flutter ---
    private fun setupEventBusListeners() {
        // Listen for MobileHubEvent (e.g., connection status, hub started/stopped)
        RxBus.listen(MobileHubEvent::class.java)
            .subscribe { event ->
                when (event.type) {
                    MobileHubEvent.EventType.CONNECTION_STATUS_CHANGED -> {
                        connectionStatusEventSink?.success(event.content)
                    }
                    MobileHubEvent.EventType.HUB_STARTED -> {
                        hubEventsEventSink?.success("HUB_STARTED")
                    }
                    MobileHubEvent.EventType.HUB_STOPPED -> {
                        hubEventsEventSink?.success("HUB_STOPPED")
                    }
                    // Add other event types as needed
                    else -> { /* Handle other events or ignore */ }
                }
            }.addTo(disposables)

        // Listen for discovered BLE devices (assuming MobileObject is sent via RxBus)
        RxBus.listen(MobileObject::class.java)
            .subscribe { mobileObject ->
                bleDiscoveredEventSink?.success(mapOf(
                    "name" to mobileObject.name,
                    "id" to mobileObject.id.id,
                    "address" to mobileObject.address
                ))
            }.addTo(disposables)

        // Listen for SensorData (assuming SensorData is sent via RxBus)
        RxBus.listen(SensorData::class.java)
            .subscribe { sensorData ->
                sensorDataEventSink?.success(sensorData.toMap()) // Assuming toMap() is an extension function
            }.addTo(disposables)

        // Listen for Message events (assuming Message is sent via RxBus)
        // You'll need to define a Message class in your core module if it's not already there
        // and add a RxBus.listen for it. For now, assuming it's part of MobileHubEvent content.
    }

    // Helper function to convert SensorData to Map<String, Any?> for Flutter
    // You might already have this as an extension function in your core module.
    // If not, you'll need to implement it.
    private fun SensorData.toMap(): Map<String, Any?> {
        return mapOf(
            "mobileObjectId" to this.mobileObjectId.id,
            "sensorName" to this.sensorName,
            "timestamp" to this.timestamp,
            "data" to this.data // Assuming data is already a Map or can be converted
        )
    }

    // Helper function to handle exceptions and send PlatformException to Flutter
    private fun handlePlatformException(result: MethodChannel.Result, methodName: String, error: Throwable) {
        val errorCode = when (error) {
            is TechnologyNotEnabledException -> "TECHNOLOGY_NOT_ENABLED"
            is TechnologyNotSupportedException -> "TECHNOLOGY_NOT_SUPPORTED"
            is PermissionException -> "PERMISSION_DENIED"
            // Add other custom exceptions from your core module here
            else -> "UNEXPECTED_ERROR"
        }
        Timber.e(error, "Error in $methodName: ${error.message}")
        result.error(errorCode, error.message, error.javaClass.simpleName)
    }
}