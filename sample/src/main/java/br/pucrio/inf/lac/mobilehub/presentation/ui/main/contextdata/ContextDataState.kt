package br.pucrio.inf.lac.mobilehub.presentation.ui.main.contextdata

import br.pucrio.inf.lac.mobilehub.presentation.ui.model.SensorDataUiModel

internal data class ContextDataState(
    val isLoading: Boolean = false,
    val contextData: List<SensorDataUiModel> = emptyList(),
    val error: Exception? = null
)
