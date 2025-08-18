package br.pucrio.inf.lac.mobilehub.domain.repository

import br.pucrio.inf.lac.mobilehub.domain.entity.ConfigurationEntity

interface ConfigurationRepository {

    suspend fun getSettings(): ConfigurationEntity

    suspend fun getSettings(wpanIndex: Int): ConfigurationEntity

    suspend fun saveSettings(settings: ConfigurationEntity)
}
