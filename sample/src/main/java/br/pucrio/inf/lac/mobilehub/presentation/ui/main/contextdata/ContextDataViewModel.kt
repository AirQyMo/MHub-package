package br.pucrio.inf.lac.mobilehub.presentation.ui.main.contextdata

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import br.pucrio.inf.lac.mobilehub.architecture.Mapper
import br.pucrio.inf.lac.mobilehub.core.MobileHub
import br.pucrio.inf.lac.mobilehub.core.MobileHubEvent
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.presentation.ui.model.SensorDataUiModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

internal class ContextDataViewModel @Inject constructor(
    private val mapper: Mapper<SensorData, SensorDataUiModel>
) : ViewModel() {
    var state by mutableStateOf(ContextDataState())
        private set

    private val disposables = CompositeDisposable()

    init {
        MobileHub.on(MobileHubEvent.NewContextData::class.java)
            .subscribe(::setContextData, Timber::e)
            .let { disposables += it }
    }

    private fun setContextData(event: MobileHubEvent.NewContextData) {
        val sensorDataUiModels = event.contextData.map(mapper::from)
        state = state.copy(contextData = sensorDataUiModels)
    }
}
