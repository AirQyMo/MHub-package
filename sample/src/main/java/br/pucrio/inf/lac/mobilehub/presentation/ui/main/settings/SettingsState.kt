package br.pucrio.inf.lac.mobilehub.presentation.ui.main.settings

import br.pucrio.inf.lac.mobilehub.presentation.ui.model.SettingUiModel

internal data class SettingsState(
    val isLoading: Boolean = false,
    val wlanOptions: List<String> = emptyList(),
    val wlanIndex: Int = -1,
    val ipAddress: SettingUiModel<String> = SettingUiModel(""),
    val port: SettingUiModel<String> = SettingUiModel(""),
    val error: Exception? = null
) {
    val wlan: String = if (wlanIndex >= 0) wlanOptions[wlanIndex] else ""
}
