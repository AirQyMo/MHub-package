package br.pucrio.inf.lac.ble

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanSettings
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import io.reactivex.disposables.Disposable
import java.util.UUID

class BleMessageReceiver(
    private val context: Context
) : PluginRegistry.RequestPermissionsResultListener {

    private val TAG = "BleMessageReceiver"
    private var activity: Activity? = null
    var onBleDataReceivedSink: EventChannel.EventSink? = null
    var onScanningStateChangedSink: EventChannel.EventSink? = null

    private lateinit var rxBleClient: RxBleClient
    private var scanSubscription: Disposable? = null
    private var isScanning: Boolean = false

    private var pendingResult: MethodChannel.Result? = null
    private var pendingUuids: List<String>? = null
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

    fun startListening(result: MethodChannel.Result, uuids: List<String>?) {
        this.pendingResult = result
        this.pendingUuids = uuids
        if (hasPermissions()) {
            startScan()
        } else {
            requestPermissions()
        }
    }

    private fun startScan() {
        val uuids = this.pendingUuids?.mapNotNull {
            try {
                UUID.fromString(it)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Invalid UUID format: $it")
                null
            }
        }
        Log.d(TAG, "startScan called with UUIDs: $uuids")

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val scanObservable = rxBleClient.scanBleDevices(scanSettings)

        scanSubscription = scanObservable
            .filter { scanResult ->
                if (uuids.isNullOrEmpty()) {
                    true // No filter, so all devices pass
                } else {
                    val serviceUuids = scanResult.scanRecord?.serviceUuids?.map { it.uuid } ?: emptyList()
                    serviceUuids.any { it in uuids }
                }
            }
            .subscribe(
                { scanResult ->
                    val serviceUuids = scanResult.scanRecord?.serviceUuids?.map { it.uuid } ?: emptyList()
                    val matchedUuid = if (!uuids.isNullOrEmpty()) {
                        serviceUuids.firstOrNull { it in uuids }
                    } else {
                        serviceUuids.firstOrNull()
                    }

                    val deviceData = mapOf(
                        "name" to scanResult.bleDevice.name,
                        "uuid" to matchedUuid?.toString(),
                        "rssi" to scanResult.rssi
                    )
                    Log.d(TAG, "Device found: $deviceData")
                    onBleDataReceivedSink?.success(deviceData)
                },
                { throwable ->
                    Log.e(TAG, "BLE scan failed: $throwable")
                    isScanning = false
                    onScanningStateChangedSink?.success(false)
                    onBleDataReceivedSink?.error("SCAN_ERROR", "BLE scan failed", throwable.message)
                }
            )
        isScanning = true
        onScanningStateChangedSink?.success(true)
        pendingResult?.success(null)
        pendingResult = null
    }

    fun stopListening() {
        Log.d(TAG, "stopListening called")
        scanSubscription?.dispose()
        isScanning = false
        onScanningStateChangedSink?.success(false)
        Log.d(TAG, "BLE scanning stopped")
    }
}