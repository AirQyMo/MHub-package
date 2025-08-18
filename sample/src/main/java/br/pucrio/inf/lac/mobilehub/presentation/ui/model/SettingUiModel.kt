package br.pucrio.inf.lac.mobilehub.presentation.ui.model

internal data class SettingUiModel<T>(
    val value: T,
    val isError: Boolean = false
)
