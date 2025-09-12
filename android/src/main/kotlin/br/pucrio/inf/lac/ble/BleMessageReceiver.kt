package br.pucrio.inf.lac.ble

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanSettings
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import io.reactivex.disposables.Disposable

class BleMessageReceiver(
    private val context: Context
) : PluginRegistry.RequestPermissionsResultListener {

    private val TAG = "BleMessageReceiver"
    private var activity: Activity? = null
    var onMessageReceivedSink: EventChannel.EventSink? = null

    private lateinit var rxBleClient: RxBleClient
    private var scanSubscription: Disposable? = null
    private var isScanning: Boolean = false

    private var pendingResult: MethodChannel.Result? = null
    private val PERMISSION_REQUEST_CODE = 3636

    init {
        rxBleClient = RxBleClient.create(context)
    }

    fun setActivity(activity: Activity?) {
        this.activity = activity
    }

    private fun hasPermissions(): Boolean {
        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        return requiredPermissions.all { context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
    }

    private fun requestPermissions() {
        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        activity?.requestPermissions(requiredPermissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startScan()
            } else {
                pendingResult?.error("PERMISSIONS_DENIED", "Permissions denied", null)
                pendingResult = null
            }
            return true
        }
        return false
    }

    fun startListening(result: MethodChannel.Result) {
        this.pendingResult = result
        if (hasPermissions()) {
            startScan()
        } else {
            requestPermissions()
        }
    }

    private fun startScan() {
        Log.d(TAG, "startScan called")

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanSubscription = rxBleClient.scanBleDevices(scanSettings)
            .subscribe(
                { scanResult ->
                    Log.d(TAG, "Device found: ${scanResult.bleDevice.macAddress}, Name: ${scanResult.bleDevice.name}")
                    onMessageReceivedSink?.success(scanResult.bleDevice.name)
                },
                { throwable ->
                    Log.e(TAG, "BLE scan failed: $throwable")
                    onMessageReceivedSink?.error("SCAN_ERROR", "BLE scan failed", throwable.message)
                }
            )
        isScanning = true
        pendingResult?.success(null)
        pendingResult = null
    }

    fun stopListening() {
        Log.d(TAG, "stopListening called")
        scanSubscription?.dispose()
        isScanning = false
        Log.d(TAG, "BLE scanning stopped")
    }
}