package br.pucrio.inf.lac.mobilehub.presentation.ui.main.home

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.entities.Moid

internal data class HomeState(
    val isInitialized: Boolean = false,
    val isLoading: Boolean = false,
    val isMobileHubRunning: Boolean = false,
    val discoveredDevices: Map<Moid, MobileObject> = emptyMap(),
    val error: Exception? = null
)
