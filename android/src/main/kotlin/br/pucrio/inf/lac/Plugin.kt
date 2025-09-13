package br.pucrio.inf.lac

import android.app.Activity
import android.content.Context
import br.pucrio.inf.lac.ble.BleMessageReceiver
import br.pucrio.inf.lac.mobilehub.core.MobileHubService
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** Plugin */
class Plugin: FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var channel : MethodChannel
    private lateinit var onScanningStateChangedChannel: EventChannel
    private lateinit var onMessageReceivedChannel: EventChannel
    private lateinit var context: Context
    private var activity: Activity? = null
    private var activityBinding: ActivityPluginBinding? = null
    private var bleMessageReceiver: BleMessageReceiver? = null

    private var onScanningStateChangedSink: EventChannel.EventSink? = null
    private var onMessageReceivedSink: EventChannel.EventSink? = null

    private val onMessageReceivedHandler = object : EventChannel.StreamHandler {
        override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
            onMessageReceivedSink = events
            bleMessageReceiver?.onMessageReceivedSink = onMessageReceivedSink
        }

        override fun onCancel(arguments: Any?) {
            onMessageReceivedSink = null
            bleMessageReceiver?.onMessageReceivedSink = null
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

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "plugin")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
        bleMessageReceiver = BleMessageReceiver(context)

        onMessageReceivedChannel = EventChannel(flutterPluginBinding.binaryMessenger, "onMessageReceived")
        onMessageReceivedChannel.setStreamHandler(onMessageReceivedHandler)

        onScanningStateChangedChannel = EventChannel(flutterPluginBinding.binaryMessenger, "onScanningStateChanged")
        onScanningStateChangedChannel.setStreamHandler(onScanningStateChangedHandler)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "startMobileHub" -> {
                MobileHubService.startService(context)
                result.success("MobileHubService started")
            }
            "stopMobileHub" -> {
                MobileHubService.stopService(context)
                result.success("MobileHubService stopped")
            }
            "isMobileHubStarted" -> {
                result.success(MobileHubService.isStarted)
            }
            "startListening" -> {
                if (activity == null) {
                    result.error("NO_ACTIVITY", "The plugin is not attached to an activity.", null)
                } else {
                    val uuid = call.argument<String>("uuid")
                    bleMessageReceiver?.startListening(result, uuid)
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
        bleMessageReceiver = null
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