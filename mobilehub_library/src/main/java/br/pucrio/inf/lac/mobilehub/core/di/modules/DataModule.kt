package br.pucrio.inf.lac.mobilehub.core.di.modules

import br.pucrio.inf.lac.mobilehub.core.data.local.source.CepQueryLocalDataSourceImpl
import br.pucrio.inf.lac.mobilehub.core.data.local.source.MobileObjectDriverLocalDataSourceImpl
import br.pucrio.inf.lac.mobilehub.core.data.remote.source.MobileObjectDriverRemoteDataSourceImpl
import br.pucrio.inf.lac.mobilehub.core.data.repositories.*
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.*
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.CepQueryRepository
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.EventRepository
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MessageRepository
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MobileObjectRepository
import dagger.Binds
import dagger.Module

@Module
internal abstract class DataModule {
    @Binds
    abstract fun bindsMobileObjectRepository(repository: MobileObjectRepositoryImpl): MobileObjectRepository

    @Binds
    abstract fun bindsMessageRepository(repository: MessageRepositoryImpl): MessageRepository

    @Binds
    abstract fun bindsEventRepository(repository: EventRepositoryImpl): EventRepository

    @Binds
    abstract fun bindsCepQueryRepository(repository: CepQueryRepositoryImpl): CepQueryRepository

    @Binds
    abstract fun bindsCepQueryLocalDataSource(localDataSource: CepQueryLocalDataSourceImpl): CepQueryLocalDataSource

    @Binds
    abstract fun bindsMobileObjectDriverRepository(
        repository: MobileObjectDriverRepositoryImpl
    ): MobileObjectDriverRepository

    @Binds
    abstract fun bindsMobileObjectDriverLocalDataSource(
        localDataSource: MobileObjectDriverLocalDataSourceImpl
    ): MobileObjectDriverLocalDataSource

    @Binds
    abstract fun bindsMobileObjectDriverRemoteDataSource(
        localDataSource: MobileObjectDriverRemoteDataSourceImpl
    ): MobileObjectDriverRemoteDataSource

    @Binds
    abstract fun bindsContextSensorDataRepository(
        repository: ContextSensorDataRepositoryImpl
    ): ContextSensorDataRepository
}
