package br.pucrio.inf.lac.mobilehub.di

import android.content.Context
import br.pucrio.inf.lac.mobilehub.di.module.ApplicationModule
import br.pucrio.inf.lac.mobilehub.di.module.ContextDataModule
import br.pucrio.inf.lac.mobilehub.di.module.DataModule
import br.pucrio.inf.lac.mobilehub.di.module.HomeModule
import br.pucrio.inf.lac.mobilehub.di.module.SettingsModule
import br.pucrio.inf.lac.mobilehub.presentation.ui.main.contextdata.ContextDataViewModel
import br.pucrio.inf.lac.mobilehub.presentation.ui.main.home.HomeViewModel
import br.pucrio.inf.lac.mobilehub.presentation.ui.main.settings.SettingsViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        HomeModule::class,
        ContextDataModule::class,
        SettingsModule::class,
        DataModule::class
    ]
)
internal interface GraphComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): GraphComponent
    }

    val settingsViewModel: SettingsViewModel

    val contextDataViewModel: ContextDataViewModel

    val homeViewModel: HomeViewModel
}
