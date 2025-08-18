package br.pucrio.inf.lac.mobilehub.di.module

import br.pucrio.inf.lac.mobilehub.architecture.Mapper
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.presentation.ui.main.contextdata.ContextDataViewModel
import br.pucrio.inf.lac.mobilehub.presentation.ui.mapper.SensorDataMapper
import br.pucrio.inf.lac.mobilehub.presentation.ui.model.SensorDataUiModel
import dagger.Module
import dagger.Provides

@Module
internal class ContextDataModule {
    @Provides
    fun provideViewModel(
        mapper: Mapper<SensorData, SensorDataUiModel>
    ): ContextDataViewModel = ContextDataViewModel(mapper)

    @Provides
    fun provideSensorDataMapper(): Mapper<SensorData, SensorDataUiModel> = SensorDataMapper()
}
