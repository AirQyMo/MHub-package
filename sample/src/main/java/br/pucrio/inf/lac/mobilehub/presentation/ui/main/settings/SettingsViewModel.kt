package br.pucrio.inf.lac.mobilehub.presentation.ui.main.settings

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.pucrio.inf.lac.mobilehub.architecture.Mapper
import br.pucrio.inf.lac.mobilehub.core.MobileHub
import br.pucrio.inf.lac.mobilehub.domain.entity.ConfigurationEntity
import br.pucrio.inf.lac.mobilehub.domain.entity.Wlan
import br.pucrio.inf.lac.mobilehub.domain.repository.ConfigurationRepository
import br.pucrio.inf.lac.mobilehub.presentation.ui.model.SettingUiModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
internal class SettingsViewModel @Inject constructor(
    private val configurationRepository: ConfigurationRepository,
    private val settingsMapper: Mapper<ConfigurationEntity, SettingsState>
) : ViewModel() {
    companion object {
        private const val INPUT_DEBOUNCE = 2000L
    }

    private var _state = MutableStateFlow(SettingsState())
    val state get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadSettings()
            _state.debounce(INPUT_DEBOUNCE)
                .drop(1)
                .distinctUntilChanged()
                .collect(::saveSettings)
        }
    }

    private suspend fun loadSettings() {
        val settings = configurationRepository.getSettings()
        _state.value = settingsMapper.from(settings)
    }

    private suspend fun saveSettings(value: SettingsState) =
        configurationRepository.saveSettings(
            settings = ConfigurationEntity(
                wlan = Wlan.values()[value.wlanIndex],
                ipAddress = value.ipAddress.value,
                port = value.port.value.toIntOrNull()
            )
        )

    fun updateWpanIndex(index: Int) {
        viewModelScope.launch {
            val settings = configurationRepository.getSettings(index)
            _state.value = settingsMapper.from(settings)
        }
    }

    fun updateIpAddress(ipAddress: String) {
        _state.value = _state.value.copy(
            ipAddress = SettingUiModel(
                value = ipAddress,
                isError = false
            )
        )
    }

    fun updatePort(port: String) {
        if (port.length > 5) return

        val isError = !SettingsValidator.isPort(port)
        _state.value = _state.value.copy(
            port = SettingUiModel(
                value = port,
                isError = isError
            )
        )
    }

    fun restart(context: Context) {
        MobileHub.stop()
        val packageManager: PackageManager = context.packageManager
        val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
        val componentName: ComponentName = intent.component!!
        val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(restartIntent)
        Runtime.getRuntime().exit(0)
    }
}
