package br.pucrio.inf.lac.mobilehub.di.module

import android.content.Context
import br.pucrio.inf.lac.mobilehub.data.local.ConfigurationLocalDataSourceImpl
import br.pucrio.inf.lac.mobilehub.data.repository.ConfigurationLocalDataSource
import br.pucrio.inf.lac.mobilehub.data.repository.ConfigurationRepositoryImpl
import br.pucrio.inf.lac.mobilehub.domain.repository.ConfigurationRepository
import dagger.Module
import dagger.Provides

@Module
internal class DataModule {
    @Provides
    fun provideConfigurationLocalDataSource(
        context: Context
    ): ConfigurationLocalDataSource = ConfigurationLocalDataSourceImpl(context)

    @Provides
    fun provideConfigurationRepository(
        localDataSource: ConfigurationLocalDataSource
    ): ConfigurationRepository = ConfigurationRepositoryImpl(localDataSource)
}
