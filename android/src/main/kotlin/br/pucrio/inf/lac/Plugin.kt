package br.pucrio.inf.lac

import android.app.Activity
import android.content.Context
import br.pucrio.inf.lac.ble.BleMessageReceiver
import br.pucrio.inf.lac.mobilehub.core.MobileHub
import br.pucrio.inf.lac.mobilehub.core.MobileHubEvent
import io.reactivex.disposables.Disposable
import br.pucrio.inf.lac.asper.AsperCEP
import br.pucrio.inf.lac.mqtt.MqttWLAN
import br.pucrio.inf.lac.ble.BleWPAN
import br.pucrio.inf.lac.mrudp.MrudpWLAN
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEP
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wpan.WPAN
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import com.google.gson.Gson

/** Plugin */
class Plugin: FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var channel : MethodChannel
    private lateinit var onScanningStateChangedChannel: EventChannel
    private lateinit var onMessageReceivedChannel: EventChannel
    private lateinit var onBleDataReceivedChannel: EventChannel
    private lateinit var context: Context
    private var activity: Activity? = null
    private var activityBinding: ActivityPluginBinding? = null
    private var bleMessageReceiver: BleMessageReceiver? = null
    private var notificationHelper: NotificationHelper? = null

    private var mrudpWlan: MrudpWLAN? = null
    private var onScanningStateChangedSink: EventChannel.EventSink? = null
    private var onMessageReceivedSink: EventChannel.EventSink? = null
    private var onBleDataReceivedSink: EventChannel.EventSink? = null
    private var messageSubscription: Disposable? = null
    private var eventSubscription: Disposable? = null


    private val onMessageReceivedHandler = object : EventChannel.StreamHandler {
        override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
            onMessageReceivedSink = events
            // Subscribe to MobileHubEvent.NewMessage events
            subscribeToMobileHubMessages()
        }

        override fun onCancel(arguments: Any?) {
            onMessageReceivedSink = null
            eventSubscription?.dispose()
        }
    }

    private val onBleDataReceivedHandler = object : EventChannel.StreamHandler {
        override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
            bleMessageReceiver?.onBleDataReceivedSink = events
        }

        override fun onCancel(arguments: Any?) {
            onBleDataReceivedSink = null
            bleMessageReceiver?.onBleDataReceivedSink = null
        }
    }

    private val onScanningStateChangedHandler = object : EventChannel.StreamHandler {
        override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
            onScanningStateChangedSink = events
            bleMessageReceiver?.onScanningStateChangedSink = onScanningStateChangedSink
        }

        override fun onCancel(arguments: Any?) {
            onScanningStateChangedSink = null
            bleMessageReceiver?.onScanningStateChangedSink = null
        }
    }

    private fun subscribeToMobileHubMessages() {
        eventSubscription?.dispose()
        eventSubscription = MobileHub.on(MobileHubEvent.NewMessage::class.java)
            .subscribe({ event ->
                // Send the message payload to Flutter
                onMessageReceivedSink?.success(event.message.payload)
            }, { error ->
                onMessageReceivedSink?.error("MOBILE_HUB_ERROR", error.localizedMessage, null)
            })
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "plugin")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
        bleMessageReceiver = BleMessageReceiver(context)
        notificationHelper = NotificationHelper(context)

        onMessageReceivedChannel = EventChannel(flutterPluginBinding.binaryMessenger, "onMessageReceived")
        onMessageReceivedChannel.setStreamHandler(onMessageReceivedHandler)

        onBleDataReceivedChannel = EventChannel(flutterPluginBinding.binaryMessenger, "onBleDataReceived")
        onBleDataReceivedChannel.setStreamHandler(onBleDataReceivedHandler)

        onScanningStateChangedChannel = EventChannel(flutterPluginBinding.binaryMessenger, "onScanningStateChanged")
        onScanningStateChangedChannel.setStreamHandler(onScanningStateChangedHandler)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "startMobileHub" -> {
                val ipAddress = call.argument<String>("ipAddress")
                val port = call.argument<Int>("port")

                if (ipAddress == null || port == null) {
                    result.error("INVALID_ARGUMENTS", "ipAddress and port must be provided", null)
                    return
                }

                val wlan = MrudpWLAN.Builder()
                    .ipAddress(ipAddress)
                    .port(port)
                    .build()
                this.mrudpWlan = wlan as MrudpWLAN

                val bleWpan: WPAN = BleWPAN.Builder(context).build()

                val asperCep: CEP = AsperCEP.Builder().build()

                MobileHub.init(context)
                    .setWlanTechnology(wlan)
                    // .addWpanTechnology(bleWpan)
                    .setCepTechnology(asperCep)
                    .setAutoConnect(true)
                    .setLog(true)
                    .build()

                MobileHub.start()
                result.success("MobileHubService started")
            }
            "updateContext" -> {
                val payload = call.argument<List<String>>("devices") ?: emptyList()
                MobileHub.updateContext(payload)
                result.success("Context updated")
            }
            "stopMobileHub" -> {
                MobileHub.stop()
                result.success("MobileHubService stopped")
            }
            "isMobileHubStarted" -> {
                result.success(MobileHub.isStarted)
            }
            "startListening" -> {
                if (activity == null) {
                    result.error("NO_ACTIVITY", "The plugin is not attached to an activity.", null)
                } else {
                    val uuids = call.argument<List<String>>("uuids")
                    bleMessageReceiver?.startListening(result, uuids)
                }
            }
            "stopListening" -> {
                bleMessageReceiver?.stopListening()
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        onScanningStateChangedChannel.setStreamHandler(null)
        onMessageReceivedChannel.setStreamHandler(null)
        onBleDataReceivedChannel.setStreamHandler(null)
        bleMessageReceiver = null
        notificationHelper = null
        eventSubscription?.dispose()
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        activityBinding = binding
        bleMessageReceiver?.setActivity(activity)
        binding.addRequestPermissionsResultListener(bleMessageReceiver!!)
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        bleMessageReceiver?.stopListening()
        activity = null
        bleMessageReceiver?.setActivity(null)
        activityBinding?.removeRequestPermissionsResultListener(bleMessageReceiver!!)
        activityBinding = null
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }
}