package br.pucrio.inf.lac.mobilehub.presentation.ui.mapper

import br.pucrio.inf.lac.mobilehub.architecture.Mapper
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.presentation.ui.model.SensorDataUiModel
import javax.inject.Inject

internal class SensorDataMapper @Inject constructor() : Mapper<SensorData, SensorDataUiModel> {
    override fun from(input: SensorData) = SensorDataUiModel(
        serviceName = input.serviceName,
        serviceData = input.serviceData.map {
            String.format("%.1f", it)
        }
    )
}
