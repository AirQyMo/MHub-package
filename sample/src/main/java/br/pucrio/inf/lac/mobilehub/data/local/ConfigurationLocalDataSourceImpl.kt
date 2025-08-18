package br.pucrio.inf.lac.mobilehub.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.pucrio.inf.lac.mobilehub.data.repository.ConfigurationLocalDataSource
import br.pucrio.inf.lac.mobilehub.domain.entity.ConfigurationEntity
import br.pucrio.inf.lac.mobilehub.domain.entity.Wlan
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class ConfigurationLocalDataSourceImpl @Inject constructor(
    private val context: Context
) : ConfigurationLocalDataSource {
    private object Default {
        const val IP = "192.168.2.31"
        const val PORT = 1883
    }

    companion object {
        private val Context.dataStore by preferencesDataStore(name = Key.NAME)
    }

    private object Key {
        const val NAME = "Settings"

        val WLAN = intPreferencesKey("wlan")

        fun ipAddress(wlan: Int) = stringPreferencesKey("${wlan}ip_address")

        fun port(wlan: Int) = intPreferencesKey("${wlan}port")
    }

    override suspend fun loadSettings(): ConfigurationEntity {
        val preferences = context.dataStore.data.first().toPreferences()
        val wlanIndex = preferences[Key.WLAN] ?: 0
        return preferences.toEntity(wlanIndex)
    }

    override suspend fun loadSettings(wlanIndex: Int): ConfigurationEntity {
        val preferences = context.dataStore.data.first().toPreferences()
        return preferences.toEntity(wlanIndex)
    }

    private fun Preferences.toEntity(wlanIndex: Int) = ConfigurationEntity(
        wlan = Wlan.values()[wlanIndex],
        ipAddress = this[Key.ipAddress(wlanIndex)] ?: Default.IP,
        port = this[Key.port(wlanIndex)] ?: Default.PORT,
    )

    override suspend fun saveSettings(settings: ConfigurationEntity) {
        val wlanIndex = Wlan.values().indexOf(settings.wlan)
        context.dataStore.edit { preferences ->
            preferences[Key.WLAN] = wlanIndex
            preferences[Key.ipAddress(wlanIndex)] = settings.ipAddress
            settings.port?.let { preferences[Key.port(wlanIndex)] = it }
        }
    }
}
