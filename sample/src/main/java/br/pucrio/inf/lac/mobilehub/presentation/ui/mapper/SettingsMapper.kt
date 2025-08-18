package br.pucrio.inf.lac.mobilehub.presentation.ui.mapper

import android.content.Context
import br.pucrio.inf.lac.mobilehub.R
import br.pucrio.inf.lac.mobilehub.architecture.Mapper
import br.pucrio.inf.lac.mobilehub.domain.entity.ConfigurationEntity
import br.pucrio.inf.lac.mobilehub.domain.entity.Wlan
import br.pucrio.inf.lac.mobilehub.presentation.ui.main.settings.SettingsState
import br.pucrio.inf.lac.mobilehub.presentation.ui.model.SettingUiModel
import javax.inject.Inject

internal class SettingsMapper @Inject constructor(
    private val context: Context
) : Mapper<ConfigurationEntity, SettingsState> {

    private val wlanTechnologies = Wlan.values()

    private val wlanTechnologyNames = wlanTechnologies.map { it.asString }

    private val Wlan.asString: String
        get() = when (this) {
            Wlan.MQTT -> context.getString(R.string.text_settings_technology_mqtt)
            Wlan.MR_UDP -> context.getString(R.string.text_settings_technology_mrudp)
        }

    override fun from(
        input: ConfigurationEntity
    ) = SettingsState(
        wlanOptions = wlanTechnologyNames,
        wlanIndex = wlanTechnologies.indexOf(input.wlan),
        ipAddress = SettingUiModel(
            value = input.ipAddress,
            isError = false
        ),
        port = SettingUiModel(
            value = input.port.toString(),
            isError = false
        )
    )
}
