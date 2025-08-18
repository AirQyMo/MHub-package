package br.pucrio.inf.lac.mobilehub

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import br.pucrio.inf.lac.asper.AsperCEP
import br.pucrio.inf.lac.ble.BleWPAN
import br.pucrio.inf.lac.ble.transcoder.lua.LuaTranscoderFactory
import br.pucrio.inf.lac.cdp.android.AndroidCDP
import br.pucrio.inf.lac.mobilehub.core.MobileHub
import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.di.DaggerGraphComponent
import br.pucrio.inf.lac.mobilehub.di.SampleInjector
import br.pucrio.inf.lac.mobilehub.drivers.CC2650SensorTag
import br.pucrio.inf.lac.mqtt.MqttWLAN
import br.pucrio.inf.lac.mrudp.MrudpWLAN

class App : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        SampleInjector.component = DaggerGraphComponent.builder()
            .context(this)
            .build()
    }
}
