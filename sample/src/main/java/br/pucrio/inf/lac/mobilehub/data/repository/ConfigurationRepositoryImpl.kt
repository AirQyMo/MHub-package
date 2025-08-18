package br.pucrio.inf.lac.mobilehub.data.repository

import br.pucrio.inf.lac.mobilehub.domain.entity.ConfigurationEntity
import br.pucrio.inf.lac.mobilehub.domain.repository.ConfigurationRepository

internal class ConfigurationRepositoryImpl(
    private val localDataSource: ConfigurationLocalDataSource
) : ConfigurationRepository {

    override suspend fun getSettings(): ConfigurationEntity = localDataSource.loadSettings()

    override suspend fun getSettings(wlanIndex: Int): ConfigurationEntity = localDataSource.loadSettings(wlanIndex)

    override suspend fun saveSettings(settings: ConfigurationEntity) = localDataSource.saveSettings(settings)
}
