package br.pucrio.inf.lac.mobilehub.core.di.components

import android.content.Context
import br.pucrio.inf.lac.mobilehub.core.MobileHubService
import br.pucrio.inf.lac.mobilehub.core.data.buffer.BufferStrategy
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEP
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wpan.WPAN
import br.pucrio.inf.lac.mobilehub.core.di.modules.*
import br.pucrio.inf.lac.mobilehub.core.di.modules.factories.WorkerFactoryImpl
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cdp.ContextDataProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApplicationModule::class,
    DomainModule::class,
    DataModule::class,
    LocalModule::class,
    RemoteModule::class,
    WorkerModule::class
])
internal interface GraphComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(context: Context): Builder

        @BindsInstance
        fun bufferStrategy(bufferStrategy: BufferStrategy): Builder

        @BindsInstance
        fun wlanTechnology(wlanTechnology: WLAN): Builder

        @BindsInstance
        fun wpanTechnologies(wpanTechnologies: HashMap<Int, WPAN>): Builder

        @BindsInstance
        fun cepTechnology(cepTechnology: CEP): Builder

        @BindsInstance
        fun contextDataProvider(contextDataProvider: ContextDataProvider): Builder

        fun build(): GraphComponent
    }

    fun factory(): WorkerFactoryImpl

    fun inject(service: MobileHubService)
}



