package br.pucrio.inf.lac.mobilehub.presentation.ui.main.settings

internal object SettingsValidator {
    fun isPort(port: String): Boolean =
        when (val value = port.toIntOrNull()) {
            null -> false
            else -> port[0].isDigit() && value in 1..65535
        }
}
