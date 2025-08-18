package br.pucrio.inf.lac.mobilehub.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import br.pucrio.inf.lac.mobilehub.presentation.ui.navigation.NavigationGraph
import io.reactivex.disposables.CompositeDisposable

class MainActivity : ComponentActivity() {
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                NavigationGraph()
            }
        }

        /*
        MobileHub.on(MobileHubEvent.NewMessage::class.java)
            .subscribe { Timber.e(it.message.toString()) }
            .let { disposables += it }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    /*private fun enableBluetooth(context: Context) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothAdapter.enable()
    }*/
}
