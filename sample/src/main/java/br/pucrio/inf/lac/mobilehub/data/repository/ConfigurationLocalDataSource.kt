package br.pucrio.inf.lac.mobilehub.data.repository

import br.pucrio.inf.lac.mobilehub.domain.entity.ConfigurationEntity

internal interface ConfigurationLocalDataSource {
    suspend fun loadSettings(): ConfigurationEntity

    suspend fun loadSettings(wpanIndex: Int): ConfigurationEntity

    suspend fun saveSettings(settings: ConfigurationEntity)
}
