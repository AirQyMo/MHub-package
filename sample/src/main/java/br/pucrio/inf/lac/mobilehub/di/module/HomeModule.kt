package br.pucrio.inf.lac.mobilehub.di.module

import br.pucrio.inf.lac.mobilehub.domain.repository.ConfigurationRepository
import br.pucrio.inf.lac.mobilehub.presentation.ui.main.home.HomeViewModel
import dagger.Module
import dagger.Provides

@Module
internal class HomeModule {
    @Provides
    fun provideViewModel(
        repository: ConfigurationRepository
    ): HomeViewModel = HomeViewModel(repository)
}
