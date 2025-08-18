package br.pucrio.inf.lac.mobilehub.di.module

import android.content.Context
import br.pucrio.inf.lac.mobilehub.architecture.Mapper
import br.pucrio.inf.lac.mobilehub.domain.entity.ConfigurationEntity
import br.pucrio.inf.lac.mobilehub.domain.repository.ConfigurationRepository
import br.pucrio.inf.lac.mobilehub.presentation.ui.main.settings.SettingsState
import br.pucrio.inf.lac.mobilehub.presentation.ui.main.settings.SettingsViewModel
import br.pucrio.inf.lac.mobilehub.presentation.ui.mapper.SettingsMapper
import dagger.Module
import dagger.Provides

@Module
internal class SettingsModule {
    @Provides
    fun provideViewModel(
        repository: ConfigurationRepository,
        mapper: Mapper<ConfigurationEntity, SettingsState>
    ): SettingsViewModel = SettingsViewModel(repository, mapper)

    @Provides
    fun provideSettingsMapper(
        context: Context
    ): Mapper<ConfigurationEntity, SettingsState> = SettingsMapper(context)
}
